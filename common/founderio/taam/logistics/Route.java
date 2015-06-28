package founderio.taam.logistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import founderio.taam.multinet.AStar.Node;

public class Route {
	
	public Route() {
		transports = new ArrayList<Transport>();
		stations = new ArrayList<Integer>();
		stationsToPlot = new Hashtable<Integer, Integer>();
		plot = new ArrayList<WorldCoord>();
	}
	
	List<Transport> transports;
	List<Integer> stations;
	
	//TODO: World coordinates
	List<WorldCoord> plot;
	Map<Integer, Integer> stationsToPlot;
	boolean hasPlot = false;
	
	public void clearPlot() {
		hasPlot = false;
		plot.clear();
		stationsToPlot.clear();
	}
	
	public List<WorldCoord> getPlot() {
		return plot;
	}
	
	public boolean plotRoute(StationGraph graph, LogisticsManager manager, IVehicle vehicle) {
		System.out.println("Plotting route.");
		
		clearPlot();
		
		if(stations.size() < 2) {
			return true;
		}
		//TODO Nullchecks!
		
		//TODO: Split Plot into one track list per station -> make replotting easier
		
		IStation current = manager.getStation(stations.get(0));
		WorldCoord currentTrack = graph.getTrackForStation(current);
		// If we are plotting for a vehicle, add the route to the first station.
		if(vehicle != null) {
			WorldCoord vehicleTrack = vehicle.getCurrentLocation();
			Node<WorldCoord> node = graph.astar(vehicleTrack, currentTrack);
			if(node == null) {
				return false;
			}
			addAstarPlot(node, vehicleTrack);
		}
		if(currentTrack == null) {
			return false;
		}
		plot.add(currentTrack);
		stationsToPlot.put(0, 0);
		for(int i = 1; i < stations.size(); i++) {
			IStation nextStation = manager.getStation(stations.get(i));
			WorldCoord nextTrack = graph.getTrackForStation(nextStation);
			if(nextTrack == null) {
				return false;
			}
			if(nextTrack == currentTrack) {
				stationsToPlot.put(i, plot.size() - 1);
				continue;
			}
			Node<WorldCoord> node = graph.astar(currentTrack, nextTrack);
			if(node == null) {
				return false;
			}
			addAstarPlot(node, currentTrack);
			// Log the location of the station in relation to the plot
			stationsToPlot.put(i, plot.size() - 1);
		}
		hasPlot = true;
		return true;
	}
	
	private void addAstarPlot(Node<WorldCoord> node, WorldCoord currentTrack) {
		// Add astar result to plot in reverse (result is from target to origin)
		List<WorldCoord> nextPlotContent = new ArrayList<WorldCoord>();
		do {
			nextPlotContent.add(node.object);
			node = node.getPredecessor();
		} while(node != null && node.object != currentTrack);
		Collections.reverse(nextPlotContent);
		plot.addAll(nextPlotContent);
	}
	
	/**
	 * Checks whether transport is on the route. Not actually contained in the
	 * data structure, but rather
	 * "Are the stations passed in the right order when following the route?".
	 * 
	 * @param transport
	 * @return 
	 * 0 if the transport is not on the route,
	 * 1 if the transport begins but does not end on the route,
	 * 2 if the transport ends but does not begin on the route;
	 * 3 it the transport begins and ends on the route,
	 */
	public int locateTransportOnRoute(Transport transport) {
		boolean fromFound = false;
		for(int i = 0; i < plot.size(); i++) {
			//TODO: Fix after the StationGraph is working usefully again.
//			if(fromFound) {
//				// TO found after FROM -> begins and ends on the route.
//				if(plot.get(i).getLocatedStations().containsKey(transport.to)) {
//					return 3;
//				}
//			} else {
//				// FROM found, no check for TO yet.
//				if(plot.get(i).getLocatedStations().containsKey(transport.from)) {
//					fromFound = true;
//				} else
//				// TO found before FROM -> ends on the route.
//				if(plot.get(i).getLocatedStations().containsKey(transport.to)) {
//					return 2;
//				}
//			}
		}
		// When FROM has been found but TO is still missing -> begins on the route.
		return fromFound ? 1 : 0;
	}
	
	public boolean canAddTransport(Transport transport, IVehicle vehicle) {
		//TODO: Check vehicle status
		return true;
	}
	
	public void addTransport(Transport transport) {
		addStation(transport.from);
		addStation(transport.to);
	}
	
	private void addStation(int station) {
		if(!stations.contains(station)) {
			if(stations.isEmpty()) {
				stations.add(station);
			} else {
				int stationIdx = stations.size();//0;
				//TODO: Fix after the StationGraph is working usefully again.
				//TODO: Find station on existing route points & insert correctly
//				for(int t = 0; t < plot.size() && stationIdx < stations.size(); t++) {
//					if(plot.get(t).getLocatedStations().containsKey(station)) {
//						break;
//					}
//				}
				stations.add(stationIdx, station);
			}
			clearPlot();
		}
	}
}
