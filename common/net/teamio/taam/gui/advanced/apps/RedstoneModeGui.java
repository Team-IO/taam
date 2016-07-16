package net.teamio.taam.gui.advanced.apps;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.gui.advanced.AppGui;
import net.teamio.taam.gui.advanced.GuiAdvancedMachine;
import net.teamio.taam.gui.util.Drawable;

public class RedstoneModeGui extends AppGui {

	public static final Drawable icon = new Drawable(
			new ResourceLocation("minecraft", "textures/items/redstone_dust.png"), 0, 0, 16, 16, 40, 40, 16, 16);

	private final RedstoneMode app;
	
	public RedstoneModeGui(RedstoneMode app) {
		this.app = app;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Drawable getIcon() {
		return icon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawBackground(GuiAdvancedMachine gui, float partialTicks, int mouseX, int mouseY) {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawForeground(GuiAdvancedMachine gui, int mouseX, int mouseY) {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onShow(GuiAdvancedMachine gui) {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onHide(GuiAdvancedMachine gui) {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initGui(GuiAdvancedMachine gui) {

	}

}
