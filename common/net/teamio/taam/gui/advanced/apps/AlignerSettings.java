package net.teamio.taam.gui.advanced.apps;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamMain;
import net.teamio.taam.conveyors.appliances.ApplianceAligner;
import net.teamio.taam.conveyors.filters.FilterSlot;
import net.teamio.taam.conveyors.filters.ItemFilterCustomizable;
import net.teamio.taam.conveyors.filters.ItemFilterCustomizable.FilterMode;
import net.teamio.taam.gui.advanced.App;
import net.teamio.taam.gui.advanced.ContainerAdvancedMachine;
import net.teamio.taam.gui.advanced.GuiAdvancedMachine;
import net.teamio.taam.gui.util.CustomButton;
import net.teamio.taam.gui.util.Drawable;
import net.teamio.taam.rendering.TaamRenderer;

public class AlignerSettings extends App {

	public static final ResourceLocation icon = new ResourceLocation("minecraft", "textures/items/stick.png");
	public static final ResourceLocation tex_machine_config = new ResourceLocation("taam", "textures/gui/machine_config.png");

	private final ApplianceAligner aligner;
	private final CustomButton[] cbExcluding;
	private final CustomButton[] cbMode;
	private final CustomButton[] cbCheckNBT;
	private final CustomButton[] cbCheckMeta;
	
	public AlignerSettings(ContainerAdvancedMachine container, ApplianceAligner aligner) {
		super(container);
		this.aligner = aligner;
		cbExcluding = new CustomButton[aligner.filters.length];
		cbMode = new CustomButton[aligner.filters.length];
		cbCheckNBT = new CustomButton[aligner.filters.length];
		cbCheckMeta = new CustomButton[aligner.filters.length];
	}

	@Override
	public String getName() {
		return "taam.app.settings.aligner";
	}

