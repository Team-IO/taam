package net.teamio.taam.conveyors.filters;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.INBTSerializable;
import net.teamio.taam.Log;
import net.teamio.taam.util.TaamUtil;

/**
 * Base interface for a single filter entry. Subclasses implement matching for
 * itemstacks, ore dictionary, mods, etc.
 * 
 * @author Oliver Kahrmann
 *
 */
public class FilterEntry implements INBTSerializable<NBTTagCompound> {

	/**
	 * Reference item stack. Can be null, then the filter entry will never
	 * match.
	 */
	public ItemStack stack;
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

	public static enum FilterMode {
		Exact, OreDict, Mod
	}

	public boolean isItemStackMatching(ItemStack stack) {
		if (stack == null) {
			Log.error("Matching null item stack. Result: false, this should not happen!");
			return false;
		}
		if (stack.getItem() == null) {
			Log.error("Matching null item in input. Result: false, this should not happen!");
			return false;
		}
		// Null Matching
		if (this.stack == null || this.stack.getItem() == null) {
			return false;
		}
		// Actual matching
		switch (mode) {
		case Exact:
			if (stack.getItem() != this.stack.getItem()) {
				return false;
			}
			if (checkMeta && stack.getMetadata() != this.stack.getMetadata()) {
				return false;
			}
			if (checkNBT && !ItemStack.areItemStackTagsEqual(stack, this.stack)) {
				return false;
			}
			return true;
		case Mod:
			return TaamUtil.isModMatch(stack, this.stack);
		case OreDict:
			return TaamUtil.isOreDictMatch(stack, this.stack);
		default:
			Log.error("Unknown filter mode {}. Result: false, this should not happen!", mode);
			return false;
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("mode", mode.ordinal());
		nbt.setBoolean("checkNBT", checkNBT);
		nbt.setBoolean("checkMeta", checkMeta);
		if (stack == null) {
			nbt.setBoolean("nullStack", true);
		} else {
			nbt.setBoolean("nullStack", false);
			stack.writeToNBT(nbt);
		}
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		int mode = nbt.getInteger("mode");
		this.mode = FilterMode.values()[MathHelper.clamp_int(mode, 0, FilterMode.values().length - 1)];
		checkNBT = nbt.getBoolean("checkNBT");
		checkMeta = nbt.getBoolean("checkMeta");
		if (nbt.getBoolean("nullStack")) {
			stack = null;
		} else {
			stack = ItemStack.loadItemStackFromNBT(nbt);
		}
	}
}
