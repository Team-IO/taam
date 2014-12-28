package founderio.taam.multinet.logistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

public class LogisticsManager {

	StationGraph graph;
	List<IStation> stations;
	List<IVehicle> vehicles;
	
	//TODO: Do we need a separate list anymore? don't think so.
	private Map<Integer, IStation> mapStationID;
	private Map<IStation, Integer> mapIDStation;
	private Map<Integer, IVehicle> mapVehicleID;
	private Map<IVehicle, Integer> mapIDVehicle;
	/*
	 * Step 1: Satisfy demands by creating transports
	 */
	public List<Demand> pendingDemands;
	public List<Demand> processingDemands;
	
	/*
	 * Step 2: Schedule Transports into routes
	 */
	public List<Transport> pendingTransport;
	List<Route> processingRoutes;
	
	public LogisticsManager() {
		stations = new ArrayList<IStation>();
		vehicles = new ArrayList<IVehicle>();
		pendingDemands = new ArrayList<Demand>();
		pendingTransport = new ArrayList<Transport>();
		processingDemands = new ArrayList<Demand>();
		processingRoutes = new ArrayList<Route>();
		
		mapStationID = new HashMap<Integer, IStation>();
		mapIDStation = new HashMap<IStation, Integer>();
		
		mapVehicleID = new HashMap<Integer, IVehicle>();
		mapIDVehicle = new HashMap<IVehicle, Integer>();
	}
	
//	public static void main(String[] args) {
//		LogisticsManager manager = new LogisticsManager();
//		manager.graph = new StationGraph();
//		
//		Station a = new Station("A");
//		Station b = new Station("B");
//		Station c = new Station("C");
//		Station d = new Station("D");
//		Station e = new Station("E");
//		
//		Track trackDA = new Track("DA");
//		Track trackBC = new Track("BC");
//		Track trackE = new Track("E");
//		
//		trackDA.setConnectedTracks(new ITrack[] {
//			trackE,
//			trackBC
//		});
//		trackDA.getLocatedStations().put(d, 2);
//		trackDA.getLocatedStations().put(a, 4);
//		trackE.setConnectedTracks(new ITrack[] {
//			trackDA
//		});
//		trackE.getLocatedStations().put(e, 2);
//		trackBC.setConnectedTracks(new ITrack[] {
//			trackDA
//		});
//		trackBC.getLocatedStations().put(b, 2);
//		trackBC.getLocatedStations().put(c, 4);
//		
//		
//		manager.graph.tracks.add(trackDA);
//		manager.graph.tracks.add(trackE);
//		manager.graph.tracks.add(trackBC);
//		
//		Transport tA = new Transport();
//		tA.created = 5;
//		tA.from = a;
//		tA.to = c;
//
//		Transport tB = new Transport();
//		tB.created = 5;
//		tB.from = d;
//		tB.to = e;
//
//		Transport tC = new Transport();
//		tC.created = 5;
//		tC.from = a;
//		tC.to = b;
//
//		Transport tD = new Transport();
//		tD.created = 5;
//		tD.from = e;
//		tD.to = b;
//
//		//TODO: Test multiple transports
//		//TODO: Test transports connected to demand (time based scheduling)
//		
//		manager.pendingTransport.add(tA);
//		
//		System.out.println("Setup done");
//		
//		manager.scheduleTransports();
//		
//		Route route = manager.processingRoutes.get(0);
//		
//		System.out.println(route.locateTransportOnRoute(tB));
//		System.out.println(route.locateTransportOnRoute(tC));
//		System.out.println(route.locateTransportOnRoute(tD));
//		
//		System.out.println("Scheduling done");
//	}
	
	
	
	public void scheduleTransports() {
		if(pendingTransport.isEmpty()) {
			return;
		}
		
		for(Route route : processingRoutes) {
			tryAppendTransports(route);
		}
		
		/*
		 * Pick starting point
		 */
		
//		TODO Maybe: When calculating due date, simulate how long the next available cart would take!

//		TODO If enabled, prioritize manual requests
//		TODO Generally make priority configurable

		// Begin with transport of closest due time - or oldest transport if none has a due time.
		Collections.sort(pendingTransport);
		
		IVehicle vehicle = null;

		/*
		 * Select a suitable free vehicle and generate route for it.
		 * If not found, try to go with the next task.
		 */
		int startingPointIndex = -1;
		Transport startingPoint = null; 
		do {
			startingPointIndex++;
			startingPoint = pendingTransport.get(startingPointIndex);
			vehicle = findSuitableVehicle(startingPoint);
		} while (vehicle == null && startingPointIndex < pendingTransport.size() - 1);
		
		Route route = new Route();
		route.transports.add(startingPoint);
		route.stations.add(startingPoint.from);
		route.stations.add(startingPoint.to);
		
		route.plotRoute(graph, this);
		
		processingRoutes.add(route);
		pendingTransport.remove(startingPointIndex);
		
		tryAppendTransports(route);
	}
	
	public int getStationID(IStation station) {
		return mapIDStation.get(station);
	}
	
	public IStation getStation(int stationID) {
		return mapStationID.get(stationID);
	}
	
	public int getVehicleID(IVehicle vehicle) {
		return mapIDVehicle.get(vehicle);
	}
	
	public IVehicle getVehicle(int vehicleID) {
		return mapVehicleID.get(vehicleID);
	}
	
	private void tryAppendTransports(Route route) {
		for(int i = 0; i < pendingTransport.size(); i++) {
			Transport transport = pendingTransport.get(i);
			int location = route.locateTransportOnRoute(transport);
			//TODO: get assigned vehicle
			if(location != 0 && route.canAddTransport(transport, null)) {
				route.addTransport(transport);
				pendingTransport.remove(i);
				i--;
			}
		}
	}
	
	private IVehicle findSuitableVehicle(Transport transport) {
		for(IVehicle vehicle : vehicles) {
			if(vehicle.hasRouteToStation(transport.from, graph, this)) {
				PredictedInventory predInv = vehicle.getPredictedInventory();
				if(predInv.canStackFit((ItemStack) transport.goods.type)) {
					return vehicle;
				}
			}
		}
		return null;
	}
	
	private int lastStationID = 0;
	private int lastVehicleID = 0;

	public int addStation(IStation station) {
		stations.add(station);
		int stationID = ++lastStationID;
		mapStationID.put(stationID, station);
		mapIDStation.put(station, stationID);
		return stationID;
	}

	public void removeStation(IStation station) {
		int stationID = getStationID(station);
		mapStationID.remove(stationID);
		mapIDStation.remove(station);
		stations.remove(station);
	}

	public Collection<IStation> getStations() {
		return Collections.unmodifiableCollection(stations);
	}

	public int addVehicle(IVehicle vehicle) {
		vehicles.add(vehicle);
		int vehicleID = ++lastVehicleID;
		mapVehicleID.put(vehicleID, vehicle);
		mapIDVehicle.put(vehicle, vehicleID);
		return vehicleID;
	}
	
	public void removeVehicle(IVehicle vehicle) {
		int vehicleID = getVehicleID(vehicle);
		mapVehicleID.remove(vehicleID);
		mapIDVehicle.remove(vehicle);
		vehicles.remove(vehicle);
	}
	
	public Collection<IVehicle> getVehicles() {
		return Collections.unmodifiableCollection(vehicles);
	}
}
