package net.teamio.taam.conveyors.filters;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.teamio.taam.Log;
import net.teamio.taam.util.InventoryUtils;
import net.teamio.taam.util.TaamUtil;

public class ItemFilterCustomizable implements IItemFilter {

	private boolean excluding;

	private final ItemStack[] entries;

	public ItemFilterCustomizable(int maxEntries) {
		this(maxEntries, false);
	}

	public ItemFilterCustomizable(int maxEntries, boolean excluding) {
		entries = new ItemStack[maxEntries];
		for (int i = 0; i < maxEntries; i++) {
			entries[i] = ItemStack.EMPTY;
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

	/**
	 * Check NBT? Applies only in {@link FilterMode#Exact}.
	 */
	public boolean checkNBT = true;
	/**
	 * Check metadata? Applies only in {@link FilterMode#Exact}.
	 */
	public boolean checkMeta = true;
	/**
	 * The filter mode to be applied.
	 */
	public FilterMode mode = FilterMode.Exact;

	public enum FilterMode {
		Exact, OreDict, Mod;

		private static final FilterMode[] nextModes = {
				OreDict, Mod, Exact
		};

		public static FilterMode getNext(FilterMode mode) {
			if (mode == null) {
				return FilterMode.Exact;
			}
			return nextModes[mode.ordinal()];
		}
	}

	public ItemStack[] getEntries() {
		return entries;
	}

	@Override
	public boolean isItemStackMatching(ItemStack stack) {
		for (int i = 0; i < entries.length; i++) {
			ItemStack filterEntry = entries[i];
			if (isItemStackMatching(filterEntry, stack)) {
				return true;
			}
		}
		return false;
	}

	public boolean isItemStackMatching(ItemStack filterEntry, ItemStack stack) {
		// Null Matching
		if (InventoryUtils.isEmpty(filterEntry) || InventoryUtils.isEmpty(stack)) {
			return false;
		}
		// Actual matching
		switch (mode) {
			case Exact:
				if (stack.getItem() != filterEntry.getItem()) {
					return false;
				}
				if (checkMeta && stack.getMetadata() != filterEntry.getMetadata()) {
					return false;
				}
				if (checkNBT && !ItemStack.areItemStackTagsEqual(stack, filterEntry)) {
					return false;
				}
				return true;
			case Mod:
				return TaamUtil.isModMatch(stack, filterEntry);
			case OreDict:
				return TaamUtil.isOreDictMatch(stack, filterEntry);
			default:
				Log.error("Unknown filter mode {}. Result: false, this should not happen!", mode);
				return false;
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		// Basic Config
		nbt.setBoolean("excluding", excluding);
		nbt.setInteger("mode", mode.ordinal());
		nbt.setBoolean("checkNBT", checkNBT);
		nbt.setBoolean("checkMeta", checkMeta);
		// Filter Entries
		for (int i = 0; i < entries.length; i++) {
			ItemStack entry = entries[i];
			NBTTagCompound entryTag = new NBTTagCompound();
			if (InventoryUtils.isEmpty(entry)) {
				entryTag.setBoolean("defined", false);
			} else {
				entryTag.setBoolean("defined", true);
				entry.writeToNBT(entryTag);
			}
			nbt.setTag("entry" + i, entryTag);
		}
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		// Basic Config
		excluding = nbt.getBoolean("excluding");
		int mode = nbt.getInteger("mode");
		this.mode = FilterMode.values()[MathHelper.clamp(mode, 0, FilterMode.values().length - 1)];
		checkNBT = nbt.getBoolean("checkNBT");
		checkMeta = nbt.getBoolean("checkMeta");
		// Filter Entries
		for (int i = 0; i < entries.length; i++) {
			NBTTagCompound entryTag = nbt.getCompoundTag("entry" + i);
			ItemStack entry;
			if (entryTag.getBoolean("defined")) {
				entry = new ItemStack(entryTag);
			} else {
				entry = ItemStack.EMPTY;
			}
			entries[i] = entry;
		}
	}

}
