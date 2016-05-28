package net.teamio.taam.integration.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.teamio.taam.Taam;
import net.teamio.taam.recipes.FluidDrierRecipe;

public class FluidDrierRecipeHandler implements IRecipeHandler<FluidDrierRecipe> {

	@Override
	public Class<FluidDrierRecipe> getRecipeClass() {
		return FluidDrierRecipe.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return Taam.INTEGRATION_JEI_CAT_FLUIDDRIER;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(FluidDrierRecipe recipe) {
		return new ProcessingRecipeFluidBasedWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(FluidDrierRecipe recipe) {
		return true;
	}

}
