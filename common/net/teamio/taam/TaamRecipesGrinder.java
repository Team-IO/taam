package net.teamio.taam;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.teamio.taam.recipes.ChancedOutput;
import net.teamio.taam.recipes.ProcessingRegistry;
import net.teamio.taam.recipes.impl.GrinderRecipe;

public class TaamRecipesGrinder {
	public static void registerRecipes() {
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER,
				new GrinderRecipe(new ItemStack(Blocks.grass),
						new ChancedOutput(new ItemStack(Blocks.dirt), 1.0f),
						new ChancedOutput(new ItemStack(Items.wheat_seeds), 0.05f),
						new ChancedOutput(new ItemStack(Items.pumpkin_seeds), 0.05f),
						new ChancedOutput(new ItemStack(Items.melon_seeds), 0.05f),
						new ChancedOutput(new ItemStack(Blocks.vine), 0.005f)
				));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe("treeLeaves",
				new ChancedOutput(new ItemStack(Items.stick), 1.0f),
				new ChancedOutput(new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.resin.ordinal()), 0.2f )
		));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe("treeSapling",
				new ChancedOutput(new ItemStack(Items.stick), 0.5f),
				new ChancedOutput(new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.resin.ordinal()), 0.001f )
		));
		for (int col = 0; col < 16; col++) {
			ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.wool, 1, col),
					new ChancedOutput(new ItemStack(Items.string, 3), 1f),
					new ChancedOutput(new ItemStack(Items.string), 0.1f),
					new ChancedOutput(new ItemStack(Items.dye, 1, 15-col), 0.001f )
			));
			ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.carpet, 1, col),
					new ChancedOutput(new ItemStack(Items.string, 6), 1f),
					new ChancedOutput(new ItemStack(Items.string, 2), 0.1f),
					new ChancedOutput(new ItemStack(Items.dye, 1, 15-col), 0.001f )
			));
		}
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.yellow_flower),
				new ChancedOutput(new ItemStack(Items.dye, 1, 11), 1f),
				new ChancedOutput(new ItemStack(Items.dye, 1, 11), 0.4f)
		));
		/*
		0 	Dandelion
		 */
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.red_flower),
				new ChancedOutput(new ItemStack(Items.dye, 1, 1), 1f),
				new ChancedOutput(new ItemStack(Items.dye, 1, 1), 0.4f)
		));
		/*




		 */
		//0 	Poppy
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.red_flower, 1, 0),
				new ChancedOutput(new ItemStack(Items.dye, 1, 1), 1f),
				new ChancedOutput(new ItemStack(Items.dye, 1, 1), 0.4f)
		));
		//1 	Blue Orchid
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.red_flower, 1, 1),
				new ChancedOutput(new ItemStack(Items.dye, 1, 12), 1f),
				new ChancedOutput(new ItemStack(Items.dye, 1, 12), 0.4f)
		));
		//2 	Allium
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.red_flower, 1, 2),
				new ChancedOutput(new ItemStack(Items.dye, 1, 13), 1f),
				new ChancedOutput(new ItemStack(Items.dye, 1, 13), 0.4f)
		));
		//3 	Azure Bluet
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.red_flower, 1, 3),
				new ChancedOutput(new ItemStack(Items.dye, 1, 7), 1f),
				new ChancedOutput(new ItemStack(Items.dye, 1, 7), 0.4f)
		));
		//4 	Red Tulip
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.red_flower, 1, 4),
				new ChancedOutput(new ItemStack(Items.dye, 1, 1), 1f),
				new ChancedOutput(new ItemStack(Items.dye, 1, 1), 0.4f)
		));
		//5 	Orange Tulip
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.red_flower, 1, 5),
				new ChancedOutput(new ItemStack(Items.dye, 1, 14), 1f),
				new ChancedOutput(new ItemStack(Items.dye, 1, 14), 0.4f)
		));
		//6 	White Tulip
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.red_flower, 1, 6),
				new ChancedOutput(new ItemStack(Items.dye, 1, 7), 1f),
				new ChancedOutput(new ItemStack(Items.dye, 1, 7), 0.4f)
		));
		//7 	Pink Tulip
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.red_flower, 1, 7),
				new ChancedOutput(new ItemStack(Items.dye, 1, 9), 1f),
				new ChancedOutput(new ItemStack(Items.dye, 1, 9), 0.4f)
		));
		//8 	Oxeye Daisy
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.red_flower, 1, 8),
				new ChancedOutput(new ItemStack(Items.dye, 1, 7), 1f),
				new ChancedOutput(new ItemStack(Items.dye, 1, 7), 0.4f)
		));
		/*
		Tall flowers
		 */
		//0 	Sunflower
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.double_plant, 1, 0),
				new ChancedOutput(new ItemStack(Items.dye, 2, 11), 1f),
				new ChancedOutput(new ItemStack(Items.dye, 1, 11), 0.4f)
		));
		//1 	Lilac
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.double_plant, 1, 1),
				new ChancedOutput(new ItemStack(Items.dye, 2, 13), 1f),
				new ChancedOutput(new ItemStack(Items.dye, 1, 13), 0.4f)
		));
		//2 	Double Tallgrass
		//3 	Large Fern
		//4 	Rose Bush
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.double_plant, 1, 4),
				new ChancedOutput(new ItemStack(Items.dye, 2, 1), 1f),
				new ChancedOutput(new ItemStack(Items.dye, 1, 1), 0.4f)
		));
		//5 	Peony
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new GrinderRecipe(new ItemStack(Blocks.double_plant, 1, 5),
				new ChancedOutput(new ItemStack(Items.dye, 2, 9), 1f),
				new ChancedOutput(new ItemStack(Items.dye, 1, 9), 0.4f)
		));
	}
}
