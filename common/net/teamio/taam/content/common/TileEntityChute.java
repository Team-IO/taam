package net.teamio.taam.content.common;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;
import net.teamio.taam.util.TaamUtil;
import codechicken.lib.inventory.InventoryRange;
import codechicken.lib.inventory.InventoryUtils;

public class TileEntityChute extends BaseTileEntity implements IInventory, ISidedInventory, IFluidHandler, IConveyorAwareTE, IRotatable {

	public boolean isConveyorVersion = false;
	private ForgeDirection direction = ForgeDirection.NORTH;
	
	public TileEntityChute(boolean isConveyorVersion) {
		this.isConveyorVersion = isConveyorVersion;
	}
	
	public TileEntityChute() {
		this(false);
	}
	
	@Override
	public void updateEntity() {
		ConveyorUtil.tryInsertItemsFromWorld(this, worldObj, null, false);
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setBoolean("isConveyorVersion", isConveyorVersion);
		if(isConveyorVersion) {
			tag.setInteger("direction", direction.ordinal());
		}
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		isConveyorVersion = tag.getBoolean("isConveyorVersion");
		if(isConveyorVersion) {
			direction = ForgeDirection.getOrientation(tag.getInteger("direction"));
			if(direction == ForgeDirection.UP || direction == ForgeDirection.DOWN || direction == ForgeDirection.UNKNOWN) {
				direction = ForgeDirection.NORTH;
			}
		}
	}
	
