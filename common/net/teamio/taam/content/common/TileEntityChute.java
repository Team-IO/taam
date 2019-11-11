package net.teamio.taam.content.common;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.teamio.taam.Taam;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.ConveyorSlotsBase;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.util.FluidHandlerOutputOnly;
import net.teamio.taam.util.FluidUtils;
import net.teamio.taam.util.InventoryUtils;
import net.teamio.taam.util.TaamUtil;
import org.apache.commons.lang3.ArrayUtils;

public class TileEntityChute extends BaseTileEntity implements IRotatable, ITickable {

	public boolean isConveyorVersion = false;
	private EnumFacing direction = EnumFacing.NORTH;
	private final FluidHandlerOutputOnly wrappedHandler = new FluidHandlerOutputOnly();

	private final ConveyorSlotsBase conveyorSlots = new ConveyorSlotsBase() {

		@Override
		public int insertItemAt(ItemStack stack, int slot, boolean simulate) {
			IItemHandler target = getTargetItemHandler();

			ItemStack notAdded = ItemHandlerHelper.insertItemStacked(target, stack, simulate);
			int added = stack.getCount();
			if (!InventoryUtils.isEmpty(notAdded))
				added -= notAdded.getCount();
			return added;
		}

		@Override
		public ItemStack removeItemAt(int slot, int amount, boolean simulate) {
			return ItemStack.EMPTY;
		}

		@Override
		public double getInsertMaxY() {
			return isConveyorVersion ? 0.9 : 1.3;
		}

		@Override
		public double getInsertMinY() {
			return isConveyorVersion ? 0.3 : 0.9;
		}
	};

	public TileEntityChute(boolean isConveyorVersion) {
		this.isConveyorVersion = isConveyorVersion;
	}

	public TileEntityChute() {
		this(false);
	}

	@Override
	public String getName() {
		return isConveyorVersion ? "tile.taam.productionline.chute.name" : "tile.taam.machines.chute.name";
	}

	@Override
	public void update() {
		// Skip item insertion if there is a solid block / other chute above us
		if (isConveyorVersion || !world.isSideSolid(pos.up(), EnumFacing.DOWN, false)) {
			ConveyorUtil.tryInsertItemsFromWorld(this, world, null, false);
		}
	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setBoolean("isConveyorVersion", isConveyorVersion);
		if (isConveyorVersion) {
			tag.setInteger("direction", direction.ordinal());
		}
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		isConveyorVersion = tag.getBoolean("isConveyorVersion");
		if (isConveyorVersion) {
			direction = EnumFacing.byIndex(tag.getInteger("direction"));
			if (direction.getAxis() == Axis.Y) {
				direction = EnumFacing.NORTH;
			}
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == Taam.CAPABILITY_CONVEYOR) {
			return true;
		}
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
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
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return (T) getTargetFluidHandlerWrapped();
		}
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) getTargetItemHandler();
		}
		return super.getCapability(capability, facing);
	}

	private TileEntity getTarget() {
		return world.getTileEntity(pos.down());
	}

	private IItemHandler getDropItemHandler() {
		return new IItemHandler() {

			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
				if (InventoryUtils.isEmpty(stack)) {
					return ItemStack.EMPTY;
				}
				if (canDrop()) {
					if (!simulate && !world.isRemote) {
						EntityItem item = new EntityItem(world, pos.getX() + 0.5, pos.getY() - 0.3, pos.getZ() + 0.5, stack);
						item.motionX = 0;
						item.motionY = 0;
						item.motionZ = 0;
						world.spawnEntity(item);
					}
					return ItemStack.EMPTY;
				}
				return stack;
			}

			@Override
			public ItemStack getStackInSlot(int slot) {
				return ItemStack.EMPTY;
			}

			@Override
			public int getSlots() {
				return 1;
			}

			@Override
			public int getSlotLimit(int slot) {
				return 64;
			}

			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				return ItemStack.EMPTY;
			}
		};
	}

	private IItemHandler getTargetItemHandler() {
		TileEntity target = getTarget();
		if (target == null) {
			return getDropItemHandler();
		}
		IItemHandler handler = InventoryUtils.getInventory(target, EnumFacing.UP);
		if (handler == null) {
			return getDropItemHandler();
		}
		return handler;
	}


	private IFluidHandler getTargetFluidHandler() {
		TileEntity target = getTarget();
		return FluidUtils.getFluidHandler(target, EnumFacing.UP);
	}

	private IFluidHandler getTargetFluidHandlerWrapped() {
		IFluidHandler handler = getTargetFluidHandler();
		if (handler == null) {
			wrappedHandler.origin = null;
			return null;
		}
		wrappedHandler.origin = handler;
		return wrappedHandler;
	}

	private boolean canDrop() {
		return TaamUtil.canDropIntoWorld(world, pos.down());
	}

	/*
	 * IRotatable Implementation
	 */

	@Override
	public EnumFacing getFacingDirection() {
		return direction;
	}

	@Override
	public EnumFacing getNextFacingDirection() {
		return direction.rotateY();
	}

	@Override
	public void setFacingDirection(EnumFacing direction) {
		if (isConveyorVersion) {
			this.direction = direction;
			if (!ArrayUtils.contains(EnumFacing.HORIZONTALS, direction)) {
				this.direction = EnumFacing.NORTH;
			}
			updateState(false, true, false);
		}
	}

}
