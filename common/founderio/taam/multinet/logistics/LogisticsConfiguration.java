package founderio.taam.multinet.logistics;

import net.minecraft.item.ItemStack;
import founderio.taam.blocks.TileEntityLogisticsStation;
import founderio.taam.blocks.TileEntityLogisticsStation.PredictedInventory;

public abstract class LogisticsConfiguration {
	
	public boolean enabled = false;
	public abstract void process(TileEntityLogisticsStation tileEntityLogisticsStation);
	
	public static class KeepStock extends LogisticsConfiguration {
		
		public ItemStack what;
		public int amount = -1;
		//public int baseStock = 0; humm.. actually.. not needed right now.
		public int reorderLevel = -1;
		
		//TODO: Batch Sized
		//TODO: Limitation, if order can be split up or not
		
		@Override
		public void process(
				TileEntityLogisticsStation tileEntityLogisticsStation) {
			// TODO Auto-generated method stub
			PredictedInventory projectedInventory = tileEntityLogisticsStation.getProjectedInventory();
			ItemStack found = projectedInventory.findSameItem(what);
			int orderAmount = 0;
			if(found == null) {
				//TODO: Handle "Infinite Stock"
				orderAmount = amount;
			} else {
				orderAmount = amount - found.stackSize;
				// Do not order if we are not at or below the reorder level yet.
				if(reorderLevel > 0 && found.stackSize > reorderLevel) {
					orderAmount = 0;
				}
			}
			if(orderAmount > 0) {
				ItemStack demand = what.copy();
				demand.stackSize = orderAmount;
				tileEntityLogisticsStation.placeDemand(demand);
			}
		}
	}
	
//	public static class Dropoff extends LogisticsConfiguration {
//		public Object what;
//
//		@Override
//		public void process(
//				TileEntityLogisticsStation tileEntityLogisticsStation) {
//			// TODO Auto-generated method stub
//			
//		}
//	}
//	
//	public static class Dispatch extends LogisticsConfiguration {
//		public Object what;
//
//		@Override
//		public void process(
//				TileEntityLogisticsStation tileEntityLogisticsStation) {
//			// TODO Auto-generated method stub
//			
//		}
//	}

}
