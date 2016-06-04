package net.teamio.taam.integration.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.teamio.taam.Taam;
import net.teamio.taam.recipes.impl.MixerRecipe;

public class MixerRecipeHandler implements IRecipeHandler<MixerRecipe> {

	@Override
	public Class<MixerRecipe> getRecipeClass() {
		return MixerRecipe.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return Taam.INTEGRATION_JEI_CAT_MIXER;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(MixerRecipe recipe) {
		return new ProcessingRecipeFluidBasedWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(MixerRecipe recipe) {
		return true;
	}

}
