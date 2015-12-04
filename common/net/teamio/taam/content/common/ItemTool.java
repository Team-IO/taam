package net.teamio.taam.content.common;

import net.minecraft.item.Item;
import net.teamio.taam.Taam;
import net.teamio.taam.Taam.ITEM_TOOL_META;

public class ItemTool extends Item {
	
	public ItemTool(ITEM_TOOL_META type) {
		super();
		setMaxStackSize(1);
		setMaxDamage(256);
		setTextureName(Taam.MOD_ID + ":tool" + type.name());
	}
}
