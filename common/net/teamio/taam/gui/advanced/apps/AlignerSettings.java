package net.teamio.taam.gui.advanced.apps;

import org.lwjgl.opengl.GL11;

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
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamMain;
import net.teamio.taam.conveyors.appliances.ApplianceAligner;
import net.teamio.taam.conveyors.filters.FilterEntry;
import net.teamio.taam.conveyors.filters.FilterSlot;
import net.teamio.taam.conveyors.filters.ItemFilterCustomizable;
import net.teamio.taam.gui.advanced.App;
import net.teamio.taam.gui.advanced.ContainerAdvancedMachine;
import net.teamio.taam.gui.advanced.GuiAdvancedMachine;
import net.teamio.taam.rendering.TaamRenderer;

public class AlignerSettings extends App {

	public static final ResourceLocation icon = new ResourceLocation("minecraft", "textures/items/stick.png");
	public static final ResourceLocation tex_machine_config = new ResourceLocation("taam", "textures/gui/machine_config.png");

	private final ApplianceAligner aligner;
	private final GuiCheckBox[] cbExcluding;
	
	public AlignerSettings(ContainerAdvancedMachine container, ApplianceAligner aligner) {
		super(container);
		this.aligner = aligner;
		cbExcluding = new GuiCheckBox[aligner.filters.length];
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
			FilterEntry[] entries = itemFilterCustomizable.getEntries();
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
		
		int leftOff =  guiTop + guiLeft + 230;
		int topOff = guiTop + ContainerAdvancedMachine.panelHeight / 2;

		int xSpace = 20;
		int ySpace = 40;
		
		int verticalOffset = topOff - aligner.filters.length / 2 * ySpace - 9;
		
		for (int i = 0; i < aligner.filters.length; i++) {
			ItemFilterCustomizable itemFilterCustomizable = aligner.filters[i];
			cbExcluding[i] = new GuiCheckBox(i, leftOff + xSpace * 3, verticalOffset + i * ySpace + 1, "Excluding", itemFilterCustomizable.isExcluding());
			gui.addButton(cbExcluding[i]);
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
			FilterEntry[] entries = itemFilterCustomizable.getEntries();
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
			cbExcluding[i].visible = true;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onHide(GuiAdvancedMachine gui) {
		for (int i = 0; i < cbExcluding.length; i++) {
			cbExcluding[i].visible = false;
		}
	}
}
