package founderio.taam.logistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class PredictedInventory {
	//TODO: Respect already scheduled transports
	
	private List<ItemStack> projected;
	
	private boolean allowNegative;
	
	private boolean respectItemMaxStackSize = true;
	private int maxStackSize = -1;
	private int maxStackCount = -1;
	
	
	
	public boolean isAllowNegative() {
		return allowNegative;
	}

	public void setAllowNegative(boolean allowNegative) {
		this.allowNegative = allowNegative;
	}

	public boolean isRespectItemMaxStackSize() {
		return respectItemMaxStackSize;
	}

	public void setRespectItemMaxStackSize(boolean respectItemMaxStackSize) {
		this.respectItemMaxStackSize = respectItemMaxStackSize;
	}

	public int getMaxStackSize() {
		return maxStackSize;
	}

	public void setMaxStackSize(int maxStackSize) {
		this.maxStackSize = maxStackSize;
	}

	public int getMaxStackCount() {
		return maxStackCount;
	}

	public void setMaxStackCount(int maxStackCount) {
		this.maxStackCount = maxStackCount;
	}

	public boolean canStackFit(ItemStack stack) {
		if(stack == null || stack.getItem() == null) {
			return false;
		}
		if(maxStackCount == -1) {
			// It will fit, since we can simply add new stacks.
			return true;
		} else if (maxStackSize == -1) {
			if(projected.size() < maxStackSize) {
				// Free slot, will fit.
				return true;
			} else {
				// Slot of same item? will fit.
				return findSameItem(stack) != null;
			}
		} else {
			//TODO: Optimize. maybe add "rollback" and just use this algorithm here to check the plausibility of an inventory?
			
			// Iterate all items & split them to apropriate stacks.
			// If we match the item we want to add, we add that amount.
			// Then, subtract the used slots from the max slot count.
			int emptySlots = maxStackCount;
			boolean found = false;
			for(ItemStack contained : projected) {
				int containedMax = maxStackSize;
				if(respectItemMaxStackSize) {
					containedMax = contained.getMaxStackSize();
				}
				int calculatedContained = contained.stackSize;
				if(contained.isItemEqual(stack)) {
					calculatedContained += stack.stackSize;
					found = true;
				}
				emptySlots -= MathHelper.ceiling_float_int((float)calculatedContained / containedMax);
			}
			// Not already in the inventory, so split into stack and calculate the remaining empty slots
			if(!found) {
				int containedMax = maxStackSize;
				if(respectItemMaxStackSize) {
					containedMax = stack.getMaxStackSize();
				}
				emptySlots -= MathHelper.ceiling_float_int((float)stack.stackSize / containedMax);
			}
			// If we have 0 or more empty slots, we could fit all items.
			return emptySlots >= 0;
		}
	}
	
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
	
	public void addTransports(Collection<Transport> transports) {
		if(transports != null) {
			for(Transport transport : transports) {
				changeStock((ItemStack) transport.goods.type, false);
			}
		}
	}
	
	public void subtractTransports(Collection<Transport> transports) {
		if(transports != null) {
			for(Transport transport : transports) {
				changeStock((ItemStack) transport.goods.type, true);
			}
		}
	}
	
	public void applyTransports(Collection<Transport> transports, boolean subtract) {
		if(transports != null) {
			for(Transport transport : transports) {
				changeStock((ItemStack) transport.goods.type, subtract);
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