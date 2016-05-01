package net.teamio.taam.content.piping;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.machines.IMachine;
import net.teamio.taam.piping.PipeEndFluidHandler;
import net.teamio.taam.piping.PipeEndSharedDistinct;
import net.teamio.taam.piping.PipeInfo;
import net.teamio.taam.piping.PipeUtil;
import net.teamio.taam.rendering.TaamRenderer;
import net.teamio.taam.rendering.TankRenderInfo;

public class MachineFluidDrier implements IMachine, IRotatable {

	private final PipeEndSharedDistinct pipeEndOut;
	private final PipeEndSharedDistinct pipeEndIn;

	private EnumFacing direction = EnumFacing.NORTH;
	private final PipeInfo info;

	private static final int capacity = 125;
	private static final int pressure = 50;

	public static final List<String> visibleParts = Lists.newArrayList("Baseplate_pmdl", "Pump_pumdl");

	public static final AxisAlignedBB boundsPump = new AxisAlignedBB(0, 0, 0, 1, 1f - 1/16f, 1);
	public static final AxisAlignedBB boundsPumpTank = new AxisAlignedBB(0, 0, 0, 3/16f, 3/16f, 3/16f);
	
	private TankRenderInfo tankRI = new TankRenderInfo(boundsPumpTank, null);
	
	public MachineFluidDrier() {
		info = new PipeInfo(capacity);
		pipeEndOut = new PipeEndSharedDistinct(direction, info, true);
		pipeEndIn = new PipeEndSharedDistinct(direction.getOpposite(), info, true);
		pipeEndOut.setPressure(pressure);
		pipeEndIn.setSuction(pressure);
	}
	
	public List<String> getVisibleParts() {
		return visibleParts;
	}
	
	@Override
	public void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setInteger("direction", direction.ordinal());
		info.writeToNBT(tag);
	}

	@Override
	public void readPropertiesFromNBT(NBTTagCompound tag) {
		direction = EnumFacing.getFront(tag.getInteger("direction"));
		if (direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
			direction = EnumFacing.NORTH;
		}
		pipeEndOut.setSide(direction);
		pipeEndIn.setSide(direction.getOpposite());
		info.readFromNBT(tag);
	}

	public void writeUpdatePacket(PacketBuffer buf) {
		NBTTagCompound tag = new NBTTagCompound();
		writePropertiesToNBT(tag);
		buf.writeNBTTagCompoundToBuffer(tag);
	}

	public void readUpdatePacket(PacketBuffer buf) {
		try {
			NBTTagCompound tag = buf.readNBTTagCompoundFromBuffer();
			readPropertiesFromNBT(tag);
		} catch (IOException e) {
			Log.error(getClass().getSimpleName()
					+ " has trouble reading tag from update packet. THIS IS AN ERROR, please report.", e);
		}
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, World world, BlockPos blockPos) {
		renderUpdate(world, blockPos);
		// Apply rotation to the model
		OBJModel.OBJState retState = new OBJModel.OBJState(getVisibleParts(), true);
		
		IExtendedBlockState extendedState = (IExtendedBlockState)state;
		
		return extendedState.withProperty(OBJModel.OBJProperty.instance, retState);
	}

	@Override
	public String getModelPath() {
		return "taam:machine";
	}

	@Override
	public void update(World world, BlockPos pos) {
		PipeUtil.processPipes(pipeEndOut, world, pos);
		PipeUtil.processPipes(pipeEndIn, world, pos);
	}

	@Override
	public boolean renderUpdate(World world, BlockPos pos) {
		return false;
	}

	@Override
	public void blockUpdate(World world, BlockPos pos) {
	}

	private static final float fromBorder = 2f/16;
	private static final float fromBorderOcclusion = 2f/16;
	public static final AxisAlignedBB bbTank = new AxisAlignedBB(fromBorder, 0, fromBorder, 1-fromBorder, 1, 1-fromBorder);
	public static final AxisAlignedBB bbCoolusion = new AxisAlignedBB(fromBorderOcclusion, fromBorderOcclusion, fromBorderOcclusion, 1-fromBorderOcclusion, 1-fromBorderOcclusion, 1-fromBorderOcclusion);

	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		if (mask.intersectsWith(bbTank)) {
			list.add(bbTank);
		}
	}

	@Override
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		list.add(bbTank);
	}

	@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {
		list.add(bbCoolusion);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == Taam.CAPABILITY_PIPE) {
			return facing.getAxis() == Axis.Y;
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
			tankRI.tankInfo = new FluidTankInfo(info.content.isEmpty() ? null : info.content.get(0), info.capacity);
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

		//TODO: updateState(true, true, true);
	}
}
