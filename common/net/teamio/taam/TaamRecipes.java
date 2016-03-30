package net.teamio.taam;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.teamio.taam.Taam.BLOCK_ORE_META;
import net.teamio.taam.recipes.ChanceBasedRecipe;
import net.teamio.taam.recipes.ChancedOutput;
import net.teamio.taam.recipes.MixerRecipe;
import net.teamio.taam.recipes.ProcessingRegistry;

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
						new ChanceBasedRecipe(new ItemStack(TaamMain.itemIngot, 1, meta),
							new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, meta), 1.0f)
							));
			}
		}
		
		String[] oreDic = {"oreCopper", "oreTin","oreAluminum","oreBauxite","oreKaolinte","oreGold","oreIron","oreCoal"};
		for (int ore = 0 ; ore < oreDic.length; ore++){
			ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
					new ChanceBasedRecipe(oreDic[ore],
						new ChancedOutput(new ItemStack(TaamMain.itemDust, 2, ore), 1.0f),
						new ChancedOutput(new ItemStack(Blocks.cobblestone), 0.4f),
						new ChancedOutput(new ItemStack(Blocks.mossy_cobblestone), 0.0001f)
					));
		}
		
		// Vanilla Ores
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.diamond_ore, 1),
					new ChancedOutput(new ItemStack(Items.diamond, 1), 1.0f),
					new ChancedOutput(new ItemStack(Items.diamond, 1), 0.01f),
					new ChancedOutput(new ItemStack(Blocks.cobblestone), 0.4f),
					new ChancedOutput(new ItemStack(Blocks.mossy_cobblestone), 0.0001f)
					));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.emerald_ore, 1),
					new ChancedOutput(new ItemStack(Items.emerald, 1), 1.0f),
					new ChancedOutput(new ItemStack(Items.emerald, 1), 0.01f),
					new ChancedOutput(new ItemStack(Blocks.cobblestone), 0.4f),
					new ChancedOutput(new ItemStack(Blocks.mossy_cobblestone), 0.0001f)
					));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.redstone_ore, 1),
					new ChancedOutput(new ItemStack(Items.redstone, 4), 1.0f),
					new ChancedOutput(new ItemStack(Items.redstone, 2), 0.05f),
					new ChancedOutput(new ItemStack(Items.redstone, 1), 0.01f),
					new ChancedOutput(new ItemStack(Blocks.cobblestone), 0.4f),
					new ChancedOutput(new ItemStack(Blocks.mossy_cobblestone), 0.0001f)
					));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.lapis_ore, 1),
					new ChancedOutput(new ItemStack(Items.dye, 4, 4), 1.0f),
					new ChancedOutput(new ItemStack(Items.dye, 2, 4), 0.05f),
					new ChancedOutput(new ItemStack(Items.dye, 1, 4), 0.01f),
					new ChancedOutput(new ItemStack(Blocks.cobblestone), 0.4f),
					new ChancedOutput(new ItemStack(Blocks.mossy_cobblestone), 0.0001f)
					));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.quartz_ore, 1),
					new ChancedOutput(new ItemStack(Items.quartz, 1), 1.0f),
					new ChancedOutput(new ItemStack(Items.quartz, 1), 0.01f),
					new ChancedOutput(new ItemStack(Blocks.netherrack), 0.4f),
					new ChancedOutput(new ItemStack(Blocks.soul_sand), 0.0001f)
					));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.coal_ore, 1),
					new ChancedOutput(new ItemStack(Items.coal, 1), 1.0f),
					new ChancedOutput(new ItemStack(Items.coal, 1), 0.01f),
					new ChancedOutput(new ItemStack(Blocks.cobblestone), 0.4f),
					new ChancedOutput(new ItemStack(Blocks.mossy_cobblestone), 0.0001f)
					));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.gold_ore, 1),
					new ChancedOutput(new ItemStack(TaamMain.itemDust, 2, Taam.BLOCK_ORE_META.gold.ordinal()), 1.0f),
					new ChancedOutput(new ItemStack(Blocks.cobblestone), 0.4f),
					new ChancedOutput(new ItemStack(Blocks.mossy_cobblestone), 0.0001f)
					));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.iron_ore, 1),
					new ChancedOutput(new ItemStack(TaamMain.itemDust, 2, Taam.BLOCK_ORE_META.iron.ordinal()), 1.0f),
					new ChancedOutput(new ItemStack(Blocks.cobblestone), 0.4f),
					new ChancedOutput(new ItemStack(Blocks.mossy_cobblestone), 0.0001f)
					));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Items.gold_ingot, 1),
					new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, Taam.BLOCK_ORE_META.gold.ordinal()), 1.0f)
					));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Items.iron_ingot, 1),
					new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, Taam.BLOCK_ORE_META.iron.ordinal()), 1.0f)
					));
		
		// Stone/Cobble/Gravel/etc
		
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
		
		// Ore/Dust Blocks
		
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
				new ChanceBasedRecipe(new ItemStack(Blocks.redstone_block),
						new ChancedOutput(new ItemStack(Items.redstone, 9), 1.0f)
				)
		);
		ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
				new ChanceBasedRecipe(new ItemStack(Blocks.lapis_block),
						new ChancedOutput(new ItemStack(Items.dye, 9, 4), 1.0f)
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
				));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new ChanceBasedRecipe("treeLeaves", 
					new ChancedOutput(new ItemStack(Items.stick), 1.0f),
					new ChancedOutput(new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.resin.ordinal()), 0.2f )
				));
		ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new ChanceBasedRecipe("treeSapling", 
				new ChancedOutput(new ItemStack(Items.stick), 0.5f),
				new ChancedOutput(new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.resin.ordinal()), 0.001f )
			));
		for (int col = 0; col < 16; col++) {
			ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new ChanceBasedRecipe(new ItemStack(Blocks.wool, 1, col), 
					new ChancedOutput(new ItemStack(Items.string, 3), 1f), 
					new ChancedOutput(new ItemStack(Items.string), 0.1f),
					new ChancedOutput(new ItemStack(Items.dye, 1, 15-col), 0.001f )
				));
			ProcessingRegistry.registerRecipe(ProcessingRegistry.GRINDER, new ChanceBasedRecipe(new ItemStack(Blocks.carpet, 1, col), 
					new ChancedOutput(new ItemStack(Items.string, 6), 1f), 
					new ChancedOutput(new ItemStack(Items.string, 2), 0.1f),
					new ChancedOutput(new ItemStack(Items.dye, 1, 15-col), 0.001f )
				));
		}
		
		/*
		 * Mixer
		 */
		ProcessingRegistry.registerRecipe(ProcessingRegistry.MIXER,
				new MixerRecipe(
						new ItemStack(Items.dye, 1, 0),
						new FluidStack(FluidRegistry.WATER, 10),
						new FluidStack(TaamMain.fluidsDye[0], 10))
				);
		
	}
	public static void addSmeltingRecipes(){
		BLOCK_ORE_META[] values = Taam.BLOCK_ORE_META.values();
		for(int meta = 0; meta < values.length; meta++) {
			if(!values[meta].ingot || !values[meta].ore) {
				continue;
			}
			GameRegistry.addSmelting(new ItemStack(TaamMain.blockOre, 1, meta), new ItemStack(TaamMain.itemIngot, 1, meta), 1);
			
		}
		for(int meta = 0; meta < values.length; meta++) {
			if(!values[meta].ingot || !values[meta].dust) {
				continue;
			}
			GameRegistry.addSmelting(new ItemStack(TaamMain.itemDust, 1, meta), new ItemStack(TaamMain.itemIngot, 1, meta), 1);
		}
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
		 * Conveyor Stuff (Production Line)
		 */
		
//		conveyor1 (Wood)
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.conveyor1.ordinal()),
				"rRP", "wSw", "WsW",
				'P', new ItemStack(Blocks.piston, 1, 0),
				'R', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.rubber_band.ordinal()),
				'w', new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.wooden_board.ordinal()),
				'S', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.support_frame_wood.ordinal()),
				'W', "plankWood",
				's', "stickWood",
				'r', "dustRedstone"
				));
