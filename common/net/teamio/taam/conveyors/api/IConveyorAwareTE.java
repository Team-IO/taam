package net.teamio.taam.conveyors.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public interface IConveyorAwareTE {
	
	boolean canSlotMove(int slot);
	boolean isSlotAvailable(int slot);
	int getMovementProgress(int slot);
	int getSpeedsteps();
	
	IItemFilter getSlotFilter(int slot);
	
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
	ItemStack getItemAt(int slot);
	ForgeDirection getMovementDirection();
	/**
	 * Used to skip default item rendering on select machines,
	 * e.g. the processors.
	 * @return
	 */
	boolean shouldRenderItemsDefault();
	
}
