package net.teamio.taam.content.conveyors;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Config;
import net.teamio.taam.Taam;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.ConveyorSlotsMoving;
import net.teamio.taam.conveyors.ConveyorUtil;

public class TileEntityConveyorElevator extends BaseTileEntity implements ITickable, IRotatable {

	private final ConveyorSlotsMoving conveyorSlots;
	private EnumFacing direction = EnumFacing.NORTH;

	public boolean isTop = false;
	public boolean isBottom = false;

	public TileEntityConveyorElevator() {
		conveyorSlots = new ConveyorSlotsMoving() {
			@Override
			public byte getSpeedsteps() {
				return Config.pl_elevator_speedsteps;
			}

			@Override
			public EnumFacing getNextSlot(int slot) {
				return TileEntityConveyorElevator.this.getNextSlot(slot);
			}

			@Override
			public void onChangeHook() {
				updateState(true, false, false);
			}
		};
		conveyorSlots.rotation = EnumFacing.UP;
	}

	@Override
	public String getName() {
		return "tile.taam.productionline.conveyor.name";
	}

	private EnumFacing getNextSlot(int slot) {
		if (isTop) {
			if (direction.getAxis() == Axis.X) {
				if (ConveyorUtil.ROWS.get(slot, EnumFacing.EAST) == 1) {
					return EnumFacing.WEST;
				}
				return EnumFacing.EAST;
			}
			if (ConveyorUtil.ROWS.get(slot, EnumFacing.SOUTH) == 1) {
				return EnumFacing.SOUTH;
			}
			return EnumFacing.NORTH;
		}

		// TODO: Handle up/down settings, exit points
		return EnumFacing.UP;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderUpdate() {
		updateAdjecents();
	}

	@Override
	public void blockUpdate() {
		updateAdjecents();
	}

	private void updateAdjecents() {
		// Check UP
		TileEntity te = worldObj.getTileEntity(pos.offset(EnumFacing.UP));
		if (te instanceof TileEntityConveyorElevator) {
			isTop = ((TileEntityConveyorElevator) te).getFacingDirection().getAxis() != this.direction.getAxis();
		} else {
			isTop = true;
		}

		// Check DOWN
		te = worldObj.getTileEntity(pos.offset(EnumFacing.DOWN));
		if (te instanceof TileEntityConveyorElevator) {
			isBottom = ((TileEntityConveyorElevator) te).getFacingDirection().getAxis() != this.direction.getAxis();
		} else {
			isBottom = true;
		}
	}

	@Override
	public void update() {

		/*
		 * Move items already on the conveyor
		 */

		// process from movement direction backward to keep slot order inside
		// one conveyor,
		// as we depend on the status of the next slot
		int[] slotOrder = ConveyorUtil.getSlotOrderForDirection(EnumFacing.UP);
		if (ConveyorUtil.defaultTransition(worldObj, pos, conveyorSlots, null, slotOrder)) {
			updateState(false, false, false);
		}
	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setTag("items", conveyorSlots.serializeNBT());
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		conveyorSlots.deserializeNBT(tag.getTagList("items", NBT.TAG_COMPOUND));
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == Taam.CAPABILITY_CONVEYOR) {
			return facing.getAxis() == direction.getAxis() || facing.getAxis() == Axis.Y;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == Taam.CAPABILITY_CONVEYOR
				&& (facing == null || facing.getAxis() == direction.getAxis() || facing.getAxis() == Axis.Y)) {
			return (T) conveyorSlots;
		}
		return super.getCapability(capability, facing);
	}

	/*
	 * IRotatable implementation
	 */

	@Override
	public EnumFacing getNextFacingDirection() {
		return direction.rotateAround(Axis.Y);
	}

	@Override
	public void setFacingDirection(EnumFacing direction) {
		if (this.direction == direction) {
			// Only update if necessary
			return;
		}
		this.direction = direction;
		if (direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
			this.direction = EnumFacing.NORTH;
		}
		updateState(false, true, true);
		worldObj.notifyBlockOfStateChange(pos, blockType);
		if (blockType != null) {
			blockType.onNeighborChange(worldObj, pos, pos);
		}
	}

	@Override
	public EnumFacing getFacingDirection() {
		return direction;
	}
}
