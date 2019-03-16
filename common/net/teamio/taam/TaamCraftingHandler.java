package net.teamio.taam;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.teamio.taam.util.InventoryUtils;

public class TaamCraftingHandler {

	@SubscribeEvent
	public void onCrafting(ItemCraftedEvent craftedEvent) {
		//ItemStack item = craftedEvent.crafting;
		IInventory craftMatrix = craftedEvent.craftMatrix;
		for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			ItemStack j = craftMatrix.getStackInSlot(i);
			if (isDamageCrafting(j)) {
				if (j.getItemDamage() + 1 < j.getItem().getMaxDamage()) {
					ItemStack k = new ItemStack(j.getItem(), 2, j.getItemDamage() + 1);
					craftMatrix.setInventorySlotContents(i, k);
				}
			}
		}
	}

	public static boolean isDamageCrafting(ItemStack item) {
		return !InventoryUtils.isEmpty(item) && item.getItem() == TaamMain.itemSaw;
	}
}
