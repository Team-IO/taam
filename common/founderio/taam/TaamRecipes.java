package founderio.taam;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;

public class TaamRecipes {

	public static void addRecipes(){
		//Photocell
		GameRegistry.addRecipe(new ItemStack(TaamMain.itemPart, 9, 0),
				"GGG", "GDG", "PRP",
				'G', Blocks.glass,
				'D', Blocks.daylight_detector,
				'P', new ItemStack(TaamMain.itemMaterial, 1, 0),//Plastic
				'R', Items.redstone);
		
		//Motion Sensor
		GameRegistry.addRecipe(new ItemStack(TaamMain.blockSensor, 1, 0),
				"PGP", "PpP", "IRI",
				'P', new ItemStack(TaamMain.itemMaterial, 1, 0),//Plastic
				'G', Blocks.glass,
				'p', new ItemStack(TaamMain.itemPart, 1, 0),//Photocell
				'I', Items.iron_ingot,
				'R', Items.redstone);
		
		//Motor
		//TODO
	}

	public static void addSmeltingRecipes(){
		for(int meta = 0; meta < Taam.BLOCK_ORE_META.length; meta++) {
			GameRegistry.addSmelting(new ItemStack(TaamMain.blockOre, 1, meta), new ItemStack(TaamMain.itemIngot, 1, meta), 1);
		}
	}

	public static void addOreRecipes(){
	}
	
	

}
