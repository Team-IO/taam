package net.teamio.taam.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class MixerRecipe extends BaseProcessingRecipeFluidBased implements IProcessingRecipeFluidBased {

	private final FluidStack outputFluid;

	public MixerRecipe(ItemStack input, FluidStack inputFluid, FluidStack outputFluid) {
		this.input = input;
		this.inputFluid = inputFluid;
		this.outputFluid = outputFluid;
	}

	/*
	 * IProcessingRecipe
	 */

	@Override
	public ChancedOutput[] getOutput() {
		return null;
	}

	@Override
	public ItemStack[] getOutput(ItemStack input) {
		return null;
	}

	/*
	 * IProcessingRecipeFluidBased
	 */

	@Override
	public FluidStack getOutputFluid() {
		return outputFluid;
	}

	@Override
	public FluidStack getOutputFluid(ItemStack input, FluidStack inputFluid) {
		return outputFluid;
	}

}
