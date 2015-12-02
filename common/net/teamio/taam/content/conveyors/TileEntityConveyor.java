package net.teamio.taam.content.conveyors;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.teamio.taam.Config;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.conveyors.ApplianceRegistry;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.IConveyorAppliance;
import net.teamio.taam.conveyors.IConveyorApplianceFactory;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;
import net.teamio.taam.conveyors.api.IItemFilter;

public class TileEntityConveyor extends BaseTileEntity implements ISidedInventory, IFluidHandler, IConveyorAwareTE, IRotatable, IConveyorApplianceHost, IWorldInteractable {

	/*
	 * Content
	 */
	private ItemWrapper[] items;
	
	/*
	 * Conveyor State
	 */
	private ForgeDirection direction = ForgeDirection.NORTH;
	private int speedLevel = 0;
	
	private boolean isEnd = false;
	private boolean isBegin = false;
	public boolean renderEnd = false;
	public boolean renderBegin = false;
	public boolean renderRight = false;
	public boolean renderLeft = false;
	public boolean renderAbove = false;
	
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
	
	public TileEntityConveyor(int speedLevel) {
		this();
		this.speedLevel = speedLevel;
	}
	
	public boolean isBegin() {
		return isBegin;
	}
	
	public boolean isEnd() {
		return isEnd;
	}
	
	@Override
	public byte getSpeedsteps() {
		return Config.pl_conveyor_speedsteps[speedLevel];
	}

	public int getSpeedLevel() {
		return speedLevel;
	}
	
	@Override
	public void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();

		if(worldObj != null) {
			// Check in front
			TileEntity te = worldObj.getTileEntity(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
			if(te instanceof TileEntityConveyor) {
				TileEntityConveyor next = (TileEntityConveyor)te;
				renderEnd = next.speedLevel != speedLevel;
				renderEnd = renderEnd || next.getFacingDirection() != direction;
				isEnd = renderEnd;
			} else {
				isEnd = true;
				renderEnd = te instanceof IConveyorAwareTE;
			}
			
			// Check behind
			ForgeDirection inverse = direction.getOpposite();
			te = worldObj.getTileEntity(xCoord + inverse.offsetX, yCoord + inverse.offsetY, zCoord + inverse.offsetZ);
			if(te instanceof TileEntityConveyor) {
				TileEntityConveyor next = (TileEntityConveyor)te;
				renderBegin = next.speedLevel != speedLevel;
				renderBegin = renderBegin || next.getFacingDirection() != direction;
				isBegin = renderBegin;
			} else {
				isBegin = true;
				renderBegin = te instanceof IConveyorAwareTE;
			}
			
			// Check right
			inverse = direction.getRotation(ForgeDirection.UP);
			te = worldObj.getTileEntity(xCoord + inverse.offsetX, yCoord + inverse.offsetY, zCoord + inverse.offsetZ);
			
			if(te instanceof TileEntityConveyor) {
				TileEntityConveyor next = (TileEntityConveyor)te;
				ForgeDirection nextFacing = next.getFacingDirection();
				renderRight = nextFacing != direction && nextFacing != direction.getOpposite();
			} else {
				renderRight = te instanceof IConveyorAwareTE;
			}
			
			// Check left
			inverse = direction.getRotation(ForgeDirection.DOWN);
			te = worldObj.getTileEntity(xCoord + inverse.offsetX, yCoord + inverse.offsetY, zCoord + inverse.offsetZ);
			
			if(te instanceof TileEntityConveyor) {
				TileEntityConveyor next = (TileEntityConveyor)te;
				ForgeDirection nextFacing = next.getFacingDirection();
				renderLeft = nextFacing != direction && nextFacing != direction.getOpposite();
			} else {
				renderLeft = te instanceof IConveyorAwareTE;
			}
			
			// Check above
			renderAbove = worldObj.isSideSolid(xCoord, yCoord + 1, zCoord, ForgeDirection.DOWN) ||
					worldObj.getTileEntity(xCoord, yCoord + 1, zCoord) instanceof IConveyorAwareTE;
		}
	}

	/**
	 * Get all items. Danger! Returns the array directly!
	 * @return
	 */
	public ItemWrapper[] getItems() {
		return items;
	}

