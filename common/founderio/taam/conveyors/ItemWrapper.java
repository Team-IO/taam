package founderio.taam.conveyors;

import codechicken.lib.inventory.InventoryUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemWrapper implements Comparable<ItemWrapper> {
	public ItemStack itemStack;
	public int progress;
	public int offset;
	public int processing;

	public ItemWrapper(ItemStack itemStack, int progress, int offset) {
		super();
		this.itemStack = itemStack;
		this.progress = progress;
		this.offset = offset;
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
		return String.format("ItemWrapper [itemStack=%s, progress=%d, offset=%d, processing=%d]",
				String.valueOf(itemStack), progress, offset, processing);
	}

	@Override
	public int compareTo(ItemWrapper other) {
		return progress - other.progress;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((itemStack == null) ? 0 : itemStack.hashCode());
		result = prime * result + offset;
		result = prime * result + processing;
		result = prime * result + progress;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemWrapper other = (ItemWrapper) obj;
		if (itemStack == null) {
			if (other.itemStack != null)
				return false;
		} else if (!itemStack.equals(other.itemStack))
			return false;
		if (offset != other.offset)
			return false;
		if (processing != other.processing)
			return false;
		if (progress != other.progress)
			return false;
		return true;
	}

	public NBTTagCompound writeToNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("progress", progress);
		tag.setInteger("processing", processing);
		tag.setInteger("offset", offset);
		if(itemStack != null) {
			itemStack.writeToNBT(tag);
		}
		return tag;
	}
	
	public static ItemWrapper readFromNBT(NBTTagCompound tag) {
		int progress = tag.getInteger("progress");
		int offset = tag.getInteger("offset");
		ItemStack itemStack = ItemStack.loadItemStackFromNBT(tag);
		ItemWrapper wrapper = new ItemWrapper(itemStack, progress, offset);
		wrapper.processing = tag.getInteger("processing");
		return wrapper;
	}

	public ItemWrapper copy() {
		ItemWrapper clone = new ItemWrapper(InventoryUtils.copyStack(itemStack, getStackSize()), progress, offset);
		clone.processing = processing;
		return clone;
	}

}