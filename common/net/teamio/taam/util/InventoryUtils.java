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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.teamio.taam.Config;

/**
 * Utilities for working with inventories. Some code is based on the
 * InventoryUtils by Chicken-Bones, as it seems CodeChickenLib is gone for
 * good..
 * 
 * @author Chicken-Bones, Oliver Kahrmann
 * 
 *         To avoid issues with licensing, this specific file is licensed under
 *         the LGPL.
 * 
 */
public final class InventoryUtils {
	private InventoryUtils() {
		// Util Class
	}

	public static boolean canStack(ItemStack stack1, ItemStack stack2) {
		return stack1 == null || stack2 == null
				|| (stack1.getItem() == stack2.getItem()
						&& (!stack2.getHasSubtypes() || stack2.getItemDamage() == stack1.getItemDamage())
						&& ItemStack.areItemStackTagsEqual(stack2, stack1)) && stack1.isStackable();
	}

	/**
	 * Copies an itemstack with a new quantity
	 * 
	 * @param stack
	 *            The original item stack. Can be null.
	 * @param quantity
	 *            The new quantity.
	 * @return A new item stack with the given quantity, or null if stack was
	 *         null.
	 * 
	 * @author Chicken-Bones
	 */
	public static ItemStack copyStack(ItemStack stack, int quantity) {
		if (stack == null)
			return null;

		stack = stack.copy();
		stack.stackSize = quantity;
		return stack;
	}

	/**
	 * Drop an item into the world, using random motion.
	 * 
	 * Does NOT check world.isRemote!
	 * 
	 * @param stack
	 * @param world
	 * @param pos
	 * @return
	 * 
	 * @author Oliver Kahrmann
	 */
	public static EntityItem dropItem(ItemStack stack, World world, BlockPos pos) {
		return dropItem(stack, world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, true);
	}

	/**
	 * Drop an item into the world, optionally using random motion.
	 * 
	 * Does NOT check world.isRemote!
	 * 
	 * @param stack
	 * @param world
	 * @param pos
	 * @param randomMotion
	 * @return
	 * 
	 * @author Oliver Kahrmann
	 */
	public static EntityItem dropItem(ItemStack stack, World world, BlockPos pos, boolean randomMotion) {
		return dropItem(stack, world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, randomMotion);
	}

	/**
	 * Drop an item into the world, using random motion.
	 * 
	 * Does NOT check world.isRemote!
	 * 
	 * @param stack
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 * 
	 * @author Oliver Kahrmann
	 */
	public static EntityItem dropItem(ItemStack stack, World world, double x, double y, double z) {
		return dropItem(stack, world, x, y, z, true);
	}

	/**
	 * Drop an item into the world, optionally using random motion.
	 * 
	 * Does NOT check world.isRemote!
	 * 
	 * @param stack
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param randomMotion
	 * @return
	 * 
	 * @author Chicken-Bones, Oliver Kahrmann
	 */
	public static EntityItem dropItem(ItemStack stack, World world, double x, double y, double z,
			boolean randomMotion) {
		EntityItem item = new EntityItem(world, x, y, z, stack);
		if (randomMotion) {
			item.motionX = world.rand.nextGaussian() * 0.05;
			item.motionY = world.rand.nextGaussian() * 0.05 + 0.2F;
			item.motionZ = world.rand.nextGaussian() * 0.05;
		}
		world.spawnEntityInWorld(item);
		return item;
	}

	/**
	 * Gets an {@link IItemHandler} from the given tile entity.
	 * 
	 * If enabled in the config, {@link ISidedInventory} and {@link IInventory}
	 * will be wrapped & returned accordingly.
	 * 
	 * @param tileEntity
	 * @param side
	 * @return An {@link IItemHandler} for the given tileEntity, or null if no
	 *         inventory was found or tileEntity was null.
	 * 
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
	 * 
	 * If enabled in the config, {@link ISidedInventory} and {@link IInventory}
	 * will be wrapped & returned accordingly.
	 * 
	 * @param world
	 * @param pos
	 * @param side
	 * @return An {@link IItemHandler} for the given tileEntity, or null if no
	 *         inventory tileEntity was found.
	 * 
	 * @author Oliver Kahrmann
	 */
	public static IItemHandler getInventory(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return getInventory(world.getTileEntity(pos), side);
	}

