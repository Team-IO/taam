package net.teamio.taam.rendering;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by oliver on 1/1/17.
 */
public final class HoloGui {
	public static final HoloGui INSTANCE = new HoloGui();

	public static final int TEX_WIDTH = 128;
	public static final int PIECE_WIDTH = 32;
	/**
	 * U/V offset for a single piece. Calculated using {@link #PIECE_WIDTH}/{@link #TEX_WIDTH}.
	 */
	public static final float SINGLE_UV = PIECE_WIDTH / (float)TEX_WIDTH;
	public static final ResourceLocation holoGuiTexture = new ResourceLocation("taam", "textures/gui/holo_gui.png");

	private HoloGui() {

	}

	/**
	 * Load hook called from client proxy
	 */
	public void load() {

	}

	/**
	 * Render a holo for top-down (or bottom-up) perspective.
	 * @param player Player entity for offset rendering while rendering overlays. Pass null to skip, for static rendering.
	 * @param partialTicks Partial ticks for offset rendering. Only used if player entity is given.
	 * @param textureManager Texture manager for texture binding. Get e.g. via renderEngine field in RenderGlobal or TileEntityRendererDispatcher.
	 * @param hx Holo texture position X (multiplied with {@link #SINGLE_UV})
	 * @param hy
	 * @param yPos
	 */
	public void renderHoloTopDown(EntityPlayer player, float partialTicks, TextureManager textureManager, int hx, int hy, float yPos) {
		// Set up GL state
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();

		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1, 1, 1, 1);

		// Offset rendering while rendering overlays
		if(player != null) {
			double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
			double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
			double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

			GL11.glTranslated(-d0, -d1, -d2);
		}

		// Set up rendering
		VertexBuffer renderer = Tessellator.getInstance().getBuffer();

		renderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		textureManager.bindTexture(holoGuiTexture);

		// Render the 2 quads
		float minU = hx * SINGLE_UV;
		float minV = hy * SINGLE_UV;
		float maxU = minU + SINGLE_UV;
		float maxV = minV + SINGLE_UV;

		renderer.pos(0,yPos,0).tex(minU,minV).normal(0,1,0).endVertex();
		renderer.pos(1,yPos,0).tex(maxU,minV).normal(0,1,0).endVertex();
		renderer.pos(1,yPos,1).tex(maxU,maxV).normal(0,1,0).endVertex();
		renderer.pos(0,yPos,1).tex(minU,maxV).normal(0,1,0).endVertex();

		Tessellator.getInstance().draw();

		// Tear down GL state
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();

		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}
}
