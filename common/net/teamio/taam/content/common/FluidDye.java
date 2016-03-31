package net.teamio.taam.content.common;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class FluidDye extends Fluid {

	public FluidDye(String fluidName) {
		super(fluidName, new ResourceLocation("taam", "dye"), new ResourceLocation("taam", "dye"));//TODO: ResourceLocations
		setViscosity(400);
		setDensity(1300);
	}

}
