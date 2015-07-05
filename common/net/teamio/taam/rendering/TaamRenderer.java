package net.teamio.taam.rendering;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.obj.WavefrontObject;
import net.minecraftforge.client.model.techne.TechneModel;
import net.minecraftforge.common.util.ForgeDirection;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.common.BlockSensor;
import net.teamio.taam.content.common.TileEntitySensor;
import net.teamio.taam.content.conveyors.BlockProductionLine;
import net.teamio.taam.content.conveyors.TileEntityConveyor;
import net.teamio.taam.content.conveyors.TileEntityConveyorHopper;
import net.teamio.taam.content.conveyors.TileEntityConveyorProcessor;
import net.teamio.taam.content.logistics.TileEntityLogisticsManager;
import net.teamio.taam.content.logistics.TileEntityLogisticsStation;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;

public class TaamRenderer extends TileEntitySpecialRenderer implements IItemRenderer {

	public final TechneModel modelSensor;
	public final ResourceLocation textureSensor;
	public final ResourceLocation textureSensorBlink;
	
	public final WavefrontObject modelConveyor;
	public final ResourceLocation textureConveyor;
	
	public final WavefrontObject modelLogisticsStation;
	public final ResourceLocation textureLogisticsStation;
	
	public final WavefrontObject modelLogisticsManager;
	public final ResourceLocation textureLogisticsManager;
	
	private RenderItem ri;
	private EntityItem ei;
	private float rot = 0;
	
	public static int renderMagneticRailID;
	
	public static ISimpleBlockRenderingHandler renderMagneticRail = new ISimpleBlockRenderingHandler() {
		
		@Override
		public boolean shouldRender3DInInventory(int modelId) {
			return false;
		}
		
		@Override
		public boolean renderWorldBlock(IBlockAccess world, int x, int y,
				int z, Block block, int modelId, RenderBlocks renderer) {
			Tessellator tessellator = Tessellator.instance;
			int metadata = world.getBlockMetadata(x, y, z);

			int rotation = metadata & 3;
			
			boolean left = (metadata & 4) == 4;
			boolean right = (metadata & 8) == 8;
			boolean forward = true;//We cannot pack that I think... (metadata & 16) == 16 is out of range :(
			
			tessellator.setBrightness(block.getMixedBrightnessForBlock(
					renderer.blockAccess, x, y, z));

			tessellator.setColorOpaque_I(block.getBlockColor());
			IIcon iicon;
			iicon = TaamMain.blockMagnetRail.connBase;
			renderFlatTexture(tessellator, iicon, x, y, z, rotation);
			if(left) {
				iicon = TaamMain.blockMagnetRail.connLeft;
				renderFlatTexture(tessellator, iicon, x, y, z, rotation);
			}
			if(right) {
				iicon = TaamMain.blockMagnetRail.connRight;
				renderFlatTexture(tessellator, iicon, x, y, z, rotation);
			}
			if(forward) {
				iicon = TaamMain.blockMagnetRail.connForward;
				renderFlatTexture(tessellator, iicon, x, y, z, rotation);
			}
			return true;
		}
		
		public void renderFlatTexture(Tessellator tessellator, IIcon iicon, int x, int y, int z, int rotation) {
			 float f = 0.015625F;
	        double d0 = (double)iicon.getMinU();
	        double d1 = (double)iicon.getMinV();
	        double d2 = (double)iicon.getMaxU();
	        double d3 = (double)iicon.getMaxV();
	        
        	int x1 = x;
        	int x2 = x+1;
        	int x3 = x+1;
        	int x4 = x;
        	int z1 = z+1;
        	int z2 = z+1;
        	int z3 = z;
        	int z4 = z;
	        
	        for(int i = 0; i < rotation; i++) {
	        	int temp = x4;
	        	x4 = x3;
	        	x3 = x2;
	        	x2 = x1;
	        	x1 = temp;
	        	temp = z4;
	        	z4 = z3;
	        	z3 = z2;
	        	z2 = z1;
	        	z1 = temp;
	        }
	        tessellator.addVertexWithUV(x1, y + f, z1, d2, d1);
	        tessellator.addVertexWithUV(x2, y + f, z2, d0, d1);
	        tessellator.addVertexWithUV(x3, y + f, z3, d0, d3);
	        tessellator.addVertexWithUV(x4, y + f, z4, d2, d3);
		}
		
		@Override
		public void renderInventoryBlock(Block block, int metadata, int modelId,
				RenderBlocks renderer) {
	       
		}
		
		@Override
		public int getRenderId() {
			return renderMagneticRailID;
		}
	};
	
