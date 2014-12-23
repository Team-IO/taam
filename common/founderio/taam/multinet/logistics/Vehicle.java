package founderio.taam.multinet.logistics;

import codechicken.lib.vec.BlockCoord;

public class Vehicle implements IVehicle {
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

	@Override
	public int getVehicleID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isConnectedToManager() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PredictedInventory getPredictedInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void linkToManager(BlockCoord coords) {
		// TODO Auto-generated method stub
		
	}
	
}
