package net.teamio.taam.conveyors.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.teamio.taam.conveyors.ItemWrapper;

public interface IConveyorSlots {
	
	boolean canSlotMove(int slot);
	boolean isSlotAvailable(int slot);
	int getMovementProgress(int slot);
	byte getSpeedsteps();
	
	/**
	 * Add an item to a slot.
	 * @param item
	 * @param slot
	 * @return The actual amount of items added
	 */
	int insertItemAt(ItemStack item, int slot);
	/**
	 * Remove an item from a slot.
	 * @param slot
	 * @return The itemStack that was in that slot before
	 */
	ItemStack removeItemAt(int slot);
	EnumFacing getMovementDirection();
	ItemWrapper getSlot(int slot);
	EnumFacing getNextSlot(int slot);
	float getVerticalPosition(int slot);
	/**
	 * Used to skip default item rendering on select machines,
	 * e.g. the processors.
	 * @return
	 */
	boolean shouldRenderItemsDefault();
	/**
	 * The minimum relative Y coord for items to be inserted
	 * @return
	 */
	double getInsertMaxY();
	/**
	 * The maximum relative Y coord for items to be inserted
	 * @return
	 */
	double getInsertMinY();
	
}
