package net.teamio.taam.content.conveyors;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.teamio.taam.Config;
import net.teamio.taam.content.IRenderable;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;

/**
 * Conveyor Trash Can.
 * Non-Ticking TE
 * @author Oliver Kahrmann
 *
 */
public class TileEntityConveyorTrashCan extends ATileEntityAttachable implements IConveyorAwareTE, IInventory, IRenderable, IWorldInteractable {

	public float fillLevel;
	public static final List<String> parts = Collections.unmodifiableList(Lists.newArrayList("BagTrash_btmdl"));
	public static final List<String> parts_filled = Collections.unmodifiableList(Lists.newArrayList("BagTrash_btmdl", "BagFilling_bfmdl"));

	public TileEntityConveyorTrashCan() {
	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setFloat("fillLevel", fillLevel);
		tag.setInteger("direction", direction.ordinal());
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		fillLevel = tag.getFloat("fillLevel");
		direction = EnumFacing.getFront(tag.getInteger("direction"));
	}

	public void clearOut() {
		fillLevel = 0;
		updateState(true, true, false);
	}


	@Override
	public List<String> getVisibleParts() {
		if(fillLevel > 0) {
			return parts_filled;
		} else {
			return parts;
		}
	}

	/*
	 * IWorldInteractable implementation
	 */

	@Override
	public boolean onBlockActivated(World world, EntityPlayer player, boolean hasWrench, EnumFacing side, float hitX,
			float hitY, float hitZ) {
		clearOut();
		return true;
	}

	@Override
	public boolean onBlockHit(World world, EntityPlayer player, boolean hasWrench) {
		return false;
	}

	/*
	 * IInventory implementation
	 */

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		float added = stack.stackSize / (float)stack.getMaxStackSize();
		if(fillLevel + added < Config.pl_trashcan_maxfill) {
			fillLevel += added;
			updateState(true, true, false);
		}
	}

	@Override
	public String getName() {
		return "tile.productionline_attachable.trashcan.name";
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(getName());
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
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
		float add = stack.stackSize / (float)stack.getMaxStackSize();
		return fillLevel + add < Config.pl_trashcan_maxfill;
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
		clearOut();
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
		float added = item.stackSize / (float)item.getMaxStackSize();
		if(fillLevel + added < Config.pl_trashcan_maxfill) {
			fillLevel += added;
			updateState(true, true, false);
			return item.stackSize;
		}
		return 0;
	}

	@Override
	public ItemStack removeItemAt(int slot) {
		return null;
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
