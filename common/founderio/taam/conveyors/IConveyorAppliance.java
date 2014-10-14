package founderio.taam.conveyors;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.IFluidHandler;
import founderio.taam.blocks.TileEntityConveyor;

public interface IConveyorAppliance extends ISidedInventory, IFluidHandler {

	public int getProgressBegin();
	public int getProgressEnd();
	
	public boolean canProcessItem(ItemWrapper wrapper);
	public void processItem(TileEntityConveyor conveyor, ItemWrapper wrapper);
	public boolean isApplianceSetupCompatible(TileEntityConveyor conveyorTarget, IConveyorAppliance applianceTarget);
	
	public void writeToNBT(NBTTagCompound tag);
	public void readFromNBT(NBTTagCompound tag);
}
