package net.teamio.taam.content.conveyors;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IHopper;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.util.Constants.NBT;
import net.teamio.taam.Config;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRedstoneControlled;
import net.teamio.taam.content.IRenderable;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;
import net.teamio.taam.network.TPMachineConfiguration;
import net.teamio.taam.util.TaamUtil;
import net.teamio.taam.util.WorldCoord;
import net.teamio.taam.util.inv.InventoryRange;
import net.teamio.taam.util.inv.InventorySimple;
import net.teamio.taam.util.inv.InventoryUtils;


public class TileEntityConveyorHopper extends BaseTileEntity implements IConveyorAwareTE, IInventory, IHopper, IRedstoneControlled, IRotatable, ITickable, IRenderable {

	private InventorySimple inventory;
	
	private boolean highSpeed;
	private int timeout;
	private boolean eject;
	private boolean stackMode;
	private boolean linearMode;
	private byte redstoneMode;
	
	private EnumFacing direction = EnumFacing.NORTH;
	
	private boolean pulseWasSent = false;
	
	public static List<String> parts_regular = Collections.unmodifiableList(Lists.newArrayList("Support_Alu_smdl_alu", "ConveyorHopper_chmdl"));
	public static List<String> parts_hs = Collections.unmodifiableList(Lists.newArrayList("Support_Alu_smdl_alu", "ConveyorHopperHighSpeed_chmdl_hs"));
	
	public TileEntityConveyorHopper() {
		this(false);
	}
	
	public TileEntityConveyorHopper(boolean highSpeed) {
		this.highSpeed = highSpeed;
		inventory = new InventorySimple(5);
	}
	
	@Override
	public List<String> getVisibleParts() {
		if(highSpeed) {
			return parts_hs;
		} else {
			return parts_regular;
		}
	}
	
