package net.teamio.taam.content.common;

//import mcmultipart.item.IItemSaw;
import net.minecraft.item.Item;
import net.teamio.taam.Taam.ITEM_TOOL_META;

public class ItemTool extends Item/* implements IItemSaw*/ {

	public final ITEM_TOOL_META type;

	public ItemTool(ITEM_TOOL_META type) {
		this.type = type;
		setMaxStackSize(1);
		setMaxDamage(256);
		setFull3D();
	}

	/*@Override
	public int getSawStrength(ItemStack stack) {
		return 2;
	}*/
}
