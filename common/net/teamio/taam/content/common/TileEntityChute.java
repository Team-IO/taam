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
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.util.TaamUtil;
import codechicken.lib.inventory.InventoryUtils;

public class TileEntityChute extends BaseTileEntity implements IInventory, ISidedInventory, IFluidHandler {

	@Override
	public void updateEntity() {
		ConveyorUtil.tryInsertItemsFromWorld(this, worldObj, null, false);
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
	}
	
	private TileEntity getTarget() {
		return worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);
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
	
	private ISidedInventory getTargetSidedInventory() {
		IInventory target = getTargetInventory();
		if(target instanceof ISidedInventory) {
			return (ISidedInventory) target;
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
		IInventory target = getTargetInventory();
		if(target == null) {
			if(canDrop()) {
				return 1;
			} else {
				return 0;
			}
		} else {
			return target.getSizeInventory();
		}
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		IInventory target = getTargetInventory();
		if(target == null) {
			return null;
		} else {
			return target.getStackInSlot(slot);
		}
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		IInventory target = getTargetInventory();
		if(target == null) {
			return null;
		} else {
			return target.decrStackSize(slot, amount);
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		IInventory target = getTargetInventory();
		if(target == null) {
			return null;
		} else {
			return target.getStackInSlotOnClosing(slot);
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		IInventory target = getTargetInventory();
		if(target == null) {
			if(!worldObj.isRemote && canDrop()) {
				EntityItem item = new EntityItem(worldObj, xCoord + 0.5, yCoord - 0.3, zCoord + 0.5, stack);
		        item.motionX = 0;
		        item.motionY = 0;
		        item.motionZ = 0;
		        worldObj.spawnEntityInWorld(item);
			}
		} else {
			target.setInventorySlotContents(slot, stack);
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
		IInventory target = getTargetInventory();
		if(target == null) {
			return canDrop();
		} else {
			return target.isItemValidForSlot(slot, stack);
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
		ISidedInventory target = getTargetSidedInventory();
		if(target == null) {
			IInventory invTarget = getTargetInventory();
			if(invTarget == null) {
				if(canDrop()) {
					return new int[] {0};
				}
			} else {
				int slots = invTarget.getSizeInventory();
				int[] accessible = new int[slots];
				for(int s = 0; s < slots; s++) {
					accessible[s] = s;
				}
				return accessible;
			}
		} else {
			return target.getAccessibleSlotsFromSide(side);
		}
		return new int[0];
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack,
			int side) {
		if(side != ForgeDirection.UP.ordinal()) {
			return false;
		}
		ISidedInventory target = getTargetSidedInventory();
		if(target == null) {
			IInventory invTarget = getTargetInventory();
			if(invTarget == null) {
				if(canDrop()) {
					return true;
				}
			} else {
				return invTarget.isItemValidForSlot(slot, stack);
			}
		} else {
			return target.canInsertItem(slot, stack, side);
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack,
			int side) {
		if(side != ForgeDirection.UP.ordinal()) {
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
		}
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
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
		if(from != ForgeDirection.UP) {
			return null;
		}
		IFluidHandler target = getTargetFluidHandler();
		if(target != null ) {
			return target.drain(from, resource, doDrain);
		} else {
			return null;
		}
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if(from != ForgeDirection.UP) {
			return null;
		}
		IFluidHandler target = getTargetFluidHandler();
		if(target != null ) {
			return target.drain(from, maxDrain, doDrain);
		} else {
			return null;
		}
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
		if(from != ForgeDirection.UP) {
			return false;
		}
		IFluidHandler target = getTargetFluidHandler();
		if(target != null ) {
			return target.canDrain(from, fluid);
		} else {
			return false;
		}
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
	 * -
	 */

}
