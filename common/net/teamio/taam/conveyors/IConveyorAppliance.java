package net.teamio.taam.conveyors;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.IFluidHandler;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;

public interface IConveyorAppliance extends ISidedInventory, IFluidHandler {

	public int getProgressBegin();
	public int getProgressEnd();
	
	public boolean canProcessItem(ItemWrapper wrapper);
	public void processItem(IConveyorApplianceHost conveyor, ItemWrapper wrapper);
	public boolean isApplianceSetupCompatible(IConveyorApplianceHost conveyorTarget, IConveyorAppliance applianceTarget);
	
	public void writeToNBT(NBTTagCompound tag);
	public void readFromNBT(NBTTagCompound tag);
}
