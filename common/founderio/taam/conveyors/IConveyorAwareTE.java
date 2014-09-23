package founderio.taam.conveyors;

import net.minecraft.item.ItemStack;

public interface IConveyorAwareTE {
	/**
	 * Insert ItemStack into conveyor system at the specified location.
	 * @param item
	 * @param x World Global X
	 * @param y World Global Y
	 * @param z World Global Z
	 * @return The number of items actually inserted.
	 */
	int addItemAt(ItemStack item, double x, double y, double z);

	/**
	 * Insert an existing ItemWrapper into the conveyor system at the specified location.
	 * @param item
	 * @param x World Global X
	 * @param y World Global Y
	 * @param z World Global Z
	 * @return The number of items actually inserted.
	 * If the number is equal to the stacksize the sent instance is to be considered re-used and NOT MODIFIABLE anymore!
	 */
	int addItemAt(ItemWrapper item, double x, double y, double z);

}
