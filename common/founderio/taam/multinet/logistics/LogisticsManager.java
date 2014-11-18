package founderio.taam.multinet.logistics;

import java.util.ArrayList;
import java.util.List;

import founderio.taam.multinet.logistics.StationGraph.Track;

public class LogisticsManager {

	StationGraph graph;
	List<Station> stations;
	List<Vehicle> vehicles;
	/*
	 * Step 1: Satisfy demands by creating transports
	 */
	List<Demand> pendingDemands;
	List<Demand> processingDemands;
	
	/*
	 * Step 2: Schedule Transports into routes
	 */
	List<Transport> pendingTransport;
	List<Route> processingRoutes;
	
	public LogisticsManager() {
		stations = new ArrayList<Station>();
		vehicles = new ArrayList<Vehicle>();
		pendingDemands = new ArrayList<Demand>();
		pendingTransport = new ArrayList<Transport>();
		processingDemands = new ArrayList<Demand>();
		processingRoutes = new ArrayList<Route>();
	}
	
	public static void main(String[] args) {
		LogisticsManager manager = new LogisticsManager();
		manager.graph = new StationGraph();
		
		Station a = new Station("A");
		Station b = new Station("B");
		Station c = new Station("C");
		Station d = new Station("D");
		Station e = new Station("E");
		
		Track trackDA = new Track("DA");
		Track trackBC = new Track("BC");
		Track trackE = new Track("E");
		
		trackDA.connectedTracks = new Track[] {
			trackE,
			trackBC
		};
		trackDA.locatedStations.put(d, 2);
		trackDA.locatedStations.put(a, 4);
		trackE.connectedTracks = new Track[] {
			trackDA
		};
		trackE.locatedStations.put(e, 2);
		trackBC.connectedTracks = new Track[] {
			trackDA
		};
		trackBC.locatedStations.put(b, 2);
		trackBC.locatedStations.put(c, 4);
		
		
		manager.graph.tracks.add(trackDA);
		manager.graph.tracks.add(trackE);
		manager.graph.tracks.add(trackBC);
		
		Transport tA = new Transport();
		tA.created = 5;
		tA.from = a;
		tA.to = c;

		Transport tB = new Transport();
		tB.created = 5;
		tB.from = d;
		tB.to = e;

		Transport tC = new Transport();
		tC.created = 5;
		tC.from = a;
		tC.to = b;

		Transport tD = new Transport();
		tD.created = 5;
		tD.from = e;
		tD.to = b;

		//TODO: Test multiple transports
		//TODO: Test transports connected to demand (time based scheduling)
		
		manager.pendingTransport.add(tA);
		
		System.out.println("Setup done");
		
		manager.scheduleTransports();
		
		Route route = manager.processingRoutes.get(0);
		
		System.out.println(route.locateTransportOnRoute(tB));
		System.out.println(route.locateTransportOnRoute(tC));
		System.out.println(route.locateTransportOnRoute(tD));
		
		System.out.println("Scheduling done");
	}
	
	
	
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
		
		// Begin with transport of closest due time:
		long minNeedBy = Long.MAX_VALUE;
		long creationDate = Long.MAX_VALUE;
		int startingPointIndex = 0;
		Transport startingPoint = pendingTransport.get(0);
		
//		TODO Maybe: When calculating due date, simulate how long the next available cart would take!

//		TODO If enabled, prioritize manual requests
//		TODO Generally make priority configurable
		
		// Ignore first one, since that is already "selected"
		for(int i = 1; i < pendingTransport.size(); i++) {
			Transport check = pendingTransport.get(i);
			// Transports with demand have priority.
			if(check.demand != null) {
				// begin with transport with the closest due time or, if not available, the oldest transport
				// means: same need by time transports will be sorted by their creation date.
				if(check.demand.needBy < minNeedBy ||
						(check.demand.needBy == minNeedBy && check.created < creationDate)) {
					minNeedBy = check.demand.needBy;
					creationDate = check.created;
					startingPointIndex = i;
					startingPoint = check;
				}
			}
		}
		
		/*
		 * Select a suitable free vehicle and generate route for it
		 */
		
		Vehicle vehicle = findSuitableVehicle(startingPoint);
		if(vehicle == null) {
			//TODO: Rework above selection code to create sorted list and pick next vehicle from list. (until all transports iterated or vehicle found.
			return;
		}
		
		
		Route route = new Route();
		route.transports.add(startingPoint);
		route.stations.add(startingPoint.from);
		route.stations.add(startingPoint.to);
		
		route.plotRoute(graph);
		
		processingRoutes.add(route);
		pendingTransport.remove(startingPointIndex);
		
		tryAppendTransports(route);
	}
	
	public void tryAppendTransports(Route route) {
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
		//TODO: Implement
	}
	
	public Vehicle findSuitableVehicle(Transport transport) {
		//TODO: Implement.
		return new Vehicle(new Vehicle.Storage[] { new Vehicle.Storage(64) });
	}
}
