package net.teamio.taam.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class Drawable {
	public final ResourceLocation texture;
	public final int u;
	public final int v;
	public final int uWidth;
	public final int uHeight;
	public final int drawWidth;
	public final int drawHeight;
	public final int textureWidth;
	public final int textureHeight;
	/**
	 * @param texture
	 * @param u
	 * @param v
	 * @param width
	 * @param height
	 */
	public Drawable(ResourceLocation texture, int u, int v, int width, int height) {
		this.texture = texture;
		this.u = u;
		this.v = v;
		this.uWidth = width;
		this.uHeight = height;
		this.drawWidth = width;
		this.drawHeight = height;
		this.textureWidth = 256;
		this.textureHeight = 256;
	}
	
	public Drawable(ResourceLocation texture, int u, int v, int uWidth, int wHeight, int drawWidth, int drawHeight) {
		this.texture = texture;
		this.u = u;
		this.v = v;
		this.uWidth = uWidth;
		this.uHeight = wHeight;
		this.drawWidth = drawWidth;
		this.drawHeight = drawHeight;
		this.textureWidth = 256;
		this.textureHeight = 256;
	}
	
	public Drawable(ResourceLocation texture, int u, int v, int uWidth, int wHeight, int drawWidth, int drawHeight, int textureWidth, int textureHeight) {
		this.texture = texture;
		this.u = u;
		this.v = v;
		this.uWidth = uWidth;
		this.uHeight = wHeight;
		this.drawWidth = drawWidth;
		this.drawHeight = drawHeight;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}
	
	public void draw(Gui gui, int x, int y) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
//		Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, width, height, drawWidth, drawHeight);
		Gui.drawScaledCustomSizeModalRect(x, y, u, v, uWidth, uHeight, drawWidth, drawHeight, textureWidth, textureHeight);
	}
	
	public void drawCentered(Gui gui, int x, int y) {
		draw(gui, x - uWidth / 2, y - uHeight / 2);
	}
	
}
