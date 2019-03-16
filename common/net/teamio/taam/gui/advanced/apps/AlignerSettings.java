package net.teamio.taam.gui.advanced.apps;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.conveyors.appliances.ApplianceAligner;
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
		// Currently no slots - the filter slots are handled as buttons,
		// as 1.12 introduced ItemStack.EMPTY and broke the fake slot implementation used before.
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
