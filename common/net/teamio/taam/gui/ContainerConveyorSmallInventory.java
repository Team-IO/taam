package net.teamio.taam.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerConveyorSmallInventory extends Container {
	protected final IItemHandler tileEntity;

	public ContainerConveyorSmallInventory(InventoryPlayer inventoryPlayer, ICapabilityProvider te, EnumFacing side) {
		this(inventoryPlayer, te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side));
	}
	
	public ContainerConveyorSmallInventory(InventoryPlayer inventoryPlayer, IItemHandler te) {
		tileEntity = te;

		for (int i = 0; i < te.getSlots(); i++) {
			addSlotToContainer(new SlotItemHandler(te, i, 44 + i * 18, 20));
		}

		bindPlayerInventory(inventoryPlayer);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, /*84*/51 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 109));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
		ItemStack stack = null;
		Slot slot = inventorySlots.get(slotID);

		// null checks and checks if the item can be stacked (maxStackSize > 1)
		if (slot != null && slot.getHasStack()) {
			ItemStack stackInSlot = slot.getStack();
			stack = stackInSlot.copy();

			// merges the item into player inventory since its in the tileEntity
			if (slotID < tileEntity.getSlots()) {
				if (!mergeItemStack(stackInSlot, tileEntity.getSlots(), inventorySlots.size(), true)) {
					return null;
				}
			}
			// merge into tileEntity inventory, since it is in player's inventory
			else if (!mergeItemStack(stackInSlot, 0, tileEntity.getSlots(), false)) {
				return null;
			}

			if (stackInSlot.stackSize == 0)
			{
				slot.putStack(null);
			}
			else
			{
				slot.onSlotChanged();
			}
		}
		return stack;
	}
}
