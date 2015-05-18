package founderio.taam.blocks;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.vec.Vector3;
import founderio.taam.conveyors.ApplianceRegistry;
import founderio.taam.conveyors.ConveyorUtil;
import founderio.taam.conveyors.IConveyorAppliance;
import founderio.taam.conveyors.IConveyorApplianceFactory;
import founderio.taam.conveyors.IRotatable;
import founderio.taam.conveyors.ItemWrapper;
import founderio.taam.conveyors.api.IConveyorApplianceHost;
import founderio.taam.conveyors.api.IConveyorAwareTE;
import founderio.taam.conveyors.api.IItemFilter;

public class TileEntityConveyor extends BaseTileEntity implements ISidedInventory, IFluidHandler, IConveyorAwareTE, IRotatable, IConveyorApplianceHost {

	public static final int maxProgress = 130;
	
	/*
	 * Content
	 */
	private ItemWrapper[] items;
	private int movementProgress;
	
	/*
	 * Conveyor State
	 */
	private ForgeDirection direction = ForgeDirection.NORTH;
	
	private boolean isEnd = false;
	private boolean isBegin = false;
	//TODO: More State to fill Gaps between conveyors
	
	/*
	 * Appliance state
	 */
	private String applianceType;
	private IConveyorAppliance appliance;


	public TileEntityConveyor() {
		items = new ItemWrapper[9];
		for(int i = 0; i < items.length; i++) {
			items[i] = new ItemWrapper(null);
		}
	}
	
	public boolean isBegin() {
		return isBegin;
	}
	
	public boolean isEnd() {
		return isEnd;
	}
	
	@Override
	public void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();
		if(worldObj != null) {
			//TODO: Refine this (can we update that in a more reliable manner?)
			TileEntity te = worldObj.getTileEntity(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
			isEnd = !(te instanceof TileEntityConveyor) || ((TileEntityConveyor)te).getFacingDirection() != direction;
			ForgeDirection inverse = direction.getOpposite();
			te = worldObj.getTileEntity(xCoord + inverse.offsetX, yCoord + inverse.offsetY, zCoord + inverse.offsetZ);
			isBegin = !(te instanceof TileEntityConveyor) || ((TileEntityConveyor)te).getFacingDirection() != direction;
		}
	}
	



	public ItemWrapper[] getItems() {
		return items;
	}


	@Override
	public int getMaxMovementProgress() {
		return maxProgress;
	}
	
	public void dropItem(int slot) {
		
		
		ItemWrapper slotObject = items[slot];
		System.out.println("Dropping slot " + slot + " >>" + slotObject.itemStack);
		
		if(!worldObj.isRemote) {
			double posX = xCoord + ConveyorUtil.getItemPositionX(slot, (float)movementProgress / maxProgress, direction);
			double posY = yCoord + 0.4f;
			double posZ = zCoord + ConveyorUtil.getItemPositionZ(slot, (float)movementProgress / maxProgress, direction);
			
			if(slotObject.itemStack != null) {
				EntityItem item = new EntityItem(worldObj, posX, posY, posZ, slotObject.itemStack);
				item.motionX = 0; 
		        item.motionY = 0; 
		        item.motionZ = 0; 
				worldObj.spawnEntityInWorld(item);
			}
		}
		
		slotObject.itemStack = null;
	}
	
	private boolean transferSlot(int slot, IConveyorAwareTE nextBlock, int nextSlot) {
		
		System.out.println("Transfer external " + slot + " to " + nextSlot);
		
		ItemWrapper slotObject = items[slot];
		
		//TODO: we would need to tell the other block the processing state....
		
		int transferred = nextBlock.insertItemAt(slotObject.itemStack.copy(), nextSlot);
		if(transferred > 0) {
			slotObject.itemStack.stackSize -= transferred;
			if(slotObject.itemStack.stackSize <= 0) {
				slotObject.itemStack = null;

				// Reset processing state, so next item starts "fresh"
				slotObject.processing = 0;
				
				// Stack moved completely
				return true;
			}
		}
		return false;
	}
	

