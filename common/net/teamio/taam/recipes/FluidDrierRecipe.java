package net.teamio.taam.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * Recipe class for the
 * {@link net.teamio.taam.content.piping.TileEntityFluidDrier}.
 * 
 * @author Oliver Kahrmann
 *
 */
public class FluidDrierRecipe extends BaseProcessingRecipeFluidBased {

	private ChancedOutput[] output;

	public FluidDrierRecipe(FluidStack input, ItemStack output) {
		this.inputFluid = input;
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

	@Override
	public ItemStack[] getOutput(ItemStack input) {
		return new ItemStack[] { output[0].output.copy() };
	}

}
