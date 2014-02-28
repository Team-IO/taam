package founderio.taam.blocks.multinet;

import net.minecraft.util.EnumMovingObjectType;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.render.TextureUtils;
import codechicken.lib.vec.Vector3;
import codechicken.microblock.ItemMicroPartRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.taam.TaamMain;

public class MultinetHandler {

	@ForgeSubscribe
    @SideOnly(Side.CLIENT)
    public void drawBlockHighlight(DrawBlockHighlightEvent event)
    {
        if(event.currentItem != null && event.currentItem.getItem() == TaamMain.itemMultinetCable && 
                event.target != null && event.target.typeOfHit == EnumMovingObjectType.TILE)
        {
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
                CCRenderState.useNormals(true);
                CCRenderState.setBrightness(event.player.worldObj, event.target.blockX, event.target.blockY, event.target.blockZ);
                CCRenderState.setAlpha(80);
                CCRenderState.useModelColours(true);
                CCRenderState.startDrawing(7);

                ForgeDirection dir = ForgeDirection.getOrientation(event.target.sideHit);
                ForgeDirection dirOpp = dir.getOpposite();
                Vector3 localHit = new Vector3(event.target.hitVec).$minus(new Vector3(event.target.blockX, event.target.blockY, event.target.blockZ));
                
                MultinetCable.render(new Vector3(dir.offsetX, dir.offsetY, dir.offsetZ), null, 1, dirOpp.ordinal(), MultinetCable.getLayer(dirOpp, localHit));
                
                CCRenderState.draw();
                
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glDepthMask(true);
                GL11.glPopMatrix();
                
               event.setCanceled(true);
            GL11.glPopMatrix();
        }
    }
	
}
