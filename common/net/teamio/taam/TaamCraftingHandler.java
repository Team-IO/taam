package net.teamio.taam;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class TaamCraftingHandler {
	
	@SubscribeEvent
	public void onCrafting(ItemCraftedEvent craftedEvent) {
		//ItemStack item = craftedEvent.crafting;
		IInventory craftMatrix = craftedEvent.craftMatrix;
		for(int i=0; i < craftMatrix.getSizeInventory(); i++)
		{        	
	    	if(craftMatrix.getStackInSlot(i) != null)
	    	{
	    		ItemStack j = craftMatrix.getStackInSlot(i);
	    		if(j.getItem() != null && isDamageCrafting(j.getItem()))
	    		{
	    			if(j.getItemDamage() + 1 < j.getItem().getMaxDamage()) {
		    			ItemStack k = new ItemStack(j.getItem(), 2, (j.getItemDamage() + 1));
		    			craftMatrix.setInventorySlotContents(i, k);
	    			}
	    		}
	    	}  
		}
	}
	
	public static boolean isDamageCrafting(Item item) {
		return item == TaamMain.itemWrench;
	}
}
