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
							new ChancedOutput(new ItemStack(Blocks.cobblestone), 0.4f),
							new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, stoneDustMeta), 0.3f),
							new ChancedOutput(new ItemStack(Blocks.mossy_cobblestone), 0.0001f)
					));
		}

		// Vanilla Ores
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe("orediamond",
						new ChancedOutput(new ItemStack(Items.diamond, 1), 1.0f),
						new ChancedOutput(new ItemStack(Items.diamond, 1), 0.01f),
						new ChancedOutput(new ItemStack(Blocks.cobblestone), 0.4f),
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, stoneDustMeta), 0.3f),
						new ChancedOutput(new ItemStack(Blocks.mossy_cobblestone), 0.0001f)
				));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe("oreemerald",
						new ChancedOutput(new ItemStack(Items.emerald, 1), 1.0f),
						new ChancedOutput(new ItemStack(Items.emerald, 1), 0.01f),
						new ChancedOutput(new ItemStack(Blocks.cobblestone), 0.4f),
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, stoneDustMeta), 0.3f),
						new ChancedOutput(new ItemStack(Blocks.mossy_cobblestone), 0.0001f)
				));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe("oreredstone",
						new ChancedOutput(new ItemStack(Items.redstone, 4), 1.0f),
						new ChancedOutput(new ItemStack(Items.redstone, 2), 0.05f),
						new ChancedOutput(new ItemStack(Items.redstone, 1), 0.01f),
						new ChancedOutput(new ItemStack(Blocks.cobblestone), 0.4f),
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, stoneDustMeta), 0.3f),
						new ChancedOutput(new ItemStack(Blocks.mossy_cobblestone), 0.0001f)
				));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe("oreLapis",
						new ChancedOutput(new ItemStack(Items.dye, 4, 4), 1.0f),
						new ChancedOutput(new ItemStack(Items.dye, 2, 4), 0.05f),
						new ChancedOutput(new ItemStack(Items.dye, 1, 4), 0.01f),
						new ChancedOutput(new ItemStack(Blocks.cobblestone), 0.4f),
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, stoneDustMeta), 0.3f),
						new ChancedOutput(new ItemStack(Blocks.mossy_cobblestone), 0.0001f)
				));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe("orequartz",
						new ChancedOutput(new ItemStack(Items.quartz, 1), 1.0f),
						new ChancedOutput(new ItemStack(Items.quartz, 1), 0.01f),
						new ChancedOutput(new ItemStack(Blocks.netherrack), 0.4f),
						new ChancedOutput(new ItemStack(Blocks.soul_sand), 0.0001f)
				));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe("orecoal",
						new ChancedOutput(new ItemStack(Items.coal, 1), 1.0f),
						new ChancedOutput(new ItemStack(Items.coal, 1), 0.01f),
						new ChancedOutput(new ItemStack(Blocks.cobblestone), 0.4f),
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, stoneDustMeta), 0.3f),
						new ChancedOutput(new ItemStack(Blocks.mossy_cobblestone), 0.0001f)
				));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe("oreGold",
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 2, Taam.BLOCK_ORE_META.gold.ordinal()), 1.0f),
						new ChancedOutput(new ItemStack(Blocks.cobblestone), 0.4f),
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, stoneDustMeta), 0.3f),
						new ChancedOutput(new ItemStack(Blocks.mossy_cobblestone), 0.0001f)
				));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe("oreIron",
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 2, Taam.BLOCK_ORE_META.iron.ordinal()), 1.0f),
						new ChancedOutput(new ItemStack(Blocks.cobblestone), 0.4f),
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, stoneDustMeta), 0.3f),
						new ChancedOutput(new ItemStack(Blocks.mossy_cobblestone), 0.0001f)
				));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe("ingotGold",
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, Taam.BLOCK_ORE_META.gold.ordinal()), 1.0f)
				));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe("ingotIron",
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, Taam.BLOCK_ORE_META.iron.ordinal()), 1.0f)
				));

		// stone/Cobble/gravel/etc

		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.stone),
						new ChancedOutput(new ItemStack(Blocks.cobblestone), 1.0f),
						new ChancedOutput(new ItemStack(Blocks.gravel), 0.15f),
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, stoneDustMeta), 0.3f)
				)
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.cobblestone),
						new ChancedOutput(new ItemStack(Blocks.gravel), 1.0f),
						new ChancedOutput(new ItemStack(Blocks.sand), 0.15f)
				)
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.gravel),
						new ChancedOutput(new ItemStack(Blocks.sand), 1.0f),
						new ChancedOutput(new ItemStack(Blocks.sand), 0.05f)
				)
		);

		// Ore/Dust Blocks

		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.gold_block),
						new ChancedOutput(new ItemStack(Items.gold_ingot, 9), 1.0f)
				)
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.iron_block),
						new ChancedOutput(new ItemStack(Items.iron_ingot, 9), 1.0f)
				)
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.diamond_block),
						new ChancedOutput(new ItemStack(Items.diamond, 9), 1.0f)
				)
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.emerald_block),
						new ChancedOutput(new ItemStack(Items.emerald, 9), 1.0f)
				)
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.redstone_block),
						new ChancedOutput(new ItemStack(Items.redstone, 9), 1.0f)
				)
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.lapis_block),
						new ChancedOutput(new ItemStack(Items.dye, 9, 4), 1.0f)
				)
		);

		// Other blocks
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.quartz_block, 1, OreDictionary.WILDCARD_VALUE),
						new ChancedOutput(new ItemStack(Items.quartz, 3), 1.0f),
						new ChancedOutput(new ItemStack(Items.quartz, 1), 0.25f)
				)
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new CrusherRecipe(new ItemStack(Blocks.glowstone),
						new ChancedOutput(new ItemStack(Items.glowstone_dust, 3), 1.0f),
						new ChancedOutput(new ItemStack(Items.glowstone_dust, 1), 0.75f)
				)
		);
	}
}
