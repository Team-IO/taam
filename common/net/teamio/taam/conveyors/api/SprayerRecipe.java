package net.teamio.taam.conveyors.api;

import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class SprayerRecipe implements IProcessingRecipe {

	private ItemStack input;
	private String inputOreDict;

	private FluidStack inputFluid;
	private ChancedOutput[] output;
	

	public SprayerRecipe(String inputOreDict, FluidStack inputFluid, ChancedOutput output) {
		this.inputOreDict = inputOreDict;
		this.output = new ChancedOutput[] { output };
		this.inputFluid = inputFluid;
	}
	
	public SprayerRecipe(ItemStack input, FluidStack inputFluid, ChancedOutput output) {
		this.input = input;
		this.output = new ChancedOutput[] { output };
		this.inputFluid = inputFluid;
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
	
	public boolean inputFluidMatches(FluidStack fluid) {
		return inputFluid.isFluidEqual(fluid);
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
		return new ItemStack[] { output[0].output.copy() };
	}

}
