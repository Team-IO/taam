package net.teamio.taam.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Drawable {
	public final ResourceLocation texture;
	public final int u;
	public final int v;
	public final int width;
	public final int height;
	/**
	 * @param texture
	 * @param u
	 * @param v
	 * @param width
	 * @param height
	 */
	public Drawable(ResourceLocation texture, int u, int v, int width, int height) {
		super();
		this.texture = texture;
		this.u = u;
		this.v = v;
		this.width = width;
		this.height = height;
	}
	
	public void draw(Gui gui, int x, int y) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		gui.drawTexturedModalRect(x, y, u, v, width, height);
	}
	
	public void drawCentered(Gui gui, int x, int y) {
		draw(gui, x - width / 2, y - height / 2);
	}
	
}
