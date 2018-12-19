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

import javax.annotation.Nonnull;

/**
 * An app for the advanced GUI.
 * Wraps screen-like elements that can be switched seamlessly
 * when using {@link ContainerAdvancedMachine} and {@link GuiAdvancedMachine}.
 * Switch apps using {@link AppButton}.
 * <p>
 * Machines have to expose the {@link net.teamio.taam.Taam#CAPABILITY_ADVANCED_GUI}
 * which allows them to set up the supported apps.
 * Use The mod GUI ID {@literal 2} to open an advanced GUI screen, e.g. (in onBlockActivated):
 * {@code player.openGui(TaamMain.instance, 2, world, pos.getX(), pos.getY(), pos.getZ());}
 * <p>
 * Subclassing: Create an {@link AppGui} instance (subclass) in {@link #createGui()}.
 * Implement your custom features in the {@link App} subclass, communicated with the GUI on your own terms,
 * then use {@link #sendPacket(NBTTagCompound)} and {@link #onPacket(NBTTagCompound)}
 * to communicate with the backend instance of your app.
 *
 * @author Oliver Kahrmann
 */
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


	@Nonnull
	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(getName());
	}

}
