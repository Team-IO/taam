/*
    Copyright (C) 2013  Chicken-Bones

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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

/**
 * Simple IInventory implementation with an array of items, name and maximum
 * stack size
 * 
 * @author Chicken-Bones
 *
 *         To avoid issues with licensing, this specific file is licensed under
 *         the LGPL.
 */
public class InventorySimple implements IInventory {
	public ItemStack[] items;
	public int limit;
	public String name;

	public InventorySimple(ItemStack[] items, int limit, String name) {
		this.items = items;
		this.limit = limit;
		this.name = name;
	}

	public InventorySimple(ItemStack[] items, String name) {
		this(items, 64, name);
	}

	public InventorySimple(ItemStack[] items, int limit) {
		this(items, limit, "inv");
	}

	public InventorySimple(ItemStack[] items) {
		this(items, 64, "inv");
	}

	public InventorySimple(int size, int limit, String name) {
		this(new ItemStack[size], limit, name);
	}

	public InventorySimple(int size, int limit) {
		this(size, limit, "inv");
	}

	public InventorySimple(int size, String name) {
		this(size, 64, name);
	}

	public InventorySimple(int size) {
		this(size, 64, "inv");
	}

	@Override
	public int getSizeInventory() {
		return items.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return items[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return InventoryUtils.decrStackSize(this, slot, amount);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return InventoryUtils.getStackInSlotOnClosing(this, slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		items[slot] = stack;
		markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return limit;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public void markDirty() {
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
	}

	@Override
	public String getCommandSenderName() {
		return "name";
	}

	@Override
	public boolean hasCustomName() {
		return true;
	}

	@Override
	public IChatComponent getDisplayName() {
		return new ChatComponentText(getCommandSenderName());
	}
}