package net.teamio.taam.gui.util;

import com.google.common.base.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.teamio.taam.gui.advanced.GuiAdvancedMachine;

public class CustomButton extends GuiButton {

	public CustomButton(int id, int xPos, int yPos, int width, int height,
			String displayString) {
		super(id, xPos, yPos, width, height, displayString);
	}

	public CustomButton(int id, int xPos, int yPos, String displayString) {
		super(id, xPos, yPos, baseTextureHeight, baseTextureHeight, displayString);
	}

	static final int baseTextureHeight = 16;
	static final int baseTextureU = 177;
	static final int baseTextureV = 0;
	boolean mouseDown = false;
	public Function<CustomButton, Boolean> eventHandler;

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		boolean inside = super.mousePressed(mc, mouseX, mouseY);
		mouseDown = inside;
		return inside;
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		mouseDown = false;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (visible) {
			hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width
					&& mouseY < yPosition + height;
			int hoverState = getHoverState(hovered);

			if (hoverState == 2 && mouseDown) {
				hoverState = 3;
			}

			GuiUtils.drawContinuousTexturedBox(GuiAdvancedMachine.guiTexture,
					xPosition, yPosition,//x y
					baseTextureU, baseTextureV + hoverState * baseTextureHeight,//u v
					width, height,//width height
					baseTextureHeight, baseTextureHeight, // texture width height
					3, zLevel);
			int color = 0xE0E0E0;

			if (packedFGColour != 0)
			{
				color = packedFGColour;
			}
			else if (!enabled)
			{
				color = 0xA0A0A0;
			}
			else if (hovered)
			{
				color = 0xFFFFA0;
			}

			String buttonText = displayString;
			int strWidth = mc.fontRendererObj.getStringWidth(buttonText);
			int ellipsisWidth = mc.fontRendererObj.getStringWidth("...");

			if (strWidth > width - 6 && strWidth > ellipsisWidth) {
				buttonText = mc.fontRendererObj.trimStringToWidth(buttonText, width - 6 - ellipsisWidth).trim() + "...";
			}

			drawCenteredString(mc.fontRendererObj, buttonText, xPosition + width / 2, yPosition + (height - 8) / 2, color);
		}
	}

}
