package net.teamio.taam.gui.util;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;

public abstract class CustomGui extends GuiContainer {

	public CustomGui(Container container) {
		super(container);
	}
	
	public static String getTranslatedInventoryName(IInventory inventory) {
		if(inventory.hasCustomName()) {
			return inventory.getDisplayName().getFormattedText();
		} else {
			return I18n.format(inventory.getDisplayName().getFormattedText(), new Object[0]);
		}
	}
}
