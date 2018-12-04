package net.teamio.taam.recipes;

import net.minecraft.item.ItemStack;

public interface IProcessingRecipe {

	boolean inputMatches(ItemStack itemStack);

	ItemStack getInput();

	String getInputOreDict();

	ChancedOutput[] getOutput();

	ItemStack[] getOutput(ItemStack input);
}
