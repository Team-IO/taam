package founderio.taam.multinet.logistics;

public class Demand {
	
	public static enum DemandCategory {
		Manual,
		FillStock,
		SupplyProduction
	}
	
	public Goods goods;
	
	public IStation station;
	
	public long needBy;
	public DemandCategory category;
}
