package net.teamio.taam.rendering;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.conveyors.TileEntityConveyorProcessor;
import net.teamio.taam.content.conveyors.TileEntityConveyorSieve;
import net.teamio.taam.content.piping.TileEntityTank;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;
import net.teamio.taam.conveyors.appliances.ApplianceSprayer;
import net.teamio.taam.piping.IPipe;

public class TaamRenderer extends TileEntitySpecialRenderer<TileEntity> {

	private RenderItem ri;
	private float rot = 0;
	private float rot_sensor = 0;
	public static double rotSin = 0;
	
	public static final float shrinkValue = -0.001f;

	public static final float b_tankBorder = 1.5f / 16f;
	public static final float b_tankBorderSprayer = b_tankBorder + 4f / 16f;
	public static final float b_basePlate = 2f / 16f;
	
	public static final AxisAlignedBB bounds_tank = new AxisAlignedBB(
			b_tankBorder,	b_basePlate,	b_tankBorder,
			1-b_tankBorder,	1,				1-b_tankBorder
	).expand(shrinkValue, shrinkValue, shrinkValue);
	
	public static AxisAlignedBB bounds_sprayer = new AxisAlignedBB(
			b_tankBorder,	b_basePlate,	b_tankBorder,
			1-b_tankBorder,	1-4f/16f,				1-b_tankBorderSprayer
	).expand(shrinkValue, shrinkValue, shrinkValue);

	public TaamRenderer() {
		ri = Minecraft.getMinecraft().getRenderItem();
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		
		bounds_sprayer = new AxisAlignedBB(
				b_tankBorder,	b_basePlate,	b_tankBorder,
				1-b_tankBorder,	1-4f/16f,				1-b_tankBorderSprayer
		).expand(shrinkValue, shrinkValue, shrinkValue);
		
		
		if (event.phase == TickEvent.Phase.END) {
			rot++;
			rot_sensor++;
			if (rot_sensor > 360) {
				rot_sensor -= 360;
			}
			rotSin = Math.sin(Math.toRadians(rot * 32));
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks, int destroyStage) {
		if (tileEntity instanceof IConveyorAwareTE) {
			renderConveyorItems((IConveyorAwareTE) tileEntity, x, y, z);
		}

		if (tileEntity instanceof TileEntityTank) {
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);
			
			FluidTank tank = ((TileEntityTank)tileEntity).getTank();
			FluidStack stack = tank.getFluid();

			renderTankContent(stack, tank.getCapacity(), bounds_tank);

			GL11.glPopMatrix();
		}
		
		if(tileEntity instanceof ApplianceSprayer) {
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);
			
			float rotationDegrees = getRotationDegrees(tileEntity);
			
			GL11.glTranslated(.5f, .5f, .5f);
			GL11.glRotatef(rotationDegrees, 0, 1, 0);
			GL11.glTranslated(-.5f, -.5f, -.5f);


			FluidTank tank = ((ApplianceSprayer)tileEntity).getTank();
			FluidStack stack = tank.getFluid();

			renderTankContent(stack, tank.getCapacity(), bounds_sprayer);

			GL11.glPopMatrix();
		}
		
		if (tileEntity instanceof IPipe) {

			FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRendererObj;

			IPipe pipe = (IPipe) tileEntity;

			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);

			GL11.glPushMatrix();
			GL11.glTranslated(0.4f, 0.4f, 0.25f);

			GL11.glScalef(.02f, .02f, .02f);
			GL11.glRotatef(180, 0, 0, 1);

			fontRendererObj.drawString(pipe.getPressure() + "-" + pipe.getSuction(), 0, 0, 0xFFFFFF);
			fontRendererObj.drawString("E: " + (pipe.getPressure() == 0 ? -pipe.getSuction() : pipe.getPressure()), 0, 8, 0xFFFF00);

			GL11.glPopMatrix();

			GL11.glTranslated(.5f, .5f, .5f);
			GL11.glRotatef(180, 0, 1, 0);
			GL11.glTranslated(-.5f, -.5f, -.5f);

			GL11.glPushMatrix();
			GL11.glTranslated(0.4f, 0.4f, 0.25f);

			GL11.glScalef(.02f, .02f, .02f);
			GL11.glRotatef(180, 0, 0, 1);

			fontRendererObj.drawString(pipe.getPressure() + "-" + pipe.getSuction(), 0, 0, 0xFFFFFF);
			fontRendererObj.drawString("E: " + (pipe.getPressure() == 0 ? -pipe.getSuction() : pipe.getPressure()), 0, 8, 0xFFFF00);

			GL11.glPopMatrix();

