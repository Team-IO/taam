package net.teamio.taam.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.teamio.taam.Config;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.content.conveyors.TileEntityConveyorElevator;
import net.teamio.taam.content.conveyors.TileEntityConveyorItemBag;
import net.teamio.taam.content.conveyors.TileEntityConveyorProcessor;
import net.teamio.taam.content.conveyors.TileEntityConveyorSieve;
import net.teamio.taam.content.conveyors.TileEntityConveyorTrashCan;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.IConveyorSlots;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.appliances.ApplianceAligner;
import net.teamio.taam.util.InventoryUtils;
import net.teamio.taam.util.WrenchUtil;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import java.util.Random;
import java.util.function.Function;

public class TaamRenderer extends TileEntitySpecialRenderer<TileEntity> {

	/**
	 * Item Renderer for the conveyors. Cached from the minecraft instance for
	 * easy access.
	 */
	public static final RenderItem ri = Minecraft.getMinecraft().getRenderItem();

	/**
	 * Rotation counter, currently only used for calculating
	 * {@link TaamRenderer#rotSin}.
	 */
	private float rot;
	/**
	 * Rotation counter, currently unused, was used for blinking the motion
	 * sensor light.
	 */
	private float rot_sensor;
	/**
	 * sin(rot), used for animating the conveyor sieve.
	 */
	public static double rotSin;

	/**
	 * Value used for expanding the rendered selection boxes below. Value is
	 * from Vanilla source.
	 */
	public static final double boundingBoxExpand = 0.0020000000949949026D;
	public static final float itemScaleFactor = 0.3f;

	public static final ResourceLocation conveyorTextures = new ResourceLocation("Taam", "blocks/conveyor");

	/**
	 * This field is used to stop rendering block highlight operations on the first exception.
	 * This is used to prevent the game being unresponsive due to continuous exceptions being written to the log.
	 */
	public static boolean failureFreeBlockHighlight = true;

