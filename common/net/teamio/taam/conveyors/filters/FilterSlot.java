package net.teamio.taam.conveyors.filters;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.teamio.taam.util.InventoryUtils;

public class FilterSlot extends HidableSlot {
    public static final IInventory emptyInventory = new InventoryBasic("[Null]", true, 0);
	
    public final ItemFilterCustomizable filter;
    public final int index;
    
    public FilterSlot(ItemFilterCustomizable filter, int index, int xPosition, int yPosition) {
		super(emptyInventory, index, xPosition, yPosition);
		this.filter = filter;
		this.index = index;
	}
	
	@Override
	public ItemStack decrStackSize(int amount) {
		filter.getEntries()[index] = null;
		this.onSlotChanged();
		return null;
	}
	
	@Override
	public boolean getHasStack() {
		return filter.getEntries()[index] != null;
	}
	
	@Override
	public ItemStack getStack() {
		return filter.getEntries()[index];
	}
	
	@Override
	public void putStack(ItemStack stack) {
		ItemStack filterEntry;
		if(stack == null) {
			filterEntry = null;
		} else {
			filterEntry = InventoryUtils.copyStack(stack, 1);
		}
		filter.getEntries()[index] = filterEntry;
		this.onSlotChanged();
	}
	
	@Override
	public int getSlotStackLimit() {
		return 0;
	}
}
