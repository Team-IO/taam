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
		filter.getEntries()[index] = ItemStack.EMPTY;
		this.onSlotChanged();
		return ItemStack.EMPTY;
	}

	@Override
	public boolean getHasStack() {
		return !InventoryUtils.isEmpty(filter.getEntries()[index]);
	}

	@Override
	public ItemStack getStack() {
		return filter.getEntries()[index];
	}

	@Override
	public void putStack(ItemStack stack) {
		ItemStack filterEntry;
		if (InventoryUtils.isEmpty(stack)) {
			filterEntry = ItemStack.EMPTY;
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
