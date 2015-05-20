package founderio.taam.client.gui.util;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;

public abstract class CustomGui extends GuiContainer {

	public CustomGui(Container container) {
		super(container);
	}

	public static void drawTexturedModalRect(int x, int y, int width, int height, float zIndex) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x,			y + height,	zIndex, 0, 1);
		tessellator.addVertexWithUV(x + width,	y + height,	zIndex, 1, 1);
		tessellator.addVertexWithUV(x + width,	y,			zIndex, 1, 0);
		tessellator.addVertexWithUV(x,			y,			zIndex, 0, 0);
		tessellator.draw();
	}

	protected void drawTexturedModalRect(int x, int y, int width, int height) {
		drawTexturedModalRect(x, y, width, height, this.zLevel);
	}

	public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, double zLevel) {
		float f = 1f / textureWidth;
		float f1 = 1f / textureHeight;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x,			y + height,	zLevel, u * f,				(v + height) * f1);
		tessellator.addVertexWithUV(x + width,	y + height,	zLevel, (u + width) * f,	(v + height) * f1);
		tessellator.addVertexWithUV(x + width,	y,			zLevel, (u + width) * f,	v * f1);
		tessellator.addVertexWithUV(x,			y,			zLevel, u * f,				v * f1);
		tessellator.draw();
	}

	protected void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
		drawTexturedModalRect(x, y, u, v, width, height, textureWidth, textureHeight, this.zLevel);
	}
	
	public static String getTranslatedInventoryName(IInventory inventory) {
		if(inventory.hasCustomInventoryName()) {
			return inventory.getInventoryName();
		} else {
			return I18n.format(inventory.getInventoryName(), new Object[0]);
		}
	}
}
