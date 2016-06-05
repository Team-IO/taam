package net.teamio.taam.conveyors.filters;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemFilterCustomizable implements IItemFilter {

	private boolean excluding;

	private final FilterEntry[] entries;

	public ItemFilterCustomizable(int maxEntries) {
		this(maxEntries, false);
	}

	public ItemFilterCustomizable(int maxEntries, boolean excluding) {
		entries = new FilterEntry[maxEntries];
		for (int i = 0; i < entries.length; i++) {
			entries[i] = new FilterEntry();
		}
		this.excluding = excluding;
	}

	public void setExcluding(boolean excluding) {
		this.excluding = excluding;
	}

	@Override
	public boolean isExcluding() {
		return excluding;
	}

	public FilterEntry[] getEntries() {
		return entries;
	}

	@Override
	public boolean isItemStackMatching(ItemStack stack) {
		for (int i = 0; i < entries.length; i++) {
			FilterEntry filterEntry = entries[i];
			if (filterEntry.isItemStackMatching(stack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("excluding", excluding);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		excluding = nbt.getBoolean("excluding");
	}

}