	@Override
	public void update() {
		if(worldObj.isRemote) {
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
		
		int slotToDecrease = 0;
		int amountToDecrease = 0;
		
		if(eject) {
			if(!TaamUtil.canDropIntoWorld(worldObj, pos.down())) {
				return;
			}
			for(int i = 0; i < this.inventory.getSizeInventory(); i++) {
				if(InventoryUtils.stackSize(this.inventory, i) > 0) {
					ItemStack stack = this.inventory.getStackInSlot(i);
					ItemStack ejectStack;
					
					if(highSpeed && stackMode) {
						// Eject whole stack
						ejectStack = InventoryUtils.copyStack(stack, stack.stackSize);
					} else {
						// Eject ONE item
						ejectStack = InventoryUtils.copyStack(stack, 1);
					}
					
					/*
					 * -----------
					 */
					
					EntityItem item = new EntityItem(worldObj, pos.getX() + 0.5, pos.getY() - 0.3, pos.getZ() + 0.5, ejectStack);
			        item.motionX = 0;
			        item.motionY = 0;
			        item.motionZ = 0;
			        worldObj.spawnEntityInWorld(item);
					
			        slotToDecrease = i;
			        amountToDecrease = ejectStack.stackSize;
					break;
				}
			}
		} else {

			IInventory inventory = InventoryUtils.getInventory(worldObj, pos.down());
			if(inventory == null) {
				return;
			}
			InventoryRange range = new InventoryRange(inventory, EnumFacing.UP.ordinal());
			
			for(int i = 0; i < this.inventory.getSizeInventory(); i++) {
				if(InventoryUtils.stackSize(this.inventory, i) > 0) {
					ItemStack stack = this.inventory.getStackInSlot(i);
					ItemStack ejectStack;
					
					if(highSpeed && stackMode) {
						// Eject whole stack
						ejectStack = InventoryUtils.copyStack(stack, stack.stackSize);
					} else {
						// Eject ONE item
						ejectStack = InventoryUtils.copyStack(stack, 1);
					}
					
					/*
					 * -----------
					 */
					
					int unableToInsert = InventoryUtils.insertItem(range, ejectStack, false);
					
					// If we fit anything, decrease inventory accordingly
					if(unableToInsert < ejectStack.stackSize) {
						slotToDecrease = i;
						amountToDecrease = ejectStack.stackSize - unableToInsert;
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
		
		if(amountToDecrease > 0) {
			InventoryUtils.decrStackSize(this.inventory, slotToDecrease, amountToDecrease);
			if(highSpeed && !(stackMode && Config.pl_hopper_stackmode_normal_speed)) {
				timeout += Config.pl_hopper_highspeed_delay;
			} else {
				timeout += Config.pl_hopper_delay;
			}
			changed = true;
		}

		// Move Items to the front in linear mode
		if(linearMode) {
			for(int i = 0; i < this.inventory.getSizeInventory() - 1; i++) {
				if(this.inventory.getStackInSlot(i) == null) {
					this.inventory.setInventorySlotContents(i, this.inventory.getStackInSlot(i + 1));
					this.inventory.setInventorySlotContents(i + 1, null);
					changed = true;
				}
			}
		}
		if(changed) {
			updateState();
		}
	}
	
	public boolean isCoolingDown() {
		return timeout > 0;
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setTag("items", InventoryUtils.writeItemStacksToTag(inventory.items));
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
		inventory.items = new ItemStack[inventory.getSizeInventory()];
		InventoryUtils.readItemStacksFromTag(inventory.items, tag.getTagList("items", NBT.TAG_COMPOUND));
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

	/*
	 * IInventory implementation
	 */
	
	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return inventory.decrStackSize(slot, amount);
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {
		return inventory.removeStackFromSlot(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
	}

	@Override
	public String getName() {
		if(highSpeed) {
			return "tile.productionline.hopper_hs.name";
		} else {
			return "tile.productionline.hopper.name";
		}
	}
	
	@Override
	public IChatComponent getDisplayName() {
		return new ChatComponentTranslation(getName());
	}
	
	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		// Nothing to do.
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		// Nothing to do.
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return inventory.isItemValidForSlot(slot, stack);
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
	}
	
	/*
	 * IConveyorAwareTE implementation
	 */

	@Override
	public boolean shouldRenderItemsDefault() {
		return false;
	}

	@Override
	public EnumFacing getMovementDirection() {
		return EnumFacing.DOWN;
	}
	
	@Override
	public int insertItemAt(ItemStack item, int slot) {
		// insertItem returns item count unable to insert.
		int inserted = item.stackSize - InventoryUtils.insertItem(inventory, item, false);
		return inserted;
	}
	
	@Override
	public boolean canSlotMove(int slot) {
		return false;
	}
	
	@Override
	public boolean isSlotAvailable(int slot) {
		return true;
	}

	@Override
	public int getMovementProgress(int slot) {
		return 0;
	}

	@Override
	public byte getSpeedsteps() {
		return 1;
	}

	@Override
	public ItemWrapper getSlot(int slot) {
		return ItemWrapper.EMPTY;
	}

	@Override
	public double getInsertMaxY() {
		return 0.9;
	}

	@Override
	public double getInsertMinY() {
		return 0.3;
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
			this.markDirty();
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
			this.markDirty();
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
			this.markDirty();
		}
	}
	
	/*
	 * IHopper implementation
	 */

	@Override
	public double getXPos() {
		return pos.getX();
	}

	@Override
	public double getYPos() {
		return pos.getY();
	}

	@Override
	public double getZPos() {
		return pos.getZ();
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
		this.redstoneMode = mode;
		if(worldObj.isRemote) {
			TPMachineConfiguration config = TPMachineConfiguration.newChangeInteger(new WorldCoord(this), (byte)1, redstoneMode);
			TaamMain.network.sendToServer(config);
		} else {
			this.markDirty();
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
		this.direction = direction;
		updateState();
		blockUpdate();
	}

	public EnumFacing getNextSlot(int slot) {
		return EnumFacing.DOWN;
	}

}
