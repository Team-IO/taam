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
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.machines.IMachine;
import net.teamio.taam.piping.PipeEndFluidHandler;
import net.teamio.taam.piping.PipeUtil;

public class MachineTank implements IMachine, IFluidHandler, IWorldInteractable {
	
	private final PipeEndFluidHandler pipeEndUP;
	private final PipeEndFluidHandler pipeEndDOWN;
	private final FluidTank tank;
	
	public static final List<String> visibleParts = Lists.newArrayList("BaseplateConnector_pmdl_c", "Tank_tmdl");
	
	public MachineTank() {
		pipeEndUP = new PipeEndFluidHandler(this, EnumFacing.UP, true);
		pipeEndDOWN = new PipeEndFluidHandler(this, EnumFacing.DOWN, true);
		pipeEndUP.setSuction(10);
		pipeEndDOWN.setSuction(9);
		tank = new FluidTank(8000);
	}
	
	public List<String> getVisibleParts() {
		return visibleParts;
	}
	
	@Override
	public void writePropertiesToNBT(NBTTagCompound tag) {
		tank.writeToNBT(tag);
	}

	@Override
	public void readPropertiesFromNBT(NBTTagCompound tag) {
		tank.readFromNBT(tag);
	}

	public void writeUpdatePacket(PacketBuffer buf) {
		NBTTagCompound tag = new NBTTagCompound();
		tank.writeToNBT(tag);
		buf.writeNBTTagCompoundToBuffer(tag);
	}

	public void readUpdatePacket(PacketBuffer buf) {
		try {
			NBTTagCompound tag = buf.readNBTTagCompoundFromBuffer();
			tank.readFromNBT(tag);
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
		PipeUtil.processPipes(pipeEndUP, world, pos);
		PipeUtil.processPipes(pipeEndDOWN, world, pos);
	}

	@Override
	public boolean renderUpdate(World world, BlockPos pos) {
		return false;
	}

	@Override
	public void blockUpdate(World world, BlockPos pos) {
	}

	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == Taam.CAPABILITY_PIPE) {
			return facing.getAxis() == Axis.Y;
		}
		return false;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == Taam.CAPABILITY_PIPE) {
			if(facing == EnumFacing.UP) {
				return (T)pipeEndUP;
			} else if(facing == EnumFacing.DOWN) {
				return (T)pipeEndDOWN;
			} else {
				return null;
			}
		}
		return null;
	}

	/*
	 * IWorldInteractable implementation
	 */
	
	@Override
	public boolean onBlockActivated(World world, EntityPlayer player, boolean hasWrench, EnumFacing side, float hitX,
			float hitY, float hitZ) {
		boolean didSomething = PipeUtil.defaultPlayerInteraction(player, tank);
		
		if(didSomething) {
			//TODO: updateState(true, false, false);
		}
		return didSomething;
	}
	
	@Override
	public boolean onBlockHit(World world, EntityPlayer player, boolean hasWrench) {
		return false;
	}
	
	/*
	 * IFluidHandler implementation
	 */
	
	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		if(from.getAxis() != Axis.Y) {
			return 0;
		}
		if(resource == null || resource.amount == 0) {
			return 0;
		}
		if(tank.getFluidAmount() == 0) {
			tank.setFluid(null);
		}
		int filled = tank.fill(resource, doFill);
		//TODO: markDirty();
		return filled;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		if(from.getAxis() != Axis.Y) {
			return null;
		}
		if(resource.isFluidEqual(tank.getFluid())) {
			//TODO: markDirty();
			FluidStack returnStack = tank.drain(resource.amount, doDrain);
			if(tank.getFluidAmount() == 0) {
				tank.setFluid(null);
			}
			return returnStack;
		} else {
			return null;
		}
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		if(from.getAxis() != Axis.Y) {
			return null;
		}
		//TODO: markDirty();
		FluidStack returnStack = tank.drain(maxDrain, doDrain);
		if(tank.getFluidAmount() == 0) {
			tank.setFluid(null);
		}
		return returnStack;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		if(from.getAxis() != Axis.Y) {
			return false;
		}
		if(tank.getFluidAmount() == 0) {
			tank.setFluid(null);
		}
		FluidStack tankFluid = tank.getFluid();
		return tankFluid == null || tankFluid.getFluid() == fluid;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		if(from.getAxis() != Axis.Y) {
			return false;
		}
		FluidStack tankFluid = tank.getFluid();
		return tankFluid != null && tankFluid.getFluid() == fluid;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		if(from.getAxis() == Axis.Y) {
			return new FluidTankInfo[] { new FluidTankInfo(tank) };
		} else {
			return new FluidTankInfo[0];
		}
	}

}