			GL11.glPopMatrix();
		}
	}

	private void renderTankContent(FluidStack content, int capacity, AxisAlignedBB bounds) {
		// Nullcheck
		if (content == null || content.amount == 0) {
			return;
		}
		Fluid fluid = content.getFluid();

		/*
		 * Get texture
		 */
		Function<ResourceLocation, TextureAtlasSprite> textureGetter;
		textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
			public TextureAtlasSprite apply(ResourceLocation location) {
				return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
			}
		};
		TextureAtlasSprite sprite = textureGetter.apply(fluid.getStill());

		/*
		 * Setup renderer
		 */

		WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();

		renderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		bindTexture(TextureMap.locationBlocksTexture);

		/*
		 * Begin rendering
		 */

		float fillFactor = content.amount / (float) capacity;

		double height = bounds.maxY - bounds.minY;
		double fillHeight = bounds.minY + fillFactor * height;

		float minU = sprite.getMinU();
		float minV = sprite.getMinV();
		float maxU = sprite.getMaxU();
		float maxV = sprite.getMaxV();

		double textureFactor = minV + (maxV - minV) * fillFactor * height;

		// +Z
		renderer.pos(bounds.minX, bounds.minY, bounds.maxZ)	.tex(maxU, minV).normal(0, 0, 1).endVertex();
		renderer.pos(bounds.maxX, bounds.minY, bounds.maxZ)	.tex(minU, minV).normal(0, 0, 1).endVertex();
		renderer.pos(bounds.maxX, fillHeight, bounds.maxZ)	.tex(minU, textureFactor).normal(0, 0, 1).endVertex();
		renderer.pos(bounds.minX, fillHeight, bounds.maxZ)	.tex(maxU, textureFactor).normal(0, 0, 1).endVertex();

		// -Z
		renderer.pos(bounds.maxX, bounds.minY, bounds.minZ)	.tex(maxU, minV).normal(0, 0, -1).endVertex();
		renderer.pos(bounds.minX, bounds.minY, bounds.minZ)	.tex(minU, minV).normal(0, 0, -1).endVertex();
		renderer.pos(bounds.minX, fillHeight, bounds.minZ)	.tex(minU, textureFactor).normal(0, 0, -1).endVertex();
		renderer.pos(bounds.maxX, fillHeight, bounds.minZ)	.tex(maxU, textureFactor).normal(0, 0, -1).endVertex();

		// +X
		renderer.pos(bounds.maxX, bounds.minY, bounds.maxZ)	.tex(maxU, minV).normal(1, 0, 0).endVertex();
		renderer.pos(bounds.maxX, bounds.minY, bounds.minZ)	.tex(minU, minV).normal(1, 0, 0).endVertex();
		renderer.pos(bounds.maxX, fillHeight, bounds.minZ)	.tex(minU, textureFactor).normal(1, 0, 0).endVertex();
		renderer.pos(bounds.maxX, fillHeight, bounds.maxZ)	.tex(maxU, textureFactor).normal(1, 0, 0).endVertex();

		// -X
		renderer.pos(bounds.minX, bounds.minY, bounds.minZ)	.tex(maxU, minV).normal(-1, 0, 0).endVertex();
		renderer.pos(bounds.minX, bounds.minY, bounds.maxZ)	.tex(minU, minV).normal(-1, 0, 0).endVertex();
		renderer.pos(bounds.minX, fillHeight, bounds.maxZ)	.tex(minU, textureFactor).normal(-1, 0, 0).endVertex();
		renderer.pos(bounds.minX, fillHeight, bounds.minZ)	.tex(maxU, textureFactor).normal(-1, 0, 0).endVertex();

		// +Y
		renderer.pos(bounds.maxX, fillHeight, bounds.minZ)	.tex(maxU, minV).normal(0, 1, 0).endVertex();
		renderer.pos(bounds.minX, fillHeight, bounds.minZ)	.tex(minU, minV).normal(0, 1, 0).endVertex();
		renderer.pos(bounds.minX, fillHeight, bounds.maxZ)	.tex(minU, maxV).normal(0, 1, 0).endVertex();
		renderer.pos(bounds.maxX, fillHeight, bounds.maxZ)	.tex(maxU, maxV).normal(0, 1, 0).endVertex();

		GL11.glEnable(GL11.GL_BLEND);

		Tessellator.getInstance().draw();

		GL11.glDisable(GL11.GL_BLEND);
	}

	private EnumFacing getDirection(Object tileEntity) {
		EnumFacing direction;
		if (tileEntity instanceof IRotatable) {
			direction = ((IRotatable) tileEntity).getFacingDirection();
		} else {
			direction = EnumFacing.SOUTH;
		}
		return direction;
	}
	
	private float getRotationDegrees(Object tileEntity) {
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

	public void renderConveyorItems(IConveyorAwareTE tileEntity, double x, double y, double z) {

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);

		if (tileEntity != null) {

			bindTexture(TextureMap.locationBlocksTexture);
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

		GL11.glPopMatrix();
	}
}
