package net.teamio.taam.gui.advanced.apps;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.conveyors.appliances.ApplianceAligner;
import net.teamio.taam.conveyors.filters.FilterEntry;
import net.teamio.taam.conveyors.filters.FilterSlot;
import net.teamio.taam.conveyors.filters.ItemFilterCustomizable;
import net.teamio.taam.gui.advanced.App;
import net.teamio.taam.gui.advanced.ContainerAdvancedMachine;
import net.teamio.taam.gui.advanced.GuiAdvancedMachine;

public class AlignerSettings extends App {

	public static final ResourceLocation icon = new ResourceLocation("minecraft", "textures/items/stick.png");

	private final ApplianceAligner aligner;

	public AlignerSettings(ContainerAdvancedMachine container, ApplianceAligner aligner) {
		super(container);
		this.aligner = aligner;
	}

	@Override
	public String getName() {
		return "taam.app.settings.aligner";
	}

	@Override
	public void setupSlots() {
		int leftOff = 200;
		int topOff = ContainerAdvancedMachine.panelHeight / 2;

		int xSpace = 20;
		int ySpace = 50;
		
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
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawForeground(GuiAdvancedMachine gui, int mouseX, int mouseY) {
//TODO: Move to background & supply guiLeft + guiTop
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiAdvancedMachine.guiTexture);
		
		int leftOff = 200;
		int topOff = ContainerAdvancedMachine.panelHeight / 2;

		int xSpace = 20;
		int ySpace = 50;
		
		int verticalOffset = topOff - aligner.filters.length / 2 * ySpace - 9;
		
		for (int i = 0; i < aligner.filters.length; i++) {
			ItemFilterCustomizable itemFilterCustomizable = aligner.filters[i];
			FilterEntry[] entries = itemFilterCustomizable.getEntries();
			for (int j = 0; j < entries.length; j++) {
				gui.drawTexturedModalRect(leftOff + j * xSpace, verticalOffset + i * ySpace, 238, 0, 18, 18);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onShow(GuiAdvancedMachine gui) {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onHide(GuiAdvancedMachine gui) {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initGui(GuiAdvancedMachine gui) {
		
	}

}
