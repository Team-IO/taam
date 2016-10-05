package net.teamio.taam.integration.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.teamio.taam.Taam;
import net.teamio.taam.recipes.impl.CrusherRecipe;

public class CrusherRecipeHandler implements IRecipeHandler<CrusherRecipe> {

	@Override
	public Class<CrusherRecipe> getRecipeClass() {
		return CrusherRecipe.class;
	}

	@Override
	@Deprecated
	public String getRecipeCategoryUid() {
		return Taam.INTEGRATION_JEI_CAT_CRUSHER;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(CrusherRecipe recipe) {
		return new ProcessingRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(CrusherRecipe recipe) {
		return true;
	}

}
