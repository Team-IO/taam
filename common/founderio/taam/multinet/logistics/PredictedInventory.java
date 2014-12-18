package founderio.taam.multinet.logistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class PredictedInventory {
	//TODO: Respect already scheduled transports
	
	private List<ItemStack> projected;
	
	private boolean allowNegative;
	
	public PredictedInventory(boolean allowNegative, IInventory inventory) {
		this.allowNegative = allowNegative;
		int size = 5;
		if(inventory != null) {
			size = inventory.getSizeInventory();
		}
		projected = new ArrayList<ItemStack>(size);
		addInventory(inventory);
	}
	

	public void addInventory(IInventory inventory) {
		applyInventory(inventory, false);
	}
	
	public void subtractInventory(IInventory inventory) {
		applyInventory(inventory, true);
	}
	
	private void applyInventory(IInventory inventory, boolean subtract) {
		if(inventory != null) {
			int sizeInventory = inventory.getSizeInventory();
			for(int i = 0; i < sizeInventory; i++) {
				changeStock(inventory.getStackInSlot(i), subtract);
			}
		}
	}
	
	public void addDemands(Collection<Demand> demands) {
		applyDemands(demands, false);
	}
	
	public void subtractDemands(Collection<Demand> demands) {
		applyDemands(demands, true);
	}
	
	public void applyDemands(Collection<Demand> demands, boolean subtract) {
		if(demands != null) {
			for(Demand demand : demands) {
				//TODO: change once demands & goods are up to speed.
				changeStock((ItemStack) demand.goods.type, subtract);
			}
		}
	}
	
	public ItemStack findSameItem(ItemStack stack) {
		for(ItemStack projectedStack : projected) {
			if(projectedStack.isItemEqual(stack)) {
				return projectedStack;
			}
		}
		return null;
	}
	
	private void changeStock(ItemStack stack, boolean subtract) {
		if(stack == null) {
			return;
		}
		Iterator<ItemStack> iter = projected.iterator();
		while(iter.hasNext()) {
			ItemStack projectedStack = iter.next();
			if(projectedStack.isItemEqual(stack)) {
				if(subtract) {
					projectedStack.stackSize -= stack.stackSize;
					if(projectedStack.stackSize == 0 || (!allowNegative && projectedStack.stackSize < 0)) {
						iter.remove();
					}
				} else {
					projectedStack.stackSize += stack.stackSize;
				}
				return;
			}
		}
		ItemStack copy = stack.copy();
		if(subtract) {
			if(!allowNegative) {
				return;
			}
			copy.stackSize *= -1;
		}
		projected.add(stack);
	}
	
}