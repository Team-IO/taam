package net.teamio.taam.conveyors.filters;

import net.minecraft.item.ItemStack;

public interface IItemFilter {
	boolean isExcluding();
	boolean isItemStackMatching(ItemStack stack);
}
