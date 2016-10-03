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
		int amountWool = 5;
		int amountCarpet = (int)Math.ceil(amountWool / 3f);
		int amountClayAndClass = (int)Math.ceil(amountWool / 8f);

		for(int dyeMeta = 0; dyeMeta < 16; dyeMeta++) {
			int blockMeta = 15-dyeMeta;
			Fluid fluid = TaamMain.fluidsDye[dyeMeta];
			// Wool
			ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
					new SprayerRecipe(
							new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE),
							new FluidStack(fluid, amountWool),
							new ItemStack(Blocks.WOOL, 1, blockMeta))
			);
			// Carpet
			ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
					new SprayerRecipe(
							new ItemStack(Blocks.CARPET, 1, OreDictionary.WILDCARD_VALUE),
							new FluidStack(fluid, amountCarpet),
							new ItemStack(Blocks.CARPET, 1, blockMeta))
			);
			// Hardened Clay
			ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
					new SprayerRecipe(
							new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, OreDictionary.WILDCARD_VALUE),
							new FluidStack(fluid, amountClayAndClass),
							new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, blockMeta))
			);
			ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
					new SprayerRecipe(
							new ItemStack(Blocks.HARDENED_CLAY, 1, 0),
							new FluidStack(fluid, amountClayAndClass),
							new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, blockMeta))
			);
			// Stained Glass
			ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
					new SprayerRecipe(
							new ItemStack(Blocks.STAINED_GLASS, 1, OreDictionary.WILDCARD_VALUE),
							new FluidStack(fluid, amountClayAndClass),
							new ItemStack(Blocks.STAINED_GLASS, 1, blockMeta))
			);
			ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
					new SprayerRecipe(
							new ItemStack(Blocks.GLASS, 1, 0),
							new FluidStack(fluid, amountClayAndClass),
							new ItemStack(Blocks.STAINED_GLASS, 1, blockMeta))
			);
			// Stained Glass Panes
			ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
					new SprayerRecipe(
							new ItemStack(Blocks.STAINED_GLASS_PANE, 1, OreDictionary.WILDCARD_VALUE),
							new FluidStack(fluid, amountClayAndClass),
							new ItemStack(Blocks.STAINED_GLASS_PANE, 1, blockMeta))
			);
			ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
					new SprayerRecipe(
							new ItemStack(Blocks.GLASS_PANE, 1, 0),
							new FluidStack(fluid, amountClayAndClass),
							new ItemStack(Blocks.STAINED_GLASS_PANE, 1, blockMeta))
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
						new FluidStack(TaamMain.fluidsDye[Taam.FLUID_DYE_META.black.ordinal()], amountWool),
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.black.ordinal()))
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
				new SprayerRecipe(
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.coated_chiseled.ordinal()),
						new FluidStack(TaamMain.fluidsDye[Taam.FLUID_DYE_META.black.ordinal()], amountWool),
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.black_chiseled.ordinal()))
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
				new SprayerRecipe(
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.black.ordinal()),
						new FluidStack(TaamMain.fluidsDye[Taam.FLUID_DYE_META.yellow.ordinal()], amountWool),
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.warn1.ordinal()))
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.SPRAYER,
				new SprayerRecipe(
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.black.ordinal()),
						new FluidStack(TaamMain.fluidsDye[Taam.FLUID_DYE_META.red.ordinal()], amountWool),
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.warn2.ordinal()))
		);
	}
}
