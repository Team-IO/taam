package founderio.taam.multinet.logistics;

import java.util.Map;

public interface ITrack {

	public abstract ITrack[] getConnectedTracks();

	public abstract Map<IStation, Integer> getLocatedStations();

}