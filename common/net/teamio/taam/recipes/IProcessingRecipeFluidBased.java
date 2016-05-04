package net.teamio.taam.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IProcessingRecipeFluidBased extends IProcessingRecipe {

	boolean inputFluidMatches(FluidStack fluid);

	FluidStack getInputFluid();

	FluidStack getOutputFluid(ItemStack input, FluidStack inputFluid);

	FluidStack getOutputFluid();

}
