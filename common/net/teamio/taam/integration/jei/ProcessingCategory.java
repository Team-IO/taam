package net.teamio.taam.integration.jei;

import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.Log;
import net.minecraft.item.ItemStack;
import net.teamio.taam.recipes.ChancedOutput;
import net.teamio.taam.recipes.IProcessingRecipe;

import java.util.List;

public abstract class ProcessingCategory extends BlankRecipeCategory {

	public static final int slotInput = 0;
	public static final int slotOutput = 1;

	public static final int MAX_ROWS = 3;

	public ProcessingCategory() {
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
		if(!(recipeWrapper instanceof ProcessingRecipeWrapper)) {
			Log.error("RecipeWrapper type unknown: {}", recipeWrapper);
			return;
		}

		ProcessingRecipeWrapper processingWrapper = (ProcessingRecipeWrapper)recipeWrapper;
		IProcessingRecipe recipe = processingWrapper.recipe;

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(slotInput, true, 4, 2);
		guiItemStacks.setFromRecipe(slotInput, recipeWrapper.getInputs());

		ChancedOutput[] co = recipe.getOutput();

		for(int i = 0; i < co.length; i++) {
			int r = i % MAX_ROWS;
			int c = i / MAX_ROWS;
			guiItemStacks.init(slotOutput + i, false, 84 + c*18, 4 + r*18);
			guiItemStacks.set(slotOutput + i, co[i].output);
		}
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
		if(!(recipeWrapper instanceof ProcessingRecipeWrapper)) {
			Log.error("RecipeWrapper type unknown: {}", recipeWrapper);
			return;
		}

		ProcessingRecipeWrapper processingWrapper = (ProcessingRecipeWrapper)recipeWrapper;
		IProcessingRecipe recipe = processingWrapper.recipe;

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(slotInput, true, 4, 2);
		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		if (inputs == null || inputs.size() != 1) {
			throw new IllegalStateException("Recipe inputs invalid: " + inputs);
		}
		guiItemStacks.set(slotInput, inputs.get(0));

		ChancedOutput[] co = recipe.getOutput();

		for(int i = 0; i < co.length; i++) {
			int r = i % MAX_ROWS;
			int c = i / MAX_ROWS;
			guiItemStacks.init(slotOutput + i, false, 84 + c*18, 4 + r*18);
			guiItemStacks.set(slotOutput + i, co[i].output);
		}
	}
}
