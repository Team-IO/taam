package net.teamio.taam.content.common;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class FluidDye extends Fluid {

	public FluidDye(String fluidName) {
		super(fluidName, getResLoc(fluidName), getResLoc(fluidName));
		setViscosity(400);
		setDensity(1300);
	}
	
	private static ResourceLocation getResLoc(String fluidName) {
		return new ResourceLocation("taam", "blocks/" + fluidName);
	}

}
