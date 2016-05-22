package net.teamio.taam.recipes;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.teamio.taam.util.TaamUtil;

public abstract class ChanceBasedRecipe extends BaseProcessingRecipe implements IProcessingRecipe {

	private ChancedOutput[] output;

	public ChanceBasedRecipe(String inputOreDict, ChancedOutput... output) {
		this.inputOreDict = inputOreDict;
		this.output = output;
	}

	public ChanceBasedRecipe(ItemStack input, ChancedOutput... output) {
		this.input = input;
		this.output = output;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inputOreDict == null) ? 0 : inputOreDict.hashCode());
		result = prime * result + ((input == null) ? 0 : input.hashCode());
		result = prime * result + Arrays.hashCode(output);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChanceBasedRecipe other = (ChanceBasedRecipe) obj;
		if (input == null) {
			if (other.input != null)
				return false;
		} else if (!input.equals(other.input))
			return false;
		if (!Arrays.equals(output, other.output))
			return false;
		return true;
	}

	@Override
	public ChancedOutput[] getOutput() {
		return output;
	}

	@Override
	public ItemStack[] getOutput(ItemStack input) {
		ArrayList<ItemStack> output = new ArrayList<ItemStack>();
		for (ChancedOutput co : this.output) {
			if (co != null && co.chance > 0 && co.output != null) {
				if (TaamUtil.RANDOM.nextFloat() < co.chance) {
					output.add(co.output.copy());
				}
			}
		}
		return output.toArray(new ItemStack[output.size()]);
	}

}