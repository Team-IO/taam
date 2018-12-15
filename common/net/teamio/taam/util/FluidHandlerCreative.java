package net.teamio.taam.util;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

/**
 * Creative fluid handler that spawns infinite amounts of a given FluidStack.
 * Does not allow filling.
 *
 * @author Oliver Kahrmann
 */
public class FluidHandlerCreative implements IFluidHandler {

	public FluidStack template;

	private final IFluidTankProperties[] tankProperties = new IFluidTankProperties[]{new IFluidTankProperties() {

		@Override
		public FluidStack getContents() {
			if (template == null) return null;
			return template.copy();
		}

		@Override
		public int getCapacity() {
			return 10000;
		}

		@Override
		public boolean canFillFluidType(FluidStack fluidStack) {
			return false;
		}

		@Override
		public boolean canFill() {
			return false;
		}

		@Override
		public boolean canDrainFluidType(FluidStack fluidStack) {
			return fluidStack != null && fluidStack.isFluidEqual(template);
		}

		@Override
		public boolean canDrain() {
			return true;
		}
	}};

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return tankProperties;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if (template != null && template.isFluidEqual(resource)) {
			return resource;
		}
		return null;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if (template == null) {
			return null;
		}
		FluidStack cloned = template.copy();
		cloned.amount = maxDrain;
		return cloned;
	}

}
