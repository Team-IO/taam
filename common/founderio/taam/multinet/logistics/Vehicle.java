package founderio.taam.multinet.logistics;

public class Vehicle {
	public static class Storage {
		
		public Storage(int maxAmount) {
			this.maxAmount = maxAmount;
		}
		
		public Goods stored;
		public int maxAmount;
	}
	
	public Vehicle() {
		storages = new Storage[0];
	}
	
	public Vehicle(Storage[] storages) {
		this.storages = storages.clone();
	}
	
	public Storage[] storages;
	
}
