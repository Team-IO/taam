package founderio.taam.logistics;


public interface IStation {

	String getName();

	int getStationID();

	boolean isConnectedToManager();

	void linkToManager(WorldCoord coords);
}