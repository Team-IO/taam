package net.teamio.taam.recipes;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IProcessingRecipeFluidBased extends IProcessingRecipe {

	boolean inputFluidMatches(FluidStack fluid);

	FluidStack getInputFluid();
	
	FluidStack getOutputFluid(ItemStack input, FluidStack inputFluid, Random rand);
	
	FluidStack getOutputFluid();
	
}
