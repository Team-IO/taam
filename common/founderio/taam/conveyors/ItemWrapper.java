package founderio.taam.conveyors;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemWrapper implements Comparable<ItemWrapper> {
	public ItemStack itemStack;
	public int progress;
	public int offset;

	public ItemWrapper(ItemStack itemStack, int progress, int offset) {
		super();
		this.itemStack = itemStack;
		this.progress = progress;
		this.offset = offset;
	}

	@Override
	public String toString() {
		return "ItemWrapper [itemStack=" + itemStack + ", progress=" + progress
				+ ", offset=" + offset + "]";
	}

	@Override
	public int compareTo(ItemWrapper other) {
		return progress - other.progress;
	}
	
	public NBTTagCompound writeToNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("progress", progress);
		tag.setInteger("offset", offset);
		itemStack.writeToNBT(tag);
		return tag;
	}
	
	public static ItemWrapper readFromNBT(NBTTagCompound tag) {
		int progress = tag.getInteger("progress");
		int offset = tag.getInteger("offset");
		ItemStack itemStack = ItemStack.loadItemStackFromNBT(tag);
		ItemWrapper wrapper = new ItemWrapper(itemStack, progress, offset);
		return wrapper;
	}

}