	public TaamRenderer() {
		ri = new RenderItem() {
			@Override
			public boolean shouldBob() {
				return false;
			}
		};
		ei = new EntityItem(null, 0, 0, 0, new ItemStack(Items.apple));
		ei.rotationPitch = 0;
		ei.rotationYaw = 0;
		ri.setRenderManager(RenderManager.instance);
		
		
		modelSensor = new TechneModel(new ResourceLocation(Taam.MOD_ID + ":models/sensor.tcn"));
		textureSensor = new ResourceLocation(Taam.MOD_ID + ":textures/models/sensor.png");
		textureSensorBlink = new ResourceLocation(Taam.MOD_ID + ":textures/models/sensor_blink.png");

		modelConveyor = new WavefrontObject(new ResourceLocation(Taam.MOD_ID + ":models/conveyor.obj"));
		textureConveyor = new ResourceLocation(Taam.MOD_ID + ":textures/models/conveyor.png");

		modelLogisticsStation = new WavefrontObject(new ResourceLocation(Taam.MOD_ID + ":models/logistics_station.obj"));
		textureLogisticsStation = new ResourceLocation(Taam.MOD_ID + ":textures/models/logistics_station.png");

		modelLogisticsManager = new WavefrontObject(new ResourceLocation(Taam.MOD_ID + ":models/logistics_station.obj"));
		textureLogisticsManager = new ResourceLocation(Taam.MOD_ID + ":textures/models/logistics_manager.png");
	}
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
	    if (event.phase == TickEvent.Phase.END) {
	    	rot++;
	    }
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		Block block = Block.getBlockFromItem(item.getItem());
		if(block instanceof BlockSensor) {
			return true;
		} else if(block instanceof BlockProductionLine) {
			return true;
		}
		return false;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		float x = 0;
		float y = 0;
		float z = 0;
		
		switch (type) {
		case ENTITY:
		case FIRST_PERSON_MAP:
		default:
			x = -0.5f;
			y = -0.5f;
			z = -0.5f;
			break;
		case INVENTORY:
			x = 0.125f;
			z = 0.125f;
			break;
		case EQUIPPED:
		case EQUIPPED_FIRST_PERSON:
			break;
		}
		
