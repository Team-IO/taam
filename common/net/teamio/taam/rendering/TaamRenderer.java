package net.teamio.taam.rendering;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.obj.WavefrontObject;
import net.minecraftforge.client.model.techne.TechneModel;
import net.minecraftforge.common.util.ForgeDirection;
import net.teamio.taam.Config;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.common.BlockChute;
import net.teamio.taam.content.common.BlockSensor;
import net.teamio.taam.content.common.TileEntityChute;
import net.teamio.taam.content.common.TileEntitySensor;
import net.teamio.taam.content.conveyors.ATileEntityAttachable;
import net.teamio.taam.content.conveyors.BlockProductionLine;
import net.teamio.taam.content.conveyors.BlockProductionLineAttachable;
import net.teamio.taam.content.conveyors.TileEntityConveyor;
import net.teamio.taam.content.conveyors.TileEntityConveyorHopper;
import net.teamio.taam.content.conveyors.TileEntityConveyorItemBag;
import net.teamio.taam.content.conveyors.TileEntityConveyorProcessor;
import net.teamio.taam.content.conveyors.TileEntityConveyorTrashCan;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class TaamRenderer extends TileEntitySpecialRenderer implements IItemRenderer {

	public final TechneModel modelSensor;
	public final ResourceLocation textureSensor;
	public final ResourceLocation textureSensorBlink;
	
	public final WavefrontObject modelConveyor;
	public final ResourceLocation textureConveyor;
	
	public final WavefrontObject modelChute;
	public final ResourceLocation textureChute;
	
	private RenderItem ri;
	private EntityItem ei;
	private float rot = 0;
	private float rot_sensor = 0;
	
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

		modelChute = new WavefrontObject(new ResourceLocation(Taam.MOD_ID + ":models/chute.obj"));
		textureChute = textureConveyor;

	}
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
	    if (event.phase == TickEvent.Phase.END) {
	    	rot++;
	    	rot_sensor++;
	    	if(rot_sensor > 360) {
	    		rot_sensor -= 360;
	    	}
	    }
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		if(item.getItem() == TaamMain.itemConveyorAppliance) {
			return true;
		}
		Block block = Block.getBlockFromItem(item.getItem());
		if(block instanceof BlockSensor) {
			return true;
		} else if(block instanceof BlockProductionLine) {
			return true;
		} else if(block instanceof BlockProductionLineAttachable) {
			return true;
		} else if(block instanceof BlockChute) {
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
			case 1:
			case 2:
				renderConveyor(null, x, y, z, meta);
				break;
			case 3:
				renderConveyorHopper(null, x, y, z, false);
				break;
			case 4:
				renderConveyorHopper(null, x, y, z, true);
				break;
			case 5:
				// Sieve
				break;
			case 6:
				renderConveyorProcessor(null, x, y, z, TileEntityConveyorProcessor.Shredder);
				break;
			case 7:
				renderConveyorProcessor(null, x, y, z, TileEntityConveyorProcessor.Grinder);
				break;
			case 8:
				renderConveyorProcessor(null, x, y, z, TileEntityConveyorProcessor.Crusher);
				break;
			}
		} else if(item.getItem() == Item.getItemFromBlock(TaamMain.blockProductionLineAttachable)) {
			int meta = item.getItemDamage() & 3;
			switch(meta) {
			default:
			case 0:
			case 1:
				renderItemBag(null, x, y, z, meta, true);
			}
		} else if(item.getItem() == TaamMain.itemConveyorAppliance) {
			int meta = item.getItemDamage();
			switch(meta) {
			default:
				break;
			case 0:
				conveyorPrepareRendering(null, x, y, z, false);
				renderConveyorAppliance(Taam.APPLIANCE_SPRAYER);
				conveyorEndRendering();
				break;
			case 1:
				conveyorPrepareRendering(null, x, y, z, false);
				//TODO: Render.
				conveyorEndRendering();
				break;
			}
		} else if(item.getItem() == Item.getItemFromBlock(TaamMain.blockChute)) {
			renderChute(x, y, z);
		}
		
	}

	private void renderItemBag(ATileEntityAttachable tileEntity, double x, double y, double z, int meta, boolean rotated) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);

		float fillPercent = 0;
		if(tileEntity instanceof TileEntityConveyorTrashCan) {
			fillPercent = ((TileEntityConveyorTrashCan) tileEntity).fillPercent;
			float maxFillPercent = Config.pl_trashcan_maxfill;
			fillPercent = fillPercent / maxFillPercent;
		} else if(tileEntity instanceof TileEntityConveyorItemBag) {
			fillPercent = ((TileEntityConveyorItemBag) tileEntity).fillPercent;
		}
		
		GL11.glTranslatef(0.5f, 0, 0.5f);
		
		int type = meta & 3;
		int rot = (meta & 12) >> 2;
		switch(rot) {
		default:
		case 0: // NORTH
			GL11.glRotatef(180, 0, 1, 0);
			break;
		case 1: // SOUTH
			break;
		case 2: // EAST
			GL11.glRotatef(90, 0, 1, 0);
			break;
		case 3: // WEST
			GL11.glRotatef(270, 0, 1, 0);
			break;
		}
		if(rotated) {
			GL11.glRotatef(180, 0, 1, 0);
		}

		GL11.glTranslated(-0.5, 0, -0.5);
		/*
		 * Bind Texture
		 */
		Minecraft.getMinecraft().renderEngine.bindTexture(textureConveyor);

		/*
		 * Render Support Frame
		 */
		switch(type) {
		default:
		case 0: // Bag
			modelConveyor.renderPart("Bag_bmdl");
			break;
		case 1: // TrashCan
			modelConveyor.renderPart("BagTrash_btmdl");
			break;
		}
		if(fillPercent > 0) {
			GL11.glTranslatef(0, fillPercent * 0.3f, 0);
			modelConveyor.renderPart("Bag_Filling_bfmdl");
		}
		
		GL11.glPopMatrix();
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
			TileEntityConveyor tec = (TileEntityConveyor) tileEntity;
			renderConveyor(tec, x, y, z, tec.getSpeedLevel());
		} else if (tileEntity instanceof TileEntityConveyorHopper) {
			renderConveyorHopper((TileEntityConveyorHopper)tileEntity, x, y, z, false);
		} else if (tileEntity instanceof TileEntityConveyorProcessor) {
			renderConveyorProcessor((TileEntityConveyorProcessor) tileEntity, x, y, z, (byte)0);
		} else if(tileEntity instanceof TileEntityChute) {
			renderChute(x, y, z);
		} else if(tileEntity instanceof TileEntityConveyorItemBag || tileEntity instanceof TileEntityConveyorTrashCan) {
			int meta = tileEntity.getBlockMetadata();
			renderItemBag((ATileEntityAttachable)tileEntity, x, y, z, meta, false);
		}
		
		if(tileEntity instanceof IConveyorAwareTE) {
			renderConveyorItems((IConveyorAwareTE) tileEntity, x, y, z);
		}
	}

	private void renderChute(double x, double y, double z) {
		GL11.glPushMatrix();
		/*
		 * Translate to coordinates
		 */
		GL11.glTranslated(x, y, z);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(textureChute);
		modelChute.renderAll();
		
		GL11.glPopMatrix();
	}
	
	private void conveyorPrepareRendering(IConveyorAwareTE tileEntity, double x, double y, double z, boolean isWood) {
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
		if(isWood) {
			modelConveyor.renderPart("Support_Wood_smdl_wood");
		} else {
			modelConveyor.renderPart("Support_Alu_smdl_alu");
		}
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
					RenderItem.renderInFrame = false;
					
					GL11.glPopMatrix();
				}
			}
			TileEntityConveyor conveyor;
			if(tileEntity instanceof TileEntityConveyor) {
				conveyor = (TileEntityConveyor) tileEntity;
				if(conveyor.getSpeedLevel() < 2) {
					conveyor = null;
				}
			} else {
				conveyor = null;
			}
			if(tileEntity.shouldRenderItemsDefault()) {
				for(int slot = 0; slot < 9; slot++) {
					ItemStack itemStack = tileEntity.getItemAt(slot);
					if(itemStack == null) {
						continue;
					}
					
					int movementProgress = tileEntity.getMovementProgress(slot);
					if(movementProgress < 0) {
						movementProgress = 0;
					}
					float speedsteps = tileEntity.getSpeedsteps();
					
					ForgeDirection renderDirection;
					if(conveyor == null) {
						renderDirection = direction;
					} else {
						renderDirection = ConveyorUtil.getHighspeedTransition(slot, direction);
					}
					
					float posX = (float)ConveyorUtil.getItemPositionX(slot, movementProgress / speedsteps, renderDirection);
					float posY = 0.1f;
					float posZ = (float)ConveyorUtil.getItemPositionZ(slot, movementProgress / speedsteps, renderDirection);
					
					GL11.glPushMatrix();
					GL11.glTranslatef(posX, posY, posZ);
					
					ei.setEntityItemStack(itemStack);
	
					RenderItem.renderInFrame = true;
					ri.doRender(ei, 0, .5f, 0, 0, 0);
					RenderItem.renderInFrame = false;
					
					GL11.glPopMatrix();
				}
			}
		}
		
		GL11.glPopMatrix();
	}
	
	//TODO: Don't display metal cap when not pointing to wards a block
	public void renderConveyor(TileEntityConveyor tileEntity, double x, double y, double z, int meta) {
		
		//TODO: Rendering depend on meta(=speedlevel)
		
		boolean isWood;
		boolean isHighSpeed;
		if(tileEntity == null) {
			isWood = meta == 0;
			isHighSpeed = meta == 2;
		} else {
			isWood = tileEntity.getSpeedLevel() == 0;
			isHighSpeed = tileEntity.getSpeedLevel() >= 2;
		}
		
		conveyorPrepareRendering(tileEntity, x, y, z, isWood);
		
		if(tileEntity != null && !tileEntity.isEnd()) {
			modelConveyor.renderPart("Conveyor_Straight_csmdl");
			if(isWood) {
				modelConveyor.renderPart("Conveyor_Straight_Framing_Wood_csmdl_wood");
				modelConveyor.renderPart("Conveyor_Straight_Walz_Wood_cwalzmdl_wood");
			} else {
				modelConveyor.renderPart("Conveyor_Straight_Framing_Alu_csmdl_alu");
				modelConveyor.renderPart("Conveyor_Straight_Walz_Alu_cwalzmdl_alu");
			}
		} else {
			modelConveyor.renderPart("Conveyor_End_cemdl");
			if(isWood) {
				modelConveyor.renderPart("Conveyor_End_Framing_Wood_cemdl_wood");
				modelConveyor.renderPart("Conveyor_End_Walz_Wood_cwalzmdl_wood");
				modelConveyor.renderPart("Conveyor_End_Cap_Wood_cecmdl_wood");
				modelConveyor.renderPart("Support_Caps_Wood_scmdl_wood");
			} else {
				modelConveyor.renderPart("Conveyor_End_Framing_Alu_cemdl_alu");
				modelConveyor.renderPart("Conveyor_End_Walz_Alu_cwalzmdl_alu");
				modelConveyor.renderPart("Conveyor_End_Cap_Alu_cecmdl_alu");
				modelConveyor.renderPart("Support_Caps_Alu_scmdl_alu");
			}
		}
		if(isHighSpeed) {
			modelConveyor.renderPart("Conveyor_High_Throughput_Framing_Alu_chtpmdl_alu");
		}
		
		GL11.glTranslated(0.5, 0, 0.5);
		GL11.glRotatef(180, 0, 1, 0);
		GL11.glTranslated(-0.5, 0, -0.5);
		
		if(tileEntity != null && !tileEntity.isBegin()) {
			modelConveyor.renderPart("Conveyor_Straight_csmdl");
			if(isWood) {
				modelConveyor.renderPart("Conveyor_Straight_Framing_Wood_csmdl_wood");
				modelConveyor.renderPart("Conveyor_Straight_Walz_Wood_cwalzmdl_wood");
			} else {
				modelConveyor.renderPart("Conveyor_Straight_Framing_Alu_csmdl_alu");
				modelConveyor.renderPart("Conveyor_Straight_Walz_Alu_cwalzmdl_alu");
			}
		} else {
			modelConveyor.renderPart("Conveyor_End_cemdl");
			if(isWood) {
				modelConveyor.renderPart("Conveyor_End_Framing_Wood_cemdl_wood");
				modelConveyor.renderPart("Conveyor_End_Walz_Wood_cwalzmdl_wood");
				modelConveyor.renderPart("Support_Caps_Wood_scmdl_wood");
			} else {
				modelConveyor.renderPart("Conveyor_End_Framing_Alu_cemdl_alu");
				modelConveyor.renderPart("Conveyor_End_Walz_Alu_cwalzmdl_alu");
				modelConveyor.renderPart("Support_Caps_Alu_scmdl_alu");
			}
		}
		
		if(tileEntity != null) {
			if(tileEntity.hasAppliance()) {
				renderConveyorAppliance(tileEntity.getApplianceType());
			}
		}
		
		GL11.glPopMatrix();
		
	}
	
	public void renderConveyorAppliance(String type) {
		if(Taam.APPLIANCE_SPRAYER.equals(type)) {
			modelConveyor.renderPart("Appliance_Sprayer_asmdl");
		}
	}
	
	public void renderConveyorHopper(TileEntityConveyorHopper tileEntity, double x, double y, double z, boolean forceHighSpeed) {
		boolean highSpeed = forceHighSpeed;
		if(tileEntity != null) {
			forceHighSpeed = forceHighSpeed || tileEntity.isHighSpeed();
		}
		conveyorPrepareRendering(tileEntity, x, y, z, false);
		
		modelConveyor.renderPart("Conveyor_Hopper_chmdl");
		modelConveyor.renderPart("Support_Caps_Alu_scmdl_alu");

		
		GL11.glTranslated(0.5, 0, 0.5);
		GL11.glRotatef(180, 0, 1, 0);
		GL11.glTranslated(-0.5, 0, -0.5);

		modelConveyor.renderPart("Support_Caps_Alu_scmdl_alu");
		//TODO: Render HighSpeed features
		
		conveyorEndRendering();
	}
	
	public void renderConveyorProcessor(TileEntityConveyorProcessor tileEntity, double x, double y, double z, byte forceMode) {
		byte mode = forceMode;
		if(forceMode == 0 && tileEntity != null) {
			mode = tileEntity.getMode();
		}
		conveyorPrepareRendering(tileEntity, x, y, z, false);
		
		modelConveyor.renderPart("Conveyor_Processing_Chute_chutemdl");
		modelConveyor.renderPart("Support_Caps_Alu_scmdl_alu");

		//TODO: Rotate Walzes (need to remove mirror first)
		


		
		renderConveyorProcessorWalz(mode);
		
		GL11.glTranslated(0.5, 0, 0.5);
		GL11.glRotatef(180, 0, 1, 0);
		GL11.glTranslated(-0.5, 0, -0.5);

		renderConveyorProcessorWalz(mode);

		modelConveyor.renderPart("Support_Caps_Alu_scmdl_alu");
		
		conveyorEndRendering();
	}
	
	private void renderConveyorProcessorWalz(byte mode) {
		GL11.glPushMatrix();
		GL11.glTranslated(0.5, 0.25683, 0.61245);
		GL11.glRotatef(-rot*16, 1, 0, 0);
		GL11.glTranslated(-0.5, -0.25683, -0.61245);
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
		GL11.glPopMatrix();
	}
	
	public void renderSensor(double x, double y, double z, int rotation, boolean fixBlink) {
		GL11.glPushMatrix();
		
		GL11.glTranslatef((float) x + 0.5f, (float) y,
				(float) z + 0.5f);
		
		if((rot_sensor % 40) == 0 || fixBlink) {
			rot_sensor = 0;
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
