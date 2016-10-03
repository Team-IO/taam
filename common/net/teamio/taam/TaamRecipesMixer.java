package net.teamio.taam;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.taam.recipes.ProcessingRegistry;
import net.teamio.taam.recipes.impl.MixerRecipe;

public class TaamRecipesMixer {
	public static void registerRecipes() {
		// Water + dye -> fluid dye
		for(int dyeMeta = 0; dyeMeta < 16; dyeMeta++) {
			ProcessingRegistry.registerRecipe(ProcessingRegistry.MIXER,
					new MixerRecipe(
							new ItemStack(Items.DYE, 1, dyeMeta),
							new FluidStack(FluidRegistry.WATER, 10),
							new FluidStack(TaamMain.fluidsDye[dyeMeta], 10))
			);
		}

		// Cement + Water -> concrete
		ProcessingRegistry.registerRecipe(ProcessingRegistry.MIXER,
				new MixerRecipe(
						new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.cement.ordinal()),
						new FluidStack(FluidRegistry.WATER, 1000),
						new FluidStack(TaamMain.fluidsMaterial[Taam.FLUID_MATERIAL_META.concreteFine.ordinal()], 250))
		);

		ProcessingRegistry.registerRecipe(ProcessingRegistry.MIXER,
				new MixerRecipe(
						new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.cementRough.ordinal()),
						new FluidStack(FluidRegistry.WATER, 1000),
						new FluidStack(TaamMain.fluidsMaterial[Taam.FLUID_MATERIAL_META.concreteRough.ordinal()], 250))
		);

		// Resin + Water? -> Coating

		ProcessingRegistry.registerRecipe(ProcessingRegistry.MIXER,
				new MixerRecipe(
						new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.resin.ordinal()),
						new FluidStack(FluidRegistry.WATER, 500),
						new FluidStack(TaamMain.fluidsMaterial[Taam.FLUID_MATERIAL_META.coating.ordinal()], 50))
		);
	}
}
