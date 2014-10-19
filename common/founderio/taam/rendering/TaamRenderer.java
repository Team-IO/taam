package founderio.taam.rendering;

import java.util.List;

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

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import founderio.taam.Taam;
import founderio.taam.TaamMain;
import founderio.taam.blocks.BlockProductionLine;
import founderio.taam.blocks.BlockSensor;
import founderio.taam.blocks.TileEntityConveyor;
import founderio.taam.blocks.TileEntityConveyorHopper;
import founderio.taam.blocks.TileEntitySensor;
import founderio.taam.conveyors.IRotatable;
import founderio.taam.conveyors.ItemWrapper;

public class TaamRenderer extends TileEntitySpecialRenderer implements IItemRenderer {

	public final TechneModel modelSensor;
	public final ResourceLocation textureSensor;
	public final ResourceLocation textureSensorBlink;
	
	public final WavefrontObject modelConveyor;
	public final ResourceLocation textureConveyor;
	
	private RenderItem ri;
	private EntityItem ei;
	private float rot = 0;
	
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
		}
	}

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
		} else if(tileEntity instanceof TileEntityConveyorHopper) {
			modelConveyor.renderPart("Conveyor_Hopper_chmdl");
		}
		
		GL11.glPopMatrix();
		
		// Item Rendering
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		
		if(conveyor != null) {
			// Turn one to the right
			ForgeDirection dirRotated = direction.getRotation(ForgeDirection.UP);
			
			List<ItemWrapper> items = conveyor.getItems();
			for(ItemWrapper wrapper : items) {
				if(wrapper == null || wrapper.itemStack == null) {
					continue;
				}
				
				float progress = wrapper.progress / 100f;
				if(direction.offsetX < 0 || direction.offsetZ < 0) {
					progress = 1-progress;
					progress *= -1;// cope for the fact that direction offset is negative
				}
				float offset = wrapper.offset / 100f;
				if(dirRotated.offsetX < 0 || dirRotated.offsetZ < 0) {
					offset = 1-offset;
					offset *= -1;// cope for the fact that direction offset is negative
				}
				
				// Absolute Position of the Item
				float absX = direction.offsetX * progress + dirRotated.offsetX * offset;
				float absY = 0.1f;
				float absZ = direction.offsetZ * progress + dirRotated.offsetZ * offset;
				
				
				GL11.glPushMatrix();
				GL11.glTranslatef(absX, absY, absZ);
				
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
