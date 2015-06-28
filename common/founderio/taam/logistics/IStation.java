package founderio.taam.logistics;

import founderio.taam.util.WorldCoord;


public interface IStation {

	String getName();

	int getStationID();

	boolean isConnectedToManager();

	void linkToManager(WorldCoord coords);
}