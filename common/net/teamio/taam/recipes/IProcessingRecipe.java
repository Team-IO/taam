package net.teamio.taam.recipes;

import java.util.Random;

import net.minecraft.item.ItemStack;

public interface IProcessingRecipe {

	public boolean inputMatches(ItemStack itemStack);

	public ItemStack getInput();

	public String getInputOreDict();

	public ChancedOutput[] getOutput();

	public ItemStack[] getOutput(ItemStack input, Random rand);
}