		if(item.getItem() == Item.getItemFromBlock(TaamMain.blockSensor)) {
			//TODO: Document meta components!
			int meta = item.getItemDamage() | 7;
			renderSensor(x, y, z, (meta & 7), false);
		} else if(item.getItem() == Item.getItemFromBlock(TaamMain.blockProductionLine)) {
			int meta = item.getItemDamage();
			switch(meta) {
			default:
			case 0:
				renderConveyor(null, x, y, z);
				break;
			case 1:
				renderConveyorHopper(null, x, y, z, false);
				break;
			case 2:
				renderConveyorHopper(null, x, y, z, true);
				break;
			case 3:
				// Sieve
				break;
			case 4:
				renderConveyorProcessor(null, x, y, z, TileEntityConveyorProcessor.Shredder);
				break;
			case 5:
				renderConveyorProcessor(null, x, y, z, TileEntityConveyorProcessor.Grinder);
				break;
			case 6:
				renderConveyorProcessor(null, x, y, z, TileEntityConveyorProcessor.Crusher);
				break;
			}
		}
		
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTickTime) {
		if (tileEntity instanceof TileEntitySensor) {
			TileEntitySensor te = ((TileEntitySensor) tileEntity);
			int meta = tileEntity.getBlockMetadata();
			switch (tileEntity.getBlockMetadata() & 8) {
			case 0:
				renderSensor(x, y, z, (meta & 7), te.isPowering() > 0);
				break;
			case 8:
				// TODO: renderMinect();
				break;
			}
		} else if (tileEntity instanceof TileEntityConveyor) {
			renderConveyor((TileEntityConveyor) tileEntity, x, y, z);
		} else if (tileEntity instanceof TileEntityConveyorHopper) {
			renderConveyorHopper((TileEntityConveyorHopper)tileEntity, x, y, z, false);
		} else if (tileEntity instanceof TileEntityConveyorProcessor) {
			renderConveyorProcessor((TileEntityConveyorProcessor) tileEntity, x, y, z, (byte)0);
		} else if (tileEntity instanceof TileEntityLogisticsStation) {
			renderLogisticsStation((TileEntityLogisticsStation) tileEntity, x, y, z);
		} else if (tileEntity instanceof TileEntityLogisticsManager) {
			renderLogisticsManager((TileEntityLogisticsManager) tileEntity, x, y, z);
		}
		
		if(tileEntity instanceof IConveyorAwareTE) {
			renderConveyorItems((IConveyorAwareTE) tileEntity, x, y, z);
		}
	}
	
	public void renderLogisticsStation(TileEntityLogisticsStation tileEntity, double x, double y, double z) {
		ForgeDirection direction;
		if(tileEntity instanceof IRotatable) {
			direction = ((IRotatable) tileEntity).getFacingDirection();
		} else {
			direction = ForgeDirection.SOUTH;
		}
		
		// Model Rendering
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		
		// Rotation
		GL11.glTranslatef(0.5f, 0, 0.5f);

		if(direction == ForgeDirection.WEST) {
			GL11.glRotatef(270, 0, 1, 0);
		} else if(direction == ForgeDirection.NORTH) {
			GL11.glRotatef(180, 0, 1, 0);
		} else if(direction == ForgeDirection.EAST) {
			GL11.glRotatef(90, 0, 1, 0);
		}
		
		GL11.glTranslated(-0.5, 0, -0.5);

		Minecraft.getMinecraft().renderEngine.bindTexture(textureLogisticsStation);

		modelLogisticsStation.renderPart("ControlPanel");

		GL11.glPopMatrix();
	}
	
	public void renderLogisticsManager(TileEntityLogisticsManager tileEntity, double x, double y, double z) {
		ForgeDirection direction;
		if(tileEntity instanceof IRotatable) {
			direction = ((IRotatable) tileEntity).getFacingDirection();
		} else {
			direction = ForgeDirection.SOUTH;
		}
		
		// Model Rendering
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		
		// Rotation
		GL11.glTranslatef(0.5f, 0, 0.5f);

		if(direction == ForgeDirection.WEST) {
			GL11.glRotatef(270, 0, 1, 0);
		} else if(direction == ForgeDirection.NORTH) {
			GL11.glRotatef(180, 0, 1, 0);
		} else if(direction == ForgeDirection.EAST) {
			GL11.glRotatef(90, 0, 1, 0);
		}
		
		GL11.glTranslated(-0.5, 0, -0.5);

		Minecraft.getMinecraft().renderEngine.bindTexture(textureLogisticsStation);

		modelLogisticsStation.renderPart("ControlPanel");

		GL11.glPopMatrix();
	}
	
	
	private void conveyorPrepareRendering(IConveyorAwareTE tileEntity, double x, double y, double z) {
		ForgeDirection direction = conveyorGetDirection(tileEntity);
		
		GL11.glPushMatrix();
		/*
		 * Translate to coordinates
		 */
		GL11.glTranslated(x, y, z);
		
		/*
		 * Rotate if needed
		 */
		GL11.glTranslatef(0.5f, 0, 0.5f);
		
		if(direction == ForgeDirection.WEST) {
			GL11.glRotatef(270, 0, 1, 0);
		} else if(direction == ForgeDirection.NORTH) {
			GL11.glRotatef(180, 0, 1, 0);
		} else if(direction == ForgeDirection.EAST) {
			GL11.glRotatef(90, 0, 1, 0);
		}

		GL11.glTranslated(-0.5, 0, -0.5);
		
		/*
		 * Bind Texture
		 */
		Minecraft.getMinecraft().renderEngine.bindTexture(textureConveyor);

		/*
		 * Render Support Frame
		 */
		modelConveyor.renderPart("Support_smdl");
	}
	
	private void conveyorEndRendering() {
		GL11.glPopMatrix();
	}
	
	private ForgeDirection conveyorGetDirection(IConveyorAwareTE tileEntity) {
		ForgeDirection direction;
		if(tileEntity instanceof IRotatable) {
			direction = ((IRotatable) tileEntity).getFacingDirection();
		} else {
			direction = ForgeDirection.SOUTH;
		}
		return direction;
	}
	
	public void renderConveyorItems(IConveyorAwareTE tileEntity, double x, double y, double z) {
		ForgeDirection direction = conveyorGetDirection(tileEntity);
		
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		
		if(tileEntity != null) {
			for(int slot = 0; slot < 9; slot++) {
				ItemStack itemStack = tileEntity.getItemAt(slot);
				if(itemStack == null) {
					continue;
				}
				
				float movementProgress = tileEntity.getMovementProgress(slot);
				if(movementProgress < 0) {
					movementProgress = 0;
				}
				int maxProgress = tileEntity.getMaxMovementProgress();
				
				float posX = (float)ConveyorUtil.getItemPositionX(slot, movementProgress / maxProgress, direction);
				float posY = 0.1f;
				float posZ = (float)ConveyorUtil.getItemPositionZ(slot, movementProgress / maxProgress, direction);
				
				GL11.glPushMatrix();
				GL11.glTranslatef(posX, posY, posZ);
				
				ei.setEntityItemStack(itemStack);

				RenderItem.renderInFrame = true;
				ri.doRender(ei, 0, .5f, 0, 0, 0);
				
				GL11.glPopMatrix();
			}
			if(tileEntity instanceof TileEntityConveyorProcessor) {
				TileEntityConveyorProcessor processor = (TileEntityConveyorProcessor) tileEntity;
				ItemStack processingStack = processor.getStackInSlot(0);
				if(processingStack != null) {
					GL11.glPushMatrix();
					Random rand = processor.getWorldObj().rand;
					GL11.glTranslatef(0.5f, -0.1f, 0.5f);
					if(!processor.isShutdown) {
						GL11.glTranslatef(
								0.015f * (1-rand.nextFloat()),
								0.025f * (1-rand.nextFloat()),
								0.015f * (1-rand.nextFloat()));
					}
					
					ei.setEntityItemStack(processingStack);

					RenderItem.renderInFrame = true;
					ri.doRender(ei, 0, .5f, 0, 0, 0);
					
					GL11.glPopMatrix();
				}
			}
		}
		
		GL11.glPopMatrix();
	}
	
	//TODO: Don't display metal cap when not pointing to wards a block
	public void renderConveyor(TileEntityConveyor tileEntity, double x, double y, double z) {
		
		conveyorPrepareRendering(tileEntity, x, y, z);
		
		if(tileEntity == null || !tileEntity.isEnd()) {
			modelConveyor.renderPart("Conveyor_Straight_csmdl");
		} else {
			modelConveyor.renderPart("Conveyor_End_cemdl");
			modelConveyor.renderPart("Conveyor_End_Cap_cecmdl");
		}
		
		GL11.glTranslated(0.5, 0, 0.5);
		GL11.glRotatef(180, 0, 1, 0);
		GL11.glTranslated(-0.5, 0, -0.5);
		
		if(tileEntity == null || !tileEntity.isBegin()) {
			modelConveyor.renderPart("Conveyor_Straight_csmdl");
		} else {
			modelConveyor.renderPart("Conveyor_End_cemdl");
		}
		
		if(tileEntity != null) {
			if(tileEntity.hasApplianceWithType(Taam.APPLIANCE_SPRAYER)) {
				modelConveyor.renderPart("Appliance_Sprayer_asmdl");
			}
		}
		
		GL11.glPopMatrix();
		
	}
	
	public void renderConveyorHopper(TileEntityConveyorHopper tileEntity, double x, double y, double z, boolean forceHighSpeed) {
		boolean highSpeed = forceHighSpeed;
		if(tileEntity != null) {
			forceHighSpeed = forceHighSpeed || tileEntity.isHighSpeed();
		}
		conveyorPrepareRendering(tileEntity, x, y, z);
		
		modelConveyor.renderPart("Conveyor_Hopper_chmdl");
		
		//TODO: Render HighSpeed features
		
		conveyorEndRendering();
	}
	
	public void renderConveyorProcessor(TileEntityConveyorProcessor tileEntity, double x, double y, double z, byte forceMode) {
		byte mode = forceMode;
		if(forceMode == 0 && tileEntity != null) {
			mode = tileEntity.getMode();
		}
		conveyorPrepareRendering(tileEntity, x, y, z);
		
		modelConveyor.renderPart("Conveyor_Processing_Chute_chutemdl");

		//TODO: Rotate Walzes (need to remove mirror first)
		
		modelConveyor.renderPart("Processor_Walzes");
		
		switch(mode) {
		case TileEntityConveyorProcessor.Crusher:
			modelConveyor.renderPart("BumpsCrusher");
			break;
		case TileEntityConveyorProcessor.Grinder:
			modelConveyor.renderPart("BumpsGrinder");
			break;
		case TileEntityConveyorProcessor.Shredder:
			modelConveyor.renderPart("BumpsShredder");
			break;
		}
		
		conveyorEndRendering();
	}
	
	public void renderSensor(double x, double y, double z, int rotation, boolean fixBlink) {
		GL11.glPushMatrix();
		
		GL11.glTranslatef((float) x + 0.5f, (float) y,
				(float) z + 0.5f);
		
		if((rot % 40) == 0 || fixBlink) {
			rot = 0;
			Minecraft.getMinecraft().renderEngine.bindTexture(textureSensorBlink);
		} else {
			Minecraft.getMinecraft().renderEngine.bindTexture(textureSensor);
		}
		ForgeDirection dir = ForgeDirection.getOrientation(rotation).getOpposite();
		switch(dir) {
		case DOWN:
			break;
		case UP:
			GL11.glTranslatef(0f, 1f, 0f);
			GL11.glRotatef(180f, 1.0f, 0, 0);
			break;
		case NORTH:
			GL11.glTranslatef(0f, 0.5f, -0.5f);
			GL11.glRotatef(90f, 1.0f, 0, 0);
			break;
		case SOUTH:
			GL11.glTranslatef(0f, 0.5f, 0.5f);
			GL11.glRotatef(90f, 1.0f, 0, 0);
			GL11.glRotatef(180f, 0, 0, 1.0f);
			break;
		case WEST:
			GL11.glTranslatef(-0.5f, 0.5f, 0f);
			GL11.glRotatef(90f, 1.0f, 0, 0);
			GL11.glRotatef(-90f, 0, 0, 1.0f);
			break;
		case EAST:
			GL11.glTranslatef(0.5f, 0.5f, 0f);
			GL11.glRotatef(90f, 1.0f, 0, 0);
			GL11.glRotatef(90f, 0, 0, 1.0f);
			break;
		case UNKNOWN:
			break;
		}
		// UNKNOWN means we are rendering for the inventory
		if(dir == ForgeDirection.UNKNOWN) {
			GL11.glScalef(0.125f, 0.125f, 0.125f);
			GL11.glRotatef(90f, 0, 1.0f, 0);
		} else {
			// 1/16th scale, as techne tends to be big..
			GL11.glScalef(0.0625f, 0.0625f, 0.0625f);
		}
		GL11.glRotatef(180f, 1.0f, 0, 0);
		
		modelSensor.renderPart("p1");
		modelSensor.renderPart("p2");
		modelSensor.renderPart("socket");
		
		if(dir != ForgeDirection.DOWN && dir != ForgeDirection.UP) {
			GL11.glRotatef(20f, 1.0f, 0, 0);
			GL11.glTranslatef(0f, 0f, 0.8f);
		}
		modelSensor.renderPart("device");

		GL11.glPopMatrix();
	}
}
