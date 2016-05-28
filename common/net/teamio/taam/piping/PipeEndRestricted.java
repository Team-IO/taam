package net.teamio.taam.piping;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;

/**
 * A pipe end that only allows one fluid at a time.
 * Useful for recipe-based processing machines.
 * 
 * @author Oliver Kahrmann
 *
 */
public class PipeEndRestricted extends PipeEnd {

	public PipeEndRestricted(EnumFacing side, int capacity, boolean active) {
		super(side, capacity, active);
	}

	public PipeEndRestricted(EnumFacing side, PipeInfo info, boolean active) {
		super(side, info, active);
	}

	@Override
	public int addFluid(FluidStack stack) {
		FluidStack inside = null;
		if(info.content.size() > 0) {
			inside = info.content.get(0);
			
			// Clean up 0-amount-stacks, fail-safe for any "stupid" cases
			while (inside != null && inside.amount == 0) {
				info.content.remove(0);
				if(info.content.size() > 0) {
					inside = info.content.get(0);
				} else {
					inside = null;
				}
			}
		}
		if(inside == null || inside.isFluidEqual(stack)) {
			return info.addFluid(stack);
		} else {
			return 0;
		}
	}

	public FluidStack getFluid() {
		FluidStack inside = null;
		if(info.content.size() > 0) {
			inside = info.content.get(0);
		}
		return inside;
	}
	
}
