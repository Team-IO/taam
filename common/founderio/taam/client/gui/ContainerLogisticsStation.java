package founderio.taam.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import founderio.taam.blocks.TileEntityLogisticsStation;

public class ContainerLogisticsStation extends Container {
	 protected TileEntityLogisticsStation tileEntity;

	public ContainerLogisticsStation(InventoryPlayer inventoryPlayer,
			TileEntityLogisticsStation te) {
		tileEntity = te;

//		for (int i = 0; i < te.getSizeInventory(); i++) {
//			addSlotToContainer(new Slot(tileEntity, i, 44 + i * 18, 20));
//		}

		bindPlayerInventory(inventoryPlayer, 91, 185);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;//tileEntity.isUseableByPlayer(player);
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer, int x, int y) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, x + j * 18, y + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, x + i * 18, y + 58));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack stack = null;
		Slot slotObject = (Slot) inventorySlots.get(slot);

		// null checks and checks if the item can be stacked (maxStackSize > 1)
		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();

			// merges the item into player inventory since its in the tileEntity
			if (slot < 9) {
				if (!this.mergeItemStack(stackInSlot, 0, 35, true)) {
					return null;
				}
			}
			// places it into the tileEntity is possible since its in the player
			// inventory
			else if (!this.mergeItemStack(stackInSlot, 0, 9, false)) {
				return null;
			}

			if (stackInSlot.stackSize == 0) {
				slotObject.putStack(null);
			} else {
				slotObject.onSlotChanged();
			}

			if (stackInSlot.stackSize == stack.stackSize) {
				return null;
			}
			slotObject.onPickupFromSlot(player, stackInSlot);
		}
		return stack;
	}
}
