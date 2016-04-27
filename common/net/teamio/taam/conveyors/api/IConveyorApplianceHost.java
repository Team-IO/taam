package net.teamio.taam.conveyors.api;

import java.util.List;

public interface IConveyorApplianceHost extends IConveyorAwareTE {

	public boolean canAcceptAppliance(String type);

	public List<IConveyorAppliance> getAppliances();
	
}