	/**
	 * NBT item loading function with support for stack sizes > 32K.
	 * 
	 * Items are loaded with the slot index, so make sure the array is sized big
	 * enough to fit all slots. Otherwise use
	 * {@link #readItemStacksFromTagSequential(ItemStack[], NBTTagList)} where
	 * the slot index is not read.
	 * 
	 * @param items
	 *            An appropriately sized array for the loaded items.
	 * @param tagList
	 * 
	 * @author Chicken-Bones
	 */
	public static void readItemStacksFromTag(ItemStack[] items, NBTTagList tagList) {
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = tagList.getCompoundTagAt(i);
			int b = tag.getShort("Slot");
			items[b] = ItemStack.loadItemStackFromNBT(tag);
			if (tag.hasKey("Quantity"))
				items[b].stackSize = ((NBTPrimitive)tag.getTag("Quantity")).getInt();
		}
	}

	/**
	 * NBT item loading function with support for stack sizes > 32K. Reads the
	 * {@link ItemStack}s without checking the slot ID. Useful for internal
	 * lists.
	 * 
	 * @author Oliver Kahrmann, based on
	 *         {@link #readItemStacksFromTag(ItemStack[], NBTTagList)}
	 * 
	 * @param items
	 * @param tagList
	 * 
	 * @author Chicken-Bones
	 */
	public static void readItemStacksFromTagSequential(ItemStack[] items, NBTTagList tagList) {
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = tagList.getCompoundTagAt(i);
			int b = tag.getShort("Slot");
			items[b] = ItemStack.loadItemStackFromNBT(tag);
			if (tag.hasKey("Quantity"))
				items[b].stackSize = ((NBTPrimitive) tag.getTag("Quantity")).getInt();
		}
	}

	/**
	 * Tries to drop an item into a player inventory or drops it at the
	 * specified coordinates.
	 * 
	 * 
	 * Checks world.isRemote before dropping items. Regular adding is also done
	 * on client side. Server-side inventory change is sent to client
	 * immediately.
	 * 
	 * @param player
	 * @param stack
	 * @param pos
	 * 
	 * @author Oliver Kahrmann
	 */
	public static void tryDropToInventory(EntityPlayer player, ItemStack stack, BlockPos pos) {
		tryDropToInventory(player, stack, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
	}

	/**
	 * Tries to drop an item into a player inventory or drops it at the
	 * specified coordinates.
	 * 
	 * Checks world.isRemote before dropping items. Regular adding is also done
	 * on client side. Server-side inventory change is sent to client
	 * immediately.
	 * 
	 * @param player
	 * @param stack
	 * @param x
	 * @param y
	 * @param z
	 * 
	 * @author Oliver Kahrmann
	 */
	public static void tryDropToInventory(EntityPlayer player, ItemStack stack, double x, double y, double z) {
		if (player.capabilities.isCreativeMode) {
			return;
		}
		if (!player.inventory.addItemStackToInventory(stack)) {
			if (!player.worldObj.isRemote) {
				dropItem(stack, player.worldObj, x, y, z);
			}
		} else if (player instanceof EntityPlayerMP) {
			((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
		}
	}

	/**
	 * NBT item saving function. Uses the default
	 * {@link #writeItemStacksToTag(ItemStack[], int)} and assumes maxQuantity
	 * 64.
	 * 
	 * @param items
	 *            The array of item stacks to be written. May contain null
	 *            values, but not BE null.
	 * @return A new tag list containing the serialized item stacks.
	 * 
	 * @author Chicken-Bones
	 */
	public static NBTTagList writeItemStacksToTag(ItemStack[] items) {
		return writeItemStacksToTag(items, 64);
	}

	/**
	 * NBT item saving function with support for stack sizes > 32K.
	 * 
	 * Items are saved with their slot index, so null stacks can be skipped.
	 * 
	 * @param items
	 *            The array of item stacks to be written. May contain null
	 *            values, but not BE null.
	 * @param maxQuantity
	 *            Determines if the stack size has to be written separately, and
	 *            which data type is used.
	 * @return A new tag list containing the serialized item stacks.
	 * 
	 * @author Chicken-Bones
	 */
	public static NBTTagList writeItemStacksToTag(ItemStack[] items, int maxQuantity) {
		NBTTagList tagList = new NBTTagList();
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setShort("Slot", (short) i);
				items[i].writeToNBT(tag);

				if (maxQuantity > Short.MAX_VALUE)
					tag.setInteger("Quantity", items[i].stackSize);
				else if (maxQuantity > Byte.MAX_VALUE)
					tag.setShort("Quantity", (short) items[i].stackSize);

				tagList.appendTag(tag);
			}
		}
		return tagList;
	}

	/**
	 * NBT item saving function
	 * 
	 * Writes the itemStacks without adding the slot ID. Useful for internal
	 * lists. Uses the default
	 * {@link #writeItemStacksToTagSequential(ItemStack[], int)} and assumes
	 * maxQuantity 64.
	 * 
	 * @param items
	 *            The array of item stacks to be written. May contain null
	 *            values, but not BE null.
	 * @return A new tag list containing the serialized item stacks.
	 * 
	 * @author Oliver Kahrmann, based on
	 *         {@link #writeItemStacksToTag(ItemStack[])}
	 */
	public static NBTTagList writeItemStacksToTagSequential(ItemStack[] items) {
		return writeItemStacksToTagSequential(items, 64);
	}

	/**
	 * NBT item saving function with support for stack sizes > 32K
	 * 
	 * Writes the itemStacks without adding the slot ID. Useful for internal
	 * lists.
	 * 
	 * @author Oliver Kahrmann, based on
	 *         {@link #writeItemStacksToTag(ItemStack[], int)}
	 * @param items
	 *            The array of item stacks to be written. May contain null
	 *            values, but not BE null.
	 * @param maxQuantity
	 *            Determines if the stack size has to be written separately, and
	 *            which data type is used.
	 * @return A new tag list containing the serialized item stacks.
	 * 
	 * @author Oliver Kahrmann, based on
	 *         {@link #writeItemStacksToTag(ItemStack[])}
	 */
	public static NBTTagList writeItemStacksToTagSequential(ItemStack[] items, int maxQuantity) {
		NBTTagList tagList = new NBTTagList();
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null) {
				NBTTagCompound tag = new NBTTagCompound();
				items[i].writeToNBT(tag);

				if (maxQuantity > Short.MAX_VALUE)
					tag.setInteger("Quantity", items[i].stackSize);
				else if (maxQuantity > Byte.MAX_VALUE)
					tag.setShort("Quantity", (short) items[i].stackSize);

				tagList.appendTag(tag);
			}
		}
		return tagList;
	}
}