	private boolean transferSlot(int slot, int nextSlot) {
		System.out.println("Transfer internal " + slot + " to " + nextSlot);
		
		ItemWrapper slotObject = items[slot];
		ItemWrapper nextSlotObject = items[nextSlot];
		if(nextSlotObject.itemStack == null) {
			nextSlotObject.itemStack = slotObject.itemStack;
			
			slotObject.itemStack = null;

			// Reset processing state, so next item starts "fresh"
			slotObject.processing = 0;
			
			// Stack moved completely
			return true;
		}
		return false;
	}
	
	
	@Override
	public void updateEntity() {

		/*
		 * Find items laying on the conveyor.
		 */

		ConveyorUtil.tryInsertItemsFromWorld(this, worldObj, null, false);
		
		/*
		 * Move items already on the conveyor
		 */
		
		boolean changed = false;
		
		// Movement blocked by at lease one slot or appliance
		boolean blockMovement = false;
		
		//process from movement direction backward to keep slot order inside one conveyor,
		// as we depend on the status of the next slot
		int[] slotOrder = ConveyorUtil.getSlotOrderForDirection(direction);
		for(int index = 0; index < slotOrder.length; index++) {
		
			int slot = slotOrder[index];
			
			ItemWrapper wrapper = items[slot];
			
			if(wrapper.itemStack == null) {
				continue;
			}
			
			if(appliance != null) {
				int min = appliance.getProgressBegin();
				int max = appliance.getProgressEnd();
				if(wrapper.processing > -1) {
//					if(wrapper.progress >= min && wrapper.progress < max) {
						System.out.println("Process");
						wrapper.processing++;
						appliance.processItem(this, wrapper);
//					}
						//TODO: allow appliance to block slots
				}
			}
			
			// Only unlock slots when starting a new cycle (otherwise the item would jump forward!)
			if(wrapper.blocked && movementProgress != 0) {
				continue;
			}
			
			// No next slot means drop to ground..
			boolean noNextSlot = false;
			boolean slotWrapped = false;
			boolean nextSlotFree = false;
			boolean nextSlotMovable = false;
			
			IConveyorAwareTE nextBlock = null;
			
			int nextSlot = ConveyorUtil.getNextSlotUnwrapped(slot, direction);
			
			if(nextSlot < 0) {
				nextSlot += 9;
				slotWrapped = true;
			} else if(nextSlot > 8) {
				nextSlot -= 9;
				slotWrapped = true;
			}
			
			// Slot wrapped to next block
			if(slotWrapped) {
				// Next block, potentially a conveyor-aware block.
				int nextBlockX = xCoord + direction.offsetX;
				int nextBlockY = yCoord + direction.offsetY;
				int nextBlockZ = zCoord + direction.offsetZ;
				
				TileEntity te = worldObj.getTileEntity(nextBlockX, nextBlockY, nextBlockZ);
				
				if(te instanceof IConveyorAwareTE) {
					nextBlock = (IConveyorAwareTE) worldObj.getTileEntity(nextBlockX, nextBlockY, nextBlockZ);
					
					nextSlotFree = nextBlock.getItemAt(nextSlot) == null;
					nextSlotMovable = nextSlotFree || (nextBlock.canSlotMove(nextSlot) && nextBlock.getMovementDirection() == direction);
				} else {
					// Drop it
					noNextSlot = true;
				}
			} else {
				nextSlotFree = items[nextSlot].itemStack == null;
				nextSlotMovable = !items[nextSlot].blocked;
			}
			
			nextSlotFree = noNextSlot || nextSlotFree;
			
			// check next slot.
			if(nextSlotFree || nextSlotMovable) {
				wrapper.blocked = false;
				
				if(movementProgress == maxProgress) {
					if(nextSlotFree) {
						if(noNextSlot) {
							dropItem(slot);
						} else {
							boolean completeTransfer;
							if(nextBlock == null) {
								completeTransfer = transferSlot(slot, nextSlot);
							} else {
								completeTransfer = transferSlot(slot, nextBlock, nextSlot);
							}
							if(!completeTransfer) {
								// We still have some items pending here..
								blockMovement = true;
							}
						}
					} else {
						blockMovement = true;
					}
				}
			} else {
				wrapper.blocked = true;
			}
			
		}
		if(!blockMovement) {
			movementProgress++;
			if(movementProgress > maxProgress) {
				movementProgress = 0;
			}
		}

		// Content changed, send Network update.
		if(changed && !worldObj.isRemote) {
			//TODO: Do not update the items all the time. (Update differently)
			updateState();
		}
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setInteger("direction", direction.ordinal());
		tag.setInteger("movementProgress", movementProgress);
		if(applianceType != null) {
			tag.setString("applianceType", applianceType);
		}
		if(appliance != null) {
			NBTTagCompound applianceData = new NBTTagCompound();
			appliance.writeToNBT(applianceData);
			tag.setTag("appliance", applianceData);
		}
		NBTTagList itemsTag = new NBTTagList();
		for(int i = 0; i < items.length; i++) {
			itemsTag.appendTag(items[i].writeToNBT());
		}
		tag.setTag("items", itemsTag);
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		direction = ForgeDirection.getOrientation(tag.getInteger("direction"));
		movementProgress = tag.getInteger("movementProgress");
		NBTTagList itemsTag = tag.getTagList("items", NBT.TAG_COMPOUND);
		if(itemsTag != null) {
			int count = Math.min(itemsTag.func_150303_d(), items.length);
			for(int i = 0; i < count; i++) {
				items[i] = ItemWrapper.readFromNBT(itemsTag.getCompoundTagAt(i));
			}
		}
		String newApplianceType = tag.getString("applianceType");
		setAppliance(newApplianceType);
		if(appliance != null) {
			NBTTagCompound applianceData = tag.getCompoundTag("appliance");
			if(applianceData != null) {
				appliance.readFromNBT(applianceData);
			}
		}
	}
	

