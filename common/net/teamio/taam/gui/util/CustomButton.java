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
	
	public Drawable image;
	public int textPadding = 2;
	public boolean trimText = true;
	/**
	 * 0: Above
	 * 1: At Top
	 * 2: Center
	 * 3: At Bottom
	 * 4: Below
	 */
	public int textVerticalAlignment = 2;
	/**
	 * 0: Beside, Left
	 * 1: Left
	 * 2: Center
	 * 3: Right
	 * 4: Beside, Right
	 */
	public int textHorizontalAlignment = 2;
	
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

			int pressOffset = hoverState == 3 ? 1 : 0;
			
			if (displayString != null) {

				int color = 0xE0E0E0;

				if (packedFGColour != 0) {
					color = packedFGColour;
				} else if (!enabled) {
					color = 0xA0A0A0;
				} else if (hovered) {
					color = 0xFFFFA0;
				}

				String buttonText = displayString;
				int strWidth = mc.fontRendererObj.getStringWidth(buttonText);
				
				boolean doEllipsis = trimText && textHorizontalAlignment > 0 && textHorizontalAlignment < 4;
				
				if(doEllipsis) {
					int ellipsisWidth = mc.fontRendererObj.getStringWidth("...");

					if (strWidth > width - textPadding && strWidth > ellipsisWidth) {
						strWidth = width - textPadding - ellipsisWidth;
						buttonText = mc.fontRendererObj.trimStringToWidth(buttonText, strWidth).trim() + "...";
					}
				}
				
				int textHeight = 8;
				
				int leftOffset;
				int topOffset;
				
				switch(textHorizontalAlignment) {
				case 0:
					leftOffset = -strWidth - textPadding;
					break;
				case 1:
					leftOffset = textPadding;
					break;
				default:
				case 2:
					leftOffset = (width - strWidth) / 2;
					break;
				case 3:
					leftOffset = width - strWidth - textPadding;
					break;
				case 4:
					leftOffset = width + textPadding;
					break;
				}

				switch(textVerticalAlignment) {
				case 0:
					topOffset = -textHeight - textPadding;
					break;
				case 1:
					topOffset = textPadding;
					break;
				default:
				case 2:
					topOffset = (height - textHeight) / 2;
					break;
				case 3:
					topOffset = height - textHeight - textPadding;
					break;
				case 4:
					topOffset = height + textPadding;
					break;
				}

				drawString(mc.fontRendererObj, buttonText, xPosition + pressOffset + leftOffset, yPosition + pressOffset + topOffset, color);
			}
			
			if(image != null) {
				image.drawCentered(this, xPosition + width / 2 + pressOffset, yPosition + height / 2 + pressOffset);
			}
		}
	}

}
