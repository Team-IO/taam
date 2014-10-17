package founderio.taam.conveyors;


public interface IConveyorApplianceFactory {
	public IConveyorAppliance setUpApplianceInventory(String type, IConveyorApplianceHost conveyor);
}
