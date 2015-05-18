package founderio.taam.conveyors;

import codechicken.lib.inventory.InventoryUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemWrapper {
	public ItemStack itemStack;
	public int processing;
	public boolean blocked;

	public ItemWrapper(ItemStack itemStack) {
		super();
		this.itemStack = itemStack;
	}

	public int getStackSize() {
		if(itemStack == null) {
			return 0;
		} else {
			return itemStack.stackSize;
		}
	}
	
	public void setStackSize(int stackSize) {
		if(itemStack != null) {
			itemStack.stackSize = stackSize;
		}
	}
	
	@Override
	public String toString() {
		return String.format("ItemWrapper [itemStack=%s, processing=%d]",
				String.valueOf(itemStack), processing);
	}

	public NBTTagCompound writeToNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("processing", processing);
		tag.setBoolean("blocked", blocked);
		if(itemStack != null) {
			itemStack.writeToNBT(tag);
		}
		return tag;
	}
	
	public static ItemWrapper readFromNBT(NBTTagCompound tag) {
		ItemStack itemStack = ItemStack.loadItemStackFromNBT(tag);
		ItemWrapper wrapper = new ItemWrapper(itemStack);
		wrapper.processing = tag.getInteger("processing");
		wrapper.blocked = tag.getBoolean("blocked");
		return wrapper;
	}

	public ItemWrapper copy() {
		ItemWrapper clone = new ItemWrapper(InventoryUtils.copyStack(itemStack, getStackSize()));
		clone.processing = processing;
		clone.blocked = blocked;
		return clone;
	}

}