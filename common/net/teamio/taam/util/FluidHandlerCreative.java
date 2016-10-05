package net.teamio.taam.util;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * Creative fluid handler that spawns infinite amounts of a given FluidStack.
 * Does not allow filling.
 * 
 * @author Oliver Kahrmann
 *
 */
public class FluidHandlerCreative implements IFluidHandler {

	public FluidStack template;

	private final FluidTankInfo[] tankProperties = new FluidTankInfo[] { new FluidTankInfo(template.copy(), 10000) };

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		if(resource == null) {
			return null;
		}
		if(resource.isFluidEqual(template)) {
			FluidStack drained = template.copy();
			drained.amount = Math.min(resource.amount, 10000);
			return drained;
		}
		return null;
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		FluidStack drained = template.copy();
		drained.amount = Math.min(maxDrain, 10000);
		return drained;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return template != null && template.getFluid() == fluid;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return tankProperties;
	}
}