package founderio.taam.conveyors;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.IFluidTank;

public class ApplianceInventory {
	public IInventory inventory;
	public IFluidTank[] fluidTanks;
	
	public void writeToNBT(NBTTagCompound tag) {
		//TODO: implement.
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		
	}
}
