package net.teamio.taam.content.conveyors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Config;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRenderable;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.conveyors.ConveyorSlotsMoving;
import net.teamio.taam.conveyors.ConveyorUtil;

import java.util.List;

public class TileEntityConveyorElevator extends BaseTileEntity implements ITickable, IRotatable, IWorldInteractable, IRenderable {

	private final ConveyorSlotsMoving conveyorSlots;
	private EnumFacing direction = EnumFacing.NORTH;
	public ElevatorDirection escalation = ElevatorDirection.UP;
	public ElevatorMode mode = ElevatorMode.PASS;

	public boolean isTop = false;
	public boolean isBottom = false;

	public enum ElevatorDirection {
		UP,
		DOWN;

		private static final ElevatorDirection[] next = {
				DOWN,
				UP,
		};

		public ElevatorDirection getNext() {
			return next[this.ordinal()];
		}
	}

	public enum ElevatorMode {
		PASS,
		ENTER,
		EXIT;

		private static final ElevatorMode[] next = {
				ENTER,
				EXIT,
				PASS,
		};

		public ElevatorMode getNext() {
			return next[this.ordinal()];
		}
	}

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

	@Override
	@SideOnly(Side.CLIENT)
	public List<String> getVisibleParts() {
		List<String> visibleParts = BaseTileEntity.visibleParts.get();

		// Visible parts list is re-used to reduce object creation
		visibleParts.clear();

		visibleParts.add("Elevator_Frame");

		if(mode == ElevatorMode.ENTER) {
			visibleParts.add("Elevator_Attach");
			if(escalation == ElevatorDirection.UP) {
				visibleParts.add("Elevator_Cap_Bottom");
			} else {
				visibleParts.add("Elevator_Cap_Top");
			}
		} else if(mode == ElevatorMode.EXIT) {
			visibleParts.add("Elevator_Attach");
			if(escalation == ElevatorDirection.UP) {
				visibleParts.add("Elevator_Cap_Top");
			} else {
				visibleParts.add("Elevator_Cap_Bottom");
			}
		}

		return visibleParts;
	}

	private EnumFacing getNextSlot(int slot) {

		if(mode == ElevatorMode.EXIT) {
			return direction.getOpposite();
		} else {
			// Slots not part of elevator shaft
			int row = ConveyorUtil.ROWS.get(slot, direction);
			if(row == 1 || row == 3) {
				return direction;
			}
			// Part of elevator shaft
			switch(escalation) {
				default:
				case UP:
					//if(isTop) do something else?
					return EnumFacing.UP;
				case DOWN:
					return EnumFacing.DOWN;
			}
		}

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
		TileEntity te = world.getTileEntity(pos.offset(EnumFacing.UP));
		if (te instanceof TileEntityConveyorElevator) {
			isTop = ((TileEntityConveyorElevator) te).getFacingDirection().getAxis() != this.direction.getAxis();
		} else {
			isTop = true;
		}

		// Check DOWN
		te = world.getTileEntity(pos.offset(EnumFacing.DOWN));
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
		if (ConveyorUtil.defaultTransition(world, pos, conveyorSlots, null, slotOrder)) {
			markDirty();
		}
	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setTag("items", conveyorSlots.serializeNBT());
		tag.setInteger("direction", direction.ordinal());
		tag.setInteger("escalation", escalation.ordinal());
		tag.setInteger("mode", mode.ordinal());
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		conveyorSlots.deserializeNBT(tag.getTagList("items", NBT.TAG_COMPOUND));

		direction = EnumFacing.getFront(tag.getInteger("direction"));
		if (direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
			direction = EnumFacing.NORTH;
		}

		escalation = ElevatorDirection.values()[MathHelper.clamp(tag.getInteger("escalation"), 0, 1)];
		mode = ElevatorMode.values()[MathHelper.clamp(tag.getInteger("mode"), 0, 2)];
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == Taam.CAPABILITY_CONVEYOR) {
			return facing == null || facing.getAxis() == direction.getAxis() || facing.getAxis() == Axis.Y;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == Taam.CAPABILITY_CONVEYOR
				&& (facing == null || (facing == direction.getOpposite() && mode != ElevatorMode.PASS) || facing.getAxis() == Axis.Y)) {
			return (T) conveyorSlots;
		}
		return super.getCapability(capability, facing);
	}

	/**
	 * Cycles between exit, entry or pass
	 */
	public void cycleMode() {
		mode = mode.getNext();
		Log.info("Setting mode to {}", mode);
		updateState(true, true, true);
	}

	/**
	 * Cycles between exit point in a certain direction or just continuing movement up or down
	 */
	public void cycleEscalation() {
		escalation = escalation.getNext();
		Log.info("Setting elevator direction to {}", escalation);
		updateState(true, true, true);
	}

	/**
	 * Cycles between exit point in a certain direction or just continuing movement up or down
	 */
	public void cycleRotation() {
		setFacingDirection(getNextFacingDirection());
		updateState(true, true, true);
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
		world.notifyNeighborsRespectDebug(pos, blockType);
		if (blockType != null) {
			blockType.onNeighborChange(world, pos, pos);
		}
	}

	@Override
	public EnumFacing getFacingDirection() {
		return direction;
	}

	/*
	 * IWorldInteractable implementation
	 */
	@Override
	public boolean onBlockActivated(World world, EntityPlayer player, EnumHand hand, boolean hasWrench, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		if(!hasWrench) {
			return false;
		}
		if(side.getAxis() == direction.getAxis()) {
			cycleMode();
			if(mode != ElevatorMode.PASS) {
				setFacingDirection(side.getOpposite());
			}
			return true;
		}
		if(side.getAxis().isVertical()) {
			cycleRotation();
		} else {
			cycleEscalation();
		}
		return true;
	}

	@Override
	public boolean onBlockHit(World world, EntityPlayer player, boolean hasWrench) {
		return false;
	}
}
