package net.teamio.taam.recipes;

import net.minecraft.item.ItemStack;

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
