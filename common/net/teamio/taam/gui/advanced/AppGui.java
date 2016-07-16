package net.teamio.taam.gui.advanced;

import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.gui.util.Drawable;

public abstract class AppGui extends Gui {

	@SideOnly(Side.CLIENT)
	public abstract void initGui(GuiAdvancedMachine gui);
	@SideOnly(Side.CLIENT)
	public abstract void onShow(GuiAdvancedMachine gui);
	@SideOnly(Side.CLIENT)
	public abstract void onHide(GuiAdvancedMachine gui);
	
	@SideOnly(Side.CLIENT)
	public abstract void drawBackground(GuiAdvancedMachine gui, float partialTicks, int mouseX, int mouseY);

	@SideOnly(Side.CLIENT)
	public abstract void drawForeground(GuiAdvancedMachine gui, int mouseX, int mouseY);

	@SideOnly(Side.CLIENT)
	public abstract Drawable getIcon();
}
