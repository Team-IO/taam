package net.teamio.taam.recipes.impl;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.taam.content.piping.MachineFluidDrier;
import net.teamio.taam.recipes.BaseProcessingRecipeFluidBased;
import net.teamio.taam.recipes.ChancedOutput;

/**
 * Recipe class for the {@link MachineFluidDrier}.
 *
 * @author Oliver Kahrmann
 *
 */
public class FluidDrierRecipe extends BaseProcessingRecipeFluidBased {

	private ChancedOutput[] output;
	private ItemStack outputStack;

	public FluidDrierRecipe(FluidStack input, ItemStack output) {
		inputFluid = input;
		outputStack = output;
		this.output = new ChancedOutput[] { new ChancedOutput(output, 1) };
	}

	@Override
	public FluidStack getOutputFluid(ItemStack input, FluidStack inputFluid) {
		return null;
	}

	@Override
	public FluidStack getOutputFluid() {
		return null;
	}

	@Override
	public ChancedOutput[] getOutput() {
		return output;
	}

	public ItemStack getOutputStack() {
		return outputStack;
	}

	@Override
	public ItemStack[] getOutput(ItemStack input) {
		return new ItemStack[] { output[0].output.copy() };
	}

}
