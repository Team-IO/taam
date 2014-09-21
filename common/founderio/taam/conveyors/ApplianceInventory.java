package founderio.taam.conveyors;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

public abstract class ApplianceInventory implements IConveyorAppliance {
	public IInventory inventory;
	public FluidTank[] fluidTanks;
	
	public ApplianceInventory(int inventorySlots, int... tankCapacities) {
		inventory = new InventoryBasic("Appliance Inventory", false, inventorySlots);
		if(tankCapacities != null) {
			fluidTanks = new FluidTank[tankCapacities.length];
			for(int i = 0; i < tankCapacities.length; i++) {
				fluidTanks[i] = new FluidTank(tankCapacities[i]);
			}
		} else {
			fluidTanks = new FluidTank[0];
		}
	}
	
	protected abstract int getTankForSide(ForgeDirection from);
	
	/*
	 * IFluidHandler
	 */
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		int tankID = getTankForSide(from);
		IFluidTank tank = getActualTank(tankID);
		if(tank == null) {
			return 0;
		}
		return tank.fill(resource, doFill);
	}
	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
		if(resource == null) {
			return null;
		}
		int tankID = getTankForSide(from);
		IFluidTank tank = getActualTank(tankID);
		if(tank == null) {
			return null;
		}
		if(!resource.isFluidEqual(tank.getFluid())) {
			return null;
		}
		return tank.drain(resource.amount, doDrain);
	}
	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		int tankID = getTankForSide(from);
		IFluidTank tank = getActualTank(tankID);
		if(tank == null) {
			return null;
		}
		return tank.drain(maxDrain, doDrain);
	}
	
	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		int tankID = getTankForSide(from);
		if(tankID == -1 || tankID >= fluidTanks.length) {
			return new FluidTankInfo[0];
		} else {
			return new FluidTankInfo[] { fluidTanks[tankID].getInfo() };
		}
	}
	
	protected IFluidTank getActualTank(int tankID) {
		if(tankID == -1 || tankID >= fluidTanks.length) {
			return null;
		} else {
			return fluidTanks[tankID];
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		if(fluidTanks != null && fluidTanks.length != 0) {
			NBTTagList tanks = new NBTTagList();
			for(int i = 0; i < fluidTanks.length; i++) {
				NBTTagCompound tank = new NBTTagCompound();
				fluidTanks[i].writeToNBT(tank);
				tanks.appendTag(tank);
			}
			tag.setTag("tanks", tanks);
		}
		//TODO: Write Inventory
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		NBTTagList tanks = tag.getTagList("tanks", NBT.TAG_COMPOUND);
		if(tanks != null) {
			int max = Math.min(tanks.func_150303_d(), fluidTanks.length);
			for(int i = 0; i < max; i++) {
				fluidTanks[i].readFromNBT(tanks.getCompoundTagAt(i));
			}
		}
		//TODO: Read Inventory
	}
	/*
	 * IInventory
	 */

	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	public ItemStack getStackInSlot(int p_70301_1_) {
		return inventory.getStackInSlot(p_70301_1_);
	}

	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		return inventory.decrStackSize(p_70298_1_, p_70298_2_);
	}

	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		return inventory.getStackInSlotOnClosing(p_70304_1_);
	}

	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
		inventory.setInventorySlotContents(p_70299_1_, p_70299_2_);
	}

	public String getInventoryName() {
		return inventory.getInventoryName();
	}

	public boolean hasCustomInventoryName() {
		return inventory.hasCustomInventoryName();
	}

	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	public void openInventory() {
		inventory.openInventory();
	}

	public void closeInventory() {
		inventory.closeInventory();
	}

	public void markDirty() {
		inventory.markDirty();
	}
	
	
}
