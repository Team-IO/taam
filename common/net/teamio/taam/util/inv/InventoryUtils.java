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
package net.teamio.taam.util.inv;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Utilities for working with inventories. Mostly code is based on by
 * ChickenBones, as it seems CodeChickenLib is gone for good..
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

	/**
	 * Tries to drop an item into a player inventory or drops it at the
	 * specified coordinates.
	 * 
	 * Checks world.isRemote.
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
		}
	}

	/**
	 * Tries to drop an item into a player inventory or drops it at the
	 * specified coordinates.
	 * 
	 * Checks world.isRemote.
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
	 * Gets an IInventory from a coordinate with support for double chests
	 * 
	 * @param world
	 * @param pos
	 * @return
	 */
	public static IInventory getInventory(IBlockAccess world, BlockPos pos) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Consumes one item from slot in inventory with support for containers.
	 * 
	 * !! Stackable items with container item will cause issues!
	 * 
	 * @param inventory
	 * @param slot
	 */
	public static void consumeItem(IInventory inventory, int slot) {
		ItemStack stack = inventory.getStackInSlot(slot);
		Item item = stack.getItem();
		if (item.hasContainerItem(stack)) {
			ItemStack container = item.getContainerItem(stack);
			inventory.setInventorySlotContents(slot, container);
		} else {
			inventory.decrStackSize(slot, 1);
		}
	}

	/**
	 * Gets the size of the stack in a slot. Returns 0 on null stacks
	 * 
	 * @param inv
	 * @param slot
	 * @return
	 */
	public static int stackSize(IInventory inv, int slot) {
		ItemStack stack = inv.getStackInSlot(slot);
		return stack == null ? 0 : stack.stackSize;
	}

	/**
	 * Copies an itemstack with a new quantity
	 * 
	 * @param stack
	 * @param quantity
	 * @return
	 */
	public static ItemStack copyStack(ItemStack stack, int quantity) {
		if (stack == null)
			return null;

		stack = stack.copy();
		stack.stackSize = quantity;
		return stack;
	}

	/**
	 * @param simulate
	 *            If set to true, no items will actually be inserted
	 * @return The number of items unable to be inserted
	 */
	public static int insertItem(InventoryRange inv, ItemStack stack, boolean simulate) {
		stack = stack.copy();
		for (int pass = 0; pass < 2; pass++) {
			for (int slot : inv.slots) {
				ItemStack base = inv.inv.getStackInSlot(slot);
				if ((pass == 0) == (base == null))
					continue;
				int fit = fitStackInSlot(inv, slot, stack);
				if (fit == 0)
					continue;

				if (base != null) {
					stack.stackSize -= fit;
					if (!simulate) {
						base.stackSize += fit;
						inv.inv.setInventorySlotContents(slot, base);
					}
				} else {
					if (!simulate)
						inv.inv.setInventorySlotContents(slot, copyStack(stack, fit));
					stack.stackSize -= fit;
				}
				if (stack.stackSize == 0)
					return 0;
			}
		}
		return stack.stackSize;
	}

	public static int insertItem(IInventory inv, ItemStack stack, boolean simulate) {
		return insertItem(new InventoryRange(inv), stack, simulate);
	}

	public static int fitStackInSlot(InventoryRange inv, int slot, ItemStack stack) {
		ItemStack base = inv.inv.getStackInSlot(slot);
		if (!canStack(base, stack) || !inv.canInsertItem(slot, stack))
			return 0;

		int fit = base != null ? incrStackSize(base, inv.inv.getInventoryStackLimit() - base.stackSize)
				: inv.inv.getInventoryStackLimit();
		return Math.min(fit, stack.stackSize);
	}

	/**
	 * Static default implementation for IInventory method
	 */
	public static ItemStack decrStackSize(IInventory inv, int slot, int size) {
		ItemStack item = inv.getStackInSlot(slot);

		if (item != null) {
			if (item.stackSize <= size) {
				inv.setInventorySlotContents(slot, null);
				inv.markDirty();
				return item;
			}
			ItemStack itemstack1 = item.splitStack(size);
			if (item.stackSize == 0)
				inv.setInventorySlotContents(slot, null);
			else
				inv.setInventorySlotContents(slot, item);

			inv.markDirty();
			return itemstack1;
		}
		return null;
	}

	/**
	 * @return The quantity of items from addition that can be added to base
	 */
	public static int incrStackSize(ItemStack base, ItemStack addition) {
		if (canStack(base, addition))
			return incrStackSize(base, addition.stackSize);

		return 0;
	}

	public static boolean canStack(ItemStack stack1, ItemStack stack2) {
		return stack1 == null || stack2 == null
				|| (stack1.getItem() == stack2.getItem()
						&& (!stack2.getHasSubtypes() || stack2.getItemDamage() == stack1.getItemDamage())
						&& ItemStack.areItemStackTagsEqual(stack2, stack1)) && stack1.isStackable();
	}

	/**
	 * @return The quantity of items from addition that can be added to base
	 */
	public static int incrStackSize(ItemStack base, int addition) {
		int totalSize = base.stackSize + addition;

		if (totalSize <= base.getMaxStackSize())
			return addition;
		else if (base.stackSize < base.getMaxStackSize())
			return base.getMaxStackSize() - base.stackSize;

		return 0;
	}

	/**
	 * Static default implementation for IInventory method
	 */
	public static ItemStack getStackInSlotOnClosing(IInventory inv, int slot) {
		ItemStack stack = inv.getStackInSlot(slot);
		inv.setInventorySlotContents(slot, null);
		return stack;
	}

	/**
	 * Gets an IInventory from a coordinate with support for double chests
	 * 
	 * @param world
	 * @param pos
	 * @return
	 */
	public static IInventory getInventory(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof IInventory))
			return null;

		if (tile instanceof TileEntityChest)
			return getChest((TileEntityChest) tile);
		return (IInventory) tile;

	}

	/**
	 * Gets the {@link InventoryLargeChest} for a chest tile entity, if it is a
	 * double chest. Else simply returns the inventory provided.
	 * 
	 * @param chest
	 *            The chest TileEntity to check for double chest.
	 * @return
	 */
	public static IInventory getChest(TileEntityChest chest) {
		for (EnumFacing fside : Plane.HORIZONTAL) {
			if (chest.getWorld().getBlockState(chest.getPos().offset(fside)).getBlock() == chest.getBlockType())
				return new InventoryLargeChest("container.chestDouble",
						(TileEntityChest) chest.getWorld().getTileEntity(chest.getPos().offset(fside)), chest);
		}
		return chest;
	}

	/**
	 * NBT item saving function
	 */
	public static NBTTagList writeItemStacksToTag(ItemStack[] items) {
		return writeItemStacksToTag(items, 64);
	}

	/**
	 * NBT item saving function with support for stack sizes > 32K
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
	 * NBT item loading function with support for stack sizes > 32K
	 */
	public static void readItemStacksFromTag(ItemStack[] items, NBTTagList tagList) {
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = tagList.getCompoundTagAt(i);
			int b = tag.getShort("Slot");
			items[b] = ItemStack.loadItemStackFromNBT(tag);
			if (tag.hasKey("Quantity"))
				items[b].stackSize = ((NBTBase.NBTPrimitive) tag.getTag("Quantity")).getInt();
		}
	}

}
