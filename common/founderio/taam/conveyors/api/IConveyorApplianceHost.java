package founderio.taam.conveyors.api;

import founderio.taam.conveyors.IConveyorAppliance;

public interface IConveyorApplianceHost {

	public boolean initAppliance(String type);
	public boolean removeAppliance();
	
	public boolean hasAppliance();
	public boolean hasApplianceWithType(String type);

	public String getApplianceType();
	public IConveyorAppliance getAppliance();

}