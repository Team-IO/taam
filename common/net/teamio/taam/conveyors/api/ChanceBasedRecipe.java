package net.teamio.taam.conveyors.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ChanceBasedRecipe implements IProcessingRecipe {

	private ItemStack input;
	private String inputOreDict;
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
	public ItemStack getInput() {
		return input;
	}
	
	@Override
	public String getInputOreDict() {
		return inputOreDict;
	}

	@Override
	public ChancedOutput[] getOutput() {
		return output;
	}

	@Override
	public ItemStack[] getOutput(ItemStack input, Random rand) {
		ArrayList<ItemStack> output = new ArrayList<ItemStack>();
		for(ChancedOutput co : this.output) {
			if(co != null && co.chance > 0 && co.output != null) {
				if(rand.nextFloat() < co.chance) {
					output.add(co.output.copy());
				}
			}
		}
		return output.toArray(new ItemStack[output.size()]);
	}

	@Override
	public boolean inputMatches(ItemStack itemStack) {
		if(itemStack == null) {
			return input != null || inputOreDict != null;
		} else {
			if(input == null) {
				int[] oreIDs = OreDictionary.getOreIDs(itemStack);
				int myID = OreDictionary.getOreID(inputOreDict);
				return ArrayUtils.contains(oreIDs, myID);
			} else {
				return input.isItemEqual(itemStack) || OreDictionary.itemMatches(itemStack, input, true);
			}
		}
	}
	
}