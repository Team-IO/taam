package net.teamio.taam.content.conveyors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.Constants.NBT;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;
import net.teamio.taam.util.inv.InventorySimple;
import net.teamio.taam.util.inv.InventoryUtils;

/**
 * Conveyor Item Bag.
 * Non-Ticking TE
 * @author founderio
 *
 */
public class TileEntityConveyorItemBag extends ATileEntityAttachable implements IConveyorAwareTE, IInventory, IRotatable {

	private InventorySimple inventory;
	
	public float fillPercent;
	public TileEntityConveyorItemBag() {
		inventory = new InventorySimple(5);
	}
	
	@Override
	public void updateRenderingInfo() {
		if(worldObj != null && worldObj.isRemote) {
			/*
			 * Fill display calculation is only needed on the client..
			 */
			
			float stackFactor = 1f / this.inventory.getSizeInventory();
			this.fillPercent = 0;
		
			for(int i = 0; i < this.inventory.getSizeInventory(); i++) {
				ItemStack stack = this.inventory.getStackInSlot(i);
				if(stack != null && stack.getItem() != null && stack.getMaxStackSize() > 0) {
					float singleFillFactor = stack.stackSize / (float)stack.getMaxStackSize();
					this.fillPercent += singleFillFactor * stackFactor;
				}
			}
		}
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setTag("items", InventoryUtils.writeItemStacksToTag(inventory.items));
		tag.setInteger("direction", direction.ordinal());
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		inventory.items = new ItemStack[inventory.getSizeInventory()];
		InventoryUtils.readItemStacksFromTag(inventory.items, tag.getTagList("items", NBT.TAG_COMPOUND));
		direction = EnumFacing.getFront(tag.getInteger("direction"));
		updateRenderingInfo();
	}

	/*
	 * IInventory implementation
	 */
	
	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = inventory.decrStackSize(slot, amount);
		updateState();
		updateRenderingInfo();
		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {
		return inventory.removeStackFromSlot(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
		updateState();
		updateRenderingInfo();
	}

	@Override
	public String getName() {
		return "tile.taam.productionline_attachable.itembag.name";
	}
	
	@Override
	public IChatComponent getDisplayName() {
		return new ChatComponentTranslation(getName());
	}
	
	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		// Nothing to do.
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		// Nothing to do.
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return inventory.isItemValidForSlot(slot, stack);
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

	/*
	 * IConveyorAwareTE implementation
	 */

	@Override
	public boolean shouldRenderItemsDefault() {
		return false;
	}

	@Override
	public EnumFacing getMovementDirection() {
		return EnumFacing.DOWN;
	}
	
	@Override
	public int insertItemAt(ItemStack item, int slot) {
		// insertItem returns item count unable to insert.
		int inserted = item.stackSize - InventoryUtils.insertItem(inventory, item, false);
		updateState();
		updateRenderingInfo();
		return inserted;
	}
	
	@Override
	public boolean canSlotMove(int slot) {
		return false;
	}
	
	@Override
	public int getMovementProgress(int slot) {
		return 0;
	}

	@Override
	public byte getSpeedsteps() {
		return 1;
	}

	@Override
	public ItemWrapper getSlot(int slot) {
		return ItemWrapper.EMPTY;
	}

	@Override
	public double getInsertMaxY() {
		return 0.9;
	}

	@Override
	public double getInsertMinY() {
		return 0.3;
	}

	public EnumFacing getNextSlot(int slot) {
		return EnumFacing.DOWN;
	}


	
}
