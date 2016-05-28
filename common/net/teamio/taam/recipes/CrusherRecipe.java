package net.teamio.taam.recipes;

import net.minecraft.item.ItemStack;

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
