package net.teamio.taam;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.teamio.taam.Taam.BLOCK_ORE_META;
import net.teamio.taam.recipes.ChancedOutput;
import net.teamio.taam.recipes.ProcessingRegistry;
import net.teamio.taam.recipes.impl.CrusherRecipe;
import net.teamio.taam.recipes.impl.FluidDrierRecipe;
import net.teamio.taam.recipes.impl.GrinderRecipe;
import net.teamio.taam.recipes.impl.MixerRecipe;
import net.teamio.taam.recipes.impl.SprayerRecipe;

public class TaamRecipes {

	public static void addRecipes(){

		/*
		 * Crusher
		 */


		BLOCK_ORE_META[] values = Taam.BLOCK_ORE_META.values();
		for(int meta = 0; meta < values.length; meta++) {
			if(!values[meta].dust) {
				continue;
			}
			if(values[meta].ingot) {
				ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
						new CrusherRecipe(new ItemStack(TaamMain.itemIngot, 1, meta),
								new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, meta), 1.0f)
								));
			}
		}

		int stoneDustMeta = Taam.BLOCK_ORE_META.stone.ordinal();

		String[] oreDic = {"oreCopper", "oreTin","oreAluminum","oreBauxite","oreKaolinte"};
		for (int ore = 0 ; ore < oreDic.length; ore++){
			ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
					new CrusherRecipe(oreDic[ore],
							new ChancedOutput(new ItemStack(TaamMain.itemDust, 2, ore), 1.0f),
							new ChancedOutput(new ItemStack(Blocks.COBBLESTONE), 0.4f),
							new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, stoneDustMeta), 0.3f),
							new ChancedOutput(new ItemStack(Blocks.MOSSY_COBBLESTONE), 0.0001f)
							));
		}

		// Vanilla Ores
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe("oreDiamond",
						new ChancedOutput(new ItemStack(Items.DIAMOND, 1), 1.0f),
						new ChancedOutput(new ItemStack(Items.DIAMOND, 1), 0.01f),
						new ChancedOutput(new ItemStack(Blocks.COBBLESTONE), 0.4f),
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, stoneDustMeta), 0.3f),
						new ChancedOutput(new ItemStack(Blocks.MOSSY_COBBLESTONE), 0.0001f)
						));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe("oreEmerald",
						new ChancedOutput(new ItemStack(Items.EMERALD, 1), 1.0f),
						new ChancedOutput(new ItemStack(Items.EMERALD, 1), 0.01f),
						new ChancedOutput(new ItemStack(Blocks.COBBLESTONE), 0.4f),
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, stoneDustMeta), 0.3f),
						new ChancedOutput(new ItemStack(Blocks.MOSSY_COBBLESTONE), 0.0001f)
						));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe("oreRedstone",
						new ChancedOutput(new ItemStack(Items.REDSTONE, 4), 1.0f),
						new ChancedOutput(new ItemStack(Items.REDSTONE, 2), 0.05f),
						new ChancedOutput(new ItemStack(Items.REDSTONE, 1), 0.01f),
						new ChancedOutput(new ItemStack(Blocks.COBBLESTONE), 0.4f),
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, stoneDustMeta), 0.3f),
						new ChancedOutput(new ItemStack(Blocks.MOSSY_COBBLESTONE), 0.0001f)
						));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe("oreLapis",
						new ChancedOutput(new ItemStack(Items.DYE, 4, 4), 1.0f),
						new ChancedOutput(new ItemStack(Items.DYE, 2, 4), 0.05f),
						new ChancedOutput(new ItemStack(Items.DYE, 1, 4), 0.01f),
						new ChancedOutput(new ItemStack(Blocks.COBBLESTONE), 0.4f),
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, stoneDustMeta), 0.3f),
						new ChancedOutput(new ItemStack(Blocks.MOSSY_COBBLESTONE), 0.0001f)
						));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe("oreQuartz",
						new ChancedOutput(new ItemStack(Items.QUARTZ, 1), 1.0f),
						new ChancedOutput(new ItemStack(Items.QUARTZ, 1), 0.01f),
						new ChancedOutput(new ItemStack(Blocks.NETHERRACK), 0.4f),
						new ChancedOutput(new ItemStack(Blocks.SOUL_SAND), 0.0001f)
						));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe("oreCoal",
						new ChancedOutput(new ItemStack(Items.COAL, 1), 1.0f),
						new ChancedOutput(new ItemStack(Items.COAL, 1), 0.01f),
						new ChancedOutput(new ItemStack(Blocks.COBBLESTONE), 0.4f),
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, stoneDustMeta), 0.3f),
						new ChancedOutput(new ItemStack(Blocks.MOSSY_COBBLESTONE), 0.0001f)
						));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe("oreGold",
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 2, Taam.BLOCK_ORE_META.gold.ordinal()), 1.0f),
						new ChancedOutput(new ItemStack(Blocks.COBBLESTONE), 0.4f),
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, stoneDustMeta), 0.3f),
						new ChancedOutput(new ItemStack(Blocks.MOSSY_COBBLESTONE), 0.0001f)
						));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe("oreIron",
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 2, Taam.BLOCK_ORE_META.iron.ordinal()), 1.0f),
						new ChancedOutput(new ItemStack(Blocks.COBBLESTONE), 0.4f),
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, stoneDustMeta), 0.3f),
						new ChancedOutput(new ItemStack(Blocks.MOSSY_COBBLESTONE), 0.0001f)
						));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe("ingotGold",
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, Taam.BLOCK_ORE_META.gold.ordinal()), 1.0f)
						));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe("ingotIron",
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, Taam.BLOCK_ORE_META.iron.ordinal()), 1.0f)
						));

		// Stone/Cobble/Gravel/etc

		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.STONE),
						new ChancedOutput(new ItemStack(Blocks.COBBLESTONE), 1.0f),
						new ChancedOutput(new ItemStack(Blocks.GRAVEL), 0.15f),
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, stoneDustMeta), 0.3f)
						)
				);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.COBBLESTONE),
						new ChancedOutput(new ItemStack(Blocks.GRAVEL), 1.0f),
						new ChancedOutput(new ItemStack(Blocks.SAND), 0.15f)
						)
				);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.GRAVEL),
						new ChancedOutput(new ItemStack(Blocks.SAND), 1.0f),
						new ChancedOutput(new ItemStack(Blocks.SAND), 0.05f)
						)
				);

		// Ore/Dust Blocks

		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.GOLD_BLOCK),
						new ChancedOutput(new ItemStack(Items.GOLD_INGOT, 9), 1.0f)
						)
				);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.IRON_BLOCK),
						new ChancedOutput(new ItemStack(Items.IRON_INGOT, 9), 1.0f)
						)
				);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.DIAMOND_BLOCK),
						new ChancedOutput(new ItemStack(Items.DIAMOND, 9), 1.0f)
						)
				);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.EMERALD_BLOCK),
						new ChancedOutput(new ItemStack(Items.EMERALD, 9), 1.0f)
						)
				);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.REDSTONE_BLOCK),
						new ChancedOutput(new ItemStack(Items.REDSTONE, 9), 1.0f)
						)
				);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.LAPIS_BLOCK),
						new ChancedOutput(new ItemStack(Items.DYE, 9, 4), 1.0f)
						)
				);

		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.QUARTZ_BLOCK),
						new ChancedOutput(new ItemStack(Items.QUARTZ, 3), 1.0f),
						new ChancedOutput(new ItemStack(Items.QUARTZ, 1), 0.25f)
						)
				);

		/*
		 * Grinder
		 */

		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER,
				new GrinderRecipe(new ItemStack(Blocks.GRASS),
						new ChancedOutput(new ItemStack(Blocks.DIRT), 1.0f),
						new ChancedOutput(new ItemStack(Items.WHEAT_SEEDS), 0.05f),
						new ChancedOutput(new ItemStack(Items.PUMPKIN_SEEDS), 0.05f),
						new ChancedOutput(new ItemStack(Items.MELON_SEEDS), 0.05f),
						new ChancedOutput(new ItemStack(Items.BEETROOT_SEEDS), 0.05f),
						new ChancedOutput(new ItemStack(Blocks.VINE), 0.005f)
						));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER,
				new GrinderRecipe(new ItemStack(Blocks.GRASS_PATH),
						new ChancedOutput(new ItemStack(Blocks.DIRT), 1.0f),
						new ChancedOutput(new ItemStack(Items.WHEAT_SEEDS), 0.02f),
						new ChancedOutput(new ItemStack(Items.PUMPKIN_SEEDS), 0.02f),
						new ChancedOutput(new ItemStack(Items.MELON_SEEDS), 0.02f),
						new ChancedOutput(new ItemStack(Items.BEETROOT_SEEDS), 0.02f)
						));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe("treeLeaves",
				new ChancedOutput(new ItemStack(Items.STICK), 1.0f),
				new ChancedOutput(new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.resin.ordinal()), 0.2f )
				));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe("treeSapling",
				new ChancedOutput(new ItemStack(Items.STICK), 0.5f),
				new ChancedOutput(new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.resin.ordinal()), 0.001f )
				));
		for (int col = 0; col < 16; col++) {
			ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.WOOL, 1, col),
					new ChancedOutput(new ItemStack(Items.STRING, 3), 1f),
					new ChancedOutput(new ItemStack(Items.STRING), 0.1f),
					new ChancedOutput(new ItemStack(Items.DYE, 1, 15-col), 0.001f )
					));
			ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.CARPET, 1, col),
					new ChancedOutput(new ItemStack(Items.STRING, 6), 1f),
					new ChancedOutput(new ItemStack(Items.STRING, 2), 0.1f),
					new ChancedOutput(new ItemStack(Items.DYE, 1, 15-col), 0.001f )
					));
		}
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.YELLOW_FLOWER),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 11), 1f),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 11), 0.4f)
		));
		/*
		0 	Dandelion
		 */
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.RED_FLOWER),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 1), 1f),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 1), 0.4f)
		));
		/*




		 */
		//0 	Poppy
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.RED_FLOWER, 1, 0),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 1), 1f),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 1), 0.4f)
		));
		//1 	Blue Orchid
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.RED_FLOWER, 1, 1),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 12), 1f),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 12), 0.4f)
		));
		//2 	Allium
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.RED_FLOWER, 1, 2),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 13), 1f),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 13), 0.4f)
		));
		//3 	Azure Bluet
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.RED_FLOWER, 1, 3),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 7), 1f),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 7), 0.4f)
		));
		//4 	Red Tulip
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.RED_FLOWER, 1, 4),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 1), 1f),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 1), 0.4f)
		));
		//5 	Orange Tulip
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.RED_FLOWER, 1, 5),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 14), 1f),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 14), 0.4f)
		));
		//6 	White Tulip
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.RED_FLOWER, 1, 6),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 7), 1f),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 7), 0.4f)
		));
		//7 	Pink Tulip
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.RED_FLOWER, 1, 7),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 9), 1f),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 9), 0.4f)
		));
		//8 	Oxeye Daisy
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.RED_FLOWER, 1, 8),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 7), 1f),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 7), 0.4f)
		));
		/*
		Tall flowers
		 */
		//0 	Sunflower
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.DOUBLE_PLANT, 1, 0),
				new ChancedOutput(new ItemStack(Items.DYE, 2, 11), 1f),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 11), 0.4f)
		));
		//1 	Lilac
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.DOUBLE_PLANT, 1, 1),
				new ChancedOutput(new ItemStack(Items.DYE, 2, 13), 1f),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 13), 0.4f)
		));
		//2 	Double Tallgrass
		//3 	Large Fern
		//4 	Rose Bush
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.DOUBLE_PLANT, 1, 4),
				new ChancedOutput(new ItemStack(Items.DYE, 2, 1), 1f),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 1), 0.4f)
		));
		//5 	Peony
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.DOUBLE_PLANT, 1, 5),
				new ChancedOutput(new ItemStack(Items.DYE, 2, 9), 1f),
				new ChancedOutput(new ItemStack(Items.DYE, 1, 9), 0.4f)
		));

		/*
		 * Mixer
		 */
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
						new FluidStack(TaamMain.fluidsMaterial[Taam.FLUID_MATERIAL_META.coating.ordinal()], 25))
				);

		/*
		 * Fluid Dryer
		 */

		ProcessingRegistry.registerRecipe(ProcessingRegistry.FLUIDDRIER,
				new FluidDrierRecipe(
						new FluidStack(TaamMain.fluidsMaterial[Taam.FLUID_MATERIAL_META.concreteFine.ordinal()], 1000),
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.fine.ordinal()))
				);

		ProcessingRegistry.registerRecipe(ProcessingRegistry.FLUIDDRIER,
				new FluidDrierRecipe(
						new FluidStack(TaamMain.fluidsMaterial[Taam.FLUID_MATERIAL_META.concreteRough.ordinal()], 1000),
						new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.rough.ordinal()))
				);

		ProcessingRegistry.registerRecipe(ProcessingRegistry.FLUIDDRIER,
				new FluidDrierRecipe(
						new FluidStack(FluidRegistry.LAVA, 1000),
						new ItemStack(Blocks.OBSIDIAN, 1))
				);


		int metaBlack = Taam.ITEM_MATERIAL_META.pigment_black.ordinal();
		for(int dyeMeta = 0; dyeMeta < 16; dyeMeta++) {
			Fluid fluid = TaamMain.fluidsDye[dyeMeta];

			// 1:1 ratio from dyes to pellets
			FluidStack input = new FluidStack(fluid, 10);
			ItemStack output = new ItemStack(TaamMain.itemMaterial, 1, metaBlack + dyeMeta);

			ProcessingRegistry.registerRecipe(ProcessingRegistry.FLUIDDRIER,
					new FluidDrierRecipe(input, output));
		}


		/*
		 * Sprayer
		 */

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
	public static void addSmeltingRecipes(){
		BLOCK_ORE_META[] values = Taam.BLOCK_ORE_META.values();
		// Ores -> Ingots
		for(int meta = 0; meta < values.length; meta++) {
			if(!values[meta].ingot || !values[meta].ore) {
				continue;
			}
			GameRegistry.addSmelting(new ItemStack(TaamMain.blockOre, 1, meta), new ItemStack(TaamMain.itemIngot, 1, meta), 1);
		}
		// Dusts -> Ingots
		for(int meta = 0; meta < values.length; meta++) {
			if(!values[meta].ingot || !values[meta].dust) {
				continue;
			}
			GameRegistry.addSmelting(new ItemStack(TaamMain.itemDust, 1, meta), new ItemStack(TaamMain.itemIngot, 1, meta), 1);
		}
		// Bauxite (+Dust) -> Aluminum Ingot
		GameRegistry.addSmelting(new ItemStack(TaamMain.blockOre, 1, Taam.BLOCK_ORE_META.bauxite.ordinal()), new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.aluminum_nugget.ordinal()), 1);
		GameRegistry.addSmelting(new ItemStack(TaamMain.itemDust, 1, Taam.BLOCK_ORE_META.bauxite.ordinal()), new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.aluminum_nugget.ordinal()), 1);
		// Resin -> Rubber Bar
		GameRegistry.addSmelting(new ItemStack(TaamMain.itemMaterial,1,Taam.ITEM_MATERIAL_META.resin.ordinal()), new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.rubber_bar.ordinal()), 1);
		//Iron Dust -> Iron Ingot
		GameRegistry.addSmelting(new ItemStack(TaamMain.itemDust, 1, Taam.BLOCK_ORE_META.iron.ordinal()), new ItemStack(TaamMain.itemIngot, 1, Taam.BLOCK_ORE_META.iron.ordinal()), 1);
		//Gold Dust -> Gold Ingot
		GameRegistry.addSmelting(new ItemStack(TaamMain.itemDust, 1, Taam.BLOCK_ORE_META.gold.ordinal()), new ItemStack(TaamMain.itemIngot, 1, Taam.BLOCK_ORE_META.gold.ordinal()), 1);
		//Rubber Bar -> Plastic sheets
		GameRegistry.addSmelting(new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.rubber_bar.ordinal()),new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.plastic_sheet.ordinal()) , 1);
	}

	public static void addOreRecipes(){
		/*
		 * Input Values for better overview
		 */
		ItemStack materialWoodenBoard = new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.wooden_board.ordinal());
		ItemStack materialAluminumPlate = new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.aluminum_plate.ordinal());

		ItemStack partSieve = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.sieve.ordinal());
		ItemStack partRubberBand = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.rubber_band.ordinal());
		ItemStack partWoodenSupportFrame = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.support_frame_wood.ordinal());
		ItemStack partMetalBearing = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.metal_bearing.ordinal());
		ItemStack partSupportFrame = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.support_frame.ordinal());
		ItemStack partIronFrame = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.iron_frame.ordinal());
		ItemStack partPump = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.pump.ordinal());
		ItemStack partTank = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.tank.ordinal());
		ItemStack partNozzle = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.nozzle.ordinal());
		ItemStack partCopperWire = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.copper_wire.ordinal());

		ItemStack blockConveyor2 = new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.conveyor2.ordinal());
		ItemStack blockTrashcan = new ItemStack(TaamMain.blockProductionLineAttachable, 1, Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.trashcan.ordinal());
		ItemStack blockChute = new ItemStack(TaamMain.blockMachines, 1, Taam.BLOCK_MACHINES_META.chute.ordinal());
		ItemStack blockPipe = new ItemStack(TaamMain.itemMachine, 1, Taam.MACHINE_META.pipe.ordinal());
		ItemStack blockMotionSensor = new ItemStack(TaamMain.blockSensor, 1, 0);

		ItemStack blockConcreteCoatedChiseled = new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.coated_chiseled.ordinal());

		ItemStack toolSawAnyDamage = new ItemStack(TaamMain.itemSaw, 1, OreDictionary.WILDCARD_VALUE);

		/*
		 * Conveyor Stuff (Production Line)
		 */

		//		conveyor1 (Wood)
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 8, Taam.BLOCK_PRODUCTIONLINE_META.conveyor1.ordinal()),
				"rRP", "wSw", "WsW",
				'P', Blocks.PISTON,
				'R', partRubberBand,
				'w', materialWoodenBoard,
				'S', partWoodenSupportFrame,
				'W', "plankWood",
				's', "stickWood",
				'r', "dustRedstone"
				));
		//		conveyor2 (Aluminum)
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 8, Taam.BLOCK_PRODUCTIONLINE_META.conveyor2.ordinal()),
				"MRM", "CSC", "ABA",
				'M', "partMotor",
				'R', partRubberBand,
				'C', partMetalBearing,
				'S', partSupportFrame,
				'A', "ingotAluminum",
				'B', "materialPlastic"));
		//		conveyor3 (High Throughput)
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.conveyor3.ordinal()),
				"YBY", "F F", "MCM",
				'M', "partMotor",
				'C', blockConveyor2,
				'F', partIronFrame,
				'Y', "dyeYellow",
				'B', "dyeBlack"
				));
		//		conveyor hopper
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.hopper.ordinal()),
				Blocks.HOPPER,
				partSupportFrame));

		//		conveyor chute
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.chute.ordinal()),
				blockChute,
				partSupportFrame));

		//		High Speed Conveyor Hopper
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.hopper_hs.ordinal()),
				"C C"," H ", " I ",
				'C', blockConveyor2,
				'H', Blocks.HOPPER,
				'I', partSupportFrame
				));
		//		Shredder
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.shredder.ordinal()),
				"P P","MTB", "bSb",
				'P', materialAluminumPlate,
				'M', "partMotor",
				'T', blockTrashcan,
				'B', partMetalBearing,
				'b', "partBasicCircuit",
				'S', partSupportFrame
				));
		//		Grinder
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.grinder.ordinal()),
				"PsP","MCB", "bSb",
				'P', materialAluminumPlate,
				's', partSieve,
				'M', "partMotor",
				'C', blockChute,
				'B', partMetalBearing,
				'b', "partBasicCircuit",
				'S', partSupportFrame
				));
		//		Crusher
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.crusher.ordinal()),
				"P P","MCB", "bSb",
				'P', materialAluminumPlate,
				'M', "partMotor",
				'C', blockChute,
				'B', partMetalBearing,
				'b', "partBasicCircuit",
				'S', partSupportFrame
				));
		//		Sieve
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.sieve.ordinal()),
				"AsA","ACA", "MSM",
				'A', "ingotAluminum",
				'S', partSieve,
				'C', blockChute,
				'M', "partMotor",
				'S', partSupportFrame,
				's', partSieve
				));

		/*
		 * Conveyor Stuff (Production Line Attachables)
		 */

		//		Conveyor Item Bag
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLineAttachable, 1, Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.itembag.ordinal()),
				"PLP","PIP","PPP",
				'P', "materialPlastic",
				'L', "dyeBlue",
				'I', partIronFrame
		));
		//		Conveyor Trash Bag
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLineAttachable, 1, Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.trashcan.ordinal()),
				"PLP","PIP","PPP",
				'P', "materialPlastic",
				'L', "dyeGreen",
				'I', partIronFrame
		));

		/*
		 * Conveyor Stuff (Appliances)
		 */

		//		sprayer
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLineAppliance, 1, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.sprayer.ordinal()),
				"RNN", "TCP", "aaa",
				'N', partNozzle,
				'C', "partBasicCircuit",
				'a', materialAluminumPlate,
				'P', partPump,
				'R', blockPipe,
				'T', partTank
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLineAppliance, 1, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.sprayer.ordinal()),
				"NNR", "PCT", "aaa",
				'N', partNozzle,
				'C', "partBasicCircuit",
				'a', materialAluminumPlate,
				'P', partPump,
				'R', blockPipe,
				'T', partTank
		));

		//      aligner
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLineAppliance, 1, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.aligner.ordinal()),
				"PSP", "AMM", "PP ",
				'S', blockMotionSensor,
				'A', "partAdvancedCircuit",
				'P', materialAluminumPlate,
				'M', "partMotor"
		));

		/*
		 * Fluid Machines
		 */
		//      pipe
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMachine, 4, Taam.MACHINE_META.pipe.ordinal()),
				"III", "nnn", "III",
				'I', "ingotIron",
				'n', "nuggetIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMachine, 4, Taam.MACHINE_META.pipe.ordinal()),
				"InI", "InI", "InI",
				'I', "ingotIron",
				'n', "nuggetIron"));
		//      tank
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMachine, 1, Taam.MACHINE_META.tank.ordinal()),
				"GGG", "G G", "SSS",
				'G', "paneGlass",
				'S', blockConcreteCoatedChiseled));
		//      pump
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMachine, 1, Taam.MACHINE_META.pump.ordinal()),
				"AAA", "PTR", "SSS",
				'A', "ingotAluminum",
				'P', partPump,
				'T', partTank,
				'R', blockPipe,
				'S', blockConcreteCoatedChiseled));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMachine, 1, Taam.MACHINE_META.pump.ordinal()),
				"AAA", "RTP", "SSS",
				'A', "ingotAluminum",
				'P', partPump,
				'T', partTank,
				'R', blockPipe,
				'S', blockConcreteCoatedChiseled));
		//      mixer
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMachine, 1, Taam.MACHINE_META.mixer.ordinal()),
				" M ", "RCR", "SSS",
				'C', blockChute,
				'M', "partMotor",
				'R', blockPipe,
				'S', blockConcreteCoatedChiseled));
		//      fluid drier
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMachine, 1, Taam.MACHINE_META.fluid_drier.ordinal()),
				" R ", "sMs", " C ",
				'C', blockChute,
				'M', "partMotor",
				's', partSieve,
				'R', blockPipe));

		/*
		 * Other Machines
		 */

		//		chute
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockMachines, 1, Taam.BLOCK_MACHINES_META.chute.ordinal()),
				"InI","InI","nIn",
				'I', "ingotIron"));

		//		motion sensor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockSensor, 1, 0),
				"PGP", "PpP", "IRI",
				'P', "materialPlastic",
				'G', "blockGlass",
				'p', "partPhotocell",
				'I', "ingotIron",
				'R', "dustRedstone"));

		//      industial lamp
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockLamp, 2, 0),
				"GPG", "grg",
				'P', "materialPlastic",
				'G', "paneGlass",
				'g', "dustGlowstone",
				'r', "dustRedstone"));
		/*
		 * Tools
		 */

		//		wrench
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemWrench, 1, 0),
				"*I*", "II*", "**I",
				'I', "ingotIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemWrench, 1, 0),
				"*I*", "*II", "I**",
				'I', "ingotIron"));
		//		saw
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemSaw, 1, 0),
				"IIS",
				'I', "ingotIron",
				'S', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemSaw, 1, 0),
				"SII",
				'I', "ingotIron",
				'S', "stickWood"));

		/*
		 * Parts
		 */
		//		photo cell
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.itemPart, 9, Taam.ITEM_PART_META.photocell.ordinal()),
				Blocks.DAYLIGHT_DETECTOR,
				toolSawAnyDamage
		));
		//		magnetic coil
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.magnetic_coil.ordinal()),
				"CCC", "CIC", "CCC",
				'C', partCopperWire,
				'I', "ingotIron"
		));
		//		metal bearing
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.metal_bearing.ordinal()),
				"INI", "N*N", "INI",
				'I', "ingotIron",
				'N', "nuggetIron"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.metal_bearing.ordinal()),
				"NIN", "I*I", "NIN",
				'I', "ingotIron",
				'N', "nuggetIron"
		));
		//		support frame
		GameRegistry.addRecipe(new ShapedOreRecipe(partSupportFrame,
				"*A*", "A*A", "AAA",
				'A', "ingotAluminum"));
		//		support frame wood
		GameRegistry.addRecipe(new ShapedOreRecipe(partWoodenSupportFrame,
				"*S*", "S*S", "WWW",
				'W', "plankWood",
				'S', "stickWood"
		));
		//		copper wire
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 12, Taam.ITEM_PART_META.copper_wire.ordinal()),
				"CCC",
				'C', "ingotCopper"
		));

		//		basic circuit
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.circuit_basic.ordinal()),
				"CCC", "RGR", "CCC",
				'C', "ingotCopper",
				'G', "ingotGold",
				'R', "dustRedstone"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.circuit_basic.ordinal()),
				"CRC", "CGC", "CRC",
				'C', "ingotCopper",
				'G', "ingotGold",
				'R', "dustRedstone"
		));
		//		advanced circuit
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.circuit_advanced.ordinal()),
				"RGR", "GCG", "RGR",
				'R', "dustRedstone",
				'G', "ingotGold",
				'C', "partBasicCircuit"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.circuit_advanced.ordinal()),
				"GRG", "RCR", "GRG",
				'R', "dustRedstone",
				'G', "ingotGold",
				'C', "partBasicCircuit"
		));
		//		logistics chip
		/*GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.logistics_chip.ordinal()),
				"PKP", "HCH", "PVP",
				'P', "materialPlastic",
				'K', Blocks.chest,
				'H', Blocks.hopper,
				'V', Items.comparator,
				'C', "partAdvancedCircuit"
		));*/
		//		iron frame
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.iron_frame.ordinal()),
				" I ", "I I", "I I",
				'I', "ingotIron"
		));

		//		rubber band
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.rubber_band.ordinal()),
				"RRR", "R R", "RRR",
				'R', "materialRubber"
		));
		//		tank
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.tank.ordinal()),
				" I ", "I I", "III",
				'I', "ingotIron"
		));
		//		nozzle
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1 ,Taam.ITEM_PART_META.nozzle.ordinal()),
				" I ", "I I", " I ",
				'I', "nuggetIron"
		));

		//		Pump
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.pump.ordinal()),
				"AAA","PMP", "AAA",
				'M', "partMotor",
				'P', Blocks.PISTON,
				'A', "ingotAluminum"
		));
		//		Motor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.motor.ordinal()),
				"ACA", "CIC", "ACA",
				'C', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.magnetic_coil.ordinal()),
				'I', "ingotIron",
				'A', "ingotAluminum"
		));
		//		Sieve
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.sieve.ordinal()),
				"IBI", "BBB", "IBI",
				'B', Blocks.IRON_BARS,
				'I', "ingotIron"
		));

		/*
		 * Materials
		 */

		//		iron nugget
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.itemMaterial, 9, Taam.ITEM_MATERIAL_META.iron_nugget.ordinal()), "ingotIron"));
		String nugget = "nuggetIron";
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.IRON_INGOT), nugget, nugget, nugget, nugget, nugget, nugget, nugget, nugget, nugget));

		//		copper nugget
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.itemMaterial, 9, Taam.ITEM_MATERIAL_META.copper_nugget.ordinal()), "ingotCopper"));
		nugget = "nuggetCopper";
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.itemIngot, 1, Taam.BLOCK_ORE_META.copper.ordinal()), nugget, nugget, nugget, nugget, nugget, nugget, nugget, nugget, nugget));

		//		tin nugget
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.itemMaterial, 9, Taam.ITEM_MATERIAL_META.tin_nugget.ordinal()), "ingotTin"));
		nugget = "nuggetTin";
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.itemIngot, 1, Taam.BLOCK_ORE_META.tin.ordinal()), nugget, nugget, nugget, nugget, nugget, nugget, nugget, nugget, nugget));

		//		aluminum nugget
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.itemMaterial, 9, Taam.ITEM_MATERIAL_META.aluminum_nugget.ordinal()), "ingotAluminum"));
		nugget = "nuggetAluminum";
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.itemIngot, 1, Taam.BLOCK_ORE_META.aluminum.ordinal()), nugget, nugget, nugget, nugget, nugget, nugget, nugget, nugget, nugget));

		//		wooden board
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMaterial, 6 ,Taam.ITEM_MATERIAL_META.wooden_board.ordinal()),
				"www", "s s",
				'w', "plankWood",
				's', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMaterial, 6 ,Taam.ITEM_MATERIAL_META.wooden_board.ordinal()),
				"s s", "www",
				'w', "plankWood",
				's', "stickWood"));

		//		aluminum plate
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMaterial, 6 ,Taam.ITEM_MATERIAL_META.aluminum_plate.ordinal()),
				" a ", "aaa",
				'a', "ingotAluminum"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMaterial, 6 ,Taam.ITEM_MATERIAL_META.aluminum_plate.ordinal()),
				"aaa", " a ",
				'a', "ingotAluminum"));
	}

}
