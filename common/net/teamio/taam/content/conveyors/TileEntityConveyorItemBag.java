package net.teamio.taam.content.conveyors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;
import net.teamio.taam.conveyors.api.IItemFilter;
import codechicken.lib.inventory.InventorySimple;
import codechicken.lib.inventory.InventoryUtils;


public class TileEntityConveyorItemBag extends BaseTileEntity implements IConveyorAwareTE, IInventory, IRotatable {

	private InventorySimple inventory;
	
	public int fillPercent;
	private ForgeDirection direction = ForgeDirection.NORTH;
	
	public TileEntityConveyorItemBag() {
		inventory = new InventorySimple(5);
	}
	
	@Override
	public void updateEntity() {
		if(worldObj.isRemote) {
			return;
		}
		
		float stackFactor = 1f / this.inventory.getSizeInventory();
		float fillFactor = 0;
	
		for(int i = 0; i < this.inventory.getSizeInventory(); i++) {
			ItemStack stack = this.inventory.getStackInSlot(i);
			if(stack != null && stack.getItem() != null && stack.getMaxStackSize() > 0) {
				float singleFillFactor = stack.stackSize / (float)stack.getMaxStackSize();
				fillFactor = singleFillFactor * stackFactor;
			}
		}
		int fillPercent = Math.round(fillFactor * 100);
		if(this.fillPercent != fillPercent) {
			this.fillPercent = fillPercent;
			updateState();
		}
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setTag("items", InventoryUtils.writeItemStacksToTag(inventory.items));
		tag.setInteger("fillPercent", fillPercent);
		tag.setInteger("direction", direction.ordinal());
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		inventory.items = new ItemStack[inventory.getSizeInventory()];
		InventoryUtils.readItemStacksFromTag(inventory.items, tag.getTagList("items", NBT.TAG_COMPOUND));
		fillPercent = tag.getInteger("fillPercent");
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
		System.out.println("Dec " + slot);
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
		System.out.println("Set " + slot + " " + stack);
		updateState();
	}

	@Override
	public String getInventoryName() {
		return "tile.taam.productionline.item_bag.name";
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
		System.out.println("Insert " + slot + " " + item);
		updateState();
		return inserted;
	}
	
	@Override
	public boolean canSlotMove(int slot) {
		return false;
	}
	
	@Override
	public boolean isSlotAvailable(int slot) {
		switch(direction) {
		default:
		case NORTH:
			return slot == 2 || slot == 5 || slot == 8;
		case EAST:
			return slot == 6 || slot == 7 || slot == 8;
		case SOUTH:
			return slot == 0 || slot == 3 || slot == 6;
		case WEST:
			return slot == 0 || slot == 1 || slot == 2;
		}
	}

	@Override
	public int getMovementProgress(int slot) {
		return 0;
	}

	@Override
	public int getSpeedsteps() {
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

	/*
	 * IRotatable implementation
	 */
	
	@Override
	public ForgeDirection getFacingDirection() {
		return direction;
	}

	@Override
	public ForgeDirection getMountDirection() {
		return ForgeDirection.DOWN;
	}

	@Override
	public ForgeDirection getNextFacingDirection() {
		return direction.getRotation(ForgeDirection.UP);
	}

	@Override
	public ForgeDirection getNextMountDirection() {
		return ForgeDirection.DOWN;
	}

	@Override
	public void setFacingDirection(ForgeDirection direction) {
		this.direction = direction;
		//if(!worldObj.isRemote) {
			int dir;
			switch(direction) {
			default:
			case NORTH:
				dir = 0;
				break;
			case SOUTH:
				dir = 1;
				break;
			case WEST:
				dir = 2;
				break;
			case EAST:
				dir = 3;
				break;
			}
			int worldMeta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, (worldMeta & 3) + (dir << 2), 3);
			updateState();
		//}
	}

	@Override
	public void setMountDirection(ForgeDirection direction) {
		// Nope, will not change that.
	}
	
}
