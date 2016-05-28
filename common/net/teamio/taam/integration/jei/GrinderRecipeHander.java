package net.teamio.taam.integration.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.teamio.taam.Taam;
import net.teamio.taam.recipes.GrinderRecipe;

public class GrinderRecipeHander implements IRecipeHandler<GrinderRecipe> {

	@Override
	public Class<GrinderRecipe> getRecipeClass() {
		return GrinderRecipe.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return Taam.INTEGRATION_JEI_CAT_GRINDER;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(GrinderRecipe recipe) {
		return new ProcessingRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(GrinderRecipe recipe) {
		return true;
	}

}
