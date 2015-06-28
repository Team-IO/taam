package net.teamio.taam.conveyors.api;

import net.teamio.taam.conveyors.IConveyorAppliance;

public interface IConveyorApplianceHost {

	public boolean initAppliance(String type);
	public boolean removeAppliance();
	
	public boolean hasAppliance();
	public boolean hasApplianceWithType(String type);

	public String getApplianceType();
	public IConveyorAppliance getAppliance();

}