package founderio.taam.conveyors;

import founderio.taam.conveyors.api.IConveyorApplianceHost;
import net.minecraft.item.ItemStack;


public interface IConveyorApplianceFactory {
	public IConveyorAppliance setUpApplianceInventory(String type, IConveyorApplianceHost conveyor);
	public ItemStack getItemStack(String type);
}
