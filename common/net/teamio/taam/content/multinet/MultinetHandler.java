package net.teamio.taam.content.multinet;

import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.util.ForgeDirection;
import net.teamio.taam.TaamMain;
import net.teamio.taam.multinet.MultinetUtil;

import org.lwjgl.opengl.GL11;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.render.TextureUtils;
import codechicken.lib.vec.Vector3;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Handler to draw overlays during placing of multinet cables
 * @author oliverkahrmann
 *
 */
public class MultinetHandler {

	@SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void drawBlockHighlight(DrawBlockHighlightEvent event)
    {
        if(event.currentItem != null && event.currentItem.getItem() == TaamMain.itemMultinetCable && 
                event.target != null && event.target.typeOfHit == MovingObjectType.BLOCK)
        {
        	ForgeDirection dir = ForgeDirection.getOrientation(event.target.sideHit);
            ForgeDirection dirOpp = dir.getOpposite();
            Vector3 localHit = new Vector3(event.target.hitVec).$minus(new Vector3(event.target.blockX, event.target.blockY, event.target.blockZ));
            
            if(MultinetUtil.canCableStay(event.player.worldObj,
            		event.target.blockX + dir.offsetX, event.target.blockY + dir.offsetY, event.target.blockZ + dir.offsetZ,
            		dirOpp)) {
            	GL11.glPushMatrix();
                RenderUtils.translateToWorldCoords(event.player, event.partialTicks);
                
                
                GL11.glPushMatrix();
                GL11.glTranslated(event.target.blockX+0.5, event.target.blockY+0.5, event.target.blockZ+0.5);
                GL11.glScaled(1.002, 1.002, 1.002);
                GL11.glTranslated(-0.5, -0.5, -0.5);
                
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDepthMask(false);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                
                CCRenderState.reset();
                TextureUtils.bindAtlas(0);
                CCRenderState.startDrawing();

                MultinetCable.render(event.player.worldObj,
                		new Vector3(dir.offsetX, dir.offsetY, dir.offsetZ),
                		1, dirOpp, MultinetUtil.getHitLayer(dirOpp, localHit), true);
                
                CCRenderState.draw();
                
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glDepthMask(true);
                GL11.glPopMatrix();
                
            GL11.glPopMatrix();
            }
				
        	
            
        }
    }
	
}
