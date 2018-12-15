package net.teamio.taam.content.piping;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.teamio.taam.Config;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.machines.IMachine;
import net.teamio.taam.machines.IMachineWrapper;
import net.teamio.taam.piping.IPipePos;
import net.teamio.taam.piping.PipeEnd;
import net.teamio.taam.piping.PipeEndSharedDistinct;
import net.teamio.taam.piping.PipeInfo;
import net.teamio.taam.piping.PipeNetwork;
import net.teamio.taam.rendering.TankRenderInfo;
import net.teamio.taam.util.FaceBitmap;

import java.io.IOException;
import java.util.List;

public class MachinePump implements IMachine, IPipePos, IRotatable {

	private final PipeEnd pipeEndOut;
	private final PipeEnd pipeEndIn;

	private EnumFacing direction = EnumFacing.NORTH;
	private final PipeInfo info;

	private static final float fromBorder = 2f / 16;
	public static final AxisAlignedBB boundsPump = new AxisAlignedBB(fromBorder, 0, fromBorder, 1 - fromBorder,
			1 - 4 / 16f, 1 - fromBorder);


	private static final float tankBottom = 5 / 16f;
	private static final float tankHeight = 5 / 16f;
	private static final float tankTop = tankBottom + tankHeight;

	private static final float tankLeft = 3 / 16f;
	private static final float tankWidth = 2 / 16f;

	private static final float tankBack = 6 / 16f;

	public static final AxisAlignedBB[] boundsPumpTank = new AxisAlignedBB[]{
			new AxisAlignedBB(1 - tankLeft, tankBottom, tankBack, 1 - tankLeft - tankWidth, tankTop, tankBack + tankWidth).expand(TankRenderInfo.shrinkValue, TankRenderInfo.shrinkValue, TankRenderInfo.shrinkValue),//S
			new AxisAlignedBB(1 - tankBack, tankBottom, 1 - tankLeft, 1 - tankBack - tankWidth, tankTop, 1 - tankLeft - tankWidth).expand(TankRenderInfo.shrinkValue, TankRenderInfo.shrinkValue, TankRenderInfo.shrinkValue),//W
			new AxisAlignedBB(tankLeft, tankBottom, 1 - tankBack, tankLeft + tankWidth, tankTop, 1 - tankBack - tankWidth).expand(TankRenderInfo.shrinkValue, TankRenderInfo.shrinkValue, TankRenderInfo.shrinkValue),//N
			new AxisAlignedBB(tankBack, tankBottom, tankLeft, tankBack + tankWidth, tankTop, tankLeft + tankWidth).expand(TankRenderInfo.shrinkValue, TankRenderInfo.shrinkValue, TankRenderInfo.shrinkValue)//E
	};

	private static final float fromBorderOcclusion = 2f / 16;
	public static final AxisAlignedBB bbCoolusion = new AxisAlignedBB(fromBorderOcclusion, fromBorderOcclusion,
			fromBorderOcclusion, 1 - fromBorderOcclusion, 1 - fromBorderOcclusion, 1 - fromBorderOcclusion);

	private final TankRenderInfo tankRI = new TankRenderInfo(boundsPumpTank[2]);

	private byte occludedSides;
	private World worldObj;
	private BlockPos pos;
	private IMachineWrapper wrapper;

	public MachinePump() {
		info = new PipeInfo(Config.pl_pump_capacity){
			@Override
			protected void onUpdate() {
				if (wrapper == null) return;
				wrapper.markAsDirty();
				wrapper.sendPacket();
			}
		};
		pipeEndOut = new PipeEndSharedDistinct(this, direction, info);
		pipeEndIn = new PipeEndSharedDistinct(this, direction.getOpposite(), info);
	}

	@Override
	public void setWrapper(IMachineWrapper wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	public void onCreated(World worldObj, BlockPos pos) {
		this.worldObj = worldObj;
		this.pos = pos;
		PipeNetwork.NET.addPipe(pipeEndIn);
		PipeNetwork.NET.addPipe(pipeEndOut);
	}

	@Override
	public void onUnload(World worldObj, BlockPos pos) {
		PipeNetwork.NET.removePipe(pipeEndIn);
		PipeNetwork.NET.removePipe(pipeEndOut);
	}

	@Override
	public IBlockAccess getPipeWorld() {
		return worldObj;
	}

	@Override
	public BlockPos getPipePos() {
		return pos;
	}

	private void updateOcclusion() {
		pipeEndOut.occluded = FaceBitmap.isSideBitSet(occludedSides, pipeEndOut.getSide());
		pipeEndIn.occluded = FaceBitmap.isSideBitSet(occludedSides, pipeEndIn.getSide());

		tankRI.bounds = boundsPumpTank[direction.getHorizontalIndex()];
	}

	@Override
	public void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setInteger("direction", direction.ordinal());
		info.writeToNBT(tag);
		tag.setByte("occludedSides", occludedSides);
	}