	private TileEntity getTarget() {
		return worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);
	}
	
	private InventoryRange getTargetRange() {
		IInventory inventory = getTargetInventory();
		if(inventory == null) {
			return null;
		} else {
			return new InventoryRange(inventory, ForgeDirection.UP.ordinal());
		}
	}
	
	private IInventory getTargetInventory() {
		return InventoryUtils.getInventory(worldObj, xCoord, yCoord - 1, zCoord);
	}
	
	private IFluidHandler getTargetFluidHandler() {
		TileEntity target = getTarget();
		if(target instanceof IFluidHandler) {
			return (IFluidHandler) target;
		} else {
			return null;
		}
	}
	
	private boolean canDrop() {
		return TaamUtil.canDropIntoWorld(worldObj, xCoord, yCoord - 1, zCoord);
	}

	/*
	 * IInventory implementation
	 */
	
	@Override
	public int getSizeInventory() {
		InventoryRange target = getTargetRange();
		if(target == null) {
			if(canDrop()) {
				return 1;
			} else {
				return 0;
			}
		} else {
			return target.slots.length;
		}
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		/*InventoryRange target = getTargetRange();
		if(target == null) {
			return null;
		} else {
			return InventoryUtils.getExtractableStack(target, target.slots[slot]);
		}*/
		return null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		InventoryRange target = getTargetRange();
		if(target == null) {
			if(!worldObj.isRemote && canDrop()) {
				EntityItem item = new EntityItem(worldObj, xCoord + 0.5, yCoord - 0.3, zCoord + 0.5, stack);
		        item.motionX = 0;
		        item.motionY = 0;
		        item.motionZ = 0;
		        worldObj.spawnEntityInWorld(item);
			}
		} else {
			target.inv.setInventorySlotContents(target.slots[slot], stack);
		}
	}

	@Override
	public String getInventoryName() {
		IInventory target = getTargetInventory();
		if(target == null) {
			return "tile.taam.chute.name";
		} else {
			return target.getInventoryName();
		}
	}

	@Override
	public boolean hasCustomInventoryName() {
		IInventory target = getTargetInventory();
		if(target == null) {
			return true;
		} else {
			return target.hasCustomInventoryName();
		}
	}

	@Override
	public int getInventoryStackLimit() {
		IInventory target = getTargetInventory();
		if(target == null) {
			return 64;
		} else {
			return target.getInventoryStackLimit();
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		IInventory target = getTargetInventory();
		if(target == null) {
			return false;
		} else {
			return target.isUseableByPlayer(player);
		}
	}

	@Override
	public void openInventory() {
		IInventory target = getTargetInventory();
		if(target != null) {
			target.openInventory();
		}
	}

	@Override
	public void closeInventory() {
		IInventory target = getTargetInventory();
		if(target != null) {
			target.closeInventory();
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		InventoryRange target = getTargetRange();
		if(target == null) {
			return canDrop();
		} else {
			return target.canInsertItem(target.slots[slot], stack);
		}
	}

	/*
	 * ISidedInventory implementation
	 */
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if(side != ForgeDirection.UP.ordinal()) {
			return new int[0];
		}
		InventoryRange target = getTargetRange();
		if(target == null) {
			if(canDrop()) {
				return new int[] {0};
			} else {
				return new int[0];
			}
		} else {
			// We reorder the slots from 0 onwards, then convert back later.
			int[] slots = new int[target.slots.length];
			for(int i = 0; i < slots.length; i++) {
				slots[i] = i;
			}
			return slots;
		}
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack,
			int side) {
		if(side != ForgeDirection.UP.ordinal()) {
			return false;
		}
		InventoryRange target = getTargetRange();
		if(target == null) {
			return canDrop();
		} else {
			return target.canInsertItem(target.slots[slot], stack);
		}
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack,
			int side) {
		/*if(side != ForgeDirection.UP.ordinal()) {
			return false;
		}
		ISidedInventory target = getTargetSidedInventory();
		if(target == null) {
			IInventory invTarget = getTargetInventory();
			if(invTarget != null) {
				return invTarget.isItemValidForSlot(slot, stack);
			}
		} else {
			return target.canExtractItem(slot, stack, side);
		}*/
		return false;
	}

	/*
	 * IFluidHandler implementation
	 */
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if(from != ForgeDirection.UP) {
			return 0;
		}
		IFluidHandler target = getTargetFluidHandler();
		if(target != null ) {
			return target.fill(from, resource, doFill);
		} else {
			return 0;
		}
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		if(from != ForgeDirection.UP) {
			return false;
		}
		IFluidHandler target = getTargetFluidHandler();
		if(target != null ) {
			return target.canFill(from, fluid);
		} else {
			return false;
		}
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		if(from != ForgeDirection.UP) {
			return new FluidTankInfo[0];
		}
		IFluidHandler target = getTargetFluidHandler();
		if(target != null ) {
			return target.getTankInfo(from);
		} else {
			return new FluidTankInfo[0];
		}
	}



	/*
	 * IConveyorAwareTE implementation
	 */
	
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
		return 0;
	}

	@Override
	public ItemWrapper getSlot(int slot) {
		return ItemWrapper.EMPTY;
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
	public int insertItemAt(ItemStack stack, int slot) {
		InventoryRange target = getTargetRange();
		if(target == null) {
			if(!worldObj.isRemote && canDrop()) {
				EntityItem item = new EntityItem(worldObj, xCoord + 0.5, yCoord - 0.3, zCoord + 0.5, stack);
		        item.motionX = 0;
		        item.motionY = 0;
		        item.motionZ = 0;
		        worldObj.spawnEntityInWorld(item);
		        return stack.stackSize;
			}
			return 0;
		} else {
			return stack.stackSize - InventoryUtils.insertItem(target, stack, false);
		}
	}

	@Override
	public ForgeDirection getMovementDirection() {
		return ForgeDirection.DOWN;
	}

	@Override
	public boolean shouldRenderItemsDefault() {
		return false;
	}

	@Override
	public double getInsertMaxY() {
		if(isConveyorVersion) {
			return 0.9;
		} else {
			return 1.3;
		}
	}

	@Override
	public double getInsertMinY() {
		if(isConveyorVersion) {
			return 0.3;
		} else {
			return 0.9;
		}
	}
	/*
	 * IRotatable Implementation
	 */
	
	@Override
	public ForgeDirection getFacingDirection() {
		return direction;
	}

	@Override
	public ForgeDirection getMountDirection() {
		return ForgeDirection.DOWN;
	}

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
		if(isConveyorVersion) {
			this.direction = direction;
			if(direction == ForgeDirection.UP || direction == ForgeDirection.DOWN || direction == ForgeDirection.UNKNOWN) {
				this.direction = ForgeDirection.NORTH;
			}
			updateState();
		}
	}

	@Override
	public void setMountDirection(ForgeDirection direction) {
	}

	public ForgeDirection getNextSlot(int slot) {
		return null;
	}

}
