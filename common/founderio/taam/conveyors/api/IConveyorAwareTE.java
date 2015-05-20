package founderio.taam.conveyors.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public interface IConveyorAwareTE {
	
	boolean canSlotMove(int slot);
	int getMovementProgress(int slot);
	int getMaxMovementProgress();
	
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
	
}
