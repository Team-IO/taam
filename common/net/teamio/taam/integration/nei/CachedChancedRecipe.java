package net.teamio.taam.integration.nei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.teamio.taam.conveyors.api.ChancedOutput;
import net.teamio.taam.conveyors.api.IProcessingRecipe;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler.CachedRecipe;

public final class CachedChancedRecipe extends CachedRecipe {
	public final IProcessingRecipe recipe;

	public CachedChancedRecipe(TemplateRecipeHandler handler, IProcessingRecipe recipe) {
		handler.super();
		this.recipe = recipe;
	}

	@Override
	public PositionedStack getIngredient() {
		ItemStack inputStack = recipe.getInput();
		if(inputStack == null) {
			return new PositionedStack(recipe.getInputOreDict(), 5, 3);
		} else {
			return new PositionedStack(inputStack, 5, 3);
		}
	}

	@Override
	public PositionedStack getResult() {
		return new PositionedStack(recipe.getOutput()[0].output, 85, 5);
	}

	public static final int MAX_ROWS = 3;
	
	@Override
	public List<PositionedStack> getOtherStacks() {
		ChancedOutput[] output = recipe.getOutput();
		List<PositionedStack> otherStacks = new ArrayList<PositionedStack>(output.length - 1);
		for(int i = 1; i < output.length; i++) {
			int r = i % MAX_ROWS;
			int c = i / MAX_ROWS;
			otherStacks.add(new PositionedStack(output[i].output, 85 + c*18, 5 + r*18));
		}
		return otherStacks;
	}
}