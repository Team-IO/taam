package founderio.taam.conveyors;

import founderio.taam.blocks.TileEntityConveyor;

public interface IConveyorAppliance {
	public int getProgressBegin();
	public int getProgressEnd();
	
	public boolean canProcessItem(ItemWrapper wrapper);
	public void processItem(TileEntityConveyor conveyor, ApplianceInventory inventory, ItemWrapper wrapper);
	public ApplianceInventory setUpApplianceInventory(TileEntityConveyor conveyor);
	public boolean isApplianceSetupCompatible(TileEntityConveyor conveyorSource, ApplianceInventory inventorySource, TileEntityConveyor conveyorTarget, ApplianceInventory inventoryTarget);
}
