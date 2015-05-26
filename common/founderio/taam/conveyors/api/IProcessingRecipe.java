package founderio.taam.conveyors.api;

import java.util.Random;

import net.minecraft.item.ItemStack;

public interface IProcessingRecipe {

	public boolean inputMatches(ItemStack itemStack);
	
	public ItemStack getInput();

	public ChancedOutput[] getOutput();

	public ItemStack[] getOutput(ItemStack input, Random rand);
}
