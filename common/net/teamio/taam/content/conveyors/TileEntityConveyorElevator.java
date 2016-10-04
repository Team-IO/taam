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
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.conveyors.ConveyorSlotsMoving;
import net.teamio.taam.conveyors.ConveyorUtil;

public class TileEntityConveyorElevator extends BaseTileEntity implements ITickable, IRotatable, IWorldInteractable {

	private final ConveyorSlotsMoving conveyorSlots;
	private EnumFacing direction = EnumFacing.NORTH;
	private ElevatorDirection exitDirection = ElevatorDirection.UP;

	public boolean isTop = false;
	public boolean isBottom = false;

	public static enum ElevatorDirection {
		UP,
		DOWN,
		FORWARD,
		BACK;

		private static ElevatorDirection[] nexts = {
				DOWN,
				FORWARD,
				BACK,
				UP,
		};

		public ElevatorDirection getNext() {
			return nexts[this.ordinal()];
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

	private EnumFacing getNextSlot(int slot) {
		if(exitDirection == ElevatorDirection.UP || exitDirection == ElevatorDirection.DOWN) {
			int row = ConveyorUtil.ROWS.get(slot, direction);
			if(row == 1) {
				return direction;
			} else if(row == 3) {
				return direction.getOpposite();
			}
		}

		switch(exitDirection) {
		default:
		case UP:
			//if(isTop) do something else?
			return EnumFacing.UP;
		case DOWN:
			return EnumFacing.DOWN;
		case FORWARD:
			return direction;
		case BACK:
			return direction.getOpposite();
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
		tag.setInteger("direction", direction.ordinal());
		tag.setInteger("exit", exitDirection.ordinal());
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		conveyorSlots.deserializeNBT(tag.getTagList("items", NBT.TAG_COMPOUND));

		direction = EnumFacing.getFront(tag.getInteger("direction"));
		if (direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
			direction = EnumFacing.NORTH;
		}

		exitDirection = ElevatorDirection.values()[MathHelper.clamp_int(tag.getInteger("exit"), 0, 3)];
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
				&& (facing == null || facing.getAxis() == direction.getAxis() || facing.getAxis() == Axis.Y)) {
			return (T) conveyorSlots;
		}
		return super.getCapability(capability, facing);
	}

	/**
	 * Cycles between exit point in a certain direction or just continuing movement up or down
	 */
	public void cycleExitDirection() {
		exitDirection = exitDirection.getNext();
		Log.info("Setting exit direction to {}", exitDirection);
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

	/*
	 * IWorldInteractable implementation
	 */
	@Override
	public boolean onBlockActivated(World world, EntityPlayer player, EnumHand hand, boolean hasWrench, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		if(hasWrench && side.getAxis() == direction.getAxis()) {
			cycleExitDirection();
			return true;
		}
		return false;
	}

	@Override
	public boolean onBlockHit(World world, EntityPlayer player, boolean hasWrench) {
		return false;
	}
}
