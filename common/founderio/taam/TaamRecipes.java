package founderio.taam;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public class TaamRecipes {

	public static void addRecipes(){
		
	}
	public static void addSmeltingRecipes(){
		for(int meta = 0; meta < Taam.BLOCK_ORE_META.length; meta++) {
			GameRegistry.addSmelting(new ItemStack(TaamMain.blockOre, 1, meta), new ItemStack(TaamMain.itemIngot, 1, meta), 1);
		}
	}

	public static void addOreRecipes(){
//		Photocell
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 9, 0),
				"GGG", "GDG", "PRP",
				'G', "blockGlass",
				'D', Blocks.daylight_detector,
				'P', "materialPlastic",
				'R', Items.redstone));
		
//		Motor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, 1),
				"PCP", "CIC", "PCP",
				'P', "materialPlastic",
				'I', Items.iron_ingot,
				'C', "materialCopper"));
		
//		SupportFrame
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, 2),
				"III", "III", "AAA",
				'I', Blocks.iron_bars,
				'A', "ingotAluminum"));
		
//		Motion Sensor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockSensor, 1, 0),
				"PGP", "PpP", "IRI",
				'P', new ItemStack(TaamMain.itemMaterial, 1, 0),//Plastic
				'G', Blocks.glass,
				'p', new ItemStack(TaamMain.itemPart, 1, 0),//Photocell
				'I', Items.iron_ingot,
				'R', Items.redstone));
		
		  
	}
	
	

}
