package net.teamio.taam.integration.jei;

import com.google.common.collect.Lists;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.taam.recipes.IProcessingRecipeFluidBased;

import java.util.List;

public class ProcessingRecipeFluidBasedWrapper extends ProcessingRecipeWrapper {

	public final IProcessingRecipeFluidBased recipe;

	public ProcessingRecipeFluidBasedWrapper(IProcessingRecipeFluidBased recipe) {
		super(recipe);
		this.recipe = recipe;
	}

	public List<FluidStack> getFluidInputs() {
		FluidStack input = recipe.getInputFluid();
		if (input == null) {
			return null;
		}
		return Lists.newArrayList(input);
	}

	public List<FluidStack> getFluidOutputs() {
		FluidStack output = recipe.getOutputFluid();
		if (output == null) {
			return null;
		}
		return Lists.newArrayList(output);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		super.getIngredients(ingredients);

		ingredients.setInputs(VanillaTypes.FLUID, getFluidInputs());
		ingredients.setOutputs(VanillaTypes.FLUID, getFluidOutputs());
	}
}
