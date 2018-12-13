package net.teamio.taam.conveyors;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Standard implementation of a slot set that has actual items in its slots and
 * does not reference any other inventory. This implementation will allow
 * insertion & removal & returns the slot if it is marked available by the slot
 * matrix.
 * <p>
 * This implementation does not mark the slots as moving.
 *
 * @author Oliver Kahrmann
 */
public class ConveyorSlotsStandard extends ConveyorSlotsBase implements INBTSerializable<NBTTagList> {

	protected final ItemWrapper[] slots;

	public ConveyorSlotsStandard() {
		slots = new ItemWrapper[9];
		for (int i = 0; i < 9; i++) {
			slots[i] = new ItemWrapper(null);
		}
	}

	@Override
	public int insertItemAt(ItemStack item, int slot, boolean simulate) {
		slot = MathHelper.clamp(slot, 0, 8);
		if (!isSlotAvailable(slot)) {
			return 0;
		}
		int count = ConveyorUtil.insertItemAt(this, item, slot, simulate);
		if (count > 0) {
			onChangeHook();
		}
		return count;
	}

	@Override
	public ItemStack removeItemAt(int slot, int amount, boolean simulate) {
		slot = MathHelper.clamp(slot, 0, 8);
		if (!isSlotAvailable(slot)) {
			return null;
		}
		ItemWrapper wrapper = slots[slot];
		ItemStack removed = wrapper.itemStack;
		if (removed == null) {
			return null;
		} else if (amount >= removed.getCount()) {
			if (simulate) {
				removed = removed.copy();
			} else {
				wrapper.itemStack = null;
				onChangeHook();
			}
			return removed;
		} else {
			removed = removed.copy();
			removed.setCount(amount);
			if (!simulate && amount > 0) {
				wrapper.itemStack.setCount(wrapper.itemStack.getCount() - amount);
				onChangeHook();
			}
			return removed;
		}
	}

	@Override
	public ItemWrapper getSlot(int slot) {
		slot = MathHelper.clamp(slot, 0, 8);
		if (isSlotAvailable(slot)) {
			return slots[slot];
		}
		return ItemWrapper.EMPTY;
	}

	/*
	 * INBTSerializable implementation
	 */

	@Override
	public NBTTagList serializeNBT() {
		NBTTagList itemsTag = new NBTTagList();
		for (int i = 0; i < slots.length; i++) {
			itemsTag.appendTag(slots[i].serializeNBT());
		}
		return itemsTag;
	}

	@Override
	public void deserializeNBT(NBTTagList nbt) {
		if (nbt != null) {
			int count = Math.min(nbt.tagCount(), slots.length);
			for (int i = 0; i < count; i++) {
				slots[i].deserializeNBT(nbt.getCompoundTagAt(i));
			}
			if (count < slots.length) {
				for (int i = count; i < slots.length; i++) {
					slots[i].itemStack = null;
				}
			}
		}
	}

}
