package net.teamio.taam.logistics;

import net.teamio.taam.util.WorldCoord;


public interface IStation {

	String getName();

	int getStationID();

	boolean isConnectedToManager();

	void linkToManager(WorldCoord coords);
}