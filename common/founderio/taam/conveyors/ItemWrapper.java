package founderio.taam.conveyors;

import codechicken.lib.inventory.InventoryUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemWrapper {
	public ItemStack itemStack;
	public int processing;
	public byte movementProgress;

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
	
	public boolean isBlocked() {
		return movementProgress < 0;
	}
	
	/**
	 * Unblocks the itemstack if it is not blocky by an appliance
	 * @return
	 */
	public boolean unblock() {
		// Blocked by appliance
		if(movementProgress < -1) {
			return false;
		}
		if(movementProgress == -1) {
			movementProgress = 0;
		}
		return true;
	}
	
	/**
	 * Unblocks the itemstack, no matter what
	 */
	public void unblockForced() {
		if(movementProgress < 0) {
			movementProgress = 0;
		}
	}
	
	public void block() {
		if(movementProgress >= 0) {
			movementProgress = -1;
		}
	}

	public void blockAppliance() {
		movementProgress = -2;
	}
	
	@Override
	public String toString() {
		return String.format("ItemWrapper [itemStack=%s, processing=%d, movementProgress=%d]",
				String.valueOf(itemStack), processing, movementProgress);
	}

	public NBTTagCompound writeToNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("proc", processing);
		tag.setByte("move", movementProgress);
		if(itemStack != null) {
			itemStack.writeToNBT(tag);
		}
		return tag;
	}
	
	public static ItemWrapper readFromNBT(NBTTagCompound tag) {
		ItemStack itemStack = ItemStack.loadItemStackFromNBT(tag);
		ItemWrapper wrapper = new ItemWrapper(itemStack);
		wrapper.processing = tag.getInteger("proc");
		wrapper.movementProgress = tag.getByte("move");
		return wrapper;
	}

	public ItemWrapper copy() {
		ItemWrapper clone = new ItemWrapper(InventoryUtils.copyStack(itemStack, getStackSize()));
		clone.processing = processing;
		clone.movementProgress = movementProgress;
		return clone;
	}

	public void resetMovement() {
		if(!isBlocked()) {
			movementProgress = 0;
		}
	}
}