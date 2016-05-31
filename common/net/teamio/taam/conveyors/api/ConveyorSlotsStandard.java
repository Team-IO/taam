package net.teamio.taam.conveyors.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.ItemWrapper;

/**
 * Standard implementation of a slot set that has actual items in its slots and
 * does not reference any other inventory. This implementation will allow
 * insertion & removal & returns the slot if it is marked available by the slot
 * matrix.
 *
 * This implementation does not mark the slots as moving.
 *
 * @author Oliver Kahrmann
 *
 */
public class ConveyorSlotsStandard extends ConveyorSlotsStatic {

	private final ItemWrapper[] slots;

	public ConveyorSlotsStandard() {
		slots = new ItemWrapper[9];
		for(int i = 0; i < 9; i++) {
			slots[i] = new ItemWrapper(null);
		}
	}

	@Override
	public int insertItemAt(ItemStack item, int slot) {
		if(!isSlotAvailable(slot)) {
			return 0;
		}
		int count = ConveyorUtil.insertItemAt(this, item, slot, false);
		return count;
	}

	@Override
	public ItemStack removeItemAt(int slot) {
		if(!isSlotAvailable(slot)) {
			return null;
		}
		ItemWrapper candidate = slots[slot];
		ItemStack removed = candidate.itemStack;
		candidate.itemStack = null;
		return removed;
	}

	@Override
	public ItemWrapper getSlot(int slot) {
		if(isSlotAvailable(slot)) {
			return slots[slot];
		} else {
			return ItemWrapper.EMPTY;
		}
	}

	public void writeToNBT(NBTTagCompound tag) {
		NBTTagList itemsTag = new NBTTagList();
		for(int i = 0; i < slots.length; i++) {
			itemsTag.appendTag(slots[i].serializeNBT());
		}
		tag.setTag("items", itemsTag);
	}

	public void readFromNBT(NBTTagCompound tag) {
		NBTTagList itemsTag = tag.getTagList("items", NBT.TAG_COMPOUND);
		if(itemsTag != null) {
			int count = Math.min(itemsTag.tagCount(), slots.length);
			for(int i = 0; i < count; i++) {
				slots[i] = ItemWrapper.readFromNBT(itemsTag.getCompoundTagAt(i));
			}
		}
	}

}
