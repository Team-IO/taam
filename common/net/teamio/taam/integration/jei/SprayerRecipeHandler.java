package net.teamio.taam.integration.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.teamio.taam.Taam;
import net.teamio.taam.recipes.impl.SprayerRecipe;

public class SprayerRecipeHandler implements IRecipeHandler<SprayerRecipe> {

	@Override
	public Class<SprayerRecipe> getRecipeClass() {
		return SprayerRecipe.class;
	}

	@Override
	@Deprecated
	public String getRecipeCategoryUid() {
		return getRecipeCategoryUid(null);
	}

	@Override
	public String getRecipeCategoryUid(SprayerRecipe recipe) {
		return Taam.INTEGRATION_JEI_CAT_SPRAYER;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(SprayerRecipe recipe) {
		return new ProcessingRecipeFluidBasedWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(SprayerRecipe recipe) {
		return true;
	}

}
