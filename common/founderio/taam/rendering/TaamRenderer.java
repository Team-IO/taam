package founderio.taam.rendering;

import java.util.List;

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

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import founderio.taam.Taam;
import founderio.taam.TaamMain;
import founderio.taam.blocks.BlockProductionLine;
import founderio.taam.blocks.BlockSensor;
import founderio.taam.blocks.TileEntityConveyor;
import founderio.taam.blocks.TileEntityConveyorHopper;
import founderio.taam.blocks.TileEntityLogisticsManager;
import founderio.taam.blocks.TileEntityLogisticsStation;
import founderio.taam.blocks.TileEntitySensor;
import founderio.taam.conveyors.ConveyorUtil;
import founderio.taam.conveyors.IRotatable;
import founderio.taam.conveyors.ItemWrapper;

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
	
	public void tickEvent(TickEvent event) {
		if(event.type == Type.CLIENT && event.phase == Phase.END) {
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
		float offX = 0;
		float offY = 0;
		float offZ = 0;
		
		switch (type) {
		case ENTITY:
		case FIRST_PERSON_MAP:
		default:
			offX = -0.5f;
			offY = -0.5f;
			offZ = -0.5f;
			break;
		case INVENTORY:
			offX = 0.125f;
			offZ = 0.125f;
			break;
		case EQUIPPED:
		case EQUIPPED_FIRST_PERSON:
			break;
		}
		
		if(item.getItem() == Item.getItemFromBlock(TaamMain.blockSensor)) {
			//TODO: Document meta components!
			int meta = item.getItemDamage() | 7;
			renderSensor(offX, offY, offZ, (meta & 7), false);
		} else if(item.getItem() == Item.getItemFromBlock(TaamMain.blockProductionLine)) {
			int meta = item.getItemDamage();
			switch(meta) {
			
			}
			//TODO: distinction between single conveyer components
			renderConveyor(null, offX, offY, offZ);
		}
		
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float partialTickTime) {
		if(tileentity instanceof TileEntitySensor) {
			TileEntitySensor te = ((TileEntitySensor) tileentity);
			int meta = tileentity.getBlockMetadata();
			switch(tileentity.getBlockMetadata() & 8) {
			case 0:
				renderSensor(x, y, z, (meta & 7), te.isPowering() > 0);
				break;
			case 8:
				//TODO: renderMinect();
				break;
			}
		} else if(tileentity instanceof TileEntityConveyor || tileentity instanceof TileEntityConveyorHopper) {
			renderConveyor(tileentity, x, y, z);
		} else if(tileentity instanceof TileEntityLogisticsStation) {
			renderLogisticsStation((TileEntityLogisticsStation)tileentity, x, y, z);
		} else if(tileentity instanceof TileEntityLogisticsManager) {
			renderLogisticsManager((TileEntityLogisticsManager)tileentity, x, y, z);
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
	
	//TODO: Don't display metal cap when not pointing to wards a block
	public void renderConveyor(TileEntity tileEntity, double x, double y, double z) {
		
		
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
		
		// Rendering
		Minecraft.getMinecraft().renderEngine.bindTexture(textureConveyor);

		modelConveyor.renderPart("Support_smdl");
		
		TileEntityConveyor conveyor = null;
		
		if(tileEntity instanceof TileEntityConveyor) {
			conveyor = (TileEntityConveyor)tileEntity;
			if(conveyor == null || !conveyor.isEnd()) {
				modelConveyor.renderPart("Conveyor_Straight_csmdl");
			} else {
				modelConveyor.renderPart("Conveyor_End_cemdl");
				//TODO: Check if other conveyor part there or actually end of line
				modelConveyor.renderPart("Conveyor_End_Cap_cecmdl");
			}
			
			GL11.glTranslated(0.5, 0, 0.5);
			GL11.glRotatef(180, 0, 1, 0);
			GL11.glTranslated(-0.5, 0, -0.5);
			
			if(conveyor == null || !conveyor.isBegin()) {
				modelConveyor.renderPart("Conveyor_Straight_csmdl");
			} else {
				modelConveyor.renderPart("Conveyor_End_cemdl");
			}
			
			if(conveyor != null) {
				if(conveyor.hasApplianceWithType(Taam.APPLIANCE_SPRAYER)) {
					modelConveyor.renderPart("Appliance_Sprayer_asmdl");
				}
			}
			
		} else if(tileEntity instanceof TileEntityConveyorHopper) {
			modelConveyor.renderPart("Conveyor_Hopper_chmdl");
		}
		
		GL11.glPopMatrix();
		
		// Item Rendering
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		
		if(conveyor != null) {
			ItemWrapper[] items = conveyor.getItems();
			for(int slot = 0; slot < items.length; slot++) {
				ItemWrapper wrapper = items[slot];
				if(wrapper == null || wrapper.itemStack == null) {
					continue;
				}
				
				float movementProgress = (float)conveyor.getMovementProgress();
				if(wrapper.blocked) {
					movementProgress = 0;
				}
				int maxProgress = conveyor.getMaxMovementProgress();
				
				float posX = (float)ConveyorUtil.getItemPositionX(slot, movementProgress / maxProgress, direction);
				float posY = 0.1f;
				float posZ = (float)ConveyorUtil.getItemPositionZ(slot, movementProgress / maxProgress, direction);
				
				GL11.glPushMatrix();
				GL11.glTranslatef(posX, posY, posZ);
				
				ei.setEntityItemStack(wrapper.itemStack);

				RenderItem.renderInFrame = true;
				ri.doRender(ei, 0, .5f, 0, 0, 0);
				
				GL11.glPopMatrix();
			}
			
		}
		
		GL11.glPopMatrix();
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
//System.out.println(rotation);
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
		if(dir == ForgeDirection.UNKNOWN) {
			GL11.glScalef(0.125f, 0.125f, 0.125f);
			GL11.glRotatef(90f, 0, 1.0f, 0);
			// 1/16th scale, as techne tends to be big..
		} else {
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
