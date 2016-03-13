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

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

/**
 * Inventory wrapper for unified ISided/IInventory access
 * 
 * @author Chicken-Bones
 *
 * To avoid issues with licensing, this specific file is licensed under the LGPL.
 */
public class InventoryRange {
	public IInventory inv;
    public EnumFacing face;
    public ISidedInventory sidedInv;
    public int[] slots;
    
    public InventoryRange(IInventory inv, int side)
    {
        this.inv = inv;
        this.face = EnumFacing.values()[side];
        if(inv instanceof ISidedInventory)
        {
            sidedInv = (ISidedInventory)inv;
            slots = sidedInv.getSlotsForFace(face);
        }
        else
        {
            slots = new int[inv.getSizeInventory()];
            for(int i = 0; i < slots.length; i++)
                slots[i] = i;
        }
    }

    public InventoryRange(IInventory inv)
    {
        this(inv, 0);
    }
    
    public InventoryRange(IInventory inv, int fslot, int lslot)
    {
        this.inv = inv;
        slots = new int[lslot-fslot];
        for(int i = 0; i < slots.length; i++)
            slots[i] = fslot+i;
    }

    public InventoryRange(IInventory inv, InventoryRange access)
    {
        this.inv = inv;
        this.slots = access.slots;
        this.face = access.face;
        if(inv instanceof ISidedInventory)
            sidedInv = (ISidedInventory) inv;
    }

    public boolean canInsertItem(int slot, ItemStack item)
    {
        return sidedInv == null ? inv.isItemValidForSlot(slot, item) : sidedInv.canInsertItem(slot, item, face);
    }
    
    public boolean canExtractItem(int slot, ItemStack item)
    {
        return sidedInv == null ? inv.isItemValidForSlot(slot, item) : sidedInv.canExtractItem(slot, item, face);
    }

    public int lastSlot()
    {
        int last = 0;
        for(int slot : slots)
            if(slot > last)
                last = slot;
        return last;
    }
}
