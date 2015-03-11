package founderio.taam;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public class TaamRecipes {

	public static void addRecipes(){
		
	}
	public static void addSmeltingRecipes(){
		//TODO: Remove Bauxite and Kanlinit ore furnace recipes
		for(int meta = 0; meta < Taam.BLOCK_ORE_META.length; meta++) {
			GameRegistry.addSmelting(new ItemStack(TaamMain.blockOre, 1, meta), new ItemStack(TaamMain.itemIngot, 1, meta), 1);
		}
	}

	public static void addOreRecipes(){
//		photocell
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 9, 0),
				"GGG", "GDG", "PRP",
				'G', "blockGlass",
				'D', Blocks.daylight_detector,
				'P', "materialPlastic",
				'R', Items.redstone));
		
//		motor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, 1),
				"PCP", "CIC", "PCP",
				'P', "materialPlastic",
				'I', Items.iron_ingot,
				'C', "materialCopper"));
		
//		support frame
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, 2),
				"III", "III", "AAA",
				'I', Blocks.iron_bars,
				'A', "ingotAluminum"));
		
//		circuit_basic
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 8, 3),
				"CGC", "PSP", "IPI",
				'C', "ingotCopper",
				'G', Items.gold_ingot,
				'I', Items.iron_ingot,
				'P', "materialPlastic",
				'S', new ItemStack(TaamMain.itemMaterial, 1, 3)));
		
//		circuit_advanced
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, 4),
				"PCP", "CGC", "PCP",
				'C', "materialBasicCircuit",
				'G', Items.gold_ingot,
				'P', "materialPlastic"));
		
//		logistics_chip
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, 2), new Object[]{ 
				"materialAdvancedCircuit",
				Blocks.chest,
				Blocks.hopper,
				Items.comparator,
				Items.repeater}));
		
//		motion sensor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockSensor, 1, 0),
				"PGP", "PpP", "IRI",
				'P', "materialPlastic",
				'G', "blockGlass",
				'p', "partPhotocell",
				'I', Items.iron_ingot,
				'R', Items.redstone));
		
//		conveyor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, 0),
				"RRR", "SMS", "AAA",
				'R', "materialRubber",
				'S', new ItemStack(TaamMain.itemPart, 1, 0),
				'M', "partMoter",
				'A', "ingotAluminum"));
	
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, 0),
				"RRR", "SMS", "AAA",
				'R', "itemRubber",
				'S', new ItemStack(TaamMain.itemPart, 1, 0),
				'M', "partMoter",
				'A', "ingotAluminum"));
		
//		hopper
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, 1),
				"RRR", "SMS", "AAA",
				'R', "materialRubber",
				'S', new ItemStack(TaamMain.itemPart, 1, 0),
				'M', "partMoter",
				'A', "ingotAluminum"));
//		hs hopper standalone
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, 3),
				"C C", " H ", 
				'C', new ItemStack(TaamMain.blockProductionLine , 1, 0),
				'H', Blocks.hopper));
		
//		hs hopper
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, 2), 
				new Object[] { new ItemStack(TaamMain.itemPart, 1, 2),
				new ItemStack(TaamMain.blockProductionLine, 1, 3)}));
		
//		logistics_manager
//		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, 4),
//				"CGC", "PSP", "IPI",
//				'C', "ingotCopper",
//				'G', Items.gold_ingot,
//				'I', Items.iron_ingot,
//				'P', "materialPlastic",
//				'S', new ItemStack(TaamMain.itemMaterial, 1, 3)));
	
	}
	
	

}
