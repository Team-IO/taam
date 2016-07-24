package net.teamio.taam.gui.advanced.apps;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.conveyors.appliances.ApplianceAligner;
import net.teamio.taam.conveyors.filters.FilterSlot;
import net.teamio.taam.conveyors.filters.ItemFilterCustomizable;
import net.teamio.taam.gui.advanced.App;
import net.teamio.taam.gui.advanced.AppGui;
import net.teamio.taam.gui.advanced.ContainerAdvancedMachine;

public class AlignerSettings extends App {

	final ApplianceAligner aligner;

	public AlignerSettings(ContainerAdvancedMachine container, ApplianceAligner aligner) {
		super(container);
		this.aligner = aligner;
		this.name = "taam.app.settings.aligner";
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected AppGui createGui() {
		return new AlignerSettingsGui(this);
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
				int x = leftOff + j * xSpace + 1;
				int y = verticalOffset + i * ySpace + 1;
				container.addSlot(new FilterSlot(itemFilterCustomizable, j, x, y));
			}
		}
	}

	@Override
	public void onPacket(NBTTagCompound tag) {
		// Load filter settings from client
		for (int i = 0; i < aligner.filters.length; i++) {
			NBTTagCompound filterTag = tag.getCompoundTag("filter" + i);
			ItemFilterCustomizable itemFilterCustomizable = aligner.filters[i];
			itemFilterCustomizable.deserializeNBT(filterTag);
		}
		// Make sure it is saved
		aligner.markDirty();
	}
}
