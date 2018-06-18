package net.teamio.taam;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.teamio.taam.Taam.BLOCK_ORE_META;

public class TaamRecipes {

	public static void registerSmeltingRecipes(){
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
		GameRegistry.addSmelting(new ItemStack(TaamMain.itemDust, 1, Taam.BLOCK_ORE_META.iron.ordinal()), new ItemStack(Items.IRON_INGOT), 1);
		//Gold Dust -> Gold Ingot
		GameRegistry.addSmelting(new ItemStack(TaamMain.itemDust, 1, Taam.BLOCK_ORE_META.gold.ordinal()), new ItemStack(Items.GOLD_INGOT), 1);
		//Rubber Bar -> Plastic sheets
		GameRegistry.addSmelting(new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.rubber_bar.ordinal()),new ItemStack(TaamMain.itemMaterial, 1, Taam.ITEM_MATERIAL_META.plastic_sheet.ordinal()) , 1);
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
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 8, Taam.BLOCK_PRODUCTIONLINE_META.conveyor1.ordinal()),
				"rBP", "wSw", "WsW",
				'P', Blocks.PISTON,
				'B', partWoodenBand,
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
				'B', "materialPlastic"
		));
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
				partSupportFrame
		));

		//		conveyor chute
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.chute.ordinal()),
				blockChute,
				partSupportFrame
		));

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
				'C', blockChute,
				'M', "partMotor",
				'S', partSupportFrame,
				's', partSieve
		));
		//		Elevator
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.elevator.ordinal()),
				"MFM","PFP", "MSM",
				'P', materialAluminumPlate,
				'M', "partMotor",
				'F', partIronFrame,
				'S', partSupportFrame
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
				'n', "nuggetIron"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMachine, 4, Taam.MACHINE_META.pipe.ordinal()),
				"InI", "InI", "InI",
				'I', "ingotIron",
				'n', "nuggetIron"
		));
		//      tank
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMachine, 1, Taam.MACHINE_META.tank.ordinal()),
				"GGG", "G G", "SSS",
				'G', "paneGlass",
				'S', blockConcreteCoatedChiseled
		));
		//      pump
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMachine, 1, Taam.MACHINE_META.pump.ordinal()),
				"AAA", "PTR", "SSS",
				'A', "ingotAluminum",
				'P', partPump,
				'T', partTank,
				'R', blockPipe,
				'S', blockConcreteCoatedChiseled
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMachine, 1, Taam.MACHINE_META.pump.ordinal()),
				"AAA", "RTP", "SSS",
				'A', "ingotAluminum",
				'P', partPump,
				'T', partTank,
				'R', blockPipe,
				'S', blockConcreteCoatedChiseled
		));
		//      mixer
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMachine, 1, Taam.MACHINE_META.mixer.ordinal()),
				" M ", "RCR", "SSS",
				'C', blockChute,
				'M', "partMotor",
				'R', blockPipe,
				'S', blockConcreteCoatedChiseled
		));
		//      fluid drier
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMachine, 1, Taam.MACHINE_META.fluid_drier.ordinal()),
				" R ", "sMs", " C ",
				'C', blockChute,
				'M', "partMotor",
				's', partSieve,
				'R', blockPipe
		));

		/*
		 * Other Machines
		 */

		//		chute
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockMachines, 1, Taam.BLOCK_MACHINES_META.chute.ordinal()),
				"InI","InI","nIn",
				'I', "ingotIron"
		));

		//		motion sensor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockSensor, 1, 0),
				"PGP", "PpP", "IRI",
				'P', "materialPlastic",
				'G', "blockGlass",
				'p', "partPhotocell",
				'I', "ingotIron",
				'R', "dustRedstone"
		));

		//      industial lamp
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockLamp, 2, 0),
				"GPG", "grg",
				'P', "materialPlastic",
				'G', "paneGlass",
				'g', "dustGlowstone",
				'r', "dustRedstone"
		));
		// Inverted
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.blockLampInverted, 1, 0),
				TaamMain.blockLamp, Blocks.REDSTONE_TORCH
		));

		/*
		 * Blocks
		 */
		//      support bream
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockSupportBeam, 16, 0),
				"FFF", "F F", "FFF",
				'F', partIronFrame
		));

		/*
		 * Tools
		 */

		//		wrench
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemWrench, 1, 0),
				"*I*", "II*", "**I",
				'I', "ingotIron"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemWrench, 1, 0),
				"*I*", "*II", "I**",
				'I', "ingotIron"
		));
		//		saw
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemSaw, 1, 0),
				"IIS",
				'I', "ingotIron",
				'S', "stickWood"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemSaw, 1, 0),
				"SII",
				'I', "ingotIron",
				'S', "stickWood"
		));

		/*
		 * Parts
		 */
		//		photo cell
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.itemPart, 9, Taam.ITEM_PART_META.photocell.ordinal()),
				Blocks.DAYLIGHT_DETECTOR,
				toolSawAnyDamage
		));
		//		magnetic coil
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 4, Taam.ITEM_PART_META.magnetic_coil.ordinal()),
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
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 6, Taam.ITEM_PART_META.support_frame.ordinal()),
				"*A*", "A*A", "AAA",
				'A', "ingotAluminum"));
		//		support frame wood
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.support_frame_wood.ordinal()),
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
		//		wooden band - with planks
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 4, Taam.ITEM_PART_META.wooden_band.ordinal()),
				"WsW", "WSW",
				's', "stickWood",
				'S', "string",
				'W', "plankWood"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 4, Taam.ITEM_PART_META.wooden_band.ordinal()),
				"WSW", "WsW",
				's', "stickWood",
				'S', "string",
				'W', "plankWood"
		));
		//		wooden band - with slabs
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 2, Taam.ITEM_PART_META.wooden_band.ordinal()),
				"WsW", "WSW",
				's', "stickWood",
				'S', "string",
				'W', "slabWood"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 2, Taam.ITEM_PART_META.wooden_band.ordinal()),
				"WSW", "WsW",
				's', "stickWood",
				'S', "string",
				'W', "slabWood"
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
				'C', partMagneticCoil,
				'I', "ingotIron",
				'A', "ingotAluminum"
		));
		//		Sieve
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.sieve.ordinal()),
				"IBI", "BBB", "IBI",
				'B', Blocks.IRON_BARS,
				'I', "ingotIron"
		));
		//		Redirector
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 4, Taam.ITEM_PART_META.redirector.ordinal()),
				"P", "I", "I",
				'P', materialAluminumPlate,
				'I', "ingotAluminum"
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
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMaterial, 6, Taam.ITEM_MATERIAL_META.wooden_board.ordinal()),
				"www", "s s",
				'w', "plankWood",
				's', "stickWood"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMaterial, 6, Taam.ITEM_MATERIAL_META.wooden_board.ordinal()),
				"s s", "www",
				'w', "plankWood",
				's', "stickWood"
		));

		//		aluminum plate
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMaterial, 6, Taam.ITEM_MATERIAL_META.aluminum_plate.ordinal()),
				" a ", "aaa",
				'a', "ingotAluminum"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemMaterial, 6, Taam.ITEM_MATERIAL_META.aluminum_plate.ordinal()),
				"aaa", " a ",
				'a', "ingotAluminum"
		));

		//      cement
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.itemMaterial, 4, Taam.ITEM_MATERIAL_META.cement.ordinal()),
				Items.CLAY_BALL, "dustStone", "dustStone", "dustStone"
		));
		//      rough cement
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.itemMaterial, 2, Taam.ITEM_MATERIAL_META.cementRough.ordinal()),
				"materialCement", "gravel"
		));

		//      concrete
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.fine.ordinal()),
				"materialCement", "materialCement", "materialCement", "materialCement", Items.WATER_BUCKET
		));
		//      rough concrete
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.rough.ordinal()),
				"materialRoughCement", "materialRoughCement", "materialRoughCement", "materialRoughCement", Items.WATER_BUCKET
		));
		//      coated concrete
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.blockConcrete, 1, Taam.BLOCK_CONCRETE_META.coated.ordinal()),
				blockConcreteFine, "materialResin", "materialResin", "materialResin", "materialResin", "materialResin", Items.WATER_BUCKET
		));

		//      chiseled concrete
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockConcrete, 4, Taam.BLOCK_CONCRETE_META.fine_chiseled.ordinal()),
				"CC", "CC",
				'C', blockConcreteFine
		));
		//      rough chiseled concrete
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockConcrete, 4, Taam.BLOCK_CONCRETE_META.rough_chiseled.ordinal()),
				"CC", "CC",
				'C', blockConcreteRough
		));
		//      coated chiseled concrete
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockConcrete, 4, Taam.BLOCK_CONCRETE_META.coated_chiseled.ordinal()),
				"CC", "CC",
				'C', blockConcreteCoated
		));
		//      black chiseled concrete
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockConcrete, 4, Taam.BLOCK_CONCRETE_META.black_chiseled.ordinal()),
				"CC", "CC",
				'C', blockConcreteBlack
		));

		// Fallback materials & error correction stuff
		ItemStack cheatyIronIngot = new ItemStack(TaamMain.itemIngot, 1, Taam.BLOCK_ORE_META.iron.ordinal());
		GameRegistry.addShapelessRecipe(new ItemStack(Items.IRON_INGOT), cheatyIronIngot);

		ItemStack cheatyGoldIngot = new ItemStack(TaamMain.itemIngot, 1, Taam.BLOCK_ORE_META.gold.ordinal());
		GameRegistry.addShapelessRecipe(new ItemStack(Items.GOLD_INGOT), cheatyGoldIngot);
	}

}