	/**
	 * Drops all contained items, exactly where they are rendered now.
	 */
	public void dropItems() {
		for (int index = 0; index < items.length; index++) {
			dropItem(index);
		}
	}
	
	/**
	 * Drops the item in the passed slot, exactly where it is rendered now.
	 * @param slot The slot to be dropped.
	 */
	public void dropItem(int slot) {
		ItemWrapper slotObject = items[slot];
		// System.out.println("Dropping slot " + slot + " >>" + slotObject.itemStack);
		
		if(!worldObj.isRemote) {
			float speedsteps = getSpeedsteps();
			
			double posX = xCoord + ConveyorUtil.getItemPositionX(slot, slotObject.movementProgress / speedsteps, direction);
			double posY = yCoord + 0.4f;
			double posZ = zCoord + ConveyorUtil.getItemPositionZ(slot, slotObject.movementProgress / speedsteps, direction);
			
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
		// System.out.println("Transfer external " + slot + " to " + nextSlot);
		
		ItemWrapper slotObject = items[slot];
		
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
		// Stack not moved at all, or has backlog
		return false;
	}

	private boolean transferSlot(int slot, int nextSlot) {
		// System.out.println("Transfer internal " + slot + " to " + nextSlot);
		
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

		if(ConveyorUtil.tryInsertItemsFromWorld(this, worldObj, null, false)) {
			updateState();
		}

		/*
		 * Move items already on the conveyor
		 */
		
		
		// process from movement direction backward to keep slot order inside one conveyor,
		// as we depend on the status of the next slot
		int[] slotOrder = ConveyorUtil.getSlotOrderForDirection(direction);
		for(int index = 0; index < slotOrder.length; index++) {
		
			int slot = slotOrder[index];
			
			ItemWrapper wrapper = items[slot];
			
			if(wrapper.itemStack == null) {
				continue;
			}
			
			if(appliance != null) {
				// System.out.println("Process");
				appliance.processItem(this, slot, wrapper);
			}
			
			boolean slotWrapped = false;
			boolean nextSlotFree = false;
			boolean nextSlotMovable = false;
			int nextSlotProgress = 0;
			boolean wrappedIsSameDirection = true;
			
			IConveyorAwareTE nextBlock = null;
			
			int nextSlot;
			if(speedLevel >= 2) {
				nextSlot = ConveyorUtil.getNextSlotUnwrappedHighspeed(slot, direction);
			} else {
				nextSlot = ConveyorUtil.getNextSlotUnwrapped(slot, direction);
			}
			
			
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
					nextBlock = (IConveyorAwareTE) te;
					
					nextSlotFree = nextBlock.getItemAt(nextSlot) == null;
					wrappedIsSameDirection = nextBlock.getMovementDirection() == direction;
					nextSlotMovable = nextBlock.canSlotMove(nextSlot) && wrappedIsSameDirection;
					nextSlotProgress = nextBlock.getMovementProgress(nextSlot);
					byte nextSpeedSteps = nextBlock.getSpeedsteps();
					if(nextSpeedSteps != getSpeedsteps()) {
						if(nextSpeedSteps == 0) {
							nextSlotProgress = 0;
						} else {
							nextSlotProgress = Math.round((nextSlotProgress / (float)nextSpeedSteps) * getSpeedsteps());
						}
						System.out.println(nextSlotProgress);
					}
					
				} else {
					// Drop it
					nextSlotFree = true;
					nextSlotMovable = true;
				}
			} else {
				nextSlotFree = items[nextSlot].itemStack == null;
				nextSlotMovable = !items[nextSlot].isBlocked();
				nextSlotProgress = items[nextSlot].movementProgress;
			}
			
			// check next slot.
			if(!wrapper.isBlocked() && (nextSlotFree || nextSlotMovable)) {
				if(wrapper.movementProgress == getSpeedsteps() && nextSlotFree) {
					if(slotWrapped && (nextBlock == null || !nextBlock.isSlotAvailable(nextSlot))) {
						// No next block, drop it.
						dropItem(slot);
					} else {
						boolean completeTransfer;
						if(slotWrapped) {
							completeTransfer = transferSlot(slot, nextBlock, nextSlot);
						} else {
							completeTransfer = transferSlot(slot, nextSlot);
						}
						if(!completeTransfer) {
							// We still have some items pending here..
							nextSlotFree = false;
							nextSlotMovable = false;
						}
					}
				}
			}
			if(nextSlotFree || (nextSlotMovable && wrappedIsSameDirection && wrapper.movementProgress < nextSlotProgress)) {
				wrapper.movementProgress++;
				if(wrapper.movementProgress > getSpeedsteps()) {
					wrapper.movementProgress = 0;//(byte) getSpeedsteps();
				}
			}
		}
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setInteger("direction", direction.ordinal());
		tag.setInteger("speedLevel", speedLevel);
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
		speedLevel = tag.getInteger("speedLevel");
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
	
