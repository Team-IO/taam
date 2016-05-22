package net.teamio.taam.integration.jei;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.fluids.FluidStack;
import net.teamio.taam.recipes.IProcessingRecipeFluidBased;

public class ProcessingRecipeFluidBasedWrapper extends ProcessingRecipeWrapper {

	public final IProcessingRecipeFluidBased recipe;

	public ProcessingRecipeFluidBasedWrapper(IProcessingRecipeFluidBased recipe) {
		super(recipe);
		this.recipe = recipe;
	}

	@Override
	public List<FluidStack> getFluidInputs() {
		FluidStack input = recipe.getInputFluid();
		if (input == null) {
			return null;
		} else {
			return Lists.newArrayList(input);
		}
	}

	@Override
	public List<FluidStack> getFluidOutputs() {
		FluidStack output = recipe.getOutputFluid();
		if (output == null) {
			return null;
		} else {
			return Lists.newArrayList(output);
		}
	}
}
