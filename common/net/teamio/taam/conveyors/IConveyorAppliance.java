package net.teamio.taam.conveyors;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.IFluidHandler;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;

public interface IConveyorAppliance extends ISidedInventory, IFluidHandler {
	
	public boolean canProcessItem(ItemWrapper wrapper);
	public void processItem(IConveyorApplianceHost conveyor, int slot, ItemWrapper wrapper);

	public ItemStack getItemStack();
	public void writeToNBT(NBTTagCompound tag);
	public void readFromNBT(NBTTagCompound tag);
}
