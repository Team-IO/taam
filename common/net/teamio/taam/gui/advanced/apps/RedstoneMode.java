package net.teamio.taam.gui.advanced.apps;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.content.IRedstoneControlled;
import net.teamio.taam.gui.advanced.App;
import net.teamio.taam.gui.advanced.ContainerAdvancedMachine;
import net.teamio.taam.gui.advanced.GuiAdvancedMachine;

public class RedstoneMode extends App {

	private final IRedstoneControlled redstoneControlled;

	public static final ResourceLocation icon = new ResourceLocation("minecraft", "textures/items/redstone_dust.png");

	public RedstoneMode(ContainerAdvancedMachine container, IRedstoneControlled redstoneControlled) {
		super(container);
		this.redstoneControlled = redstoneControlled;
	}

	@Override
	public void setupSlots() {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawBackground(GuiAdvancedMachine gui, float partialTicks, int mouseX, int mouseY) {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawForeground(GuiAdvancedMachine gui, int mouseX, int mouseY) {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getIcon() {
		return icon;
	}

	@Override
	public String getName() {
		return "taam.app.common.redstone";
	}

	@Override
	public void onPacket(NBTTagCompound tag) {

	}

	@Override
	public void onShow() {
		
	}

	@Override
	public void onHide() {
		
	}
}
