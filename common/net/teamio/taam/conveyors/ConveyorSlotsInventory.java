package net.teamio.taam.conveyors;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * A {@link IConveyorSlots} implementation that inputs into an inventory. No
 * extraction possible.
 *
 * @author Oliver Kahrmann
 *
 */
public class ConveyorSlotsInventory extends ConveyorSlotsBase {

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
	public int insertItemAt(ItemStack item, int slot, boolean simulate) {
		if (item == null || item.stackSize == 0) {
			return 0;
		}
		ItemStack notAdded = ItemHandlerHelper.insertItemStacked(inventory, item, simulate);
		int added = item.stackSize;
		if(notAdded != null)
			added -= notAdded.stackSize;
		return added;
	}

	@Override
	public ItemStack removeItemAt(int slot, int amount, boolean simulate) {
		return null;
	}

}
