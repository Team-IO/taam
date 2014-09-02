package founderio.taam.conveyors;

import net.minecraft.item.ItemStack;

public interface IConveyorAwareTE {
	boolean addItemAt(ItemStack item, double x, double y, double z);
	boolean addItemAt(ItemWrapper item, double x, double y, double z);
	
}