//		conveyor2 (Aluminum)
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.conveyor2.ordinal()),
				"MRM", "CSC", "ABA",
				'M', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.motor.ordinal()),
				'R', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.rubber_band.ordinal()),
				'C', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.metal_bearing.ordinal()),
				'S', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.support_frame.ordinal()),
				'A', "ingotAluminum",
				'B', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_MATERIAL_META.plastic_sheet.ordinal())));
//		conveyor3 (High Througput)
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.conveyor3.ordinal()),
				"YBY", "F F", "MCM",
				'M', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.motor.ordinal()),
				'C', new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.conveyor2.ordinal()),
				'F', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.iron_frame.ordinal()),
				'Y', "dyeYellow",
				'B', "dyeBlack"
		));
//		conveyor hopper
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.hopper.ordinal()),
				Blocks.hopper, new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.support_frame.ordinal())));

//		High Speed Conveyor Hopper
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.hopper_hs.ordinal()),
				"C C"," H ", " I ",
				'C', new ItemStack(TaamMain.blockProductionLine,1,Taam.BLOCK_PRODUCTIONLINE_META.conveyor2.ordinal()),
				'H', Blocks.hopper,
				'I', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.support_frame.ordinal())
				));
//		Shredder
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.shredder.ordinal()),
				"PAP","MTB", "bSb",
				'P', new ItemStack(TaamMain.itemMaterial,1,Taam.ITEM_MATERIAL_META.aluminum_plate.ordinal()),
				'A', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.circuit_advanced.ordinal()),
				'M', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.motor.ordinal()),
				'T', new ItemStack(TaamMain.blockProductionLineAttachable,1,Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.trashcan.ordinal()),
				'B', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.metal_bearing.ordinal()),
				'b', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.circuit_basic.ordinal()),
				'S', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.support_frame.ordinal())
				));
