package net.teamio.taam.gui.advanced.apps;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.content.IRedstoneControlled;
import net.teamio.taam.gui.advanced.App;
import net.teamio.taam.gui.advanced.AppGui;
import net.teamio.taam.gui.advanced.ContainerAdvancedMachine;

public class RedstoneMode extends App {

	private final IRedstoneControlled redstoneControlled;

	public RedstoneMode(ContainerAdvancedMachine container, IRedstoneControlled redstoneControlled) {
		super(container);
		this.redstoneControlled = redstoneControlled;
		this.name = "taam.app.common.redstone";
	}

	@Override
	public void setupSlots() {
	}

	@Override
	public void onPacket(NBTTagCompound tag) {

	}

	@Override
	@SideOnly(Side.CLIENT)
	protected AppGui createGui() {
		return new RedstoneModeGui(this);
	}
}
