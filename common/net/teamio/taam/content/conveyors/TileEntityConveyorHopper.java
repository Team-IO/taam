package net.teamio.taam.content.conveyors;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.teamio.taam.Config;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRedstoneControlled;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.ConveyorSlotsInventory;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.network.TPMachineConfiguration;
import net.teamio.taam.util.TaamUtil;
import net.teamio.taam.util.WorldCoord;


public class TileEntityConveyorHopper extends BaseTileEntity implements IRedstoneControlled, IRotatable, ITickable {

	private boolean highSpeed;
	private int timeout;
	private boolean eject;
	private boolean stackMode;
	private boolean linearMode;
	private byte redstoneMode;

	private EnumFacing direction = EnumFacing.NORTH;

	private boolean pulseWasSent = false;

	private ItemStackHandler itemHandler;
	private ConveyorSlotsInventory conveyorSlots;

	public TileEntityConveyorHopper() {
		this(false);
	}

	public TileEntityConveyorHopper(boolean highSpeed) {
		this.highSpeed = highSpeed;
		itemHandler = new ItemStackHandler(5);
		conveyorSlots = new ConveyorSlotsInventory(itemHandler) {
			@Override
			public void onChangeHook() {
				updateState(true, false, false);
			};
		};
	}

	@Override
	public String getName() {
		if (highSpeed) {
			return "tile.taam.productionline.hopper_hs.name";
		} else {
			return "tile.taam.productionline.hopper.name";
		}
	}

	@Override
	public void update() {
		if (worldObj.isRemote) {
			return;
		}

		/*
		 * Find items laying on the conveyor.
		 */

		ConveyorUtil.tryInsertItemsFromWorld(this, worldObj, null, true);



		boolean isShutdown = false;


		boolean redstoneHigh = worldObj.isBlockIndirectlyGettingPowered(pos) > 0;
		boolean isPulsing = false;

		// Redstone. Other criteria?
		if(redstoneMode == IRedstoneControlled.MODE_ACTIVE_ON_HIGH && !redstoneHigh) {
			isShutdown = true;
		} else if(redstoneMode == IRedstoneControlled.MODE_ACTIVE_ON_LOW && redstoneHigh) {
			isShutdown = true;
		} else if(redstoneMode == IRedstoneControlled.MODE_ACTIVE_ON_HIGH_PULSE) {
			// Pulse, send Item on high edge.
			isShutdown = !(!pulseWasSent && redstoneHigh);
			isPulsing = true;
		} else if(redstoneMode == IRedstoneControlled.MODE_ACTIVE_ON_LOW_PULSE) {
			// Pulse, send Item on low edge.
			isShutdown = !(pulseWasSent && !redstoneHigh);
			isPulsing = true;
		} else if(redstoneMode > 4 || redstoneMode < 0) {
			isShutdown = worldObj.rand.nextBoolean();
		}

		pulseWasSent = redstoneHigh;

		if(isShutdown) {
			return;
		}

		if(isPulsing) {
			timeout = 0;
		} else if(isCoolingDown()) {
			timeout--;
			return;
		}

		boolean somethingEjected = false;
		boolean wholeStack = highSpeed && stackMode;

		if(eject) {
			if(!TaamUtil.canDropIntoWorld(worldObj, pos.down())) {
				return;
			}
			for(int i = 0; i < itemHandler.getSlots(); i++) {
				ItemStack stack = itemHandler.extractItem(i, wholeStack ? 1 : 64, false);
				if(stack == null || stack.stackSize == 0) {
					continue;
				}

				/*
				 * -----------
				 */

				EntityItem item = new EntityItem(worldObj, pos.getX() + 0.5, pos.getY() - 0.3, pos.getZ() + 0.5, stack);
				item.motionX = 0;
				item.motionY = 0;
				item.motionZ = 0;
				worldObj.spawnEntityInWorld(item);

				somethingEjected = true;
				break;
			}
		} else {

			TileEntity ent = worldObj.getTileEntity(pos.down());
			if(ent == null) {
				return;
			}
			IItemHandler targetHandler = ent.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
			if(targetHandler == null) {
				return;
			}

			for(int i = 0; i < itemHandler.getSlots(); i++) {
				// Simulate extracting first to see if there is anything in there
				ItemStack stack = itemHandler.extractItem(i,  wholeStack ? 1 : 64, true);
				if(stack != null && stack.stackSize == 0) {
					/*
					 * -----------
					 */
					ItemStack unableToInsert = ItemHandlerHelper.insertItemStacked(targetHandler, stack, false);

					int amount = stack.stackSize;
					if(unableToInsert != null) {
						amount -= unableToInsert.stackSize;
					}
					// If we fit anything, decrease inventory accordingly
					if(amount > 0) {
						itemHandler.extractItem(i,  wholeStack ? 1 : 64, false);
						somethingEjected = true;
						break;
					}
					// In linear mode, we only look at the front most stack that has items
					if(linearMode) {
						break;
					}

				}
			}
		}
		boolean changed = false;

		if(somethingEjected) {
			if(highSpeed && !(stackMode && Config.pl_hopper_stackmode_normal_speed)) {
				timeout += Config.pl_hopper_highspeed_delay;
			} else {
				timeout += Config.pl_hopper_delay;
			}
			changed = true;
		}

		// Move Items to the front in linear mode
		if(linearMode) {
			for(int i = 0; i < itemHandler.getSlots() - 1; i++) {
				if(itemHandler.getStackInSlot(i) == null) {
					itemHandler.setStackInSlot(i, itemHandler.getStackInSlot(i + 1));
					itemHandler.setStackInSlot(i + 1, null);
					changed = true;
				}
			}
		}
		if(changed) {
			updateState(false, false, false);
		}
	}

