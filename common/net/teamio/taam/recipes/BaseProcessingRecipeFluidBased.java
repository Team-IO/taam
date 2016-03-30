package net.teamio.taam.recipes;

import net.minecraftforge.fluids.FluidStack;

public abstract class BaseProcessingRecipeFluidBased extends BaseProcessingRecipe
		implements IProcessingRecipeFluidBased {

	protected FluidStack inputFluid;

	@Override
	public boolean inputFluidMatches(FluidStack fluid) {
		return inputFluid.isFluidEqual(fluid);
	}

	@Override
	public FluidStack getInputFluid() {
		return inputFluid;
	}

}