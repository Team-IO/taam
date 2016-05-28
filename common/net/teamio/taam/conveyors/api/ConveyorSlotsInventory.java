package net.teamio.taam.conveyors.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.teamio.taam.conveyors.SlotMatrix;
import net.teamio.taam.util.inv.InventoryUtils;

/**
 * A {@link IConveyorSlots} implementation that inputs into an inventory. No
 * extraction possible.
 *
 * @author Oliver Kahrmann
 *
 */
public class ConveyorSlotsInventory extends ConveyorSlotsStatic {

	private IItemHandler inventory;

	/**
	 * Creates a new {@link ConveyorSlotsInventory} instance for an
	 * {@link IItemHandler} with all slots available.
	 *
	 * @param pos
	 * @param inventory
	 */
	public ConveyorSlotsInventory(IItemHandler inventory) {
		this.inventory = inventory;
	}

	/**
	 *
	 * Creates a new {@link ConveyorSlotsInventory} instance for an
	 * {@link IItemHandler} with a custom slot availability matrix.
	 *
	 * @param inventory
	 * @param slotMatrix
	 */
	public ConveyorSlotsInventory(IItemHandler inventory, SlotMatrix slotMatrix) {
		this.inventory = inventory;
		this.slotMatrix = slotMatrix;
	}

	@Override
	public int insertItemAt(ItemStack item, int slot) {
		if (item == null || item.stackSize == 0) {
			return 0;
		}
		int inserted = item.stackSize - InventoryUtils.insertItem(inventory, item, false);
		// TODO: updateState(false, false, false);
		return inserted;
	}

	@Override
	public ItemStack removeItemAt(int slot) {
		return null;
	}

}
