package founderio.taam.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.common.util.Constants.NBT;
import codechicken.lib.inventory.InventoryRange;
import codechicken.lib.inventory.InventorySimple;
import codechicken.lib.inventory.InventoryUtils;
import founderio.taam.conveyors.ConveyorUtil;
import founderio.taam.conveyors.IConveyorAwareTE;
import founderio.taam.conveyors.ItemWrapper;


public class TileEntityConveyorHopper extends BaseTileEntity implements IConveyorAwareTE, IInventory {

	private InventorySimple inventory;
	
	public TileEntityConveyorHopper() {
		inventory = new InventorySimple(5, "Conveyor Hopper");
	}
	
	@Override
	public void updateEntity() {
		/*
		 * Find items laying on the conveyor.
		 */

		ConveyorUtil.tryInsertItemsFromWorld(this, worldObj, null, true);
		
		//TODO: Check Redstone Status
		
		IInventory inventory = InventoryUtils.getInventory(worldObj, xCoord, yCoord - 1, zCoord);
		if(inventory != null) {
			InventoryRange range = new InventoryRange(inventory, ForgeDirection.UP.ordinal());
			for(int i = 0; i < this.inventory.getSizeInventory(); i++) {
				if(InventoryUtils.stackSize(this.inventory, i) > 0) {
					System.out.println("Trying Transfer Stack");
					// Transfer ONE item down
					ItemStack oneItem = InventoryUtils.copyStack(this.inventory.getStackInSlot(i), 1);
					if(InventoryUtils.insertItem(range, oneItem, false) == 0) {
						InventoryUtils.decrStackSize(this.inventory, i, 1);
						updateState();
						break;
					}
				}
			}
		}
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setTag("items", InventoryUtils.writeItemStacksToTag(inventory.items));
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		InventoryUtils.readItemStacksFromTag(inventory.items, tag.getTagList("items", NBT.TAG_COMPOUND));
	}

	@Override
	public int addItemAt(ItemStack item, double x, double y, double z) {
		x -= xCoord;
		y -= yCoord;
		z -= zCoord;
		if(y < 0.4 || y > 1) {
			return 0;
		}
		if(x > 1.1 || x < -0.1 || z > 1.1 || z < -0.1) {
			return 0;
		}
		// insertItem returns item count unable to insert.
		int inserted = item.stackSize - InventoryUtils.insertItem(inventory, item, false);
		System.out.println("Inserting " + inserted);
		System.out.println("Inve Slot 0: " + inventory.getStackInSlot(0));
		return inserted;
	}

	@Override
	public int addItemAt(ItemWrapper item, double x, double y, double z) {
		return addItemAt(item.itemStack, x, y, z);
	}

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
		return inventory.decrStackSize(slot, amount);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
	}

	@Override
	public String getInventoryName() {
		return inventory.getInventoryName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return inventory.hasCustomInventoryName();
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

}