//		Grinder
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.grinder.ordinal()),
				"WAW","MCB", "bSb",
				'P', new ItemStack(TaamMain.itemMaterial,1,Taam.ITEM_MATERIAL_META.wooden_board.ordinal()),
				'A', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.circuit_advanced.ordinal()),
				'M', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.motor.ordinal()),
				'C', new ItemStack(TaamMain.blockMachines,1,Taam.BLOCK_MACHINES_META.chute.ordinal()),
				'B', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.metal_bearing.ordinal()),
				'b', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.circuit_basic.ordinal()),
				'S', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.support_frame.ordinal())
				));
//		Crusher
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.crusher.ordinal()),
				"PAP","MCB", "bSb",
				'P', new ItemStack(TaamMain.itemMaterial,1,Taam.ITEM_MATERIAL_META.aluminum_plate.ordinal()),
				'A', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.circuit_advanced.ordinal()),
				'M', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.motor.ordinal()),
				'C', new ItemStack(TaamMain.blockMachines,1,Taam.BLOCK_MACHINES_META.chute.ordinal()),
				'B', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.metal_bearing.ordinal()),
				'b', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.circuit_basic.ordinal()),
				'S', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.support_frame.ordinal())
				));
//		Shieve
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.sieve.ordinal()),
				"AsA","ACA", "MSM",
				'A', "ingotAluminum",
				'S', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.sieve.ordinal()),
				'C', new ItemStack(TaamMain.blockMachines,1,Taam.BLOCK_MACHINES_META.chute.ordinal()),
				'M', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.motor.ordinal()),
				'S', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.support_frame.ordinal()),
				's', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.sieve.ordinal())
				));

		/*
		 * Conveyor Stuff (Production Line Attachables)
		 */
		
//		Conveyor Item Bag
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLineAttachable, 1, Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.itembag.ordinal()),
				"PLP","PIP","PPP",
				'P', "materialPlastic",
				'L', "dyeBlue",
				'I', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.iron_frame.ordinal())
				));
//		Conveyor Trash Bag
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLineAttachable, 1, Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.trashcan.ordinal()),
				"PLP","PIP","PPP",
				'P', "materialPlastic",
				'L', "dyeGreen",
				'I', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.iron_frame.ordinal())
				));
		
		/*
		 * Conveyor Stuff (Appliances)
		 */
		
//		sprayer
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemConveyorAppliance, 1, 0),
				"NFN", "N N", "TCP",
				'N', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.nozzle.ordinal()),
				'C', "partBasicCircuit",
				'P', new ItemStack(TaamMain.itemPart, 1 ,Taam.ITEM_PART_META.pump.ordinal()),
				'F', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.iron_frame.ordinal()),
				'T', new ItemStack(TaamMain.itemPart, 1 ,Taam.ITEM_PART_META.tank.ordinal())));
		
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
				'R', Items.redstone));

		/*
		 * Misc Items
		 */
		
