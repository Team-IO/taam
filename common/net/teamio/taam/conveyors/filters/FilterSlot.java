package net.teamio.taam.conveyors.filters;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.teamio.taam.util.InventoryUtils;

public class FilterSlot extends HidableSlot {
    public static final IInventory emptyInventory = new InventoryBasic("[Null]", true, 0);
	
    public final ItemFilterCustomizable filter;
    public int index;
    
    public FilterSlot(ItemFilterCustomizable filter, int index, int xPosition, int yPosition) {
		super(emptyInventory, index, xPosition, yPosition);
		this.filter = filter;
		this.index = index;
	}
	
	public FilterEntry getFilterEntry() {
		return filter.getEntries()[index];
	}
	
	@Override
	public ItemStack decrStackSize(int amount) {
		getFilterEntry().stack = null;
		this.onSlotChanged();
		return null;
	}
	
	@Override
	public boolean getHasStack() {
		return getFilterEntry().stack != null;
	}
	
	@Override
	public ItemStack getStack() {
		return getFilterEntry().stack;
	}
	
	@Override
	public void putStack(ItemStack stack) {
		FilterEntry filterEntry = getFilterEntry();
		if(stack == null) {
			filterEntry.stack = null;
		} else {
			filterEntry.stack = InventoryUtils.copyStack(stack, 1);
		}
		this.onSlotChanged();
	}
	
	@Override
	public int getSlotStackLimit() {
		return 0;
	}
}
