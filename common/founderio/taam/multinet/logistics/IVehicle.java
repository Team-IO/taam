package founderio.taam.multinet.logistics;


public interface IVehicle {
	String getName();
	void setName(String name);
	
	PredictedInventory getPredictedInventory();
	PredictedInventory getCurrentInventory();
	
	int getVehicleID();

	boolean isConnectedToManager();
	
	void linkToManager(WorldCoord coords);
	boolean hasRouteToStation(int stationID, StationGraph graph, LogisticsManager manager);
	WorldCoord getCurrentLocation();
	void setRoute(Route route);
}