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
	public static class Track implements ITrack {
		
		
		public Track(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return "Track [" + name + "]";
		}
		
		/* (non-Javadoc)
		 * @see founderio.taam.multinet.logistics.ITrack#getConnectedTracks()
		 */
		@Override
		public ITrack[] getConnectedTracks() {
			return connectedTracks;
		}

		public void setConnectedTracks(ITrack[] connectedTracks) {
			this.connectedTracks = connectedTracks;
		}

		/* (non-Javadoc)
		 * @see founderio.taam.multinet.logistics.ITrack#getLocatedStations()
		 */
		@Override
		public Map<IStation, Integer> getLocatedStations() {
			return locatedStations;
		}

		public void setLocatedStations(Map<IStation, Integer> locatedStations) {
			this.locatedStations = locatedStations;
		}

		private String name = "";
		int length;
		/**
		 * Mapping from connected stations to -> position in reference to start
		 */
		private Map<IStation, Integer> locatedStations = new Hashtable<IStation, Integer>();
		
		private ITrack[] connectedTracks = new ITrack[0];
	}
	
	public static class Navigator implements AStar.Navigator<ITrack> {

		public static final Navigator instance = new Navigator();
		
		private Navigator() {
			
		}
		
		@Override
		public BlockCoord getCoords(ITrack object) {
			//TODO: actually use coords...
			return new BlockCoord(0, 0, 0);
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<ITrack> findNeighbors(ITrack object) {
			//TODO: Use InBlockRoutes
			//TODO: We need the previous route here...
			return Arrays.asList(object.getConnectedTracks());
		}
		
	}
	
	public StationGraph() {
		tracks = new ArrayList<Track>();
	}
	
	public List<Track> tracks;
	
	public Track getTrackForStation(IStation station) {
		for(Track track : tracks) {
			if(track.getLocatedStations().containsKey(station)) {
				return track;
			}
		}
		return null;
	}
	
	public static Node<ITrack> astar(ITrack origin, ITrack target) {
		return AStar.astar(origin, target, Navigator.instance);
	}
}
