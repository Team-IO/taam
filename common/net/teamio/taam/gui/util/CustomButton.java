package net.teamio.taam.gui.util;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiUtils;

public class CustomButton extends GuiButton {
	
	public CustomButton(int id, int xPos, int yPos, int width, int height,
			String displayString) {
		super(id, xPos, yPos, width, height, displayString);
	}

	public CustomButton(int id, int xPos, int yPos, String displayString) {
		super(id, xPos, yPos, baseTextureHeight, baseTextureHeight, displayString);
	}

    static final int baseTextureHeight = 16;
    static final int baseTextureU = 342;
    static final int baseTextureV = 0;
	boolean mouseDown = false;
	public Function<CustomButton, Boolean> eventHandler;
	
	@Override
	public boolean mousePressed(Minecraft mc, int mouseX,
			int mouseY) {
		boolean inside = super.mousePressed(mc, mouseX, mouseY);
		mouseDown = inside;
		return inside;
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		mouseDown = false;
	}
	
	@Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int k = this.getHoverState(this.hovered);
            
            if(k == 2 && mouseDown) {
            	k = 3;
            }
            
            int bthHalf = baseTextureHeight/2;

			GL11.glEnable(GL11.GL_BLEND);
            // For some reason all sizes here have to be halved.
			// FIXME: We need a texture here. Central GUI Texture would be good I guess..
            GuiUtils.drawContinuousTexturedBox(null,
            		this.xPosition, this.yPosition,//x y
            		baseTextureU / 2, baseTextureV / 2 + k * bthHalf,//u v
            		width, height,//width height
            		bthHalf, bthHalf, // texture width height
            		2, 2, 2, 2, this.zLevel);
			GL11.glDisable(GL11.GL_BLEND);
            this.mouseDragged(mc, mouseX, mouseY);
            int color = 0xE0E0E0;
            
            if (packedFGColour != 0)
            {
                color = packedFGColour;
            }
            else if (!this.enabled)
            {
                color = 0xA0A0A0;
            }
            else if (this.hovered)
            {
                color = 0xFFFFA0;
            }
            
            String buttonText = this.displayString;
            int strWidth = mc.fontRendererObj.getStringWidth(buttonText);
            int ellipsisWidth = mc.fontRendererObj.getStringWidth("...");
            
            if (strWidth > width - 6 && strWidth > ellipsisWidth)
                buttonText = mc.fontRendererObj.trimStringToWidth(buttonText, width - 6 - ellipsisWidth).trim() + "...";
            
            this.drawCenteredString(mc.fontRendererObj, buttonText, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, color);
        }
    }

}