//		wrench
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemWrench, 1, 0),
				"*I*", "II*", "**I",
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
				Blocks.daylight_detector, new ItemStack(TaamMain.itemSaw, 1, OreDictionary.WILDCARD_VALUE)));
//		magnetic coil
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.magnetic_coil.ordinal()),
				"CCC", "CIC", "CCC",
				'C', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.copper_wire.ordinal()),
				'I', "ingotIron"));
//		metal bearing
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.metal_bearing.ordinal()), 
				"INI", "N*N", "INI",
				'I', "ingotIron",
				'N', "nuggetIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.metal_bearing.ordinal()), 
				"NIN", "I*I", "NIN",
				'I', "ingotIron",
				'N', "nuggetIron"));
//		support frame
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1 ,Taam.ITEM_PART_META.support_frame.ordinal()),
				"*A*", "A*A", "AAA",
				'A', "ingotAluminum"));
//		support frame wood
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1 ,Taam.ITEM_PART_META.support_frame_wood.ordinal()),
				"*S*", "S*S", "WWW",
				'W', "plankWood",
				'S', "stickWood"));
//		copper wire
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 12, Taam.ITEM_PART_META.copper_wire.ordinal()), 
				"CCC",
				'C', "ingotCopper"));
		
//		basic circuit
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.circuit_basic.ordinal()),
				"CCC", "RGR", "CCC",
				'C', "ingotCopper",
				'G', Items.gold_ingot,
				'R', Items.redstone));
//		advanced circuit
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.circuit_advanced.ordinal()),
				"RGR", "GCG", "RGR",
				'R', Items.redstone,
				'G', Items.gold_ingot,
				'C', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.circuit_basic.ordinal())));
//		logistics chip
		/*GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.logistics_chip.ordinal()),
				"PKP", "HCH", "PVP",
				'P', "materialPlastic",
				'K', Blocks.chest,
				'H', Blocks.hopper,
				'V', Items.comparator,
				'C', new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.circuit_advanced.ordinal())));*/
//		iron frame
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.iron_frame.ordinal()),
				"III", "I I", "I I",
				'I', "ingotIron"));
		
//		rubber band
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.rubber_band.ordinal()),
				"RRR", "R R", "RRR",
				'R', "materialRubber"));
//		tank
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.tank.ordinal()),
				" I ", "I I", "III",
				'I', "ingotIron"));
//		nozzle
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1 ,Taam.ITEM_PART_META.nozzle.ordinal()),
				" I ", "I I", " I ",
				'I', "ingotIron"));
		
//		Pump
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.pump.ordinal()),
				"AAA","PMP", "AAA",
				'M', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.motor.ordinal()),
				'P', Blocks.piston,
				'A', "ingotAluminum"
				));
//		Motor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.motor.ordinal()),
				"ACA", "CIC", "ACA",
				'C', new ItemStack(TaamMain.itemPart,1,Taam.ITEM_PART_META.magnetic_coil.ordinal()),
				'I', Items.iron_ingot,
				'A', "ingotAluminum"
				));
//		Sieve
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.sieve.ordinal()),
				"IBI", "BBB", "IBI",
				'B', Blocks.iron_bars,
				'I', Items.iron_ingot
				));		
		
		/*
		 * Materials
		 */
		
//		iron nugget
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.itemMaterial, 9, Taam.ITEM_MATERIAL_META.iron_nugget.ordinal()),
				"ingotIron"));
//		wooden board
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMaterial, 6 ,Taam.ITEM_MATERIAL_META.wooden_board.ordinal()),
				"www", "s s",
				'w', "plankWood",
				's', "stickWood"));
//		aluminum plate
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMaterial, 6 ,Taam.ITEM_MATERIAL_META.aluminum_plate.ordinal()),
				"aaa", " a ",
				'a', "ingotAluminum"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMaterial, 6 ,Taam.ITEM_MATERIAL_META.aluminum_plate.ordinal()),
				"aaa", " a ",
				'a', "ingotAluminium"));
	}

}
