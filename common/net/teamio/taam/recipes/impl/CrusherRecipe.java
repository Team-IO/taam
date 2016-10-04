package net.teamio.taam.recipes.impl;

import net.minecraft.item.ItemStack;
import net.teamio.taam.recipes.ChanceBasedRecipe;
import net.teamio.taam.recipes.ChancedOutput;

public class CrusherRecipe extends ChanceBasedRecipe {

	/**
	 * @param input
	 * @param output
	 */
	public CrusherRecipe(ItemStack input, ChancedOutput... output) {
		super(input, output);
	}

	/**
	 * @param inputOreDict
	 * @param output
	 */
	public CrusherRecipe(String inputOreDict, ChancedOutput... output) {
		super(inputOreDict, output);
	}

}