	public boolean isCoolingDown() {
		return timeout > 0;
	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setTag("items", itemHandler.serializeNBT());
		tag.setBoolean("highSpeed", highSpeed);
		tag.setInteger("timeout", timeout);
		tag.setBoolean("eject", eject);
		tag.setBoolean("linearMode", linearMode);
		tag.setBoolean("stackMode", stackMode);
		tag.setByte("redstoneMode", redstoneMode);

		tag.setBoolean("pulseWasSent", pulseWasSent);
		tag.setInteger("direction", direction.ordinal());
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		NBTTagCompound itemTag = tag.getCompoundTag("items");
		if(itemTag != null) {
			itemHandler.deserializeNBT(itemTag);
		}
		highSpeed = tag.getBoolean("highSpeed");
		timeout = tag.getInteger("timeout");
		eject = tag.getBoolean("eject");
		linearMode = tag.getBoolean("linearMode");
		stackMode = tag.getBoolean("stackMode");
		redstoneMode = tag.getByte("redstoneMode");

		pulseWasSent = tag.getBoolean("pulseWasSent");
		direction = EnumFacing.getFront(tag.getInteger("direction"));
		if(direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
			direction = EnumFacing.NORTH;
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		if(capability == Taam.CAPABILITY_CONVEYOR) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) itemHandler;
		}
		if(capability == Taam.CAPABILITY_CONVEYOR) {
			return (T) conveyorSlots;
		}
		return super.getCapability(capability, facing);
	}

	/*
	 * Accessors
	 */

	public boolean isHighSpeed() {
		return highSpeed;
	}

	public void setHighSpeed(boolean highSpeed) {
		this.highSpeed = highSpeed;
	}

	public boolean isEject() {
		return eject;
	}

	public void setEject(boolean eject) {
		this.eject = eject;
		if(worldObj.isRemote) {
			TPMachineConfiguration config = TPMachineConfiguration.newChangeBoolean(new WorldCoord(this), (byte)1, eject);
			TaamMain.network.sendToServer(config);
		} else {
			markDirty();
		}
	}

	public boolean isStackMode() {
		return stackMode;
	}

	public void setStackMode(boolean stackMode) {
		this.stackMode = stackMode;
		if(worldObj.isRemote) {
			TPMachineConfiguration config = TPMachineConfiguration.newChangeBoolean(new WorldCoord(this), (byte)2, stackMode);
			TaamMain.network.sendToServer(config);
		} else {
			markDirty();
		}
	}


	public boolean isLinearMode() {
		return linearMode;
	}

	public void setLinearMode(boolean linearMode) {
		this.linearMode = linearMode;
		if(worldObj.isRemote) {
			TPMachineConfiguration config = TPMachineConfiguration.newChangeBoolean(new WorldCoord(this), (byte)3, linearMode);
			TaamMain.network.sendToServer(config);
		} else {
			markDirty();
		}
	}

	/*
	 * IRedstoneControlled implementation
	 */

	@Override
	public boolean isPulsingSupported() {
		return true;
	}

	@Override
	public byte getRedstoneMode() {
		return redstoneMode;
	}

	@Override
	public void setRedstoneMode(byte mode) {
		redstoneMode = mode;
		if(worldObj.isRemote) {
			TPMachineConfiguration config = TPMachineConfiguration.newChangeInteger(new WorldCoord(this), (byte)1, redstoneMode);
			TaamMain.network.sendToServer(config);
		} else {
			markDirty();
		}
	}

	/*
	 * IRotatable Implementation
	 */

	@Override
	public EnumFacing getFacingDirection() {
		return direction;
	}

	@Override
	public EnumFacing getNextFacingDirection() {
		return direction.rotateY();
	}

	@Override
	public void setFacingDirection(EnumFacing direction) {
		if(this.direction != direction) {
			// Only update if necessary
			this.direction = direction;
			updateState(false, true, false);
		}
	}

}
