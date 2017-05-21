package net.teamio.taam.rendering;

import com.google.common.base.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

/**
 * Created by oliver on 1/1/17.
 */
public final class RenderUtil {
	/**
	 * Function for fetching texture sprites.
	 */
	public static final Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
		@Override
		public TextureAtlasSprite apply(ResourceLocation location) {
			return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
		}
	};

	private RenderUtil() {
		// Util class
	}

	/**
	 * Draw a single selection box at the given bounding box.
	 *
	 * @param player
	 * @param partialTicks
	 * @param box
	 */
	public static void drawSelectionBoundingBox(EntityPlayer player, float partialTicks, AxisAlignedBB box) {
		drawSelectionBoundingBox(player, partialTicks, 2.0f, 0, 0, 0, 0.4f, box);
	}

	/**
	 * Draw a single selection box at the given bounding box.
	 *
	 * @param player
	 * @param partialTicks
	 * @param lineWidth
	 * @param box
	 */
	public static void drawSelectionBoundingBox(EntityPlayer player, float partialTicks, float lineWidth, AxisAlignedBB box) {
		drawSelectionBoundingBox(player, partialTicks, lineWidth, 0, 0, 0, 0.4f, box);
	}

	/**
	 * Draw a single selection box at the given bounding box.
	 *
	 * @param player
	 * @param partialTicks
	 * @param lineWidth
	 * @param colorR
	 * @param colorG
	 * @param colorB
	 * @param colorA
	 * @param box
	 */
	public static void drawSelectionBoundingBox(EntityPlayer player, float partialTicks, float lineWidth, float colorR, float colorG, float colorB, float colorA, AxisAlignedBB box) {
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();

		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(colorR, colorG, colorB, colorA);
		GL11.glLineWidth(lineWidth);
		// For whatever reason, we need to enable, THEN disable. Otherwise color gets somewhat garbled..
		GlStateManager.enableTexture2D();
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);

		double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

		GL11.glTranslated(-d0, -d1, -d2);

		RenderGlobal.drawSelectionBoundingBox(box.expand(TaamRenderer.boundingBoxExpand, TaamRenderer.boundingBoxExpand, TaamRenderer.boundingBoxExpand));

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();

		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}

	/**
	 * Set up default GL flags for rendering.
	 *
	 * Remember to use {@link #tearDownDefaultGL()} after rendering.
	 */
	public static void setupDefaultGL() {
		GlStateManager.pushAttrib();

		RenderHelper.enableStandardItemLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableAlpha();
		GlStateManager.color(1, 1, 1, 1);
	}

	/**
	 * Restore previous GL flags, to not disturb other renderers.
	 *
	 * Remember to use {@link #setupDefaultGL()} before rendering.
	 */
	public static void tearDownDefaultGL() {
		// Just paranoid:
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		RenderHelper.disableStandardItemLighting();

		GlStateManager.popAttrib();
	}

	public static class ObjFace {
		public int[] vertexIndexes;
		public int[] textureIndexes;
		public int normalIndex;
		/**
		 * @param vertexIndexes
		 * @param textureIndexes
		 * @param normalIndex
		 */
		public ObjFace(int[] vertexIndexes, int[] textureIndexes, int normalIndex) {
			this.vertexIndexes = vertexIndexes;
			this.textureIndexes = textureIndexes;
			this.normalIndex = normalIndex;
		}

	}
}
