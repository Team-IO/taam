package founderio.taam.conveyors;

import founderio.taam.blocks.TileEntityConveyor;

public interface IConveyorApplianceFactory {
	public IConveyorAppliance setUpApplianceInventory(String type, TileEntityConveyor conveyor);
}
