package net.teamio.taam.recipes.impl;

import net.minecraft.item.ItemStack;
import net.teamio.taam.recipes.ChanceBasedRecipe;
import net.teamio.taam.recipes.ChancedOutput;

public class GrinderRecipe extends ChanceBasedRecipe {

	/**
	 * @param input
	 * @param output
	 */
	public GrinderRecipe(ItemStack input, ChancedOutput... output) {
		super(input, output);
	}

	/**
	 * @param inputOreDict
	 * @param output
	 */
	public GrinderRecipe(String inputOreDict, ChancedOutput... output) {
		super(inputOreDict, output);
	}

}
