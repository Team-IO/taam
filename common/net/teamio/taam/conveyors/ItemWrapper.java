package net.teamio.taam.conveyors;

import codechicken.lib.inventory.InventoryUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Wrapper for item stacks on the conveyor system. Keeps track of movement,
 * processing and blocking.
 * 
 * @author oliverkahrmann
 */
public class ItemWrapper {
	public ItemStack itemStack;
	public int processing;
	public byte movementProgress;

	public ItemWrapper(ItemStack itemStack) {
		super();
		this.itemStack = itemStack;
	}

	public int getStackSize() {
		if (itemStack == null) {
			return 0;
		} else {
			return itemStack.stackSize;
		}
	}

	public void setStackSize(int stackSize) {
		if (itemStack != null) {
			itemStack.stackSize = stackSize;
		}
	}

	public boolean isBlocked() {
		return movementProgress < 0;
	}

	/**
	 * Unblocks the itemstack if it is blocked (by an appliance)
	 */
	public void unblock() {
		if (movementProgress < 0) {
			movementProgress = 0;
		}
	}

	/**
	 * Blocks the item stack (will stay locked at zero progress) if it is not
	 * already moving. (movement progress has to be zero!)
	 * 
	 * @return true if it could be locked or was already locked.
	 */
	public boolean block() {
		if (movementProgress <= 0) {
			movementProgress = -1;
			return true;
		}
		return false;
	}

	/**
	 * Blocks the item stack (will stay locked at zero progress) even if it is
	 * already moving. (Will revert moving items!!)
	 */
	public void blockForce() {
		movementProgress = -1;
	}

	/**
	 * Resets movement if it is not blocked. (Result is a wrapper at zero
	 * progress, or blocked.)
	 */
	public void resetMovement() {
		if (!isBlocked()) {
			movementProgress = 0;
		}
	}

	@Override
	public String toString() {
		return String
				.format("ItemWrapper [itemStack=%s, processing=%d, movementProgress=%d]",
						String.valueOf(itemStack), processing, movementProgress);
	}

	public NBTTagCompound writeToNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("proc", processing);
		tag.setByte("move", movementProgress);
		if (itemStack != null) {
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

	/**
	 * Executes a deep-copy of this ItemWrapper.
	 * 
	 * @return An exact copy of this ItemWrapper, including an exact copy of its
	 *         contents.
	 */
	public ItemWrapper copy() {
		ItemWrapper clone = new ItemWrapper(
				InventoryUtils.copyStack(itemStack, getStackSize()));
		clone.processing = processing;
		clone.movementProgress = movementProgress;
		return clone;
	}
}