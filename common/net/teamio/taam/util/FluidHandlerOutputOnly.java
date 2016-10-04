package net.teamio.taam.util;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

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
	public IFluidTankProperties[] getTankProperties() {
		return origin.getTankProperties();
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		return origin.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return null;
	}

}