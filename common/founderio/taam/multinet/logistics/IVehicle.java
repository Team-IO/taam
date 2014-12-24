package founderio.taam.multinet.logistics;

import codechicken.lib.vec.BlockCoord;

public interface IVehicle {
	String getName();
	void setName(String name);
	
	PredictedInventory getPredictedInventory();
	PredictedInventory getCurrentInventory();
	
	int getVehicleID();

	boolean isConnectedToManager();
	
	void linkToManager(BlockCoord coords);
	boolean hasRouteToStation(int stationID);
}