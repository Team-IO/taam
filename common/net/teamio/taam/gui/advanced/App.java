package net.teamio.taam.gui.advanced;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IWorldNameable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.TaamMain;
import net.teamio.taam.network.TPAdvancedGuiAppData;

public abstract class App implements IWorldNameable {

	public final ContainerAdvancedMachine container;
	public final int appContainerId;
	/**
	 * The first slot that belongs to this app. Set by the
	 * {@link ContainerAdvancedMachine}. Used for enabling & disabling slots
	 * that can be disabled. Set before {@link #setupSlots()}
	 */
	public int firstSlot;
	/**
	 * The number of slot that belongs to this app. Set by the
	 * {@link ContainerAdvancedMachine}. Used for enabling & disabling slots
	 * that can be disabled. Set after {@link #setupSlots()}
	 */
	public int slotCount;
	
	protected String name;

	@SideOnly(Side.CLIENT)
	public AppGui gui;
	
	public App(ContainerAdvancedMachine container) {
		this.container = container;
		appContainerId = container.register(this);
	}

	public abstract void setupSlots();

	@SideOnly(Side.CLIENT)
	protected abstract AppGui createGui();

	public void sendPacket(NBTTagCompound tag) {
		TPAdvancedGuiAppData message = new TPAdvancedGuiAppData(tag, appContainerId);
		if (container.isRemote) {
			TaamMain.network.sendToServer(message);
		} else {
			TaamMain.network.sendTo(message, (EntityPlayerMP) container.player);
		}
	}

	public abstract void onPacket(NBTTagCompound tag);
	
	/*
	 * IWorldNameable implementation
	 */


	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public boolean hasCustomName() {
		return false;
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(getName());
	}

}
