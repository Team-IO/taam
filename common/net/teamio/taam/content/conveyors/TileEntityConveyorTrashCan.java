package net.teamio.taam.content.conveyors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.teamio.taam.Config;
import net.teamio.taam.Taam;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.conveyors.ConveyorSlotsBase;

/**
 * Conveyor Trash Can.
 * Non-Ticking TE
 *
 * @author Oliver Kahrmann
 */
public class TileEntityConveyorTrashCan extends ATileEntityAttachable implements IWorldInteractable {

	public float fillLevel;

	private final ConveyorSlotsBase conveyorSlots = new ConveyorSlotsBase() {

		{
			// Use default attachable slot matrix
			slotMatrix = SLOT_MATRIX;
			rotation = direction;
		}

		@Override
		public int insertItemAt(ItemStack stack, int slot, boolean simulate) {
			float added = stack.stackSize / (float) stack.getMaxStackSize();
			if (fillLevel + added < Config.pl_trashcan_maxfill) {
				fillLevel += added;
				updateState(true, false, false);
				return stack.stackSize;
			}
			return 0;
		}

		@Override
		public ItemStack removeItemAt(int slot, int amount, boolean simulate) {
			return null;
		}
	};

	private final IItemHandler itemHandler = new IItemHandler() {

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			float added = stack.stackSize / (float) stack.getMaxStackSize();
			if (fillLevel + added < Config.pl_trashcan_maxfill) {
				fillLevel += added;
				updateState(true, true, false);
				return null;
			}
			return stack;
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			return null;
		}

		@Override
		public int getSlots() {
			return 1;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return null;
		}
	};

	public TileEntityConveyorTrashCan() {
	}

	@Override
	public String getName() {
		return "tile.taam.productionline_attachable.trashcan.name";
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
		conveyorSlots.rotation = direction;
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
		if (capability == Taam.CAPABILITY_CONVEYOR) {
			return (T) conveyorSlots;
		}
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) itemHandler;
		}
		return super.getCapability(capability, facing);
	}

	public void clearOut() {
		fillLevel = 0;
		updateState(true, true, false);
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

	/*
	 * IWorldInteractable implementation
	 */

	@Override
	public boolean onBlockActivated(World world, EntityPlayer player, EnumHand hand, boolean hasWrench, EnumFacing side, float hitX, float hitY, float hitZ) {
		clearOut();
		return true;
	}

	@Override
	public boolean onBlockHit(World world, EntityPlayer player, boolean hasWrench) {
		return false;
	}
}