	public void dropItems() {
		for (int index = 0; index < items.length; index++) {
			dropItem(index);
		}
	}
	
	/*
	 * IConveyorAwareTE implementation
	 */
	
	@Override
	public int insertItemAt(ItemStack item, int slot) {
		ItemWrapper slotObject = items[slot];
		if(slotObject.itemStack == null) {
			slotObject.itemStack = item.copy();
			slotObject.blocked = true;
			updateState();
			return slotObject.itemStack.stackSize;
		} else if(slotObject.itemStack.isItemEqual(item)) {
			int availableSpace = slotObject.itemStack.getMaxStackSize() - slotObject.itemStack.stackSize;
			if(availableSpace > 0) {
				availableSpace = Math.min(availableSpace, item.stackSize);
				slotObject.itemStack.stackSize += availableSpace;
				updateState();
				return availableSpace;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	@Override
	public int getMovementProgress() {
		return movementProgress;
	}
	
	@Override
	public boolean canSlotMove(int slot) {
		ItemWrapper slotObject = items[slot];
		return !slotObject.blocked;
	};
	
	@Override
	public ItemStack getItemAt(int slot) {
		ItemWrapper slotObject = items[slot];
		return slotObject.itemStack;
	};
	
	@Override
	public IItemFilter getSlotFilter(int slot) {
		return null;
	}
	
	@Override
	public int posX() {
		return xCoord;
	}
	
	@Override
	public int posY() {
		return yCoord;
	}
	
	@Override
	public int posZ() {
		return zCoord;
	}

	@Override
	public ForgeDirection getMovementDirection() {
		return direction;
	}
	
	/*
	 * IRotatable implementation
	 */
	
	@Override
	public ForgeDirection getNextFacingDirection() {
		return direction.getRotation(ForgeDirection.UP);
	}

	@Override
	public ForgeDirection getNextMountDirection() {
		return ForgeDirection.DOWN;
	}

	@Override
	public void setFacingDirection(ForgeDirection direction) {
		this.direction = direction;
		updateState();
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, blockType);
	}

	@Override
	public void setMountDirection(ForgeDirection direction) {
		return;
	}

	@Override
	public ForgeDirection getFacingDirection() {
		return direction;
	}

	@Override
	public ForgeDirection getMountDirection() {
		return ForgeDirection.DOWN;
	}
	
	/*
	 * IConveyorApplianceHost implementation
	 */
	
	@Override
	public boolean initAppliance(String name) {
		if(hasAppliance()) {
			return false;
		}
		boolean result = setAppliance(name);
		updateState();
		updateContainingBlockInfo();
		return result;
	}
	
	private boolean setAppliance(String type) {
		if(type == null) {
			appliance = null;
			applianceType = null;
			return true;
		}
		if(type.equals(applianceType)) {
			return false;
		}
		IConveyorApplianceFactory factory = ApplianceRegistry.getFactory(type);
		if(factory == null) {
			appliance = null;
			applianceType = null;
		} else {
			appliance = factory.setUpApplianceInventory(type, this);
			applianceType = type;
		}
		return true;
	}
	
	@Override
	public boolean hasAppliance() {
		return appliance != null;
	}

	@Override
	public boolean hasApplianceWithType(String type) {
		return hasAppliance() && applianceType.equals(type);
	}

	@Override
	public String getApplianceType() {
		return applianceType;
	}

	@Override
	public IConveyorAppliance getAppliance() {
		return appliance;
	}

	@Override
	public boolean removeAppliance() {
		boolean hadAppliance = hasAppliance();
		appliance = null;
		applianceType = null;
		updateState();
		return hadAppliance;
	}

	/*
	 * IInventory implementation
	 */
	
	@Override
	public int getSizeInventory() {
		if(appliance == null) {
			return 9;
		} else {
			return appliance.getSizeInventory();
		}
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if(appliance == null) {
			return getItemAt(slot);
		} else {
			return appliance.getStackInSlot(slot);
		}
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if(appliance == null) {
			return null;
		} else {
			return appliance.decrStackSize(slot, amount);
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if(appliance == null) {
			return null;
		} else {
			return appliance.getStackInSlotOnClosing(slot);
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		if(appliance == null) {
			if(itemStack != null)
				insertItemAt(itemStack, slot);
		} else {
			appliance.setInventorySlotContents(slot, itemStack);
		}
	}

	@Override
	public String getInventoryName() {
		if(appliance == null) {
			return "tile.taam.productionline.conveyor.name";
		} else {
			return appliance.getInventoryName();
		}
	}

	@Override
	public boolean hasCustomInventoryName() {
		if(appliance == null) {
			return false;
		} else {
			return appliance.hasCustomInventoryName();
		}
	}

	@Override
	public int getInventoryStackLimit() {
		if(appliance == null) {
			return 64;
		} else {
			return appliance.getInventoryStackLimit();
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if(appliance == null) {
			return true;
		} else {
			return appliance.isUseableByPlayer(player);
		}
	}

	@Override
	public void openInventory() {
		if(appliance != null) {
			appliance.openInventory();
		}
	}

	@Override
	public void closeInventory() {
		if(appliance != null) {
			appliance.closeInventory();
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		if(appliance == null) {
			return true;
		} else {
			return appliance.isItemValidForSlot(slot, itemStack);
		}
	}
	
	/*
	 * ISidedInventory implementation
	 */
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if(appliance == null) {
			//TODO: make the respective slots available!
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			if(dir == ForgeDirection.UP) {
				return new int[] { 0 };
			} else {
				return new int[0];
			}
		} else {
			return appliance.getAccessibleSlotsFromSide(side);
		}
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
		if(appliance == null) {
			//TODO: same check as in insertItemAt()
			return true;
		} else {
			return appliance.canInsertItem(slot, itemStack, side);
		}
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemStack, int side) {
		if(appliance == null) {
			return false;
		} else {
			return appliance.canExtractItem(slot, itemStack, side);
		}
	}

	/*
	 * IFluidHandler implementation
	 */
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if(appliance == null) {
			return 0;
		} else {
			return appliance.fill(from, resource, doFill);
		}
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
		if(appliance == null) {
			return null;
		} else {
			return appliance.drain(from, resource, doDrain);
		}
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if(appliance == null) {
			return null;
		} else {
			return appliance.drain(from, maxDrain, doDrain);
		}
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		if(appliance == null) {
			return false;
		} else {
			return appliance.canFill(from, fluid);
		}
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		if(appliance == null) {
			return false;
		} else {
			return appliance.canDrain(from, fluid);
		}
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		if(appliance == null) {
			return new FluidTankInfo[0];
		} else {
			return appliance.getTankInfo(from);
		}
	}
}
