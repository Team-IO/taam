package founderio.taam.conveyors;

public interface IConveyorApplianceHost {

	public boolean initAppliance(String type);
	public boolean hasAppliance();
	public boolean hasApplianceWithType(String type);
	public String getApplianceType();
	public IConveyorAppliance getAppliance();
	public boolean removeAppliance();
}