	/**
	 * Function for fetching texture sprites.
	 */
	public static final Function<ResourceLocation, TextureAtlasSprite> textureGetter = location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());

	/*
	 * Vertex infos from exported .obj file
	 * These are used in renderSieveMesh
	 */
	final Vector3f[] sieve_vertices = new Vector3f[]{
			new Vector3f(0.9375f, 0.49f, 0.0625f),
			new Vector3f(0.9375f, 0.52f, 0.0625f),
			new Vector3f(0.0625f, 0.49f, 0.0625f),
			new Vector3f(0.5000f, 0.49f, 0.0625f),
			new Vector3f(0.0625f, 0.52f, 0.0625f),
			new Vector3f(0.5000f, 0.52f, 0.0625f),
			new Vector3f(0.9375f, 0.45f, 0.9375f),
			new Vector3f(0.9375f, 0.47f, 0.5000f),
			new Vector3f(0.9375f, 0.50f, 0.5000f),
			new Vector3f(0.9375f, 0.48f, 0.9375f),
			new Vector3f(0.0625f, 0.45f, 0.9375f),
			new Vector3f(0.0625f, 0.47f, 0.5000f),
			new Vector3f(0.5000f, 0.45f, 0.9375f),
			new Vector3f(0.5000f, 0.47f, 0.5000f),
			new Vector3f(0.5000f, 0.50f, 0.5000f),
			new Vector3f(0.0625f, 0.50f, 0.5000f),
			new Vector3f(0.0625f, 0.48f, 0.9375f),
			new Vector3f(0.5000f, 0.48f, 0.9375f)
	};
	final Vector2f[] sieve_tex = new Vector2f[]{
			new Vector2f(0.000000f, 0.359375f),
			new Vector2f(0.058594f, 0.359375f),
			new Vector2f(0.058594f, 0.417969f),
			new Vector2f(0.000000f, 0.417969f),
			new Vector2f(0.003906f, 0.355469f),
			new Vector2f(0.003906f, 0.417969f),
			new Vector2f(0.000000f, 0.355469f),
			new Vector2f(0.000000f, 0.414062f),
			new Vector2f(0.062500f, 0.414062f),
			new Vector2f(0.062500f, 0.417969f)
	};
	final Vector3f[] sieve_normals = new Vector3f[]{
			new Vector3f(0.000000f, -0.99900f, -0.0457f),
			new Vector3f(0.000000f, 0.000000f, -1.00000f),
			new Vector3f(0.000000f, 0.999000f, 0.045700f),
			new Vector3f(1.000000f, 0.000000f, 0.000000f),
			new Vector3f(-1.00000f, 0.00000f, 0.00000f),
			new Vector3f(0.000000f, 0.000000f, 1.000000f)
	};
	final ObjFace[] sieve_faces = new ObjFace[]{
			new ObjFace(new int[]{8, 14, 4, 1}, new int[]{1, 2, 3, 4}, 1),
			new ObjFace(new int[]{1, 4, 6, 2}, new int[]{5, 6, 4, 7}, 2),
			new ObjFace(new int[]{9, 2, 6, 15}, new int[]{1, 4, 3, 2}, 3),
			new ObjFace(new int[]{8, 1, 2, 9}, new int[]{8, 9, 10, 4}, 4),
			new ObjFace(new int[]{12, 3, 4, 14}, new int[]{1, 4, 3, 2}, 1),
			new ObjFace(new int[]{3, 5, 6, 4}, new int[]{5, 7, 4, 6}, 2),
			new ObjFace(new int[]{16, 15, 6, 5}, new int[]{1, 2, 3, 4}, 3),
			new ObjFace(new int[]{12, 16, 5, 3}, new int[]{8, 4, 10, 9}, 5),
			new ObjFace(new int[]{8, 7, 13, 14}, new int[]{1, 4, 3, 2}, 1),
			new ObjFace(new int[]{7, 10, 18, 13}, new int[]{5, 7, 4, 6}, 6),
			new ObjFace(new int[]{9, 15, 18, 10}, new int[]{1, 2, 3, 4}, 3),
			new ObjFace(new int[]{8, 9, 10, 7}, new int[]{8, 4, 10, 9}, 4),
			new ObjFace(new int[]{12, 14, 13, 11}, new int[]{1, 2, 3, 4}, 1),
			new ObjFace(new int[]{11, 13, 18, 17}, new int[]{5, 6, 4, 7}, 6),
			new ObjFace(new int[]{16, 17, 18, 15}, new int[]{1, 4, 3, 2}, 3),
			new ObjFace(new int[]{12, 11, 17, 16}, new int[]{8, 9, 10, 4}, 5)
	};

	/**
	 * Holder for static object faces.
	 * This is used for dynamic rendering of object models.
	 * The values are extracted from an exported .obj file.
	 */
	public static class ObjFace {
		public final int[] vertexIndexes;
		public final int[] textureIndexes;
		public final int normalIndex;

		/**
		 * @param vertexIndexes  The sequence of vertex indices in render order. Should have a length of 4, matching to textureIndices.
		 * @param textureIndexes The sequence of texture indices in render order. Should have a length of 4, matching to vertexIndexes.
		 * @param normalIndex    The normal index for this face, to be looked up in an associated array of normal vectors.
		 */
		public ObjFace(@Nonnull int[] vertexIndexes, @Nonnull int[] textureIndexes, int normalIndex) {
			this.vertexIndexes = vertexIndexes;
			this.textureIndexes = textureIndexes;
			this.normalIndex = normalIndex;
		}

	}

	/**
	 * Executed each client tick to update the animated values. Client tick,
	 * because that is fixed timing, so not frame rate dependent.
	 *
	 * @param event client tick event, called once per game tick
	 */
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			rot++;
			rot_sensor++;
			if (rot_sensor > 360) {
				rot_sensor -= 360;
			}
			rotSin = Math.sin(Math.toRadians(rot * 32));
		}
	}

	/**
	 * Draw custom highlight boxes on conveyor machines with default movement.
	 * (e.g. conveyors themselves or the conveyor sieve)
	 * <p>
	 * The highlight box will be drawn around the slot aimed at.
	 *
	 * @param event draw block highlight event, called when the user directly looks at a block
	 */
	@SubscribeEvent
	public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
		// If we crash, turn of Block Highlight drawing to prevent getting locked out of a world
		if (!failureFreeBlockHighlight) {
			return;
		}
		RayTraceResult target = event.getTarget();
		BlockPos pos;
		if (target != null && (pos = target.getBlockPos()) != null) {
			EntityPlayer player = event.getPlayer();
			boolean playerHasDebugTool = WrenchUtil.playerHoldsDebugTool(player);
			World world = player.world;
			TileEntity te = world.getTileEntity(pos);

			try {
				if (playerHasDebugTool) {
					TaamRendererDebug.renderTEDebug(te, player, target.sideHit, event.getPartialTicks());
				}

				if (target.sideHit == EnumFacing.UP) {
					// TODO: extract
					IConveyorSlots cte = ConveyorUtil.getSlots(te, null);
					if (cte != null) {

						// Only render for TEs that actually have the items there
						if (!cte.shouldRenderItemsDefault()) {
							return;
						}

						Vec3d hitVec = target.hitVec;
						int slot = ConveyorUtil.getSlotForRelativeCoordinates(hitVec.xCoord - pos.getX(),
								hitVec.zCoord - pos.getZ());


						ItemWrapper wrapper = cte.getSlot(slot);

						EnumFacing dir = cte.getNextSlot(slot);

						// General Position of the slot
						double x = pos.getX() + Math.floor(slot / 3f) * ConveyorUtil.oneThird;
						double y = pos.getY() + cte.getVerticalPosition(slot);
						double z = pos.getZ() + slot % 3 * ConveyorUtil.oneThird;


						if (wrapper.isEmpty() && !InventoryUtils.isEmpty(player.getHeldItemMainhand())) {
							drawSelectionBoundingBox(player, event.getPartialTicks(), 4, 1, 1, 1, 1, new AxisAlignedBB(x, y, z,
									x + ConveyorUtil.oneThird, y + 0.1d, z + ConveyorUtil.oneThird));
						} else {
							drawSelectionBoundingBox(player, event.getPartialTicks(), 2, 1, 1, 1, 1, new AxisAlignedBB(x, y, z,
									x + ConveyorUtil.oneThird, y + 0.1d, z + ConveyorUtil.oneThird));
						}

						if (playerHasDebugTool) {
							drawSlotInfo(player, x, y, z, slot, cte.getMovementDirection(), event.getPartialTicks());
						}

						if (!wrapper.isEmpty()) {
							float progress = wrapper.movementProgress;

							if (wrapper.isRenderingInterpolated()) {
								progress += event.getPartialTicks();
							} else {
								// Interpolation since last frame already advanced to almost 1, so we prevent stutter by "skipping ahead"
								progress += 1;
							}

							progress *= ConveyorUtil.oneThird / cte.getSpeedsteps();

							// Apply movement of the item
							x += dir.getFrontOffsetX() * progress;
							z += dir.getFrontOffsetZ() * progress;
							y += dir.getFrontOffsetY() * progress * 3;

							drawSelectionBoundingBox(player, event.getPartialTicks(), 4, 1, 1, 1, 1, new AxisAlignedBB(x, y, z,
									x + ConveyorUtil.oneThird, y + ConveyorUtil.oneThird, z + ConveyorUtil.oneThird));
						}

					}
				}

			} catch (Exception e) {
				Log.error("Error drawing block highlight for a tile entity. Disabling block highlight drawing to prevent you from crashing - This is an error, please report!", e);
				failureFreeBlockHighlight = false;
			}
		}
	}

	private void drawSlotInfo(EntityPlayer player, double x, double y, double z, int slot, EnumFacing direction, float partialTicks) {
		int lane = ConveyorUtil.LANES.get(slot, direction);
		int row = ConveyorUtil.ROWS.get(slot, direction);
		String text1 = String.format("Slot: %d %s", slot, direction.toString());
		String text2 = String.format("Row: %d", row);
		String text3 = String.format("Lane: %d", lane);
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();


		double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

		GL11.glTranslated(-d0, -d1, -d2);

		GlStateManager.translate(x + 1 / 6f, y + 1 / 3f, z + 1 / 6f);

		GlStateManager.rotate(180, 0, 1, 0);


		float rotation = (float) Math.floor((player.rotationYaw + 45) / 90f) * 90;

		GlStateManager.rotate(-rotation, 0, 1, 0);

		GlStateManager.translate(-1 / 6f, 0, 0);

		GlStateManager.rotate(90, 1, 0, 0);

		GlStateManager.scale(0.01, 0.01, 0.01);
		// Edge case: sometimes highlighting kicks in before this is filled, it seems
		if (this.getFontRenderer() != null) {
			this.getFontRenderer().drawString(text1, 0, -10, 0xFFFF00);
			this.getFontRenderer().drawString(text2, 0, 0, 0xFFFF00);
			this.getFontRenderer().drawString(text3, 0, 10, 0xFFFF00);
		}

		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
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
		GL11.glLineWidth(lineWidth);
		// For whatever reason, we need to enable, THEN disable. Otherwise color gets somewhat garbled..
		GlStateManager.enableTexture2D();
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);

		double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

		GL11.glTranslated(-d0, -d1, -d2);

		RenderGlobal.drawSelectionBoundingBox(box.expand(boundingBoxExpand, boundingBoxExpand, boundingBoxExpand), colorR, colorG, colorB, colorA);

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();

		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {

		if (Config.render_tank_content) {
			TankRenderInfo[] tankRI = te.getCapability(Taam.CAPABILITY_RENDER_TANK, null);

			if (tankRI != null) {
				GL11.glPushMatrix();
				GL11.glTranslated(x, y, z);

				float rotationDegrees = RenderUtil.getRotationDegrees(te);

				GL11.glTranslated(.5f, .5f, .5f);
				GL11.glRotatef(rotationDegrees, 0, 1, 0);
				GL11.glTranslated(-.5f, -.5f, -.5f);

				for (TankRenderInfo renderInfo : tankRI) {
					renderTankContent(renderInfo.fluid, renderInfo.capacity, renderInfo.bounds);
				}
				GL11.glPopMatrix();
			}
		}

		IConveyorSlots conveyorSlots = te.getCapability(Taam.CAPABILITY_CONVEYOR, EnumFacing.UP);
		if (conveyorSlots != null) {
			boolean oscillate = false;

			if (te instanceof TileEntityConveyorSieve) {
				oscillate = !((TileEntityConveyorSieve) te).isShutdown;
			}

			if (Config.render_items) {
				renderConveyorItems(conveyorSlots, x, y, z, partialTicks, oscillate);
			}
		}

		if (Config.render_items && te instanceof TileEntityConveyorProcessor) {
			TileEntityConveyorProcessor processor = (TileEntityConveyorProcessor) te;
			ItemStack processingStack = processor.getRenderStack();

			if (!InventoryUtils.isEmpty(processingStack)) {
				IBakedModel model = ri.getItemModelMesher().getItemModel(processingStack);

				GL11.glPushMatrix();
				GL11.glTranslated(x, y, z);

				setupDefaultGL();


				bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

				/*
				 * Get Rotation
				 */

				float rotationDegrees = RenderUtil.getRotationDegrees(te);

				GL11.glTranslatef(0.5f, 0.4f, 0.5f);

				/*
				 * Rotate the items
				 */

				GL11.glRotatef(rotationDegrees, 0, 1, 0);

				/*
				 * Shaking inside the processor
				 */
				if (!processor.isShutdown) {
					Random rand = processor.getWorld().rand;
					GL11.glTranslatef(
							0.015f * (1 - rand.nextFloat()),
							0.025f * (1 - rand.nextFloat()),
							0.015f * (1 - rand.nextFloat()));
				}
				GL11.glScalef(itemScaleFactor, itemScaleFactor, itemScaleFactor);

				ri.renderItem(processingStack, model);

				tearDownDefaultGL();

				GL11.glPopMatrix();
			}
		}

		if (te instanceof TileEntityConveyorSieve) {
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);

			float rotationDegrees = RenderUtil.getRotationDegrees(te);

			GL11.glTranslated(.5f, .5f, .5f);
			GL11.glRotatef(rotationDegrees, 0, 1, 0);
			GL11.glTranslated(-.5f, -.5f, -.5f);

			renderSieveMesh(((TileEntityConveyorSieve) te).isShutdown);

			GL11.glPopMatrix();
		}

		if (te instanceof TileEntityConveyorItemBag) {
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);

			float rotationDegrees = RenderUtil.getRotationDegrees(te);

			GL11.glTranslated(.5f, .5f, .5f);
			GL11.glRotatef(rotationDegrees, 0, 1, 0);
			GL11.glTranslated(-.5f, -.5f, -.5f);

			float fillFactor = ((TileEntityConveyorItemBag) te).fillPercent;

			renderBagFilling(fillFactor);

			GL11.glPopMatrix();
		}

		if (te instanceof TileEntityConveyorTrashCan) {
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);

			float rotationDegrees = RenderUtil.getRotationDegrees(te);

			GL11.glTranslated(.5f, .5f, .5f);
			GL11.glRotatef(rotationDegrees, 0, 1, 0);
			GL11.glTranslated(-.5f, -.5f, -.5f);

			float fillFactor = ((TileEntityConveyorTrashCan) te).fillLevel / Config.pl_trashcan_maxfill;

			renderBagFilling(fillFactor);

			GL11.glPopMatrix();
		}

		if (te instanceof ApplianceAligner) {


			ApplianceAligner aligner = (ApplianceAligner) te;

			EnumFacing direction = aligner.getFacingDirection();
			EnumFacing conveyorDirection = aligner.conveyorDirection;

			if (aligner.clientRenderCache != null && conveyorDirection != null && direction.getAxis() != conveyorDirection.getAxis()) {
				float rotationDegrees = RenderUtil.getRotationDegrees(te);

				byte conveyorSpeedsteps = aligner.conveyorSpeedsteps;

				if (conveyorSpeedsteps < 2) {
					// Prevent div/0
					conveyorSpeedsteps = 2;
				}

				double offset = 0.36 / 3;
				double size = 0.01;

				int animFrames = 5;

				GlStateManager.pushMatrix();
				setupDefaultGL();

				GL11.glTranslated(x, y, z);

				GL11.glTranslated(direction.getFrontOffsetX(), direction.getFrontOffsetY() + 0.06, direction.getFrontOffsetZ());

				GL11.glTranslated(.5f, .5f, .5f);
				GL11.glRotatef(rotationDegrees, 0, 1, 0);

				// Offset to the correct position in conveyor direction
				GL11.glTranslated(conveyorDirection.getFrontOffsetX() * offset - size / 2, 0, conveyorDirection.getFrontOffsetZ() * offset - size / 2);

				for (int i = 0; i < 4; i++) {
					ItemWrapper wrapper = aligner.clientRenderCache[i];
					if (wrapper == null) {
						continue;
					}
					if (wrapper.movementProgress >= conveyorSpeedsteps) {
						aligner.clientRenderCache[i] = null;
						continue;
					}
					int rotateDown = wrapper.movementProgress;
					if (rotateDown > conveyorSpeedsteps / 2) {
						rotateDown = conveyorSpeedsteps - rotateDown;
					}
					int rotateSide = rotateDown;
					if (rotateSide > animFrames * 3) {
						rotateSide = animFrames * 3;
					}
					if (rotateDown > animFrames) {
						rotateDown = animFrames;
					}


					boolean rotateTwice = i % 2 == 0;
					double forwardBackward = (i > 1 ? -offset : offset);

					rotateSide -= animFrames * 2;

					if (!rotateTwice) {
						rotateSide = -rotateSide;
					}


					GlStateManager.pushMatrix();

					// Offset to the correct position in aligner direction, left/right to conveyor
					GL11.glTranslated(direction.getFrontOffsetX() * forwardBackward, 0, direction.getFrontOffsetZ() * forwardBackward);

					// Rotate correctly
					GL11.glTranslated(size / 2, 0, size / 2);
					if (rotateTwice) {
						GL11.glRotated(45, 0, 1, 0);
					} else {
						GL11.glRotated(135, 0, 1, 0);
					}
					GL11.glTranslated(-size / 2, 0, -size / 2);

					// Flip down
					GL11.glRotated((15f / animFrames) * rotateSide, 0, 1, 0);
					GL11.glRotated(-(90f / animFrames) * rotateDown, 1, 0, 0);
					GlStateManager.disableLighting();
					GlStateManager.disableTexture2D();

					// Render bit
					RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(0, 0, 0, size, 0.8 / 3, size), 30, 80, 80, 255);

					GlStateManager.popMatrix();
				}

				tearDownDefaultGL();
				GlStateManager.popMatrix();
			}
		}

		if (te instanceof TileEntityConveyorElevator) {
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);

			float rotationDegrees = RenderUtil.getRotationDegrees(te);

			GL11.glTranslated(.5f, .5f, .5f);
			GL11.glRotatef(rotationDegrees, 0, 1, 0);
			GL11.glTranslated(-.5f, -.5f, -.5f);


			TileEntityConveyorElevator.ElevatorDirection escalation = ((TileEntityConveyorElevator) te).escalation;

			renderElevator(escalation, partialTicks);

			GL11.glPopMatrix();
		}
	}

	public void renderElevator(TileEntityConveyorElevator.ElevatorDirection escalation, float partialTicks) {

		GlStateManager.pushMatrix();

		setupDefaultGL();

		GlStateManager.color(0.75f, 0.75f, 0.75f, 1);
		GL11.glLineWidth(20);
		// For whatever reason, we need to enable, THEN disable. Otherwise color gets somewhat garbled..
		GlStateManager.enableTexture2D();
		GlStateManager.disableTexture2D();


		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();

		final float margin = 0.02f;
		final float margin2 = 0.03f;

		final int stepAmount = 4;
		final double stepHeight = ConveyorUtil.oneThird / stepAmount * 3;
		final int speed = Config.pl_elevator_speedsteps / stepAmount;

		double escalationOffset = stepHeight * ((rot + partialTicks) % speed) / speed;
		if (escalation != TileEntityConveyorElevator.ElevatorDirection.UP) {
			escalationOffset = stepHeight - escalationOffset;
		}

		vertexbuffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

		for (int y = 0; y < stepAmount; y++) {
			for (int x = 0; x < 3; x++) {
				double minX = x * ConveyorUtil.oneThird + margin;
				double maxX = minX + ConveyorUtil.oneThird - margin * 2;
				double minZ = ConveyorUtil.oneThird + margin2;
				double maxZ = minZ + ConveyorUtil.oneThird - margin2 * 2;
				double miny = y * stepHeight + escalationOffset;

				vertexbuffer.pos(minX, miny, minZ).endVertex();
				vertexbuffer.pos(maxX, miny, minZ).endVertex();

				vertexbuffer.pos(maxX, miny, maxZ).endVertex();
				vertexbuffer.pos(minX, miny, maxZ).endVertex();

				if (x > 0) {
					vertexbuffer.pos(minX, miny, minZ).endVertex();
					vertexbuffer.pos(minX, miny, maxZ).endVertex();
				}
				if (x < 2) {
					vertexbuffer.pos(maxX, miny, minZ).endVertex();
					vertexbuffer.pos(maxX, miny, maxZ).endVertex();
				}
			}

		}
		tessellator.draw();

		GlStateManager.enableTexture2D();
		tearDownDefaultGL();
		GlStateManager.popMatrix();

	}

	public void renderTankContent(FluidStack content, int capacity, AxisAlignedBB bounds) {
		// Nullcheck
		if (content == null || content.amount == 0) {
			return;
		}
		Fluid fluid = content.getFluid();

		/*
		 * Get texture
		 */
		TextureAtlasSprite sprite = textureGetter.apply(fluid.getStill());

		VertexBuffer renderer = Tessellator.getInstance().getBuffer();

		renderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		/*
		 * Begin rendering
		 */

		float fillFactor = content.amount / (float) capacity;

		double height = bounds.maxY - bounds.minY;
		double widthX = bounds.maxX - bounds.minX;
		double widthZ = bounds.maxZ - bounds.minZ;
		double fillHeight = bounds.minY + fillFactor * height;

		float minU = sprite.getMinU();
		float minV = sprite.getMinV();
		float maxU = sprite.getMaxU();
		float maxV = sprite.getMaxV();

		float V = maxV - minV;
		float U = maxU - minU;

		double minV_Y = minV;
		double maxV_Y = minV + V * fillFactor * height;

		double centeringOffsetZ = U * (1 - widthZ) / 2f;
		double centeringOffsetX = U * (1 - widthX) / 2f;

		double minU_Z = minU + centeringOffsetZ;
		double maxU_Z = minU + U * widthZ + centeringOffsetZ;
		double minU_X = minU + centeringOffsetX;
		double maxU_X = minU + U * widthX + centeringOffsetX;

		double minV_Z = minV + centeringOffsetZ;
		double maxV_Z = minV + V * widthZ + centeringOffsetZ;

		if (height > 0.01f) {
			// +Z
			renderer.pos(bounds.minX, bounds.minY, bounds.maxZ).tex(maxU_Z, minV_Y).normal(0, 0, 1).endVertex();
			renderer.pos(bounds.maxX, bounds.minY, bounds.maxZ).tex(minU_Z, minV_Y).normal(0, 0, 1).endVertex();
			renderer.pos(bounds.maxX, fillHeight, bounds.maxZ).tex(minU_Z, maxV_Y).normal(0, 0, 1).endVertex();
			renderer.pos(bounds.minX, fillHeight, bounds.maxZ).tex(maxU_Z, maxV_Y).normal(0, 0, 1).endVertex();

			// -Z
			renderer.pos(bounds.maxX, bounds.minY, bounds.minZ).tex(maxU_Z, minV_Y).normal(0, 0, -1).endVertex();
			renderer.pos(bounds.minX, bounds.minY, bounds.minZ).tex(minU_Z, minV_Y).normal(0, 0, -1).endVertex();
			renderer.pos(bounds.minX, fillHeight, bounds.minZ).tex(minU_Z, maxV_Y).normal(0, 0, -1).endVertex();
			renderer.pos(bounds.maxX, fillHeight, bounds.minZ).tex(maxU_Z, maxV_Y).normal(0, 0, -1).endVertex();

			// +X
			renderer.pos(bounds.maxX, bounds.minY, bounds.maxZ).tex(maxU_X, minV_Y).normal(1, 0, 0).endVertex();
			renderer.pos(bounds.maxX, bounds.minY, bounds.minZ).tex(minU_X, minV_Y).normal(1, 0, 0).endVertex();
			renderer.pos(bounds.maxX, fillHeight, bounds.minZ).tex(minU_X, maxV_Y).normal(1, 0, 0).endVertex();
			renderer.pos(bounds.maxX, fillHeight, bounds.maxZ).tex(maxU_X, maxV_Y).normal(1, 0, 0).endVertex();

			// -X
			renderer.pos(bounds.minX, bounds.minY, bounds.minZ).tex(maxU_X, minV_Y).normal(-1, 0, 0).endVertex();
			renderer.pos(bounds.minX, bounds.minY, bounds.maxZ).tex(minU_X, minV_Y).normal(-1, 0, 0).endVertex();
			renderer.pos(bounds.minX, fillHeight, bounds.maxZ).tex(minU_X, maxV_Y).normal(-1, 0, 0).endVertex();
			renderer.pos(bounds.minX, fillHeight, bounds.minZ).tex(maxU_X, maxV_Y).normal(-1, 0, 0).endVertex();

		}

		// +Y
		renderer.pos(bounds.maxX, fillHeight, bounds.minZ).tex(maxU_X, minV_Z).normal(0, 1, 0).endVertex();
		renderer.pos(bounds.minX, fillHeight, bounds.minZ).tex(minU_X, minV_Z).normal(0, 1, 0).endVertex();
		renderer.pos(bounds.minX, fillHeight, bounds.maxZ).tex(minU_X, maxV_Z).normal(0, 1, 0).endVertex();
		renderer.pos(bounds.maxX, fillHeight, bounds.maxZ).tex(maxU_X, maxV_Z).normal(0, 1, 0).endVertex();

		setupDefaultGL();

		Tessellator.getInstance().draw();

		tearDownDefaultGL();
	}

	public void renderBagFilling(float fillFactor) {
		if (fillFactor == 0f) {
			return;
		}
		/*
		 * Prepare rendering
		 */

		TextureAtlasSprite sprite = textureGetter.apply(conveyorTextures);
		VertexBuffer renderer = Tessellator.getInstance().getBuffer();

		renderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		/* Vertex infos from exported .obj file
		v 0.020000 0.030001 0.977282
		v 0.980000 0.030001 0.977282
		v 0.111853 0.029999 0.667282
		v 0.888147 0.029999 0.667282
		vt 0.187500 0.527344
		vt 0.000000 0.527344
		vt 0.000000 0.480469
		vt 0.187500 0.480469*/

		GL11.glTranslatef(0, fillFactor * 0.3f, 0);

		float minU = sprite.getMinU();
		float minV = sprite.getMinV();
		float maxU = sprite.getMaxU();
		float maxV = sprite.getMaxV();

		float V = maxV - minV;
		float U = maxU - minU;

		/*
		 * Begin rendering
		 */
		renderer.pos(0.020000, 0.03, 0.977282).tex(minU + U * 0.187500, minV + V * (1 - 0.527344)).normal(0, 1, 0).endVertex();
		renderer.pos(0.980000, 0.03, 0.977282).tex(minU + U * 0.000000, minV + V * (1 - 0.527344)).normal(0, 1, 0).endVertex();
		renderer.pos(0.888147, 0.03, 0.667282).tex(minU + U * 0.000000, minV + V * (1 - 0.480469)).normal(0, 1, 0).endVertex();
		renderer.pos(0.111853, 0.03, 0.667282).tex(minU + U * 0.187500, minV + V * (1 - 0.480469)).normal(0, 1, 0).endVertex();

		setupDefaultGL();

		Tessellator.getInstance().draw();

		tearDownDefaultGL();
	}

	public void renderSieveMesh(boolean shutDown) {
		/*
		 * Prepare rendering
		 */

		TextureAtlasSprite sprite = textureGetter.apply(conveyorTextures);
		VertexBuffer renderer = Tessellator.getInstance().getBuffer();

		renderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		if (shutDown) {
			GL11.glTranslated(0, 0.01, 0);
		} else {
			GL11.glTranslated(0, 0.01 + 0.04 * rotSin, 0);
		}

		float minU = sprite.getMinU();
		float minV = sprite.getMinV();
		float maxU = sprite.getMaxU();
		float maxV = sprite.getMaxV();

		float V = maxV - minV;
		float U = maxU - minU;


		for (ObjFace face : sieve_faces) {
			for (int i = 0; i < 4; i++) {
				Vector3f vertice = sieve_vertices[face.vertexIndexes[i] - 1];
				Vector2f uv = sieve_tex[face.textureIndexes[i] - 1];
				Vector3f normal = sieve_normals[face.normalIndex - 1];
				renderer
						.pos(vertice.x, vertice.y, vertice.z)
						.tex(minU + U * uv.x, minV + V * (1 - uv.y))
						.normal(normal.x, normal.y, normal.z)
						.endVertex();
			}
		}

		setupDefaultGL();

		Tessellator.getInstance().draw();

		tearDownDefaultGL();
	}

	/**
	 * Set up default GL flags for rendering.
	 * <p>
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
	 * <p>
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

	public void renderConveyorItems(IConveyorSlots tileEntity, double x, double y, double z, float partialTicks, boolean oscillate) {


		/*
		 * Check if the block actually wants rendering
		 */
		if (!tileEntity.shouldRenderItemsDefault()) return;

		// Check early if any wrapper is non-empty and skip rendering altogether
		boolean allEmpty = true;
		for (int slot = 0; slot < 9; slot++) {
			ItemWrapper wrapper = tileEntity.getSlot(slot);

			if (wrapper != null && !wrapper.isEmpty()) {
				allEmpty = false;
				break;
			}
		}
		if (allEmpty) return;

		// Render items on conveyor slots

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);

		setupDefaultGL();

		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		float posYOffset = 0.15f;
		if (oscillate) {
			posYOffset += (float) (rotSin * 0.04);
		}
		float speedsteps = tileEntity.getSpeedsteps();

		for (int slot = 0; slot < 9; slot++) {
			ItemWrapper wrapper = tileEntity.getSlot(slot);

			if (wrapper == null || wrapper.isEmpty()) {
				continue;
			}
			ItemStack itemStack = wrapper.itemStack;

			int movementProgress = wrapper.movementProgress;
			if (movementProgress < 0) {
				movementProgress = 0;
			}

			EnumFacing renderDirection = tileEntity.getNextSlot(slot);

			float progress = movementProgress;
			if (wrapper.isRenderingInterpolated()) {
				progress += partialTicks;
			} else {
				// Interpolation since last frame already advanced to almost 1, so we prevent stutter by "skipping ahead"
				progress += 1;
			}
			progress /= speedsteps;
			float posX = (float) ConveyorUtil.getItemPositionX(slot, progress, renderDirection);
			float posZ = (float) ConveyorUtil.getItemPositionZ(slot, progress, renderDirection);
			float posY = tileEntity.getVerticalPosition(slot) + renderDirection.getFrontOffsetY() * progress;

			GL11.glPushMatrix();
			GL11.glTranslatef(posX, posYOffset + posY, posZ);
			GL11.glScalef(itemScaleFactor, itemScaleFactor, itemScaleFactor);

			IBakedModel model = ri.getItemModelMesher().getItemModel(itemStack);
			ri.renderItem(itemStack, model);

			GL11.glPopMatrix();
		}
		tearDownDefaultGL();

		GL11.glPopMatrix();


	}
}
