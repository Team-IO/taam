package net.teamio.taam.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.teamio.taam.gui.advanced.GuiAdvancedMachine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public class CustomButton extends GuiButton {

	private static final int baseTextureSize = 16;
	private static final int baseTextureU = 177;
	private static final int baseTextureV = 0;
	private static final int textPadding = 2;

	private boolean mouseDown;
	public Function<CustomButton, Boolean> eventHandler;

	public Drawable image;
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

	public CustomButton(int id, int xPos, int yPos, int width, int height, @Nullable String displayString) {
		super(id, xPos, yPos, width, height, displayString);
	}

	public CustomButton(int id, int xPos, int yPos, String displayString) {
		super(id, xPos, yPos, baseTextureSize, baseTextureSize, displayString);
	}

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
	public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (!visible) {
			return;
		}
		hovered = mouseX >= x && mouseY >= y && mouseX < x + width
				&& mouseY < y + height;
		int hoverState = getHoverState(hovered);

		if (hoverState == 2 && mouseDown) {
			hoverState = 3;
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GuiUtils.drawContinuousTexturedBox(GuiAdvancedMachine.guiTexture,
				x, y,//x y
				baseTextureU, baseTextureV + hoverState * baseTextureSize,//u v
				width, height,//width height
				baseTextureSize, baseTextureSize, // texture width height
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
			int strWidth = mc.fontRenderer.getStringWidth(buttonText);

			boolean doEllipsis = trimText && textHorizontalAlignment > 0 && textHorizontalAlignment < 4;

			if (doEllipsis) {
				int ellipsisWidth = mc.fontRenderer.getStringWidth("...");

				if (strWidth > width - textPadding && strWidth > ellipsisWidth) {
					strWidth = width - textPadding - ellipsisWidth;
					buttonText = mc.fontRenderer.trimStringToWidth(buttonText, strWidth).trim() + "...";
				}
			}

			int textHeight = 8;

			int leftOffset;
			int topOffset;

			switch (textHorizontalAlignment) {
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

			switch (textVerticalAlignment) {
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

			drawString(mc.fontRenderer, buttonText, x + pressOffset + leftOffset, y + pressOffset + topOffset, color);
		}

		if (image != null) {
			image.drawCentered(x + width / 2 + pressOffset, y + height / 2 + pressOffset);
		}
	}

}
