package net.teamio.taam.content.piping;

import java.io.IOException;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.teamio.taam.Config;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.machines.IMachine;
import net.teamio.taam.piping.PipeEndFluidHandler;
import net.teamio.taam.piping.PipeUtil;
import net.teamio.taam.rendering.TankRenderInfo;
import net.teamio.taam.util.FaceBitmap;

public class MachineTank implements IMachine, IWorldInteractable {

	public static final float b_basePlate = 2f / 16;
	public static final float b_border = 1.5f / 16;
	public static final float b_occlusion = 2f / 16;

	public static final AxisAlignedBB bbTankContent = new AxisAlignedBB(
			b_border,   b_basePlate, b_border,
			1-b_border, 1,		   1-b_border
			).expand(TankRenderInfo.shrinkValue, TankRenderInfo.shrinkValue, TankRenderInfo.shrinkValue);
	public static final AxisAlignedBB bbTank = new AxisAlignedBB(b_border, 0, b_border, 1-b_border, 1, 1-b_border);
	public static final AxisAlignedBB bbCoolusion = new AxisAlignedBB(b_occlusion, b_occlusion, b_occlusion, 1-b_occlusion, 1-b_occlusion, 1-b_occlusion);

	private final PipeEndFluidHandler pipeEndUP;
	private final PipeEndFluidHandler pipeEndDOWN;
	private final FluidTank tank;

	private final TankRenderInfo tankRI = new TankRenderInfo(bbTankContent);

	private byte occludedSides;

	private World worldObj;
	private BlockPos pos;

	public MachineTank() {
		tank = new FluidTank(Config.pl_tank_capacity) {
			@Override
			protected void onContentsChanged() {
				//TODO: Mark Dirty
			}
		};
		pipeEndUP = new PipeEndFluidHandler(tank, EnumFacing.UP, true);
		pipeEndDOWN = new PipeEndFluidHandler(tank, EnumFacing.DOWN, true);
		pipeEndUP.setSuction(Config.pl_tank_suction);
		// Suction on lower end of the tank is always 1 lower than on the top, so stacked tanks always transfer down.
		pipeEndDOWN.setSuction(Config.pl_tank_suction - 1);
	}

	@Override
	public void onCreated(World worldObj, BlockPos pos) {
		this.worldObj = worldObj;
		this.pos = pos;
	}

	private void updateOcclusion() {
		pipeEndUP.occluded = FaceBitmap.isSideBitSet(occludedSides, EnumFacing.UP);
		pipeEndDOWN.occluded = FaceBitmap.isSideBitSet(occludedSides, EnumFacing.DOWN);
	}

	@Override
	public void writePropertiesToNBT(NBTTagCompound tag) {
		tank.writeToNBT(tag);
		tag.setByte("occludedSides", occludedSides);
	}

	@Override
	public void readPropertiesFromNBT(NBTTagCompound tag) {
		tank.readFromNBT(tag);
		occludedSides = tag.getByte("occludedSides");
		updateOcclusion();
	}

	@Override
	public void writeUpdatePacket(PacketBuffer buf) {
		NBTTagCompound tag = new NBTTagCompound();
		tank.writeToNBT(tag);
		buf.writeNBTTagCompoundToBuffer(tag);
		buf.writeByte(occludedSides);
	}

	@Override
	public void readUpdatePacket(PacketBuffer buf) {
		try {
			NBTTagCompound tag = buf.readNBTTagCompoundFromBuffer();
			tank.readFromNBT(tag);
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
		PipeUtil.processPipes(pipeEndUP, world, pos);
		PipeUtil.processPipes(pipeEndDOWN, world, pos);
		return true;
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
		if (mask.intersectsWith(bbTank)) {
			list.add(bbTank);
		}
		if (mask.intersectsWith(MachinePipe.bbBaseplate)) {
			list.add(MachinePipe.bbBaseplate);
		}
	}

	@Override
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		list.add(bbTank);
		list.add(MachinePipe.bbBaseplate);
	}

	@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {
		list.add(bbCoolusion);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == Taam.CAPABILITY_PIPE) {
			return facing.getAxis() == Axis.Y;
		}
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return facing.getAxis() == Axis.Y;
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
			if (facing == EnumFacing.UP) {
				return (T) pipeEndUP;
			} else if (facing == EnumFacing.DOWN) {
				return (T) pipeEndDOWN;
			} else {
				return null;
			}
		}
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing.getAxis() == Axis.Y) {
			return (T) tank;
		}
		if (capability == Taam.CAPABILITY_RENDER_TANK) {
			tankRI.setInfo(tank);
			return (T) tankRI.asArray();
		}
		return null;
	}

	/*
	 * IWorldInteractable implementation
	 */

	@Override
	public boolean onBlockActivated(World world, EntityPlayer player, EnumHand hand, boolean hasWrench, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		boolean didSomething = PipeUtil.defaultPlayerInteraction(player, tank);

		if (didSomething) {
			// TODO: updateState(true, false, false);
		}
		return didSomething;
	}

	@Override
	public boolean onBlockHit(World world, EntityPlayer player, boolean hasWrench) {
		return false;
	}
}
