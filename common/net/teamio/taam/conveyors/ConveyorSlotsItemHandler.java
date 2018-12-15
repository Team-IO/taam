package net.teamio.taam.conveyors;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.teamio.taam.util.InventoryUtils;

public class ConveyorSlotsItemHandler implements IItemHandlerModifiable {

	public final IConveyorSlots slots;
	public final int slot;

	public ConveyorSlotsItemHandler(IConveyorSlots slots, EnumFacing side) {
		this.slots = slots;
		this.slot = ConveyorUtil.getSlot(side);
	}

	public ConveyorSlotsItemHandler(IConveyorSlots slots, int slot) {
		this.slots = slots;
		this.slot = slot;
	}

	/**
	 * This method should be called by implementations when content changes,
	 * that needs to be saved / updated.
	 *
	 * Implementations further down can then call their own logic in here.
	 */
	public void onChangeHook() {
	}

	@Override
	public int getSlots() {
		return slots.isSlotAvailable(slot) ? 1 : 0;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if (slots.isSlotAvailable(slot)) {
			return slots.getSlot(slot).itemStack;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertItem(int unused, ItemStack stack, boolean simulate) {
		if (InventoryUtils.isEmpty(stack)) {
			return ItemStack.EMPTY;
		}
		int amount = slots.insertItemAt(stack, slot, simulate);
		if (amount == stack.getCount()) {
			onChangeHook();
			return ItemStack.EMPTY;
		}
		ItemStack didNotFit = stack.copy();
		if(amount > 0) {
			onChangeHook();
			didNotFit = InventoryUtils.adjustCount(didNotFit, -amount);
		}
		return didNotFit;
	}

	@Override
	public ItemStack extractItem(int unused, int amount, boolean simulate) {
		if (amount == 0 || !slots.isSlotAvailable(slot)) {
			return ItemStack.EMPTY;
		}
		ItemStack removed = slots.removeItemAt(slot, amount, simulate);
		if(!InventoryUtils.isEmpty(removed)) {
			onChangeHook();
		}
		return removed;
	}

	@Override
	public void setStackInSlot(int unused, ItemStack stack) {
		if (!slots.isSlotAvailable(slot)) {
			throw new RuntimeException("setStackInSlot called on unavailable slot, cannot comply. SORRY! This is a bug, report it to the mod further up the call stack!");
		}
		onChangeHook();
		slots.getSlot(slot).setStack(stack);
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}
}
