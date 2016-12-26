package net.teamio.taam;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.teamio.taam.recipes.ChancedOutput;
import net.teamio.taam.recipes.ProcessingRegistry;
import net.teamio.taam.recipes.impl.CrusherRecipe;

public class TaamRecipesCrusher {
	public static void registerRecipes() {
		// Ingots -> Dusts
		Taam.BLOCK_ORE_META[] values = Taam.BLOCK_ORE_META.values();
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

		// Ores -> Dusts & stuff
		int stoneDustMeta = Taam.BLOCK_ORE_META.stone.ordinal();

		String[] oreDic = { "oreCopper", "oreTin", "oreAluminum", "oreBauxite", "oreKaolinite" };
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

		// Other blocks
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.QUARTZ_BLOCK, 1, OreDictionary.WILDCARD_VALUE),
						new ChancedOutput(new ItemStack(Items.QUARTZ, 3), 1.0f),
						new ChancedOutput(new ItemStack(Items.QUARTZ, 1), 0.25f)
				)
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.GLOWSTONE),
						new ChancedOutput(new ItemStack(Items.GLOWSTONE_DUST, 3), 1.0f),
						new ChancedOutput(new ItemStack(Items.GLOWSTONE_DUST, 1), 0.75f)
				)
		);
	}
}
