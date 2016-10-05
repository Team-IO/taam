package net.teamio.taam.util;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * Fluid handler wrapper that only allows output into a given
 * {@link IFluidHandler}. Drain methods will always return null.
 * 
 * @author Oliver Kahrmann
 *
 */
public class FluidHandlerOutputOnly implements IFluidHandler {

	public IFluidHandler origin;

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		if(origin == null) {
			return 0;
		}
		return origin.fill(from, resource, doFill);
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		if(origin == null) {
			return false;
		}
		return origin.canFill(from, fluid);
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		if(origin == null) {
			return new FluidTankInfo[0];
		}
		return origin.getTankInfo(from);
	}
}