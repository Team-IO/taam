/*
    Copyright (C) 2013  Chicken-Bones, Oliver Kahrmann

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.teamio.taam.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.teamio.taam.Config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utilities for working with inventories. Some code is based on the
 * InventoryUtils by Chicken-Bones, as it seems CodeChickenLib is gone for
 * good..
 *
 * @author Chicken-Bones, Oliver Kahrmann
 * <p>
 * To avoid issues with licensing, this specific file is licensed under
 * the LGPL.
 */
public final class InventoryUtils {

	/**
	 * Tag for reading/writing stack sizes of more than 127 to NBT.
	 */
	public static final String QUANTITY_TAG = "Quantity";
	/**
	 * Tag for reading/writing the slot ID of item stacks to NBT.
	 */
	public static final String SLOT_TAG = "Slot";

	private InventoryUtils() {
		// Util Class
	}

	public static boolean isItem(@Nullable ItemStack stack, @Nonnull Item item, int meta) {
		return !isEmpty(stack) && stack.getItem() == item && stack.getMetadata() == meta;
	}

	/**
	 * Checks if the given stack is empty. ({@literal null} or {{@link ItemStack#getItem()}} == null
	 *
	 * @param stack The stack to be checked, may be null.
	 * @return true if the stack is null or empty
	 * @author Oliver Kahrmann
	 */
	public static boolean isEmpty(@Nullable ItemStack stack) {
		return stack == null || stack.getItem() == null || stack.stackSize == 0;
	}

	/**
	 * Replaces stacks with null items with a null stack.
	 *
	 * @param stack Any stack, including null
	 * @return null or a valid stack.
	 * @author Oliver Kahrmann
	 */
	public static ItemStack guardAgainstNull(@Nullable ItemStack stack) {
		if (isEmpty(stack)) return null;
		return stack;
	}

	/**
	 * Adds the given amount to the stack. Pass a negative amount to subtract.
	 * If this results in an empty stack, null is returned.
	 * Otherwise, the stack is adjusted and returned with the new stack size.
	 * No checks on upper/lower limits are done, the number is processed as is.
	 *
	 * @param stack  A non-empty stack. If an empty stack is passed in, null is returned.
	 * @param amount The amount to be added/subtracted (no checks are done!)
	 * @return null or the same stack with a different stack size (same object)
	 * @author Oliver Kahrmann
	 */
	public static ItemStack adjustCount(@Nullable ItemStack stack, int amount) {
		if (isEmpty(stack)) return null;

		int count = stack.stackSize + amount;
		if (count <= 0) {
			return null;
		}
		stack.stackSize = count;
		return stack;
	}

	/**
	 * Sets the stack size of the given stack.
	 * If this results in an empty stack, null is returned.
	 * Otherwise, the stack is adjusted and returned with the new stack size.
	 *
	 * @param stack A non-null stack. If an null stack is passed in, an empty stack is returned.
	 * @param count The amount to be set
	 * @return null or the same stack with a different stack size (same object)
	 * @author Oliver Kahrmann
	 */
	public static ItemStack setCount(@Nullable ItemStack stack, int count) {
		if (isEmpty(stack)) return null;

		if (count <= 0) {
			return null;
		}
		stack.stackSize = count;
		return stack;
	}

	/**
	 * Checks if two stacks can be stacked together.
	 * Compares item, metadata (if applicable), NBT, and if the stacks want to stack.
	 * Null and empty stacks can be stacked together, but not with non-empty stacks.
	 * Does not check the max stack size, only if the items are compatible.
	 *
	 * @param stack1 The first stack to be checked, may be null.
	 * @param stack2 The second stack to be checked, may be null.
	 * @return true if the two stack can be safely stacked into one.
	 * @author Oliver Kahrmann
	 */
	public static boolean canStack(@Nullable ItemStack stack1, @Nullable ItemStack stack2) {
		// Stackable if both are empty
		if (isEmpty(stack1)) return isEmpty(stack2);
		if (isEmpty(stack2)) return false;

		// Both stacks need to be able to stack
		if (!stack1.isStackable() || !stack2.isStackable()) return false;
		// Equal items & damage value
		if (!stack1.isItemEqual(stack2)) return false;
		// Equal NBT
		if (!ItemStack.areItemStackTagsEqual(stack2, stack1)) return false;

		return true;
	}

