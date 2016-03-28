package net.teamio.taam.content;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface IRenderableItem {
	public List<String> getVisibleParts(ItemStack stack);
}
