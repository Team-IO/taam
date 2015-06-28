package net.teamio.taam.content.common;

import net.minecraftforge.fluids.Fluid;

public class FluidDye extends Fluid {

	public FluidDye(String fluidName) {
		super(fluidName);
		setViscosity(400);
		setDensity(1300);
	}

}
