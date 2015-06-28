package net.teamio.taam.conveyors.api;

import net.minecraft.item.ItemStack;

public interface IItemFilter {
	boolean isExcluding();
	boolean isItemStackMatching(ItemStack stack);
}
