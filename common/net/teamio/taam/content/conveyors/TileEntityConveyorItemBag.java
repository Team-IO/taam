package net.teamio.taam.content.conveyors;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.teamio.taam.Taam;
import net.teamio.taam.conveyors.ConveyorSlotsInventory;

/**
 * Conveyor Item Bag.
 * Non-Ticking TE
 * @author founderio
 *
 */
public class TileEntityConveyorItemBag extends ATileEntityAttachable {

	private final ItemStackHandler itemHandler;
	private final ConveyorSlotsInventory conveyorSlots;

	public float fillPercent;

	public TileEntityConveyorItemBag() {
		itemHandler = new ItemStackHandler(5);
		conveyorSlots = new ConveyorSlotsInventory(itemHandler, SLOT_MATRIX) {
			@Override
			public void onChangeHook() {
				updateState(true, false, false);
			}
		};
		conveyorSlots.rotation = direction;
	}

	@Override
	public String getName() {
		return "tile.taam.productionline_attachable.itembag.name";
	}

	@Override
	public void blockUpdate() {
		if(world != null && world.isRemote) {
			/*
			 * Fill display calculation is only needed on the client..
			 */

			float stackFactor = 1f / itemHandler.getSlots();
			fillPercent = 0;

			for(int i = 0; i < itemHandler.getSlots(); i++) {
				ItemStack stack = itemHandler.getStackInSlot(i);
				if(stack != null && stack.getItem() != null && stack.getMaxStackSize() > 0) {
					float singleFillFactor = stack.getCount() / (float)stack.getMaxStackSize();
					fillPercent += singleFillFactor * stackFactor;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see net.teamio.taam.content.conveyors.ATileEntityAttachable#setFacingDirection(net.minecraft.util.EnumFacing)
	 *
	 * Overridden because of slots rotation
	 */
	@Override
	public void setFacingDirection(EnumFacing direction) {
		super.setFacingDirection(direction);
		conveyorSlots.rotation = direction;
	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setTag("items", itemHandler.serializeNBT());
		tag.setInteger("direction", direction.ordinal());
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		NBTTagCompound itemTag = tag.getCompoundTag("items");
		if(itemTag != null) {
			itemHandler.deserializeNBT(itemTag);
		}
		direction = EnumFacing.byIndex(tag.getInteger("direction"));
		conveyorSlots.rotation = direction;
		blockUpdate();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == Taam.CAPABILITY_CONVEYOR) {
			return true;
		}
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == Taam.CAPABILITY_CONVEYOR) {
			return (T) conveyorSlots;
		}
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) itemHandler;
		}
		return super.getCapability(capability, facing);
	}
}