	/**
	 * Copies an itemstack with a new quantity
	 *
	 * @param stack    The original item stack. If an empty/null stack is passed in, an empty stack is returned.
	 * @param quantity The new quantity.
	 * @return A new item stack with the given quantity, or ItemStack.EMPTY if stack was
	 * null or empty
	 * @author Oliver Kahrmann based on code by Chicken-Bones
	 */
	public static ItemStack copyStack(@Nullable ItemStack stack, int quantity) {
		if (isEmpty(stack)) return null;

		ItemStack copy = stack.copy();
		copy.stackSize = quantity;
		return copy;
	}

	/**
	 * Drop an item into the world, optionally using random motion.
	 * <p>
	 * Does NOT check world.isRemote!
	 *
	 * @param stack        An item stack to be dropped as an {@link EntityItem}
	 * @param world        The destination world
	 * @param pos          Destination coordinates for the entity. 0.5 is added to each coordinate to spawn in the middle of the block.
	 * @param randomMotion Enable random motion like in vanilla code. Pass false to spawn the item without motion.
	 * @return The spawned EntityItem or null, if the given stack was empty.
	 * @author Oliver Kahrmann
	 */
	public static EntityItem dropItem(ItemStack stack, World world, BlockPos pos, boolean randomMotion) {
		return dropItem(stack, world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, randomMotion);
	}

	/**
	 * Drop an item into the world, optionally using random motion.
	 * <p>
	 * Does NOT check world.isRemote!
	 *
	 * @param stack        An item stack to be dropped as an {@link EntityItem}
	 * @param world        The destination world
	 * @param x            Destination coordinates for the entity
	 * @param y            Destination coordinates for the entity
	 * @param z            Destination coordinates for the entity
	 * @param randomMotion Enable random motion like in vanilla code. Pass false to spawn the item without motion.
	 * @return The spawned EntityItem or null, if the given stack was empty.
	 * @author Oliver Kahrmann based on code by Chicken-Bones
	 */
	public static EntityItem dropItem(@Nullable ItemStack stack, World world, double x, double y, double z, boolean randomMotion) {
		if (isEmpty(stack)) return null;

		EntityItem item = new EntityItem(world, x, y, z, stack);
		if (randomMotion) {
			item.motionX = world.rand.nextGaussian() * 0.05;
			item.motionY = world.rand.nextGaussian() * 0.05 + 0.2F;
			item.motionZ = world.rand.nextGaussian() * 0.05;
		}
		world.spawnEntity(item);
		return item;
	}

	/**
	 * Gets an {@link IItemHandler} from the given tile entity.
	 * <p>
	 * If enabled in the config, {@link ISidedInventory} and {@link IInventory}
	 * will be wrapped & returned accordingly.
	 *
	 * @param tileEntity
	 * @param side
	 * @return An {@link IItemHandler} for the given tileEntity, or null if no
	 * inventory was found or tileEntity was null.
	 * @author Oliver Kahrmann
	 */
	public static IItemHandler getInventory(TileEntity tileEntity, EnumFacing side) {
		if (tileEntity == null) {
			return null;
		}
		IItemHandler itemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
		if (itemHandler == null && Config.use_iinventory_compat) {
			if (tileEntity instanceof ISidedInventory) {
				itemHandler = new SidedInvWrapper((ISidedInventory) tileEntity, side);
			} else if (tileEntity instanceof IInventory) {
				itemHandler = new InvWrapper((IInventory) tileEntity);
			}
		}
		return itemHandler;
	}

	/**
	 * Gets an {@link IItemHandler} from the given position in the world.
	 * <p>
	 * If enabled in the config, {@link ISidedInventory} and {@link IInventory}
	 * will be wrapped & returned accordingly.
	 *
	 * @param world
	 * @param pos
	 * @param side
	 * @return An {@link IItemHandler} for the given tileEntity, or null if no
	 * inventory tileEntity was found.
	 * @author Oliver Kahrmann
	 */
	public static IItemHandler getInventory(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return getInventory(world.getTileEntity(pos), side);
	}

