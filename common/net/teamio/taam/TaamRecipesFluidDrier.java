package net.teamio.taam;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.taam.recipes.ProcessingRegistry;
import net.teamio.taam.recipes.impl.FluidDrierRecipe;

public class TaamRecipesFluidDrier {
	public static void registerRecipes() {
		// fine concrete
		ProcessingRegistry.registerRecipe(ProcessingRegistry.FLUIDDRIER,
				new FluidDrierRecipe(
						new FluidStack(TaamMain.fluidsMaterial[Taam.FLUID_MATERIAL_META.concreteFine.ordinal()], 1000),
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.fine.ordinal()))
		);

		// rough concrete
		ProcessingRegistry.registerRecipe(ProcessingRegistry.FLUIDDRIER,
				new FluidDrierRecipe(
						new FluidStack(TaamMain.fluidsMaterial[Taam.FLUID_MATERIAL_META.concreteRough.ordinal()], 1000),
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.rough.ordinal()))
		);

		// lava -> obsidian
		ProcessingRegistry.registerRecipe(ProcessingRegistry.FLUIDDRIER,
				new FluidDrierRecipe(
						new FluidStack(FluidRegistry.LAVA, 1000),
						new ItemStack(Blocks.obsidian, 1))
		);

		// dyes -> pigment pellet
		int metaBlack = Taam.ITEM_MATERIAL_META.pigment_black.ordinal();
		for(int dyeMeta = 0; dyeMeta < 16; dyeMeta++) {
			Fluid fluid = TaamMain.fluidsDye[dyeMeta];

			// 1:1 ratio from dyes to pellets
			FluidStack input = new FluidStack(fluid, 10);
			ItemStack output = new ItemStack(TaamMain.itemMaterial, 1, metaBlack + dyeMeta);

			ProcessingRegistry.registerRecipe(ProcessingRegistry.FLUIDDRIER,
					new FluidDrierRecipe(input, output));
		}
	}
}
