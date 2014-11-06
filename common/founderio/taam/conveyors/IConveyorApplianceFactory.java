package founderio.taam.conveyors;

import net.minecraft.item.ItemStack;


public interface IConveyorApplianceFactory {
	public IConveyorAppliance setUpApplianceInventory(String type, IConveyorApplianceHost conveyor);
	public ItemStack getItemStack(String type);
}