	/*
	 * IConveyorAwareTE implementation
	 */

	@Override
	public boolean shouldRenderItemsDefault() {
		return true;
	}
	
	@Override
	public int insertItemAt(ItemStack item, int slot) {
		return insertItemAt(item, slot, false);
	}
	
	private int insertItemAt(ItemStack item, int slot, boolean simulate) {
		ItemWrapper slotObject = items[slot];
		if(slotObject.itemStack == null) {
			if(!simulate) {
				slotObject.itemStack = item.copy();
				slotObject.unblock();
				slotObject.resetMovement();
				updateState();
			}
			return item.stackSize;
		} else if(slotObject.itemStack.isItemEqual(item)) {
			int availableSpace = slotObject.itemStack.getMaxStackSize() - slotObject.itemStack.stackSize;
			if(availableSpace > 0) {
				availableSpace = Math.min(availableSpace, item.stackSize);
				if(!simulate) {
					slotObject.itemStack.stackSize += availableSpace;
					updateState();
				}
				return availableSpace;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	@Override
	public boolean canSlotMove(int slot) {
		ItemWrapper slotObject = items[slot];
		return !slotObject.isBlocked();
	};
	
	@Override
	public boolean isSlotAvailable(int slot) {
		return true;
	}
	
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

	@Override
	public int getMovementProgress(int slot) {
		ItemWrapper slotObject = items[slot];
		return slotObject.movementProgress;
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
		if(blockType != null) {
			blockType.onNeighborBlockChange(worldObj, xCoord, yCoord, zCoord, blockType);
		}
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
	public boolean canAcceptAppliance(String type) {
		// Only "regular" conveyors can accept appliances
		return speedLevel == 1;
	}
	
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
		if(!canAcceptAppliance(type)) {
			return false;
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
			if(itemStack == null) {
				items[slot].itemStack = null;
				updateState();
			} else {
				insertItemAt(itemStack, slot);
			}
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
			final ForgeDirection dir = ForgeDirection.getOrientation(side);
			final int slot = ConveyorUtil.getSlot(dir);
			if(slot == -1) {
				return new int[0];
			} else {
				return new int[] { slot };
			}
		} else {
			return appliance.getAccessibleSlotsFromSide(side);
		}
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
		if(appliance == null) {
			return insertItemAt(itemStack, slot, true) > 0;
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

	/*
	 * IWorldInteractable implementation
	 */
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, boolean playerHasWrench, int side, float hitX, float hitY, float hitZ) {
		if(side != ForgeDirection.UP.ordinal()) {
			return false;
		}
		int clickedSlot = ConveyorUtil.getSlotForRelativeCoordinates(hitX, hitZ);
		int playerSlot = player.inventory.currentItem;
		ItemStack playerStack = player.inventory.getCurrentItem();
		if(playerStack == null) {
			// Take from Conveyor
			ItemStack taken = getItemAt(clickedSlot);
			if(taken != null) {
				player.inventory.setInventorySlotContents(playerSlot, taken);
				setInventorySlotContents(clickedSlot, null);
			}
		} else {
			// Put on conveyor
			int inserted = insertItemAt(playerStack, clickedSlot);
			if(inserted == playerStack.stackSize) {
				player.inventory.setInventorySlotContents(playerSlot, null);
			} else {
				playerStack.stackSize -= inserted;
				player.inventory.setInventorySlotContents(playerSlot, playerStack);
			}
		}
		return true;
	}
}