	@Override
	public void setupSlots() {
		int leftOff = 230;
		int topOff = ContainerAdvancedMachine.panelHeight / 2;

		int xSpace = 20;
		int ySpace = 40;
		
		int verticalOffset = topOff - aligner.filters.length / 2 * ySpace - 9;
		
		for (int i = 0; i < aligner.filters.length; i++) {
			ItemFilterCustomizable itemFilterCustomizable = aligner.filters[i];
			ItemStack[] entries = itemFilterCustomizable.getEntries();
			for (int j = 0; j < entries.length; j++) {
				container.addSlot(new FilterSlot(itemFilterCustomizable, j, leftOff + j * xSpace + 1, verticalOffset + i * ySpace + 1));
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initGui(GuiAdvancedMachine gui) {
		int guiLeft = gui.getGuiLeft();
		int guiTop = gui.getGuiTop();
		
		int leftOff =  guiLeft + 230;
		int topOff = guiTop + ContainerAdvancedMachine.panelHeight / 2;

		int xSpace = 20;
		int ySpace = 40;
		
		int verticalOffset = topOff - aligner.filters.length / 2 * ySpace - 9;

		int buttonOffset = leftOff + xSpace * 3;

		for (int i = 0; i < aligner.filters.length; i++) {
			cbExcluding[i] = new CustomButton(i, leftOff, verticalOffset + i * ySpace + 19, 10, 10, "Excluding");
			cbExcluding[i].image = GuiAdvancedMachine.iconCheckbox;
			cbExcluding[i].textHorizontalAlignment = 4;
			cbExcluding[i].eventHandler = new Function<CustomButton, Boolean>() {
				
				@Override
				public Boolean apply(CustomButton input) {
					ItemFilterCustomizable filter = aligner.filters[input.id];
					filter.setExcluding(!filter.isExcluding());
					//TODO: Network
					onShow(null);
					return true;
				}
			};
			gui.addButton(cbExcluding[i]);
			
			cbMode[i] = new CustomButton(i, buttonOffset, verticalOffset + i * ySpace + 2, 14, 14, null);
			cbMode[i].image = GuiAdvancedMachine.iconMatchExact;
			cbMode[i].eventHandler = new Function<CustomButton, Boolean>() {
				
				@Override
				public Boolean apply(CustomButton input) {
					ItemFilterCustomizable filter = aligner.filters[input.id];
					filter.mode = FilterMode.getNext(filter.mode);
					//TODO: Network
					onShow(null);
					return true;
				}
			};
			gui.addButton(cbMode[i]);

			cbCheckMeta[i] = new CustomButton(i, buttonOffset + 14, verticalOffset + i * ySpace - 3, 12, 12, null);
			cbCheckMeta[i].image = GuiAdvancedMachine.iconCheckMeta;
			cbCheckMeta[i].eventHandler = new Function<CustomButton, Boolean>() {
				
				@Override
				public Boolean apply(CustomButton input) {
					ItemFilterCustomizable filter = aligner.filters[input.id];
					filter.checkMeta = !filter.checkMeta;
					//TODO: Network
					onShow(null);
					return true;
				}
			};
			gui.addButton(cbCheckMeta[i]);

			cbCheckNBT[i] = new CustomButton(i, buttonOffset + 14, verticalOffset + i * ySpace - 3 + 12, 12, 12, null);
			cbCheckNBT[i].image = GuiAdvancedMachine.iconDontCheckNBT;
			cbCheckNBT[i].eventHandler = new Function<CustomButton, Boolean>() {
				
				@Override
				public Boolean apply(CustomButton input) {
					ItemFilterCustomizable filter = aligner.filters[input.id];
					filter.checkNBT = !filter.checkNBT;
					//TODO: Network
					onShow(null);
					return true;
				}
			};
			gui.addButton(cbCheckNBT[i]);
		}
	}


	@Override
	public void onPacket(NBTTagCompound tag) {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getIcon() {
		return icon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawBackground(GuiAdvancedMachine gui, float partialTicks, int mouseX, int mouseY) {
		int guiLeft = gui.getGuiLeft();
		int guiTop = gui.getGuiTop();
		
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		textureManager.bindTexture(GuiAdvancedMachine.guiTexture);
		
		int leftOff = guiLeft + 230;

		int yCenter = guiTop + ContainerAdvancedMachine.panelHeight / 2;
		int xCenter = guiLeft + ContainerAdvancedMachine.panelWidth / 2;
		
		int xSpace = 20;
		int ySpace = 40;
		
		int verticalOffset = yCenter - aligner.filters.length / 2 * ySpace - 9;
		
		for (int i = 0; i < aligner.filters.length; i++) {
			ItemFilterCustomizable itemFilterCustomizable = aligner.filters[i];
			ItemStack[] entries = itemFilterCustomizable.getEntries();
			for (int j = 0; j < entries.length; j++) {
				gui.drawTexturedModalRect(leftOff + j * xSpace, verticalOffset + i * ySpace, 238, 0, 18, 18);
			}
		}
		


		final ItemStack renderStack = new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.conveyor2.ordinal());
		
		{
			textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

			GL11.glPushMatrix();

			RenderHelper.enableGUIStandardItemLighting();
			GlStateManager.enableRescaleNormal();

			GL11.glTranslated(xCenter, yCenter, 0.5);
			GL11.glScaled(60, -60, 2);
			GL11.glRotated(90, 1, 0, 0);
			float degrees = TaamRenderer.getRotationDegrees(aligner.conveyorDirection);
			GL11.glRotated(degrees, 0, 1, 0);

			RenderItem ri = Minecraft.getMinecraft().getRenderItem();
			IBakedModel model = ri.getItemModelMesher().getItemModel(renderStack);
			ri.renderItem(renderStack, model);

			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableRescaleNormal();

			GL11.glPopMatrix();
		}
		

		textureManager.bindTexture(GuiAdvancedMachine.guiTexture);
//		textureManager.bindTexture(tex_machine_config);
		

		GL11.glTranslated(0, 0, 1.5);
		
		gui.drawTexturedModalRect(leftOff - 30, yCenter - 32, 230, 18, 26, 63);
		
		if(aligner.conveyorDirection == aligner.getFacingDirection().rotateY()) {
			// Draw right Arrow
			gui.drawTexturedModalRect(xCenter + 6, yCenter - 9, 222, 18, 8, 17);
		} else if(aligner.conveyorDirection == aligner.getFacingDirection().rotateYCCW()) {
			// Draw left Arrow
			gui.drawTexturedModalRect(xCenter - 15, yCenter - 9, 201, 18, 8, 17);
		} else {
			// Draw Error Mark
			gui.drawTexturedModalRect(xCenter - 15, yCenter - 9, 201, 18, 29, 17);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawForeground(GuiAdvancedMachine gui, int mouseX, int mouseY) {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onShow(GuiAdvancedMachine gui) {
		for (int i = 0; i < cbExcluding.length; i++) {
			boolean isExact = false;
			Drawable image;
			switch(aligner.filters[i].mode) {
			default:
			case Exact:
				isExact = true;
				image = GuiAdvancedMachine.iconMatchExact;
				break;
			case Mod:
				image = GuiAdvancedMachine.iconMatchMod;
				break;
			case OreDict:
				image = GuiAdvancedMachine.iconMatchOredict;
				break;
			}
			cbExcluding[i].visible = true;
			cbExcluding[i].image = aligner.filters[i].isExcluding() ? GuiAdvancedMachine.iconCheckbox : null;

			cbMode[i].visible = true;
			cbMode[i].image = image;

			cbCheckNBT[i].visible = isExact;
			cbCheckNBT[i].image = aligner.filters[i].checkNBT ? GuiAdvancedMachine.iconCheckNBT : GuiAdvancedMachine.iconDontCheckNBT;
			cbCheckMeta[i].visible = isExact;
			cbCheckMeta[i].image = aligner.filters[i].checkMeta ? GuiAdvancedMachine.iconCheckMeta : GuiAdvancedMachine.iconDontCheckMeta;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onHide(GuiAdvancedMachine gui) {
		for (int i = 0; i < cbExcluding.length; i++) {
			cbExcluding[i].visible = false;
			cbMode     [i].visible = false; 
			cbCheckNBT [i].visible = false; 
			cbCheckMeta[i].visible = false; 
		}
	}
}