	/**
	 * Tries to drop an item into a player inventory or drops it at the
	 * specified coordinates.
	 * <p>
	 * <p>
	 * Checks world.isRemote before dropping items. Regular adding is also done
	 * on client side. Server-side inventory change is sent to client
	 * immediately.
	 *
	 * @param player
	 * @param stack
	 * @param pos
	 * @author Oliver Kahrmann
	 */
	public static void tryDropToInventory(EntityPlayer player, ItemStack stack, BlockPos pos) {
		tryDropToInventory(player, stack, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
	}

	/**
	 * Tries to drop an item into a player inventory or drops it at the
	 * specified coordinates.
	 * <p>
	 * Checks world.isRemote before dropping items. Regular adding is also done
	 * on client side. Server-side inventory change is sent to client
	 * immediately.
	 *
	 * @param player
	 * @param stack
	 * @param x
	 * @param y
	 * @param z
	 * @author Oliver Kahrmann
	 */
	public static void tryDropToInventory(EntityPlayer player, @Nullable ItemStack stack, double x, double y, double z) {
		if (isEmpty(stack)) return;
		if (player.capabilities.isCreativeMode) return;

		if (!player.inventory.addItemStackToInventory(stack)) {
			if (!player.world.isRemote) {
				dropItem(stack, player.world, x, y, z, true);
			}
		} else if (player instanceof EntityPlayerMP) {
			((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
		}
	}

	/**
	 * NBT item saving function with support for stack sizes > 32K.
	 * ItemStacks only write a byte of data, so max 127 items per stack.
	 * <p>
	 * Items are saved with their slot index, so null stacks can be skipped.
	 *
	 * @param items       The array of item stacks to be written. May contain null
	 *                    values, but not BE null.
	 * @param maxQuantity Determines if the stack size has to be written separately, and
	 *                    which data type is used. Careful, too small values result in data loss.
	 * @param sequential  If sequential, the slot number is not written. Empty stacks are simply skipped.
	 *                    Useful for internal arrays where the exact position is less important than the order.
	 * @return A new tag list containing the serialized item stacks.
	 * @author Oliver Kahrmann, based on code by Chicken-Bones
	 */
	public static NBTTagList writeItemStacksToTag(ItemStack[] items, int maxQuantity, boolean sequential) {
		NBTTagList tagList = new NBTTagList();
		for (int i = 0; i < items.length; i++) {
			if (!isEmpty(items[i])) {
				NBTTagCompound tag = new NBTTagCompound();
				if (!sequential) {
					tag.setShort(SLOT_TAG, (short) i);
				}
				items[i].writeToNBT(tag);

				if (maxQuantity > Short.MAX_VALUE)
					tag.setInteger(QUANTITY_TAG, items[i].stackSize);
				else if (maxQuantity > Byte.MAX_VALUE)
					tag.setShort(QUANTITY_TAG, (short) items[i].stackSize);

				tagList.appendTag(tag);
			}
		}
		return tagList;
	}

	/**
	 * NBT item loading function with support for stack sizes > 32K.
	 * ItemStacks only write a byte of data, so max 127 items per stack.
	 * <p>
	 * Items are loaded with the slot index, so make sure the array is sized big
	 * enough to fit all slots. Otherwise use the sequential flag where
	 * the slot index is not read.
	 * <p>
	 * Empty spots and invalid stacks in the items array are replaced with ItemStack.EMPTY.
	 *
	 * @param items      An appropriately sized array for the loaded items.
	 *                   You can use the tag count of the list for sequentially written tags.
	 * @param tagList    The NBT list to read from
	 * @param sequential Read the entries sequentially, ignoring a slot number if present.
	 *                   Useful for internal lists.
	 * @author Oliver Kahrmann, based on code by Chicken-Bones
	 */
	public static void readItemStacksFromTag(ItemStack[] items, NBTTagList tagList, boolean sequential) {
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = tagList.getCompoundTagAt(i);
			int slot;
			if (sequential) {
				slot = i;
			} else {
				slot = tag.getShort(SLOT_TAG);
			}
			items[slot] = ItemStack.loadItemStackFromNBT(tag);
			if (tag.hasKey(QUANTITY_TAG, Constants.NBT.TAG_SHORT))
				items[slot].stackSize = tag.getShort(QUANTITY_TAG);
			else if (tag.hasKey(QUANTITY_TAG, Constants.NBT.TAG_INT))
				items[slot].stackSize = tag.getInteger(QUANTITY_TAG);
		}
		for (int i = 0; i < items.length; i++) {
			if (isEmpty(items[i])) {
				items[i] = null;
			}
		}
	}
}
