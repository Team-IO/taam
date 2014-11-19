package founderio.taam.multinet.logistics;

import java.util.ArrayList;
import java.util.Collections;
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
		
//		TODO Maybe: When calculating due date, simulate how long the next available cart would take!

//		TODO If enabled, prioritize manual requests
//		TODO Generally make priority configurable

		// Begin with transport of closest due time - or oldest transport if none has a due time.
		Collections.sort(pendingTransport);
		
		Vehicle vehicle = null;

		/*
		 * Select a suitable free vehicle and generate route for it.
		 * If not found, try to go with the next task.
		 */
		int startingPointIndex = -1;
		Transport startingPoint = null; 
		do {
			startingPointIndex++;
			startingPoint = pendingTransport.get(0);
			vehicle = findSuitableVehicle(startingPoint);
		} while (vehicle == null);
		
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
