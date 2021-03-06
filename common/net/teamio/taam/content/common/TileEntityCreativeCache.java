package net.teamio.taam.content.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.util.InventoryUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityCreativeCache extends BaseTileEntity implements IInventory, IWorldInteractable {

	private ItemStack template = ItemStack.EMPTY;

	public void setTemplate(@Nullable ItemStack stack) {
		if (InventoryUtils.isEmpty(stack)) {
			template = ItemStack.EMPTY;
		} else {
			template = stack.copy();
		}
	}

	public ItemStack getTemplate() {
		return template;
	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		if (!InventoryUtils.isEmpty(template)) {
			NBTTagCompound templateTag = new NBTTagCompound();
			template.writeToNBT(templateTag);
			tag.setTag("template", templateTag);
		}
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		NBTTagCompound templateTag = tag.getCompoundTag("template");
		if (templateTag != null) {
			template = new ItemStack(templateTag);
		} else {
			template = ItemStack.EMPTY;
		}
	}

	@Override
	public boolean onBlockActivated(World world, EntityPlayer player, EnumHand hand, boolean hasWrench, EnumFacing side,
	                                float hitX, float hitY, float hitZ) {
		ItemStack stack = player.inventory.getStackInSlot(player.inventory.currentItem);
		setTemplate(stack);
		updateState(true, false, true);
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
	public boolean isEmpty() {
		return InventoryUtils.isEmpty(template);
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		if (InventoryUtils.isEmpty(template)) {
			return ItemStack.EMPTY;
		}
		return template.copy();
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(int slot, int amount) {
		if (InventoryUtils.isEmpty(template)) {
			return ItemStack.EMPTY;
		}
		return InventoryUtils.setCount(template.copy(), Math.min(amount, template.getMaxStackSize()));
	}

	@Override
	@Nonnull
	public ItemStack removeStackFromSlot(int slot) {
		if (InventoryUtils.isEmpty(template)) {
			return ItemStack.EMPTY;
		}
		return template.copy();
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return false;
	}

	@Override
	public String getName() {
		return "tile.taam.machines.creativecache.name";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation("tile.taam.machines.creativecache.name");
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
}
