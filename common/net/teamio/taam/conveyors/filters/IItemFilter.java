package net.teamio.taam.conveyors.filters;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IItemFilter extends INBTSerializable<NBTTagCompound> {
	/**
	 * Determine if the filter is in exclude mode. Utilization is up to the
	 * caller and not to the filter.
	 * 
	 * @return
	 */
	boolean isExcluding();

	/**
	 * Does the given item match the filter? Should NOT take exclude mode into
	 * consideration. That is up to the caller.
	 * 
	 * @param stack
	 * @return true, if the item stack matches any of the entries, regardless of
	 *         exclude mode.
	 */
	boolean isItemStackMatching(ItemStack stack);
}
