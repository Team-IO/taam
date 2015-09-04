package net.teamio.taam.conveyors;

import net.teamio.taam.conveyors.api.IConveyorApplianceHost;


public interface IConveyorApplianceFactory {
	public IConveyorAppliance setUpApplianceInventory(String type, IConveyorApplianceHost conveyor);
}
