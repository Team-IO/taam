package net.teamio.taam.content.conveyors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;
import net.teamio.taam.conveyors.api.IItemFilter;
import codechicken.lib.inventory.InventorySimple;
import codechicken.lib.inventory.InventoryUtils;


public class TileEntityConveyorItemBag extends ATileEntityAttachable implements IConveyorAwareTE, IInventory, IRotatable {

	private InventorySimple inventory;
	
	public float fillPercent;
	public TileEntityConveyorItemBag() {
		inventory = new InventorySimple(5);
	}
	
	@Override
	public void updateEntity() {
		if(worldObj.isRemote) {
			
			/*
			 * Fill display calculation is only needed on the client..
			 */
			
			float stackFactor = 1f / this.inventory.getSizeInventory();
			float fillFactor = 0;
		
			for(int i = 0; i < this.inventory.getSizeInventory(); i++) {
				ItemStack stack = this.inventory.getStackInSlot(i);
				if(stack != null && stack.getItem() != null && stack.getMaxStackSize() > 0) {
					float singleFillFactor = stack.stackSize / (float)stack.getMaxStackSize();
					fillFactor += singleFillFactor * stackFactor;
				}
			}
			float fillPercent = fillFactor;
			if(this.fillPercent != fillPercent) {
				this.fillPercent = fillPercent;
			}
			
			return;
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
		direction = ForgeDirection.getOrientation(tag.getInteger("direction"));
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
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
		updateState();
	}

	@Override
	public String getInventoryName() {
		return "tile.taam.productionline_attachable.itembag.name";
	}

	@Override
	public boolean hasCustomInventoryName() {
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
	public void openInventory() {
		// Nothing to do.
	}

	@Override
	public void closeInventory() {
		// Nothing to do.
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return inventory.isItemValidForSlot(slot, stack);
	}

	/*
	 * IConveyorAwareTE implementation
	 */

	@Override
	public boolean shouldRenderItemsDefault() {
		return false;
	}

	@Override
	public ForgeDirection getMovementDirection() {
		return ForgeDirection.DOWN;
	}
	
	@Override
	public int insertItemAt(ItemStack item, int slot) {
		// insertItem returns item count unable to insert.
		int inserted = item.stackSize - InventoryUtils.insertItem(inventory, item, false);
		updateState();
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
	public IItemFilter getSlotFilter(int slot) {
		return null;
	}

	@Override
	public int posX() {
		return xCoord;
	}

	@Override
	public int posY() {
		return yCoord;
	}

	@Override
	public int posZ() {
		return zCoord;
	}

	@Override
	public ItemStack getItemAt(int slot) {
		return null;
	}

	@Override
	public double getInsertMaxY() {
		return 0.9;
	}

	@Override
	public double getInsertMinY() {
		return 0.3;
	}


	
}
