package net.teamio.taam.gui.advanced.apps;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
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
	public ResourceLocation getIcon() {
		return icon;
	}

	@Override
	public void setupSlots() {
		for (int i = 0; i < aligner.filters.length; i++) {
			ItemFilterCustomizable itemFilterCustomizable = aligner.filters[i];
			FilterEntry[] entries = itemFilterCustomizable.getEntries();
			for (int j = 0; j < entries.length; j++) {
				container.addSlot(new FilterSlot(itemFilterCustomizable, j, i * 20, j * 20));
			}
		}
	}

	@Override
	public void drawBackground(GuiAdvancedMachine gui, float partialTicks, int mouseX, int mouseY) {
	}

	@Override
	public void drawForeground(GuiAdvancedMachine gui, int mouseX, int mouseY) {
	}

	@Override
	public void onShow() {
		
	}

	@Override
	public void onHide() {
		
	}

	@Override
	public void onPacket(NBTTagCompound tag) {

	}

}
