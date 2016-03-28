package net.teamio.taam.content.piping;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
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

	private final PipeEndFluidHandler pipeEnd;
	private final FluidTank tank;

	private static final List<String> visibleParts = Lists.newArrayList("BaseplateConnector_pmdl_c", "Tank_tmdl");
	
	public TileEntityTank() {
		pipeEnd = new PipeEndFluidHandler(this, EnumFacing.UP);
		tank = new FluidTank(10000);
	}
	
	@Override
	public List<String> getVisibleParts() {
		return visibleParts;
	}
	
	@Override
	public void update() {
		pipeEnd.setSuction(10);
		PipeUtil.processPipes(pipeEnd, worldObj, pos);
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
			return new IPipe[] { pipeEnd };
		} else {
			return null;
		}
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		if(from != EnumFacing.UP) {
			return 0;
		}
		int filled = tank.fill(resource, doFill);
		//System.out.println("Filled " + filled+ " (" + tank.getFluidAmount() + " in tank)");
		markDirty();
		return filled;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		if(from != EnumFacing.UP) {
			return null;
		}
		if(resource.isFluidEqual(tank.getFluid())) {
			return tank.drain(resource.amount, doDrain);
		} else {
			return null;
		}
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		if(from != EnumFacing.UP) {
			return null;
		}
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		if(from != EnumFacing.UP) {
			return false;
		}
		FluidStack tankFluid = tank.getFluid();
		return tankFluid == null || tankFluid.getFluid() == fluid;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		if(from != EnumFacing.UP) {
			return false;
		}
		FluidStack tankFluid = tank.getFluid();
		return tankFluid != null && tankFluid.getFluid() == fluid;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return new FluidTankInfo[] { new FluidTankInfo(tank) };
	}

}
