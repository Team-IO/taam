package founderio.taam.multinet.logistics;

public class Transport {
	public Goods goods;
	/**
	 * Related demand, can be null.
	 */
	public Demand demand;
	
	public long created;
	
	public Station from;
	public Station to;
}
