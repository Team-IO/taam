package founderio.taam.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import founderio.taam.blocks.TileEntityConveyorHopper;

public class ContainerConveyorHopper extends Container {
	 protected TileEntityConveyorHopper tileEntity;

	public ContainerConveyorHopper(InventoryPlayer inventoryPlayer,
			TileEntityConveyorHopper te) {
		tileEntity = te;

		for (int i = 0; i < te.getSizeInventory(); i++) {
			addSlotToContainer(new Slot(tileEntity, i, 44 + i * 18, 20));
		}

		bindPlayerInventory(inventoryPlayer);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
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
		Slot slot = (Slot) inventorySlots.get(slotID);

		// null checks and checks if the item can be stacked (maxStackSize > 1)
		if (slot != null && slot.getHasStack()) {
			ItemStack stackInSlot = slot.getStack();
			stack = stackInSlot.copy();
			
			// merges the item into player inventory since its in the tileEntity
			if (slotID < tileEntity.getSizeInventory()) {
				if (!this.mergeItemStack(stackInSlot, tileEntity.getSizeInventory(), this.inventorySlots.size(), true)) {
					return null;
				}
			}
			// merge into tileEntity inventory, since it is in player's inventory
			else if (!this.mergeItemStack(stackInSlot, 0, tileEntity.getSizeInventory(), false)) {
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
