package founderio.taam.conveyors;

import net.minecraft.item.ItemStack;

public class ItemWrapper {
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

}