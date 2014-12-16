package founderio.taam.multinet.logistics;

public class Transport implements Comparable<Transport> {
	public Goods goods;
	/**
	 * Related demand, can be null.
	 */
	public Demand demand;
	
	public long created;
	
	public IStation from;
	public IStation to;
	
	@Override
	public int compareTo(Transport other) {
		/*
		 * Info: Lowest has priority -> means if we are to have priority, we are "smaller" than the other task.
		 */
		if(this == other) {
			// Same transport, no comparison.
			return 0;
		}
		if(this.demand  == null) {
			if(other.demand == null) {
				// Both have no demand. Oldest wins.
				return sign(this.created - other.created);
			}
			// Other one has demand, we don't. Give other one precedence.
			return 1;
		}
		if(other.demand == null) {
			// We have demand, other one doesn't. We have priority.
			return -1;
		} else {
			// Both have demand, closest need by date wins.
			return sign(this.demand.needBy - other.demand.needBy);
		}
	}
	
	private int sign(long value) {
		if(value == 0) {
			return 0;
		}
		if(value > 0) {
			return 1;
		}
		return -1;
	}
}
