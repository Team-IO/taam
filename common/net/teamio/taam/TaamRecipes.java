package net.teamio.taam;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.teamio.taam.conveyors.api.ChanceBasedRecipe;
import net.teamio.taam.conveyors.api.ChancedOutput;
import net.teamio.taam.conveyors.api.ProcessingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class TaamRecipes {

	public static void addRecipes(){
		
		/*
		 * Crusher
		 */
		
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.stone),
						new ChancedOutput(new ItemStack(Blocks.cobblestone), 1.0f),
						new ChancedOutput(new ItemStack(Blocks.gravel), 0.15f)
				)
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.cobblestone),
						new ChancedOutput(new ItemStack(Blocks.gravel), 1.0f),
						new ChancedOutput(new ItemStack(Blocks.sand), 0.15f)
				)
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.gravel),
						new ChancedOutput(new ItemStack(Blocks.sand), 1.0f),
						new ChancedOutput(new ItemStack(Blocks.sand), 0.05f)
				)
		);
		
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.gold_block),
						new ChancedOutput(new ItemStack(Items.gold_ingot, 9), 1.0f)
				)
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.iron_block),
						new ChancedOutput(new ItemStack(Items.iron_ingot, 9), 1.0f)
				)
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.diamond_block),
						new ChancedOutput(new ItemStack(Items.diamond, 9), 1.0f)
				)
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.emerald_block),
						new ChancedOutput(new ItemStack(Items.emerald, 9), 1.0f)
				)
		);
		
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.quartz_block),
						new ChancedOutput(new ItemStack(Items.quartz, 3), 1.0f),
						new ChancedOutput(new ItemStack(Items.quartz, 1), 0.25f)
				)
		);
		
		/*
		 * Grinder
		 */

		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER,
				new ChanceBasedRecipe(new ItemStack(Blocks.grass),
						new ChancedOutput(new ItemStack(Blocks.dirt), 1.0f),
						new ChancedOutput(new ItemStack(Items.wheat_seeds), 0.05f),
						new ChancedOutput(new ItemStack(Items.pumpkin_seeds), 0.05f),
						new ChancedOutput(new ItemStack(Items.melon_seeds), 0.05f),
						new ChancedOutput(new ItemStack(Blocks.vine), 0.005f)
				)
		);
		
	}
	public static void addSmeltingRecipes(){
		//TODO: Remove Bauxite and Kaolinit ore furnace recipes
		for(int meta = 0; meta < Taam.BLOCK_ORE_META.length; meta++) {
			if(Taam.isOreOnly(meta)) {
				continue;
			}
			GameRegistry.addSmelting(new ItemStack(TaamMain.blockOre, 1, meta), new ItemStack(TaamMain.itemIngot, 1, meta), 1);
		}
	}

	public static void addOreRecipes(){
		
//		motion sensor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockSensor, 1, 0),
				"PGP", "PpP", "IRI",
				'P', "materialPlastic",
				'G', "blockGlass",
				'p', "partPhotocell",
				'I', "ingotIron",
				'R', Items.redstone));
		
//		logistics chip
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, 5),
				"PKP", "HCH", "PVP",
				'P', "materialPlastic",
				'K', Blocks.chest,
				'H', Blocks.hopper,
				'V', Items.comparator,
				'C', new ItemStack(TaamMain.itemPart, 1, 4)));
//		basic circuit
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, 3),
				"CCC", "RGR", "CCC",
				'C', "ingotCopper",
				'G', Items.gold_ingot,
				'R', Items.redstone));
//		advanced circuit
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, 4),
				"RGR", "GCG", "RGR",
				'R', Items.redstone,
				'G', Items.gold_ingot,
				'C', new ItemStack(TaamMain.itemPart, 1, 3)));
//		iron frame
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, 11),
				"III", "I I", "I I",
				'I', "ingotIron"));
		
//		rubber band
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, 6),
				"RRR", "R R", "RRR",
				'R', "materialRubber"));
//		tank
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, 8),
				" I ", "I I", "III",
				'I', "ingotIron"));
//		nozzle
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1 ,9),
				" I ", "I I", " I ",
				'I', "ingotIron"));
//		sprayer
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemConveyorAppliance, 1, 0),
				"NFN", "N N", "TCT",
				'N', new ItemStack(TaamMain.itemPart, 1, 9),
				'C', "partBasicCircuit",
				'T', new ItemStack(TaamMain.itemPart, 1 ,8),
				'F', new ItemStack(TaamMain.itemPart, 1, 11)));
//		photo cell
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.itemPart, 9, 0),
				Blocks.daylight_detector));

//		wrench
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemWrench, 1, 0), 
				"*I*", "II*", "**I",
				'I', "ingotIron"));
//		magnetic coil
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, 10),
				"CCC", "CIC", "CCC",
				'C', "ingotCopper",
				'I', "ingotIron"));
//		iron nugget
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.itemPart, 9, 14),
				"ingotIron"));
//		metal bearing
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, 12), 
				"INI", "N*N", "INI",
				'I', "ingotIron",
				'N', "nuggetIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, 12), 
				"NIN", "I*I", "NIN",
				'I', "ingotIron",
				'N', "nuggetIron"));
//		support frame
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1 ,2),
				"*A*", "A*A", "AAA",
				'A', "ingotAluminum"));
//		copper wire
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 12, 13), 
				"CCC",
				'C', "ingotCopper"));
//		conveyor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, 0),
				"MRM", "CSC", "ABA",
				'M', new ItemStack(TaamMain.itemPart, 1, 1),
				'R', new ItemStack(TaamMain.itemPart, 1, 6),
				'C', new ItemStack(TaamMain.itemPart, 1, 13),
				'S', new ItemStack(TaamMain.itemPart, 1, 2),
				'A', "ingotAluminum",
				'B', new ItemStack(TaamMain.itemPart, 1, 3)));
//			conveyor hopper
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, 1),
				Blocks.hopper, new ItemStack(TaamMain.itemPart, 1, 2)));
	
	}
	
	
	
	

}
