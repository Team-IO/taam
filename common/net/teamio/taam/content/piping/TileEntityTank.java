package net.teamio.taam.content.piping;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRenderable;
import net.teamio.taam.piping.IPipe;
import net.teamio.taam.piping.IPipeTE;
import net.teamio.taam.piping.PipeEndFluidHandler;
import net.teamio.taam.piping.PipeUtil;

public class TileEntityTank extends BaseTileEntity implements IFluidHandler, IPipeTE, ITickable, IRenderable {

	private final PipeEndFluidHandler pipeEndUP;
	private final PipeEndFluidHandler pipeEndDOWN;
	private final FluidTank tank;

	public static final List<String> visibleParts = Lists.newArrayList("BaseplateConnector_pmdl_c", "Tank_tmdl");
	
	public TileEntityTank() {
		pipeEndUP = new PipeEndFluidHandler(this, EnumFacing.UP);
		pipeEndDOWN = new PipeEndFluidHandler(this, EnumFacing.DOWN);
		pipeEndUP.setSuction(10);
		pipeEndDOWN.setSuction(10);
		tank = new FluidTank(8000);
	}
	
	@Override
	public List<String> getVisibleParts() {
		return visibleParts;
	}
	
	@Override
	public void update() {
		PipeUtil.processPipes(pipeEndUP, worldObj, pos);
		PipeUtil.processPipes(pipeEndDOWN, worldObj, pos);
	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		// TODO Auto-generated method stub

	}

	@Override
	public IPipe[] getPipesForSide(EnumFacing side) {
		if (side == EnumFacing.UP) {
			return new IPipe[] { pipeEndUP };
		} else if (side == EnumFacing.DOWN) {
			return new IPipe[] { pipeEndDOWN };
		} else {
			return null;
		}
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		if(from.getAxis() != Axis.Y) {
			return 0;
		}
		int filled = tank.fill(resource, doFill);
		markDirty();
		return filled;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		if(from.getAxis() != Axis.Y) {
			return null;
		}
		if(resource.isFluidEqual(tank.getFluid())) {
			markDirty();
			return tank.drain(resource.amount, doDrain);
		} else {
			return null;
		}
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		if(from.getAxis() != Axis.Y) {
			return null;
		}
		markDirty();
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		if(from.getAxis() != Axis.Y) {
			return false;
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

	public FluidStack getFluid() {
		return tank.getFluid();
	}

}
