package founderio.taam.logistics;

public class Demand {
	
	public static enum DemandCategory {
		Manual,
		FillStock,
		SupplyProduction
	}
	
	public Goods goods;
	
	public int station;
	
	public long needBy;
	public DemandCategory category;
	
	public Demand copy() {
		Demand copy = new Demand();
		copy.category = this.category;
		copy.needBy = this.needBy;
		copy.station = this.station;
		copy.goods = new Goods();
		copy.goods.type = this.goods.type;
		copy.goods.amount = this.goods.amount;
		return copy;
	}
	
	@Override
	public String toString() {
		return "Demand from " + station + " over " + goods.amount + " " + goods.type + ", needed by " + needBy + " category " + category;
	}
}
