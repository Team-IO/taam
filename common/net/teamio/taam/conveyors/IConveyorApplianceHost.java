package net.teamio.taam.conveyors;

import java.util.List;

public interface IConveyorApplianceHost {

	boolean canAcceptAppliance(String type);

	List<IConveyorAppliance> getAppliances();

	IConveyorSlots getSlots();
}