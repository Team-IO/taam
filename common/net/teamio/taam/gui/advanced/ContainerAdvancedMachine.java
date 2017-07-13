package net.teamio.taam.gui.advanced;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Log;
import net.teamio.taam.conveyors.filters.HidableSlot;
import net.teamio.taam.network.TPAdvancedGuiAppData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContainerAdvancedMachine extends Container {

	public final IAdvancedMachineGUI machine;
	public final boolean isRemote;
	public final EntityPlayer player;
	
	/**
	 * The app that is active in the GUI. Only for rendering & GUI purposes, all
	 * other logic is handled by the apps themselves. (Server does not care
	 * which app is active)
	 */
	@SideOnly(Side.CLIENT)
	public App activeApp;

	/**
	 * Internal list of registered apps.
	 */
	private final List<App> apps = new ArrayList<App>();
	/**
	 * Public, unmodifiable access to the registered apps.
	 */
	public final List<App> registeredApps = Collections.unmodifiableList(apps);
	
	private int nextAppId = 0;
	public static final int panelHeight = 160;
	public static final int panelWidth = 340;
	
	public ContainerAdvancedMachine(EntityPlayer player, IAdvancedMachineGUI machine) {
		if(player == null) {
			throw new IllegalArgumentException("Attempted creation of " + getClass().getName() + " with null player.");
		}
		if(machine == null) {
			throw new IllegalArgumentException("Attempted creation of " + getClass().getName() + " with null machine.");
		}
		this.machine = machine;
		this.player = player;
		isRemote = player.worldObj.isRemote;
		
		bindPlayerInventory(player.inventory);
		
		machine.setup(this);
		
		for(App app : apps) {
			app.firstSlot = inventorySlots.size();
			app.setupSlots();
			app.slotCount = inventorySlots.size() - app.firstSlot;
		}
	}

	/**
	 * {@link #addSlotToContainer(Slot)} for access from apps.
	 * 
	 * @param slot
	 */
	public void addSlot(Slot slot) {
		addSlotToContainer(slot);
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		final int xOff = panelWidth / 2 - 176 / 2 + 8;//91;
		final int yOff = panelHeight + 5;
		final int yOffHotbar = yOff + 58;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, xOff + j * 18, yOff + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, xOff + i * 18, yOffHotbar));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		machine.markDirty();
	}
	
	public void onAppPacket(TPAdvancedGuiAppData message) {
		if(message.appContainerId < 0 || message.appContainerId >= apps.size()) {
			Log.error("Received update packet for app {}. List size: {}. Packet was discarded.", message.appContainerId, apps.size());
			return;
		}
		App app = apps.get(message.appContainerId);
		try {
			app.onPacket(message.tag);
		} catch (Exception e) {
			Log.error("Error processing update packet for app " + message.appContainerId, e);
		}
	}

	/**
	 * Registers a new App with this container. Called from the constructor
	 * {@link App#App(ContainerAdvancedMachine)}. You do not need to call this
	 * yourself.
	 * 
	 * @param app
	 * @return
	 */
	public int register(App app) {
		apps.add(app);
		return nextAppId++;
	}

	/**
	 * Switches apps & updates slot visibility where possible.
	 * 
	 * @param app
	 */
	public void switchApp(App app) {
		this.activeApp = app;
		for (Slot slot : inventorySlots) {
			if (slot instanceof HidableSlot) {
				boolean slotEnabled = app != null && slot.slotNumber >= app.firstSlot
						&& slot.slotNumber < app.firstSlot + app.slotCount;
				((HidableSlot) slot).setEnabled(slotEnabled);
			}
		}
	}

}
