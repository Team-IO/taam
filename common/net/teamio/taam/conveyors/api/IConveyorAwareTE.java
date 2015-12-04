package net.teamio.taam.conveyors.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.teamio.taam.conveyors.ItemWrapper;

public interface IConveyorAwareTE {
	
	boolean canSlotMove(int slot);
	boolean isSlotAvailable(int slot);
	int getMovementProgress(int slot);
	byte getSpeedsteps();
	
	int posX();
	int posY();
	int posZ();
	
	/**
	 * 
	 * @param item
	 * @param slot
	 * @return The actual amount of items added
	 */
	int insertItemAt(ItemStack item, int slot);
	ForgeDirection getMovementDirection();
	ItemWrapper getSlot(int slot);
	ForgeDirection getNextSlot(int slot);
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
