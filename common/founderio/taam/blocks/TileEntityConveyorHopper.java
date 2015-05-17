package founderio.taam.blocks;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IHopper;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;
import codechicken.lib.inventory.InventoryRange;
import codechicken.lib.inventory.InventorySimple;
import codechicken.lib.inventory.InventoryUtils;
import founderio.taam.Config;
import founderio.taam.TaamMain;
import founderio.taam.conveyors.ConveyorUtil;
import founderio.taam.conveyors.IConveyorAwareTE;
import founderio.taam.conveyors.ItemWrapper;
import founderio.taam.multinet.logistics.WorldCoord;
import founderio.taam.network.TPMachineConfiguration;


public class TileEntityConveyorHopper extends BaseTileEntity implements IConveyorAwareTE, IInventory, IHopper {

	private InventorySimple inventory;
	
	private boolean highSpeed;
	private int timeout;
	private boolean eject;
	private boolean stackMode;
	private boolean linearMode;
	private int redstoneMode;
	
	private boolean pulseWasSent = false;
	
	public TileEntityConveyorHopper() {
		this(false);
	}
	
	public TileEntityConveyorHopper(boolean highSpeed) {
		this.highSpeed = highSpeed;
		inventory = new InventorySimple(5, (highSpeed ? "High Speed " : "") + "Conveyor Hopper");
	}
	
	public boolean isCoolingDown() {
		return timeout > 0;
	}
	
	@Override
	public void updateEntity() {
		if(worldObj.isRemote) {
			return;
		}
		
		/*
		 * Find items laying on the conveyor.
		 */
		
		ConveyorUtil.tryInsertItemsFromWorld(this, worldObj, null, true);
		
		
		
		boolean isShutdown = false;
		
		
		boolean redstoneHigh = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		boolean isPulsing = false;
		
		// Redstone. Other criteria?
		if(redstoneMode == 1 && !redstoneHigh) {
			isShutdown = true;
		} else if(redstoneMode == 2 && redstoneHigh) {
			isShutdown = true;
		} else if(redstoneMode == 3) {
			// Pulse, send Item on high edge.
			isShutdown = !(!pulseWasSent && redstoneHigh);
			isPulsing = true;
		} else if(redstoneMode == 4) {
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
			if(!worldObj.isAirBlock(xCoord, yCoord - 1, zCoord)) {
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
					
					EntityItem item = new EntityItem(worldObj, xCoord + 0.5, yCoord - 0.2, zCoord + 0.5, ejectStack);
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

			IInventory inventory = InventoryUtils.getInventory(worldObj, xCoord, yCoord - 1, zCoord);
			if(inventory == null) {
				return;
			}
			InventoryRange range = new InventoryRange(inventory, ForgeDirection.UP.ordinal());
			
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
					if(unableToInsert < stack.stackSize) {
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
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setTag("items", InventoryUtils.writeItemStacksToTag(inventory.items));
		tag.setBoolean("highSpeed", highSpeed);
		tag.setInteger("timeout", timeout);
		tag.setBoolean("eject", eject);
		tag.setBoolean("linearMode", linearMode);
		tag.setBoolean("stackMode", stackMode);
		tag.setInteger("redstoneMode", redstoneMode);
		
		tag.setBoolean("pulseWasSent", pulseWasSent);
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		InventoryUtils.readItemStacksFromTag(inventory.items, tag.getTagList("items", NBT.TAG_COMPOUND));
		highSpeed = tag.getBoolean("highSpeed");
		timeout = tag.getInteger("timeout");
		eject = tag.getBoolean("eject");
		linearMode = tag.getBoolean("linearMode");
		stackMode = tag.getBoolean("stackMode");
		redstoneMode = tag.getInteger("redstoneMode");

		pulseWasSent = tag.getBoolean("pulseWasSent");
	}

	public boolean isHighSpeed() {
		return highSpeed;
	}

	public void setHighSpeed(boolean highSpeed) {
		this.highSpeed = highSpeed;
	}

	@Override
	public int addItemAt(ItemStack item, double x, double y, double z) {
		x -= xCoord;
		y -= yCoord;
		z -= zCoord;
		if(y < 0.4 || y > 1) {
			return 0;
		}
		if(x > 1.1 || x < -0.1 || z > 1.1 || z < -0.1) {
			return 0;
		}
		// insertItem returns item count unable to insert.
		int inserted = item.stackSize - InventoryUtils.insertItem(inventory, item, false);
		System.out.println("Inserting " + inserted);
		System.out.println("Inve Slot 0: " + inventory.getStackInSlot(0));
		return inserted;
	}

	@Override
	public int addItemAt(ItemWrapper item, double x, double y, double z) {
		return addItemAt(item.itemStack, x, y, z);
	}

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
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
	}

	@Override
	public String getInventoryName() {
		return inventory.getInventoryName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return inventory.hasCustomInventoryName();
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
	public void openInventory() {
		// Nothing to do.
	}

	@Override
	public void closeInventory() {
		// Nothing to do.
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return inventory.isItemValidForSlot(slot, stack);
	}

	@Override
	public double getXPos() {
		return this.xCoord;
	}

	@Override
	public double getYPos() {
		return this.yCoord;
	}

	@Override
	public double getZPos() {
		return this.zCoord;
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

	public int getRedstoneMode() {
		return redstoneMode;
	}
	
	public void setRedstoneMode(int redstoneMode) {
		this.redstoneMode = redstoneMode;
		if(worldObj.isRemote) {
			TPMachineConfiguration config = TPMachineConfiguration.newChangeInteger(new WorldCoord(this), (byte)1, redstoneMode);
			TaamMain.network.sendToServer(config);
		} else {
			this.markDirty();
		}
	}


}
