package net.teamio.taam.rendering;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.TextureCoordinate;
import net.minecraftforge.client.model.obj.Vertex;
import net.minecraftforge.client.model.obj.WavefrontObject;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamClientProxy;
import net.teamio.taam.TaamMain;

public class TaamBlockRenderer implements ISimpleBlockRenderingHandler {

	public final WavefrontObject modelBlocks;
	
	public TaamBlockRenderer() {
		modelBlocks = new CachingWavefrontObject(new ResourceLocation(Taam.MOD_ID + ":models/blocks.obj"));
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		if(block == TaamMain.blockSupportBeam) {
			Tessellator.getInstance().startDrawingQuads();
			renderSupportBeam(block, 0, -1f/8, 0);
			Tessellator.getInstance().draw();
		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
			RenderBlocks renderer) {
		if(block == TaamMain.blockSupportBeam) {

			Tessellator.getInstance().setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
			renderSupportBeam(block, x, y, z);
			return true;
		}
		return false;
	}
	
	public void renderSupportBeam(Block block, float x, float y, float z) {
		IIcon icon = block.getBlockTextureFromSide(0);
		
		float widthFactorV = icon.getMaxV() - icon.getMinV();
		float widthFactorZ = icon.getMaxU() - icon.getMinU();
		
		Tessellator.getInstance().addTranslation(x, y, z);
		Tessellator.getInstance().setColorRGBA(255, 255, 255, 255);
		GroupObject go = modelBlocks.groupObjects.get(0);
		
		for(int f = 0; f < go.faces.size(); f++) {
			Face face = go.faces.get(f);
			for(int i = 0; i < face.vertices.length; i++) {
				Vertex vert = face.vertices[i];
				TextureCoordinate tex = face.textureCoordinates[i];
				Tessellator.getInstance().addVertexWithUV(vert.x, vert.y, vert.z, icon.getMinU() + tex.u * widthFactorZ, icon.getMinV() + tex.v * widthFactorV);
			}
		}
		
		
		Tessellator.getInstance().addTranslation(-x, -y, -z);
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return TaamClientProxy.blockRendererId;
	}

}
