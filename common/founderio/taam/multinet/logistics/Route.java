package founderio.taam.multinet.logistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import founderio.taam.multinet.AStar.Node;
import founderio.taam.multinet.logistics.StationGraph.Track;

public class Route {
	
	public Route() {
		transports = new ArrayList<Transport>();
		stations = new ArrayList<IStation>();
		stationsToPlot = new Hashtable<Integer, Integer>();
		plot = new ArrayList<ITrack>();
	}
	
	List<Transport> transports;
	List<IStation> stations;
	
	List<ITrack> plot;
	Map<Integer, Integer> stationsToPlot;
	boolean hasPlot = false;
	
	public void clearPlot() {
		hasPlot = false;
		plot.clear();
		stationsToPlot.clear();
	}
	
	public boolean plotRoute(StationGraph graph) {
		clearPlot();
		
		if(stations.size() < 2) {
			return true;
		}
		//TODO Nullchecks!
		
		//TODO: Split Plot into one track list per station -> make replotting easier
		
		IStation current = stations.get(0);
		Track currentTrack = graph.getTrackForStation(current);
		plot.add(currentTrack);
		stationsToPlot.put(0, 0);
		for(int i = 1; i < stations.size(); i++) {
			IStation nextStation = stations.get(i);
			ITrack nextTrack = graph.getTrackForStation(nextStation);
			if(nextTrack == null) {
				return false;
			}
			if(nextTrack == currentTrack) {
				stationsToPlot.put(i, plot.size() - 1);
				continue;
			}
			Node<ITrack> node = StationGraph.astar(currentTrack, nextTrack);
			if(node == null) {
				return false;
			}
			// Add astar result to plot in reverse (result is from target to origin)
			List<ITrack> nextPlotContent = new ArrayList<ITrack>();
			do {
				nextPlotContent.add(node.object);
				node = node.getPredecessor();
			} while(node != null && node.object != currentTrack);
			Collections.reverse(nextPlotContent);
			plot.addAll(nextPlotContent);
			// Log the location of the station in relation to the plot
			stationsToPlot.put(i, plot.size() - 1);
		}
		hasPlot = true;
		return true;
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
			if(fromFound) {
				// TO found after FROM -> begins and ends on the route.
				if(plot.get(i).getLocatedStations().containsKey(transport.to)) {
					return 3;
				}
			} else {
				// FROM found, no check for TO yet.
				if(plot.get(i).getLocatedStations().containsKey(transport.from)) {
					fromFound = true;
				} else
				// TO found before FROM -> ends on the route.
				if(plot.get(i).getLocatedStations().containsKey(transport.to)) {
					return 2;
				}
			}
		}
		// When FROM has been found but TO is still missing -> begins on the route.
		return fromFound ? 1 : 0;
	}
	
	public boolean canAddTransport(Transport transport, Vehicle vehicle) {
		//TODO: Check vehicle status
		return true;
	}
	
	public void addTransport(Transport transport) {
		addStation(transport.from);
		addStation(transport.to);
	}
	
	private void addStation(IStation station) {
		if(!stations.contains(station)) {
			if(stations.isEmpty()) {
				stations.add(station);
			} else {
				int stationIdx = 0;
				for(int t = 0; t < plot.size() && stationIdx < stations.size(); t++) {
					if(ArrayUtils.contains(plot.get(t).getConnectedTracks(), station)) {
						break;
					}
				}
				stations.add(stationIdx, station);
			}
			clearPlot();
		}
	}
}
