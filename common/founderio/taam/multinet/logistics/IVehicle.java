package founderio.taam.multinet.logistics;

import codechicken.lib.vec.BlockCoord;

public interface IVehicle {
	String getName();
	void setName(String name);
	
	PredictedInventory getPredictedInventory();

	int getVehicleID();

	boolean isConnectedToManager();
	
	void linkToManager(BlockCoord coords);
}