	@Override
	public void readPropertiesFromNBT(NBTTagCompound tag) {
		direction = EnumFacing.byIndex(tag.getInteger("direction"));
		if (direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
			direction = EnumFacing.NORTH;
		}
		pipeEndOut.setSide(direction);
		pipeEndIn.setSide(direction.getOpposite());
		info.readFromNBT(tag);
		occludedSides = tag.getByte("occludedSides");
		updateOcclusion();
	}

	@Override
	public void writeUpdatePacket(PacketBuffer buf) {
		NBTTagCompound tag = new NBTTagCompound();
		writePropertiesToNBT(tag);
		buf.writeCompoundTag(tag);
		buf.writeByte(occludedSides);
	}

	@Override
	public void readUpdatePacket(PacketBuffer buf) {
		try {
			NBTTagCompound tag = buf.readCompoundTag();
			readPropertiesFromNBT(tag);
			occludedSides = buf.readByte();
			updateOcclusion();
		} catch (IOException e) {
			Log.error(getClass().getSimpleName()
					+ " has trouble reading tag from update packet. THIS IS AN ERROR, please report.", e);
		}
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos blockPos) {
		return state;
	}

	@Override
	public String getModelPath() {
		return "taam:machine";
	}

	@Override
	public boolean update(World world, BlockPos pos) {
		// Pump fluid
		//int amount = PipeUtil.transferContent(pipeEndIn, pipeEndOut, Config.pl_pump_pressure);
		// Apply excess pressure directly
		//int left = Config.pl_pump_pressure - amount;
		int amount = 0;
		//if(left > 0) {
			// Results are added to amount to mark the whole pump as dirty if a pipe end changed
			amount -= pipeEndIn.applyPressure(-Config.pl_pump_pressure);
			amount += pipeEndOut.applyPressure(Config.pl_pump_pressure);
		//}
		return amount > 0;
	}

	@Override
	public boolean renderUpdate(IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public void blockUpdate(World world, BlockPos pos, byte occlusionField) {
		occludedSides = occlusionField;
		updateOcclusion();
	}

	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		if (mask.intersects(boundsPump)) {
			list.add(boundsPump);
		}
		if (mask.intersects(MachinePipe.bbBaseplate)) {
			list.add(MachinePipe.bbBaseplate);
		}
	}

	@Override
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		list.add(boundsPump);
		list.add(MachinePipe.bbBaseplate);
		if (direction.getAxis() == Axis.X) {
			list.add(MachinePipe.bbFlanges[EnumFacing.EAST.ordinal()]);
			list.add(MachinePipe.bbFlanges[EnumFacing.WEST.ordinal()]);
		} else {
			list.add(MachinePipe.bbFlanges[EnumFacing.NORTH.ordinal()]);
			list.add(MachinePipe.bbFlanges[EnumFacing.SOUTH.ordinal()]);
		}
	}

	@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {
		list.add(bbCoolusion);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == Taam.CAPABILITY_PIPE) {
			return facing.getAxis() == direction.getAxis();
		}
		if (capability == Taam.CAPABILITY_RENDER_TANK) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == Taam.CAPABILITY_PIPE) {
			if (facing == direction) {
				return (T) pipeEndOut;
			} else if (facing == direction.getOpposite()) {
				return (T) pipeEndIn;
			} else {
				return null;
			}
		}
		if (capability == Taam.CAPABILITY_RENDER_TANK) {
			tankRI.fluid = info.content.isEmpty() ? null : info.content.get(0);
			tankRI.capacity = info.capacity;
			return (T) tankRI.asArray();
		}
		return null;
	}

	/*
	 * IRotatable implementation
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
		if (direction.getAxis() == Axis.Y) {
			return;
		}
		this.direction = direction;

		pipeEndOut.setSide(direction);
		pipeEndIn.setSide(direction.getOpposite());
		updateOcclusion();

		// TODO: updateState(true, true, true);
	}
}
