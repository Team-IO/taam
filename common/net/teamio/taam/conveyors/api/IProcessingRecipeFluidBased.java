package net.teamio.taam.conveyors.api;

import net.minecraftforge.fluids.FluidStack;

public interface IProcessingRecipeFluidBased extends IProcessingRecipe {

	boolean inputFluidMatches(FluidStack fluid);

	FluidStack getInputFluid();
	
}
