package net.teamio.taam.rendering;

import java.util.Random;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
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
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.teamio.taam.Config;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.conveyors.TileEntityConveyorItemBag;
import net.teamio.taam.content.conveyors.TileEntityConveyorProcessor;
import net.teamio.taam.content.conveyors.TileEntityConveyorSieve;
import net.teamio.taam.content.conveyors.TileEntityConveyorTrashCan;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorSlots;
import net.teamio.taam.conveyors.appliances.ApplianceSprayer;
import net.teamio.taam.piping.IPipe;
import net.teamio.taam.util.WrenchUtil;

public class TaamRenderer extends TileEntitySpecialRenderer<TileEntity> {

	private RenderItem ri;
	private float rot = 0;
	private float rot_sensor = 0;
	public static double rotSin = 0;

	public static final double boundingBoxExpand = 0.0020000000949949026D;

	public static boolean failureFreeBlockHightlight = true;
	
	public static final float shrinkValue = -0.001f;

	public static final float b_tankBorder = 1.5f / 16f;
	public static final float b_tankBorderSprayer = b_tankBorder + 4f / 16f;
	public static final float b_basePlate = 2f / 16f;

	public static AxisAlignedBB bounds_sprayer = new AxisAlignedBB(
			b_tankBorder,	b_basePlate,	b_tankBorder,
			1-b_tankBorder,	1-4f/16f,		1-b_tankBorderSprayer
			).expand(shrinkValue, shrinkValue, shrinkValue);


	Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
		@Override
		public TextureAtlasSprite apply(ResourceLocation location) {
			return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
		}
	};

	public TaamRenderer() {
		ri = Minecraft.getMinecraft().getRenderItem();
	}

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

	@SubscribeEvent
	public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
		// If we crash, turn of Block Highlight drawing to prevent getting locked out of a world
		if(!failureFreeBlockHightlight) {
			return;
		}
		RayTraceResult target = event.getTarget();
		if(target != null && target.sideHit == EnumFacing.UP) {
			BlockPos pos = target.getBlockPos();
			TileEntity te;
			if(pos != null) {
				EntityPlayer player = event.getPlayer();
				World world = player.worldObj;
				te = world.getTileEntity(pos);
				try {
					IConveyorSlots cte = ConveyorUtil.getSlots(te, null);
					if (cte != null) {

						// Only render for TEs that actually have the items there
						if (!cte.shouldRenderItemsDefault()) {
							return;
						}

						Vec3d hitVec = target.hitVec;
						int slot = ConveyorUtil.getSlotForRelativeCoordinates(hitVec.xCoord - pos.getX(),
								hitVec.zCoord - pos.getZ());

					EnumFacing dir = cte.getNextSlot(slot);
					float speedsteps = cte.getSpeedsteps();
					float progress = cte.getMovementProgress(slot) / speedsteps;

					double x = pos.getX() + Math.floor(slot / 3) * ConveyorUtil.oneThird // General Position
							+ dir.getFrontOffsetX() * progress * ConveyorUtil.oneThird; // Apply Slot Movement
					double y = pos.getY() + cte.getVerticalPosition(slot);
					double z = pos.getZ() + slot % 3 * ConveyorUtil.oneThird // General Position
							+ dir.getFrontOffsetZ() * progress * ConveyorUtil.oneThird; // Apply Slot Movement

						float progress = wrapper.movementProgress;

						if (wrapper.isRenderingInterpolated()) {
							progress += event.getPartialTicks();
						} else {
							// Interpolation since last frame already advanced to almost 1, so we prevent stutter by "skipping ahead"
							progress += 1;
						}

						progress *= ConveyorUtil.oneThird / speedsteps;

						double x = pos.getX() + Math.floor(slot / 3) * ConveyorUtil.oneThird // General Position
								+ dir.getFrontOffsetX() * progress; // Apply Slot Movement
						double y = pos.getY() + cte.getVerticalPosition(slot);
						double z = pos.getZ() + slot % 3 * ConveyorUtil.oneThird // General Position
								+ dir.getFrontOffsetZ() * progress; // Apply Slot Movement

						drawSelectionBoundingBox(player, event.getPartialTicks(), new AxisAlignedBB(x, y, z,
								x + ConveyorUtil.oneThird, y + ConveyorUtil.oneThird, z + ConveyorUtil.oneThird));
					} 
				} catch (Exception e) {
					Log.error("Error drawing block highlight for a tile entity. Disabling block highlight drawing to prevent you from crashing - This is an error, please report!", e);
					failureFreeBlockHightlight = false;
				}
			}
		}
	}


	public void drawSelectionBoundingBox(EntityPlayer player, float partialTicks, AxisAlignedBB box) {
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
		GL11.glLineWidth(2.0F);
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);

		double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

		RenderGlobal.drawSelectionBoundingBox(box.offset(-d0, -d1, -d2).expand(boundingBoxExpand, boundingBoxExpand, boundingBoxExpand));

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks, int destroyStage) {

		TankRenderInfo[] tankRI = tileEntity.getCapability(Taam.CAPABILITY_RENDER_TANK, null);

		if(tankRI != null) {
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);

			float rotationDegrees = getRotationDegrees(tileEntity);

			GL11.glTranslated(.5f, .5f, .5f);
			GL11.glRotatef(rotationDegrees, 0, 1, 0);
			GL11.glTranslated(-.5f, -.5f, -.5f);

			for(TankRenderInfo renderInfo : tankRI) {
				renderTankContent(renderInfo.tankInfo.fluid, renderInfo.tankInfo.capacity, renderInfo.bounds);
			}
			GL11.glPopMatrix();
		}

		if (tileEntity instanceof IConveyorSlots) {
			renderConveyorItems((IConveyorSlots) tileEntity, x, y, z);
		}

		//TODO: replace with capability!

		//		if (tileEntity instanceof TileEntityTank) {
		//			GL11.glPushMatrix();
		//			GL11.glTranslated(x, y, z);
		//
		//			FluidTank tank = ((TileEntityTank) tileEntity).getTank();
		//			FluidStack stack = tank.getFluid();
		//
		//			renderTankContent(stack, tank.getCapacity(), bounds_tank);
		//
		//			GL11.glPopMatrix();
		//		}

		if(tileEntity instanceof TileEntityConveyorSieve) {
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);

			float rotationDegrees = getRotationDegrees(tileEntity);

			GL11.glTranslated(.5f, .5f, .5f);
			GL11.glRotatef(rotationDegrees, 0, 1, 0);
			GL11.glTranslated(-.5f, -.5f, -.5f);

			renderSieveMesh(((TileEntityConveyorSieve) tileEntity).isShutdown);
			
			GL11.glPopMatrix();
		}
		
		if (tileEntity instanceof ApplianceSprayer) {
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);

			float rotationDegrees = getRotationDegrees(tileEntity);

			GL11.glTranslated(.5f, .5f, .5f);
			GL11.glRotatef(rotationDegrees, 0, 1, 0);
			GL11.glTranslated(-.5f, -.5f, -.5f);

			FluidTank tank = ((ApplianceSprayer) tileEntity).getTank();
			FluidStack stack = tank.getFluid();

			renderTankContent(stack, tank.getCapacity(), bounds_sprayer);

			GL11.glPopMatrix();
		}

		if(tileEntity instanceof TileEntityConveyorItemBag) {
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);

			float rotationDegrees = getRotationDegrees(tileEntity);

			GL11.glTranslated(.5f, .5f, .5f);
			GL11.glRotatef(rotationDegrees, 0, 1, 0);
			GL11.glTranslated(-.5f, -.5f, -.5f);

			float fillFactor = ((TileEntityConveyorItemBag) tileEntity).fillPercent;

			renderBagFilling(fillFactor);

			GL11.glPopMatrix();
		}

		if(tileEntity instanceof TileEntityConveyorTrashCan) {
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);

			float rotationDegrees = getRotationDegrees(tileEntity);

			GL11.glTranslated(.5f, .5f, .5f);
			GL11.glRotatef(rotationDegrees, 0, 1, 0);
			GL11.glTranslated(-.5f, -.5f, -.5f);

			float fillFactor = ((TileEntityConveyorTrashCan) tileEntity).fillLevel / Config.pl_trashcan_maxfill;

			renderBagFilling(fillFactor);

			GL11.glPopMatrix();
		}

		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		boolean hasDebugTool = player != null && WrenchUtil.playerHasDebugTool(player);

		if (hasDebugTool && tileEntity instanceof IPipe) {

			FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRendererObj;

			IPipe pipe = (IPipe) tileEntity;

			int fillLevel = 0;

			//TODO: Pipe Fill Level
			//			if(pipe instanceof TileEntityPipe) {
			//				fillLevel = ((TileEntityPipe) pipe).getFillLevel();
			//			}

			String info0 = String.format("%03d/%d",
					fillLevel, pipe.getCapacity());;
					String info1 = String.format("%d-%d",
							pipe.getPressure(), pipe.getSuction());
					String info2 = "E: " + (pipe.getPressure() == 0 ? -pipe.getSuction() : pipe.getPressure());

					GL11.glPushMatrix();
					{
						GL11.glTranslated(x, y, z);

						GL11.glTranslated(.5f, .5f, .5f);

						float playerRot = player.getRotationYawHead();
						float pitch = player.rotationPitch;

						GL11.glRotatef(180, 0, 0, 1);
						GL11.glRotatef(playerRot, 0, 1, 0);
						GL11.glRotatef(-pitch, 1, 0, 0);
						GL11.glTranslated(-.5f, -.5f, -.5f);

						GL11.glPushMatrix();
						{
							GL11.glTranslated(0.25f, 0.25f, 0.15f);

							GL11.glScalef(.02f, .02f, .02f);

							fontRendererObj.drawString(info0, 0, 0, 0x00FFFF);
							fontRendererObj.drawString(info1, 0, 8, 0xFFFFFF);
							fontRendererObj.drawString(info2, 0, 16, 0xFFFF00);
						}
						GL11.glPopMatrix();
					}
					GL11.glPopMatrix();
		}
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

		double centeringOffsetZ = U * (1-widthZ) / 2f;
		double centeringOffsetX = U * (1-widthX) / 2f;

		double minU_Z = minU + centeringOffsetZ;
		double maxU_Z = minU + U * widthZ + centeringOffsetZ;
		double minU_X = minU + centeringOffsetX;
		double maxU_X = minU + U * widthX + centeringOffsetX;

		double minV_Z = minV + centeringOffsetZ;
		double maxV_Z = minV + V * widthZ + centeringOffsetZ;

		if(height > 0.01f) {
			// +Z
			renderer.pos(bounds.minX, bounds.minY, bounds.maxZ)	.tex(maxU_Z, minV_Y).normal(0, 0, 1).endVertex();
			renderer.pos(bounds.maxX, bounds.minY, bounds.maxZ)	.tex(minU_Z, minV_Y).normal(0, 0, 1).endVertex();
			renderer.pos(bounds.maxX, fillHeight, bounds.maxZ)	.tex(minU_Z, maxV_Y).normal(0, 0, 1).endVertex();
			renderer.pos(bounds.minX, fillHeight, bounds.maxZ)	.tex(maxU_Z, maxV_Y).normal(0, 0, 1).endVertex();

			// -Z
			renderer.pos(bounds.maxX, bounds.minY, bounds.minZ)	.tex(maxU_Z, minV_Y).normal(0, 0, -1).endVertex();
			renderer.pos(bounds.minX, bounds.minY, bounds.minZ)	.tex(minU_Z, minV_Y).normal(0, 0, -1).endVertex();
			renderer.pos(bounds.minX, fillHeight, bounds.minZ)	.tex(minU_Z, maxV_Y).normal(0, 0, -1).endVertex();
			renderer.pos(bounds.maxX, fillHeight, bounds.minZ)	.tex(maxU_Z, maxV_Y).normal(0, 0, -1).endVertex();

			// +X
			renderer.pos(bounds.maxX, bounds.minY, bounds.maxZ)	.tex(maxU_X, minV_Y).normal(1, 0, 0).endVertex();
			renderer.pos(bounds.maxX, bounds.minY, bounds.minZ)	.tex(minU_X, minV_Y).normal(1, 0, 0).endVertex();
			renderer.pos(bounds.maxX, fillHeight, bounds.minZ)	.tex(minU_X, maxV_Y).normal(1, 0, 0).endVertex();
			renderer.pos(bounds.maxX, fillHeight, bounds.maxZ)	.tex(maxU_X, maxV_Y).normal(1, 0, 0).endVertex();

			// -X
			renderer.pos(bounds.minX, bounds.minY, bounds.minZ)	.tex(maxU_X, minV_Y).normal(-1, 0, 0).endVertex();
			renderer.pos(bounds.minX, bounds.minY, bounds.maxZ)	.tex(minU_X, minV_Y).normal(-1, 0, 0).endVertex();
			renderer.pos(bounds.minX, fillHeight, bounds.maxZ)	.tex(minU_X, maxV_Y).normal(-1, 0, 0).endVertex();
			renderer.pos(bounds.minX, fillHeight, bounds.minZ)	.tex(maxU_X, maxV_Y).normal(-1, 0, 0).endVertex();

		}

		// +Y
		renderer.pos(bounds.maxX, fillHeight, bounds.minZ)	.tex(maxU_X, minV_Z).normal(0, 1, 0).endVertex();
		renderer.pos(bounds.minX, fillHeight, bounds.minZ)	.tex(minU_X, minV_Z).normal(0, 1, 0).endVertex();
		renderer.pos(bounds.minX, fillHeight, bounds.maxZ)	.tex(minU_X, maxV_Z).normal(0, 1, 0).endVertex();
		renderer.pos(bounds.maxX, fillHeight, bounds.maxZ)	.tex(maxU_X, maxV_Z).normal(0, 1, 0).endVertex();

		setupDefaultGL();

		Tessellator.getInstance().draw();

		tearDownDefaultGL();
	}

	public static final ResourceLocation conveyorTextures = new ResourceLocation("Taam", "blocks/conveyor");

	public void renderBagFilling(float fillFactor) {
		if(fillFactor == 0f) {
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
		renderer.pos(0.020000, 0.03, 0.977282).tex(minU + U*0.187500, minV + V*(1-0.527344)).normal(0, 1, 0).endVertex();
		renderer.pos(0.980000, 0.03, 0.977282).tex(minU + U*0.000000, minV + V*(1-0.527344)).normal(0, 1, 0).endVertex();
		renderer.pos(0.888147, 0.03, 0.667282).tex(minU + U*0.000000, minV + V*(1-0.480469)).normal(0, 1, 0).endVertex();
		renderer.pos(0.111853, 0.03, 0.667282).tex(minU + U*0.187500, minV + V*(1-0.480469)).normal(0, 1, 0).endVertex();

		setupDefaultGL();

		Tessellator.getInstance().draw();

		tearDownDefaultGL();
	}
	
	/*
	 * Vertex infos from exported .obj file
	 */
	Vector3f[] sieve_vertices = new Vector3f[] {
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
	Vector2f[] sieve_tex = new Vector2f[] {
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
	Vector3f[] sieve_normals = new Vector3f[] {
			new Vector3f(0.000000f, -0.99900f,  -0.0457f),
			new Vector3f(0.000000f, 0.000000f, -1.00000f),
			new Vector3f(0.000000f, 0.999000f, 0.045700f),
			new Vector3f(1.000000f, 0.000000f, 0.000000f),
			new Vector3f(-1.00000f,  0.00000f,  0.00000f),
			new Vector3f(0.000000f, 0.000000f, 1.000000f)
	};
	ObjFace[] sieve_faces = new ObjFace[] {
		new	ObjFace(new int[] {8 , 14, 4 , 1 }, new int[] {1, 2, 3 , 4}, 1),
		new	ObjFace(new int[] {1 , 4 , 6 , 2 }, new int[] {5, 6, 4 , 7}, 2),
		new	ObjFace(new int[] {9 , 2 , 6 , 15}, new int[] {1, 4, 3 , 2}, 3),
		new	ObjFace(new int[] {8 , 1 , 2 , 9 }, new int[] {8, 9, 10, 4}, 4),
		new	ObjFace(new int[] {12, 3 , 4 , 14}, new int[] {1, 4, 3 , 2}, 1),
		new	ObjFace(new int[] {3 , 5 , 6 , 4 }, new int[] {5, 7, 4 , 6}, 2),
		new	ObjFace(new int[] {16, 15, 6 , 5 }, new int[] {1, 2, 3 , 4}, 3),
		new	ObjFace(new int[] {12, 16, 5 , 3 }, new int[] {8, 4, 10, 9}, 5),
		new	ObjFace(new int[] {8 , 7 , 13, 14}, new int[] {1, 4, 3 , 2}, 1),
		new	ObjFace(new int[] {7 , 10, 18, 13}, new int[] {5, 7, 4 , 6}, 6),
		new	ObjFace(new int[] {9 , 15, 18, 10}, new int[] {1, 2, 3 , 4}, 3),
		new	ObjFace(new int[] {8 , 9 , 10, 7 }, new int[] {8, 4, 10, 9}, 4),
		new	ObjFace(new int[] {12, 14, 13, 11}, new int[] {1, 2, 3 , 4}, 1),
		new	ObjFace(new int[] {11, 13, 18, 17}, new int[] {5, 6, 4 , 7}, 6),
		new	ObjFace(new int[] {16, 17, 18, 15}, new int[] {1, 4, 3 , 2}, 3),
		new	ObjFace(new int[] {12, 11, 17, 16}, new int[] {8, 9, 10, 4}, 5)
	};
	
	public void renderSieveMesh(boolean shutDown) {
		/*
		 * Prepare rendering
		 */

		TextureAtlasSprite sprite = textureGetter.apply(conveyorTextures);
		VertexBuffer renderer = Tessellator.getInstance().getBuffer();

		renderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		if(shutDown) {
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
		

		for(ObjFace face : sieve_faces) {
			for(int i = 0; i < 4; i++) {
				Vector3f vertice = sieve_vertices[face.vertexIndexes[i]-1];
				Vector2f uv = sieve_tex[face.textureIndexes[i]-1];
				Vector3f normal = sieve_normals[face.normalIndex-1];
				renderer
				.pos(vertice.x, vertice.y, vertice.z)
				.tex(minU + U*uv.x, minV + V*(1-uv.y))
				.normal(normal.x, normal.y, normal.z)
				.endVertex();
			}
		}

		setupDefaultGL();

		Tessellator.getInstance().draw();

		tearDownDefaultGL();
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

	private void setupDefaultGL() {
		RenderHelper.enableStandardItemLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableAlpha();
		GlStateManager.color(1, 1, 1, 1);
	}

	private void tearDownDefaultGL() {
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		RenderHelper.disableStandardItemLighting();
	}

	public static EnumFacing getDirection(Object tileEntity) {
		EnumFacing direction;
		if (tileEntity instanceof IRotatable) {
			direction = ((IRotatable) tileEntity).getFacingDirection();
		} else {
			direction = EnumFacing.SOUTH;
		}
		return direction;
	}

	public static float getRotationDegrees(Object tileEntity) {
		EnumFacing direction = getDirection(tileEntity);
		float rotationDegrees = 0;
		if (direction == EnumFacing.WEST) {
			rotationDegrees = 270;
		} else if (direction == EnumFacing.NORTH) {
			rotationDegrees = 180;
		} else if (direction == EnumFacing.EAST) {
			rotationDegrees = 90;
		}
		return rotationDegrees;
	}

	public void renderConveyorItems(IConveyorSlots tileEntity, double x, double y, double z) {

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);

		setupDefaultGL();

		if (tileEntity != null) {

			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			if (tileEntity instanceof TileEntityConveyorProcessor) {

				/*
				 * Get Rotation
				 */

				float rotationDegrees = getRotationDegrees(tileEntity);

				TileEntityConveyorProcessor processor = (TileEntityConveyorProcessor) tileEntity;
				ItemStack processingStack = processor.getStackInSlot(0);
				if (processingStack != null) {
					GL11.glPushMatrix();
					Random rand = processor.getWorld().rand;
					GL11.glTranslatef(0.5f, 0.4f, 0.5f);

					/*
					 * Rotate the items
					 */

					GL11.glRotatef(rotationDegrees, 0, 1, 0);

					/*
					 * Shaking inside the processor
					 */
					if (!processor.isShutdown) {
						GL11.glTranslatef(
								0.015f * (1-rand.nextFloat()),
								0.025f * (1-rand.nextFloat()),
								0.015f * (1-rand.nextFloat()));
					}
					GL11.glScalef(0.4f, 0.4f, 0.4f);

					IBakedModel model = ri.getItemModelMesher().getItemModel(processingStack);
					ri.renderItem(processingStack, model);

					GL11.glPopMatrix();
				}
			}
			/*
			 * Regular rendering, meaning conveyors & similar
			 */
			if (tileEntity.shouldRenderItemsDefault()) {
				float posY = 0.1f;
				if (tileEntity instanceof TileEntityConveyorSieve) {
					// TODO extract into separate method getItemRenderPosY() in
					// IConveyorAwareTE
					if (((TileEntityConveyorSieve) tileEntity).isShutdown) {
						// posY = 0;
					} else {
						posY += (float) (rotSin * 0.04);
					}
				}
				for (int slot = 0; slot < 9; slot++) {
					ItemWrapper wrapper = tileEntity.getSlot(slot);

					ItemStack itemStack;
					if (wrapper == null || wrapper.isEmpty() || (itemStack = wrapper.itemStack) == null) {
						continue;
					}

					int movementProgress = tileEntity.getMovementProgress(slot);
					if (movementProgress < 0) {
						movementProgress = 0;
					}
					float speedsteps = tileEntity.getSpeedsteps();

					EnumFacing renderDirection = tileEntity.getNextSlot(slot);

					float posX = (float) ConveyorUtil.getItemPositionX(slot, movementProgress / speedsteps, renderDirection);
					float posZ = (float) ConveyorUtil.getItemPositionZ(slot, movementProgress / speedsteps, renderDirection);

					GL11.glPushMatrix();
					GL11.glTranslatef(posX, posY + 0.51f, posZ);
					GL11.glScalef(0.4f, 0.4f, 0.4f);

					IBakedModel model = ri.getItemModelMesher().getItemModel(itemStack);
					ri.renderItem(itemStack, model);

					GL11.glPopMatrix();
				}
			}
		}

		tearDownDefaultGL();

		GL11.glPopMatrix();
	}
}
