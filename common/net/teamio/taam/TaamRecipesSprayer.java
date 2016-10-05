package net.teamio.taam;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.teamio.taam.recipes.ProcessingRegistry;
import net.teamio.taam.recipes.impl.SprayerRecipe;

public class TaamRecipesSprayer {
	public static void registerRecipes() {
		int amountwool = 5;
		int amountcarpet = (int)Math.ceil(amountwool / 3f);
		int amountClayAndClass = (int)Math.ceil(amountwool / 8f);

		for(int dyeMeta = 0; dyeMeta < 16; dyeMeta++) {
			int blockMeta = 15-dyeMeta;
			Fluid fluid = TaamMain.fluidsDye[dyeMeta];
			// wool
			ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
					new SprayerRecipe(
							new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE),
							new FluidStack(fluid, amountwool),
							new ItemStack(Blocks.wool, 1, blockMeta))
			);
			// carpet
			ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
					new SprayerRecipe(
							new ItemStack(Blocks.carpet, 1, OreDictionary.WILDCARD_VALUE),
							new FluidStack(fluid, amountcarpet),
							new ItemStack(Blocks.carpet, 1, blockMeta))
			);
			// Hardened Clay
			ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
					new SprayerRecipe(
							new ItemStack(Blocks.stained_hardened_clay, 1, OreDictionary.WILDCARD_VALUE),
							new FluidStack(fluid, amountClayAndClass),
							new ItemStack(Blocks.stained_hardened_clay, 1, blockMeta))
			);
			ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
					new SprayerRecipe(
							new ItemStack(Blocks.hardened_clay, 1, 0),
							new FluidStack(fluid, amountClayAndClass),
							new ItemStack(Blocks.stained_hardened_clay, 1, blockMeta))
			);
			// Stained glass
			ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
					new SprayerRecipe(
							new ItemStack(Blocks.stained_glass, 1, OreDictionary.WILDCARD_VALUE),
							new FluidStack(fluid, amountClayAndClass),
							new ItemStack(Blocks.stained_glass, 1, blockMeta))
			);
			ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
					new SprayerRecipe(
							new ItemStack(Blocks.glass, 1, 0),
							new FluidStack(fluid, amountClayAndClass),
							new ItemStack(Blocks.stained_glass, 1, blockMeta))
			);
			// Stained glass panes
			ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
					new SprayerRecipe(
							new ItemStack(Blocks.stained_glass_pane, 1, OreDictionary.WILDCARD_VALUE),
							new FluidStack(fluid, amountClayAndClass),
							new ItemStack(Blocks.stained_glass_pane, 1, blockMeta))
			);
			ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
					new SprayerRecipe(
							new ItemStack(Blocks.glass_pane, 1, 0),
							new FluidStack(fluid, amountClayAndClass),
							new ItemStack(Blocks.stained_glass_pane, 1, blockMeta))
			);
		}
		// Concrete Coating
		ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
				new SprayerRecipe(
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.fine.ordinal()),
						new FluidStack(TaamMain.fluidsMaterial[Taam.FLUID_MATERIAL_META.coating.ordinal()], 250),
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.coated.ordinal()))
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
				new SprayerRecipe(
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.fine_chiseled.ordinal()),
						new FluidStack(TaamMain.fluidsMaterial[Taam.FLUID_MATERIAL_META.coating.ordinal()], 250),
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.coated_chiseled.ordinal()))
		);
		// Concrete Coloring
		ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
				new SprayerRecipe(
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.coated.ordinal()),
						new FluidStack(TaamMain.fluidsDye[Taam.FLUID_DYE_META.black.ordinal()], amountwool),
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.black.ordinal()))
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
				new SprayerRecipe(
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.coated_chiseled.ordinal()),
						new FluidStack(TaamMain.fluidsDye[Taam.FLUID_DYE_META.black.ordinal()], amountwool),
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.black_chiseled.ordinal()))
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
				new SprayerRecipe(
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.black.ordinal()),
						new FluidStack(TaamMain.fluidsDye[Taam.FLUID_DYE_META.yellow.ordinal()], amountwool),
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.warn1.ordinal()))
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
				new SprayerRecipe(
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.black.ordinal()),
						new FluidStack(TaamMain.fluidsDye[Taam.FLUID_DYE_META.red.ordinal()], amountwool),
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.warn2.ordinal()))
		);
	}
}
