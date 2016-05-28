package net.teamio.taam.content.common;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.teamio.taam.Taam;

public class FluidMaterial extends Fluid {

	public FluidMaterial(Taam.FLUID_MATERIAL_META fluidInfo) {
		super(fluidInfo.registryName, getResLoc(fluidInfo.registryName), getResLoc(fluidInfo.registryName));
		setViscosity(fluidInfo.viscosity);
		setDensity(fluidInfo.density);
	}

	private static ResourceLocation getResLoc(String fluidName) {
		return new ResourceLocation("taam", "blocks/" + fluidName);
	}

}
