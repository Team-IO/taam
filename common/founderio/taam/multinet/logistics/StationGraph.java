package founderio.taam.multinet.logistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import scala.actors.threadpool.Arrays;
import codechicken.lib.vec.BlockCoord;

import java.util.Hashtable;

import founderio.taam.multinet.AStar;
import founderio.taam.multinet.AStar.Node;

public class StationGraph {
	public static class Track {
		
		private String name = "";
		
		public Track(String name) {
			this.name = name;
			locatedStations = new Hashtable<Station, Integer>();
		}
		
		@Override
		public String toString() {
			return "Track [" + name + "]";
		}
		
		int length;
		/**
		 * Mapping from connected stations to -> position in reference to start
		 */
		public Map<Station, Integer> locatedStations;
		
		public Track[] connectedTracks;
	}
	
	public static class Navigator implements AStar.Navigator<Track> {

		public static final Navigator instance = new Navigator();
		
		private Navigator() {
			
		}
		
		@Override
		public BlockCoord getCoords(Track object) {
			//TODO: actually use coords...
			return new BlockCoord(0, 0, 0);
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<Track> findNeighbors(Track object) {
			return Arrays.asList(object.connectedTracks);
		}
		
	}
	
	public StationGraph() {
		tracks = new ArrayList<Track>();
	}
	
	public List<Track> tracks;
	
	public Track getTrackForStation(Station station) {
		for(Track track : tracks) {
			if(track.locatedStations.containsKey(station)) {
				return track;
			}
		}
		return null;
	}
	
	public static Node<Track> astar(Track origin, Track target) {
		return AStar.astar(origin, target, Navigator.instance);
	}
}
