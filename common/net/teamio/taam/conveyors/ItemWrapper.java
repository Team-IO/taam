package net.teamio.taam.conveyors;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.util.InventoryUtils;

/**
 * Wrapper for item stacks on the conveyor system. Keeps track of movement,
 * processing and blocking.
 *
 * @author oliverkahrmann
 */
public class ItemWrapper implements INBTSerializable<NBTTagCompound>{
	public static final ItemWrapper EMPTY = new ItemWrapper(null) {
		@Override
		public boolean isEmpty() {
			return true;
		}
	};

	public ItemStack itemStack;
	public byte movementProgress;
	
	@SideOnly(Side.CLIENT)
	private boolean stuck;

	public ItemWrapper() {
	}
	
	public ItemWrapper(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public boolean isEmpty() {
		return itemStack == null;
	}

	public int getStackSize() {
		if (itemStack == null) {
			return 0;
		}
		return itemStack.stackSize;
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
	
	/**
	 * Sets a client-side flag used for rendering. Stuck wrappers are not
	 * interpolated in between game ticks to prevent stuttering.
	 * 
	 * @param value
	 */
	@SideOnly(Side.CLIENT)
	public void setStuck(boolean value) {
		stuck = value;
	}

	/**
	 * Gets a client-side flag used for rendering. Stuck wrappers are not
	 * interpolated in between game ticks to prevent stuttering.
	 * 
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public boolean isStuck() {
		return stuck;
	}

	/**
	 * Checks whether the client-side rendering shall be interpolated in between
	 * game ticks.
	 * 
	 * Checks if the wrapper is neither stuck (client-side flag), blocked nor
	 * empty. (Last one especially for highlight box rendering)
	 * 
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public boolean isRenderingInterpolated() {
		return !isStuck() && !isBlocked() && !isEmpty();
	}

	@Override
	public String toString() {
		return String.format("ItemWrapper [itemStack=%s, movementProgress=%d]",
				String.valueOf(itemStack), movementProgress);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setByte("move", movementProgress);
		if (itemStack != null) {
			itemStack.writeToNBT(tag);
		}
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		itemStack = ItemStack.loadItemStackFromNBT(nbt);
		movementProgress = nbt.getByte("move");
	}

	public static ItemWrapper readFromNBT(NBTTagCompound nbt) {
		ItemWrapper wrapper = new ItemWrapper();
		wrapper.deserializeNBT(nbt);
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
		clone.movementProgress = movementProgress;
		return clone;
	}
}