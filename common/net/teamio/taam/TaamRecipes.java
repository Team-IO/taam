package net.teamio.taam;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.teamio.taam.Taam.BLOCK_ORE_META;

public class TaamRecipes {

	public static void registerSmeltingRecipes() {
		BLOCK_ORE_META[] values = Taam.BLOCK_ORE_META.values();
		// Ores -> Ingots
		for (int meta = 0; meta < values.length; meta++) {
			if (!values[meta].ingot || !values[meta].ore) {
				continue;
			}
			GameRegistry.addSmelting(new ItemStack(TaamMain.blockOre, 1, meta), new ItemStack(TaamMain.itemIngot, 1, meta), 1);
		}
		// Dusts -> Ingots
		for (int meta = 0; meta < values.length; meta++) {
			if (!values[meta].ingot || !values[meta].dust) {
				continue;
			}
			GameRegistry.addSmelting(new ItemStack(TaamMain.itemDust, 1, meta), new ItemStack(TaamMain.itemIngot, 1, meta), 1);
		}
		// Bauxite (+Dust) -> Aluminum Ingot
		GameRegistry.addSmelting(new ItemStack(TaamMain.blockOre, 1, Taam.BLOCK_ORE_META.bauxite.ordinal()), new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.aluminum_nugget.ordinal()), 1);
		GameRegistry.addSmelting(new ItemStack(TaamMain.itemDust, 1, Taam.BLOCK_ORE_META.bauxite.ordinal()), new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.aluminum_nugget.ordinal()), 1);
		// Resin -> Rubber Bar
		GameRegistry.addSmelting(new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.resin.ordinal()), new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.rubber_bar.ordinal()), 1);
		//Iron Dust -> Iron Ingot
		GameRegistry.addSmelting(new ItemStack(TaamMain.itemDust, 1, Taam.BLOCK_ORE_META.iron.ordinal()), new ItemStack(Items.IRON_INGOT), 1);
		//Gold Dust -> Gold Ingot
		GameRegistry.addSmelting(new ItemStack(TaamMain.itemDust, 1, Taam.BLOCK_ORE_META.gold.ordinal()), new ItemStack(Items.GOLD_INGOT), 1);
		//Rubber Bar -> Plastic sheets
		GameRegistry.addSmelting(new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.rubber_bar.ordinal()), new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.plastic_sheet.ordinal()), 1);
	}

	public static void registerCraftingRecipes() {
		/*
		 * Input Values for better overview
		 */
		ItemStack materialWoodenBoard = new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.wooden_board.ordinal());
		ItemStack materialAluminumPlate = new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.aluminum_plate.ordinal());

		ItemStack partSieve = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.sieve.ordinal());
		ItemStack partRubberBand = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.rubber_band.ordinal());
		ItemStack partWoodenBand = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.wooden_band.ordinal());
		ItemStack partWoodenSupportFrame = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.support_frame_wood.ordinal());
		ItemStack partMetalBearing = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.metal_bearing.ordinal());
		ItemStack partSupportFrame = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.support_frame.ordinal());
		ItemStack partIronFrame = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.iron_frame.ordinal());
		ItemStack partPump = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.pump.ordinal());
		ItemStack partTank = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.tank.ordinal());
		ItemStack partNozzle = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.nozzle.ordinal());
		ItemStack partCopperWire = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.copper_wire.ordinal());
		ItemStack partMagneticCoil = new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.magnetic_coil.ordinal());

		ItemStack blockConveyor2 = new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.conveyor2.ordinal());
		ItemStack blockTrashcan = new ItemStack(TaamMain.blockProductionLineAttachable, 1, Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.trashcan.ordinal());
		ItemStack blockChute = new ItemStack(TaamMain.blockMachines, 1, Taam.BLOCK_MACHINES_META.chute.ordinal());
		ItemStack blockPipe = new ItemStack(TaamMain.itemMachine, 1, Taam.MACHINE_META.pipe.ordinal());
		ItemStack blockMotionSensor = new ItemStack(TaamMain.blockSensor, 1, 0);

		ItemStack blockConcreteCoatedChiseled = new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.coated_chiseled.ordinal());
		ItemStack blockConcreteFine = new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.fine.ordinal());
		ItemStack blockConcreteRough = new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.rough.ordinal());
		ItemStack blockConcreteCoated = new ItemStack(TaamMain.blockConcrete, 4, Taam.BLOCK_CONCRETE_META.coated.ordinal());
		ItemStack blockConcreteBlack = new ItemStack(TaamMain.blockConcrete, 4, Taam.BLOCK_CONCRETE_META.black.ordinal());

		ItemStack toolSawAnyDamage = new ItemStack(TaamMain.itemSaw, 1, OreDictionary.WILDCARD_VALUE);

		/*
		 * Conveyor Stuff (Production Line)
		 */

		//		conveyor1 (Wood)
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.blockProductionLine, 8, Taam.BLOCK_PRODUCTIONLINE_META.conveyor1.ordinal()),
				"rBP", "wSw", "WsW",
				'P', Blocks.PISTON,
				'B', partWoodenBand,
				'w', materialWoodenBoard,
				'S', partWoodenSupportFrame,
				'W', "plankWood",
				's', "stickWood",
				'r', "dustRedstone"
		).setRegistryName(Taam.MOD_ID, "conveyor_wood"));
		//		conveyor2 (Aluminum)
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.blockProductionLine, 8, Taam.BLOCK_PRODUCTIONLINE_META.conveyor2.ordinal()),
				"MRM", "CSC", "ABA",
				'M', "partMotor",
				'R', partRubberBand,
				'C', partMetalBearing,
				'S', partSupportFrame,
				'A', "ingotAluminum",
				'B', "materialPlastic"
		).setRegistryName(Taam.MOD_ID, "conveyor_alu"));
		//		conveyor3 (High Throughput)
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.conveyor3.ordinal()),
				"YBY", "F F", "MCM",
				'M', "partMotor",
				'C', blockConveyor2,
				'F', partIronFrame,
				'Y', "dyeYellow",
				'B', "dyeBlack"
		).setRegistryName(Taam.MOD_ID, "conveyor_hs"));

		//		conveyor hopper
		ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(null, new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.hopper.ordinal()),
				Blocks.HOPPER,
				partSupportFrame
		).setRegistryName(Taam.MOD_ID, "conveyor_hopper"));
		//		conveyor chute
		ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(null, new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.chute.ordinal()),
				blockChute,
				partSupportFrame
		).setRegistryName(Taam.MOD_ID, "conveyor_chute"));

		//		High Speed Conveyor Hopper
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.hopper_hs.ordinal()),
				"C C", " H ", " I ",
				'C', blockConveyor2,
				'H', Blocks.HOPPER,
				'I', partSupportFrame
		).setRegistryName(Taam.MOD_ID, "conveyor_hopper_hs"));
		//		Shredder
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.shredder.ordinal()),
				"P P", "MTB", "bSb",
				'P', materialAluminumPlate,
				'M', "partMotor",
				'T', blockTrashcan,
				'B', partMetalBearing,
				'b', "partBasicCircuit",
				'S', partSupportFrame
		).setRegistryName(Taam.MOD_ID, "conveyor_shredder"));
		//		Grinder
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.grinder.ordinal()),
				"PsP", "MCB", "bSb",
				'P', materialAluminumPlate,
				's', partSieve,
				'M', "partMotor",
				'C', blockChute,
				'B', partMetalBearing,
				'b', "partBasicCircuit",
				'S', partSupportFrame
		).setRegistryName(Taam.MOD_ID, "conveyor_grinder"));
		//		Crusher
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.crusher.ordinal()),
				"P P", "MCB", "bSb",
				'P', materialAluminumPlate,
				'M', "partMotor",
				'C', blockChute,
				'B', partMetalBearing,
				'b', "partBasicCircuit",
				'S', partSupportFrame
		).setRegistryName(Taam.MOD_ID, "conveyor_crusher"));
		//		Sieve
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.sieve.ordinal()),
				"AsA", "ACA", "MSM",
				'A', "ingotAluminum",
				'C', blockChute,
				'M', "partMotor",
				'S', partSupportFrame,
				's', partSieve
		).setRegistryName(Taam.MOD_ID, "conveyor_sieve"));
		//		Elevator
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.elevator.ordinal()),
				"MFM", "PFP", "MSM",
				'P', materialAluminumPlate,
				'M', "partMotor",
				'F', partIronFrame,
				'S', partSupportFrame
		).setRegistryName(Taam.MOD_ID, "conveyor_elevator"));

		/*
		 * Conveyor Stuff (Production Line Attachables)
		 */

		//		Conveyor Item Bag
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.blockProductionLineAttachable, 1, Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.itembag.ordinal()),
				"PLP", "PIP", "PPP",
				'P', "materialPlastic",
				'L', "dyeBlue",
				'I', partIronFrame
		).setRegistryName(Taam.MOD_ID, "conveyor_item_bag"));
		//		Conveyor Trash Bag
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.blockProductionLineAttachable, 1, Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.trashcan.ordinal()),
				"PLP", "PIP", "PPP",
				'P', "materialPlastic",
				'L', "dyeGreen",
				'I', partIronFrame
		).setRegistryName(Taam.MOD_ID, "conveyor_trash_bag"));

		/*
		 * Conveyor Stuff (Appliances)
		 */

		//		sprayer
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "sprayer"), new ItemStack(TaamMain.blockProductionLineAppliance, 1, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.sprayer.ordinal()),
				"RNN", "TCP", "aaa",
				'N', partNozzle,
				'C', "partBasicCircuit",
				'a', materialAluminumPlate,
				'P', partPump,
				'R', blockPipe,
				'T', partTank
		).setRegistryName(Taam.MOD_ID, "sprayer_1"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "sprayer"), new ItemStack(TaamMain.blockProductionLineAppliance, 1, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.sprayer.ordinal()),
				"NNR", "PCT", "aaa",
				'N', partNozzle,
				'C', "partBasicCircuit",
				'a', materialAluminumPlate,
				'P', partPump,
				'R', blockPipe,
				'T', partTank
		).setRegistryName(Taam.MOD_ID, "sprayer_2"));

		//      aligner
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.blockProductionLineAppliance, 1, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.aligner.ordinal()),
				"PSP", "AMM", "PP ",
				'S', blockMotionSensor,
				'A', "partAdvancedCircuit",
				'P', materialAluminumPlate,
				'M', "partMotor"
		).setRegistryName(Taam.MOD_ID, "aligner"));

		/*
		 * Fluid Machines
		 */
		//      pipe
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "pipe"), new ItemStack(TaamMain.itemMachine, 4, Taam.MACHINE_META.pipe.ordinal()),
				"III", "nnn", "III",
				'I', "ingotIron",
				'n', "nuggetIron"
		).setRegistryName(Taam.MOD_ID, "pipe_1"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "pipe"), new ItemStack(TaamMain.itemMachine, 4, Taam.MACHINE_META.pipe.ordinal()),
				"InI", "InI", "InI",
				'I', "ingotIron",
				'n', "nuggetIron"
		).setRegistryName(Taam.MOD_ID, "pipe_2"));
		//      tank
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.itemMachine, 1, Taam.MACHINE_META.tank.ordinal()),
				"GGG", "G G", "SSS",
				'G', "paneGlass",
				'S', blockConcreteCoatedChiseled
		).setRegistryName(Taam.MOD_ID, "tank"));
		//      pump
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "pump"), new ItemStack(TaamMain.itemMachine, 1, Taam.MACHINE_META.pump.ordinal()),
				"AAA", "PTR", "SSS",
				'A', "ingotAluminum",
				'P', partPump,
				'T', partTank,
				'R', blockPipe,
				'S', blockConcreteCoatedChiseled
		).setRegistryName(Taam.MOD_ID, "pump_1"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "pump"), new ItemStack(TaamMain.itemMachine, 1, Taam.MACHINE_META.pump.ordinal()),
				"AAA", "RTP", "SSS",
				'A', "ingotAluminum",
				'P', partPump,
				'T', partTank,
				'R', blockPipe,
				'S', blockConcreteCoatedChiseled
		).setRegistryName(Taam.MOD_ID, "pump_2"));
		//      mixer
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.itemMachine, 1, Taam.MACHINE_META.mixer.ordinal()),
				" M ", "RCR", "SSS",
				'C', blockChute,
				'M', "partMotor",
				'R', blockPipe,
				'S', blockConcreteCoatedChiseled
		).setRegistryName(Taam.MOD_ID, "mixer"));
		//      fluid drier
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.itemMachine, 1, Taam.MACHINE_META.fluid_drier.ordinal()),
				" R ", "sMs", " C ",
				'C', blockChute,
				'M', "partMotor",
				's', partSieve,
				'R', blockPipe
		).setRegistryName(Taam.MOD_ID, "fluid_drier"));

		/*
		 * Other Machines
		 */

		//		chute
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.blockMachines, 1, Taam.BLOCK_MACHINES_META.chute.ordinal()),
				"I I", "I I", " I ",
				'I', "ingotIron"
		).setRegistryName(Taam.MOD_ID, "chute"));

		//		motion sensor
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.blockSensor, 1, 0),
				"PGP", "PpP", "IRI",
				'P', "materialPlastic",
				'G', "blockGlass",
				'p', "partPhotocell",
				'I', "ingotIron",
				'R', "dustRedstone"
		).setRegistryName(Taam.MOD_ID, "motion_sensor"));

		//      industial lamp
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.blockLamp, 2, 0),
				"GPG", "grg",
				'P', "materialPlastic",
				'G', "paneGlass",
				'g', "dustGlowstone",
				'r', "dustRedstone"
		).setRegistryName(Taam.MOD_ID, "lamp"));
		// Inverted
		ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(null, new ItemStack(TaamMain.blockLampInverted, 1, 0),
				TaamMain.blockLamp, Blocks.REDSTONE_TORCH
		).setRegistryName(Taam.MOD_ID, "lamp_inverted"));

		/*
		 * Blocks
		 */
		//      support bream
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.blockSupportBeam, 16, 0),
				"FFF", "F F", "FFF",
				'F', partIronFrame
		).setRegistryName(Taam.MOD_ID, "support_beam"));

		/*
		 * Tools
		 */

		//		wrench
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "wrench"), new ItemStack(TaamMain.itemWrench, 1, 0),
				" I ", "II ", "  I",
				'I', "ingotIron"
		).setRegistryName(Taam.MOD_ID, "wrench"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "wrench"), new ItemStack(TaamMain.itemWrench, 1, 0),
				" I ", " II", "I  ",
				'I', "ingotIron"
		).setRegistryName(Taam.MOD_ID, "wrench"));
		//		saw
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "saw"), new ItemStack(TaamMain.itemSaw, 1, 0),
				"IIS",
				'I', "ingotIron",
				'S', "stickWood"
		).setRegistryName(Taam.MOD_ID, "saw"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "saw"), new ItemStack(TaamMain.itemSaw, 1, 0),
				"SII",
				'I', "ingotIron",
				'S', "stickWood"
		).setRegistryName(Taam.MOD_ID, "saw"));

		/*
		 * Parts
		 */
		//		photo cell
		ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(null, new ItemStack(TaamMain.itemPart, 9, Taam.ITEM_PART_META.photocell.ordinal()),
				Blocks.DAYLIGHT_DETECTOR,
				toolSawAnyDamage
		).setRegistryName(Taam.MOD_ID, "photo_cell"));
		//		magnetic coil
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.itemPart, 4, Taam.ITEM_PART_META.magnetic_coil.ordinal()),
				"CCC", "CIC", "CCC",
				'C', partCopperWire,
				'I', "ingotIron"
		).setRegistryName(Taam.MOD_ID, "magnetic_coil"));
		//		metal bearing
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "metal_bearing"), new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.metal_bearing.ordinal()),
				"INI", "N N", "INI",
				'I', "ingotIron",
				'N', "nuggetIron"
		).setRegistryName(Taam.MOD_ID, "metal_bearing_1"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "metal_bearing"), new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.metal_bearing.ordinal()),
				"NIN", "I I", "NIN",
				'I', "ingotIron",
				'N', "nuggetIron"
		).setRegistryName(Taam.MOD_ID, "metal_bearing_2"));
		//		support frame
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.itemPart, 6, Taam.ITEM_PART_META.support_frame.ordinal()),
				" A ", "A A", "AAA",
				'A', "ingotAluminum"
		).setRegistryName(Taam.MOD_ID, "support_frame"));
		//		support frame wood
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.support_frame_wood.ordinal()),
				" S ", "S S", "WWW",
				'W', "plankWood",
				'S', "stickWood"
		).setRegistryName(Taam.MOD_ID, "support_frame_wood"));
		//		copper wire
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.itemPart, 12, Taam.ITEM_PART_META.copper_wire.ordinal()),
				"CCC",
				'C', "ingotCopper"
		).setRegistryName(Taam.MOD_ID, "copper_wire"));

		//		basic circuit
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "circuit_basic"), new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.circuit_basic.ordinal()),
				"CCC", "RGR", "CCC",
				'C', "ingotCopper",
				'G', "ingotGold",
				'R', "dustRedstone"
		).setRegistryName(Taam.MOD_ID, "circuit_basic_1"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "circuit_basic"), new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.circuit_basic.ordinal()),
				"CRC", "CGC", "CRC",
				'C', "ingotCopper",
				'G', "ingotGold",
				'R', "dustRedstone"
		).setRegistryName(Taam.MOD_ID, "circuit_basic_2"));
		//		advanced circuit
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "circuit_advanced"), new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.circuit_advanced.ordinal()),
				"RGR", "GCG", "RGR",
				'R', "dustRedstone",
				'G', "ingotGold",
				'C', "partBasicCircuit"
		).setRegistryName(Taam.MOD_ID, "circuit_advanced_1"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "circuit_advanced"), new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.circuit_advanced.ordinal()),
				"GRG", "RCR", "GRG",
				'R', "dustRedstone",
				'G', "ingotGold",
				'C', "partBasicCircuit"
		).setRegistryName(Taam.MOD_ID, "circuit_advanced_2"));
		//		logistics chip
		/*ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null,new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.logistics_chip.ordinal()),
				"PKP", "HCH", "PVP",
				'P', "materialPlastic",
				'K', Blocks.chest,
				'H', Blocks.hopper,
				'V', Items.comparator,
				'C', "partAdvancedCircuit"
		));*/
		//		iron frame
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.iron_frame.ordinal()),
				" I ", "I I", "I I",
				'I', "ingotIron"
		).setRegistryName(Taam.MOD_ID, "iron_frame"));

		//		rubber band
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.rubber_band.ordinal()),
				"RRR", "R R", "RRR",
				'R', "materialRubber"
		).setRegistryName(Taam.MOD_ID, "rubber_band"));
		//		wooden band - with planks
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "wooden_band"), new ItemStack(TaamMain.itemPart, 4, Taam.ITEM_PART_META.wooden_band.ordinal()),
				"WsW", "WSW",
				's', "stickWood",
				'S', "string",
				'W', "plankWood"
		).setRegistryName(Taam.MOD_ID, "wooden_band_1"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "wooden_band"), new ItemStack(TaamMain.itemPart, 4, Taam.ITEM_PART_META.wooden_band.ordinal()),
				"WSW", "WsW",
				's', "stickWood",
				'S', "string",
				'W', "plankWood"
		).setRegistryName(Taam.MOD_ID, "wooden_band_2"));
		//		wooden band - with slabs
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "wooden_band"), new ItemStack(TaamMain.itemPart, 2, Taam.ITEM_PART_META.wooden_band.ordinal()),
				"WsW", "WSW",
				's', "stickWood",
				'S', "string",
				'W', "slabWood"
		).setRegistryName(Taam.MOD_ID, "wooden_band_3"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "wooden_band"), new ItemStack(TaamMain.itemPart, 2, Taam.ITEM_PART_META.wooden_band.ordinal()),
				"WSW", "WsW",
				's', "stickWood",
				'S', "string",
				'W', "slabWood"
		).setRegistryName(Taam.MOD_ID, "wooden_band_4"));
		//		tank
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.tank.ordinal()),
				" I ", "I I", "III",
				'I', "ingotIron"
		).setRegistryName(Taam.MOD_ID, "tank_part"));
		//		nozzle
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.nozzle.ordinal()),
				" I ", "I I", " I ",
				'I', "nuggetIron"
		).setRegistryName(Taam.MOD_ID, "nozzle"));

		//		Pump
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.pump.ordinal()),
				"AAA", "PMP", "AAA",
				'M', "partMotor",
				'P', Blocks.PISTON,
				'A', "ingotAluminum"
		).setRegistryName(Taam.MOD_ID, "pump_part"));
		//		Motor
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.motor.ordinal()),
				"ACA", "CIC", "ACA",
				'C', partMagneticCoil,
				'I', "ingotIron",
				'A', "ingotAluminum"
		).setRegistryName(Taam.MOD_ID, "motor_part"));
		//		Sieve
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.sieve.ordinal()),
				"IBI", "BBB", "IBI",
				'B', Blocks.IRON_BARS,
				'I', "ingotIron"
		).setRegistryName(Taam.MOD_ID, "sieve_part"));
		//		Redirector
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.itemPart, 4, Taam.ITEM_PART_META.redirector.ordinal()),
				"P", "I", "I",
				'P', materialAluminumPlate,
				'I', "ingotAluminum"
		).setRegistryName(Taam.MOD_ID, "redirector_part"));

		/*
		 * Materials
		 */

		//		copper nugget
		ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(null, new ItemStack(TaamMain.itemMaterial, 9, Taam.ITEM_MATERIAL_META.copper_nugget.ordinal()), "ingotCopper")
				.setRegistryName(Taam.MOD_ID, "copper_ingot_nugget"));
		String nugget = "nuggetCopper";
		ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(null, new ItemStack(TaamMain.itemIngot, 1, Taam.BLOCK_ORE_META.copper.ordinal()), nugget, nugget, nugget, nugget, nugget, nugget, nugget, nugget, nugget)
				.setRegistryName(Taam.MOD_ID, "copper_nugget_ingot"));

		//		tin nugget
		ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(null, new ItemStack(TaamMain.itemMaterial, 9, Taam.ITEM_MATERIAL_META.tin_nugget.ordinal()), "ingotTin")
				.setRegistryName(Taam.MOD_ID, "tin_ingot_nugget"));
		nugget = "nuggetTin";
		ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(null, new ItemStack(TaamMain.itemIngot, 1, Taam.BLOCK_ORE_META.tin.ordinal()), nugget, nugget, nugget, nugget, nugget, nugget, nugget, nugget, nugget)
				.setRegistryName(Taam.MOD_ID, "tin_nugget_ingot"));

		//		aluminum nugget
		ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(null, new ItemStack(TaamMain.itemMaterial, 9, Taam.ITEM_MATERIAL_META.aluminum_nugget.ordinal()), "ingotAluminum")
				.setRegistryName(Taam.MOD_ID, "aluminum_ingot_nugget"));
		nugget = "nuggetAluminum";
		ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(null, new ItemStack(TaamMain.itemIngot, 1, Taam.BLOCK_ORE_META.aluminum.ordinal()), nugget, nugget, nugget, nugget, nugget, nugget, nugget, nugget, nugget)
				.setRegistryName(Taam.MOD_ID, "aluminum_nugget_ingot"));

		//		wooden board
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "wooden_board"), new ItemStack(TaamMain.itemMaterial, 6, Taam.ITEM_MATERIAL_META.wooden_board.ordinal()),
				"www", "s s",
				'w', "plankWood",
				's', "stickWood"
		).setRegistryName(Taam.MOD_ID, "wooden_board_1"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "wooden_board"), new ItemStack(TaamMain.itemMaterial, 6, Taam.ITEM_MATERIAL_META.wooden_board.ordinal()),
				"s s", "www",
				'w', "plankWood",
				's', "stickWood"
		).setRegistryName(Taam.MOD_ID, "wooden_board_2"));

		//		aluminum plate
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "aluminum_plate"), new ItemStack(TaamMain.itemMaterial, 6, Taam.ITEM_MATERIAL_META.aluminum_plate.ordinal()),
				" a ", "aaa",
				'a', "ingotAluminum"
		).setRegistryName(Taam.MOD_ID, "aluminum_plate_1"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(Taam.MOD_ID, "aluminum_plate"), new ItemStack(TaamMain.itemMaterial, 6, Taam.ITEM_MATERIAL_META.aluminum_plate.ordinal()),
				"aaa", " a ",
				'a', "ingotAluminum"
		).setRegistryName(Taam.MOD_ID, "aluminum_plate_2"));

		//      cement
		ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(null, new ItemStack(TaamMain.itemMaterial, 4, Taam.ITEM_MATERIAL_META.cement.ordinal()),
				Items.CLAY_BALL, "dustStone", "dustStone", "dustStone"
		).setRegistryName(Taam.MOD_ID, "cement"));
		//      rough cement
		ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(null, new ItemStack(TaamMain.itemMaterial, 2, Taam.ITEM_MATERIAL_META.cementRough.ordinal()),
				"materialCement", "gravel"
		).setRegistryName(Taam.MOD_ID, "cement_rough"));

		//      concrete
		ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(null, new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.fine.ordinal()),
				"materialCement", "materialCement", "materialCement", "materialCement", Items.WATER_BUCKET
		).setRegistryName(Taam.MOD_ID, "concrete"));
		//      rough concrete
		ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(null, new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.rough.ordinal()),
				"materialRoughCement", "materialRoughCement", "materialRoughCement", "materialRoughCement", Items.WATER_BUCKET
		).setRegistryName(Taam.MOD_ID, "concrete_rough"));
		//      coated concrete
		ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(null, new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.coated.ordinal()),
				blockConcreteFine, "materialResin", "materialResin", "materialResin", "materialResin", "materialResin", Items.WATER_BUCKET
		).setRegistryName(Taam.MOD_ID, "concrete_coated"));

		//      chiseled concrete
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.blockConcrete, 4, Taam.BLOCK_CONCRETE_META.fine_chiseled.ordinal()),
				"CC", "CC",
				'C', blockConcreteFine
		).setRegistryName(Taam.MOD_ID, "concrete_chiseled"));
		//      rough chiseled concrete
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.blockConcrete, 4, Taam.BLOCK_CONCRETE_META.rough_chiseled.ordinal()),
				"CC", "CC",
				'C', blockConcreteRough
		).setRegistryName(Taam.MOD_ID, "concrete_chiseled_rough"));
		//      coated chiseled concrete
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.blockConcrete, 4, Taam.BLOCK_CONCRETE_META.coated_chiseled.ordinal()),
				"CC", "CC",
				'C', blockConcreteCoated
		).setRegistryName(Taam.MOD_ID, "concrete_coated_chiseled"));
		//      black chiseled concrete
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, new ItemStack(TaamMain.blockConcrete, 4, Taam.BLOCK_CONCRETE_META.black_chiseled.ordinal()),
				"CC", "CC",
				'C', blockConcreteBlack
		).setRegistryName(Taam.MOD_ID, "concrete_coated_black"));

		// Fallback materials & error correction stuff
		ItemStack cheatyIronIngot = new ItemStack(TaamMain.itemIngot, 1, Taam.BLOCK_ORE_META.iron.ordinal());
		GameRegistry.addShapelessRecipe(new ResourceLocation(Taam.MOD_ID, "cheaty_iron_ingot"), null, new ItemStack(Items.IRON_INGOT), Ingredient.fromStacks(cheatyIronIngot));

		ItemStack cheatyGoldIngot = new ItemStack(TaamMain.itemIngot, 1, Taam.BLOCK_ORE_META.gold.ordinal());
		GameRegistry.addShapelessRecipe(new ResourceLocation(Taam.MOD_ID, "cheaty_gold_ingot"), null, new ItemStack(Items.GOLD_INGOT), Ingredient.fromStacks(cheatyGoldIngot));
	}

}
