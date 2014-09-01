package founderio.taam;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;

public class TaamRecipes {

	public static void addRecipes(){
		GameRegistry.addRecipe(new ItemStack(TaamMain.itemPhotoCell, 9), "GGG", "GDG", "PRP", 'G', Blocks.glass, 'D', Blocks.daylight_detector, 'P', new ItemStack(TaamMain.itemPlastic), 'R', Items.redstone);
		GameRegistry.addRecipe(new ItemStack(TaamMain.blockSensor, 1), "PGP", "PpP", "IRI", 'P', new ItemStack(TaamMain.itemPlastic), 'G', Blocks.glass, 'p', new ItemStack(TaamMain.itemPhotoCell), 'I', Items.iron_ingot, 'R', Items.redstone);
	}

	public static void addSmeltingRecipes(){
		GameRegistry.addSmelting(new ItemStack(TaamMain.blockOre, 1, 0), new ItemStack(TaamMain.itemIngot, 1, 0), 1);
		GameRegistry.addSmelting(new ItemStack(TaamMain.blockOre, 1, 1), new ItemStack(TaamMain.itemIngot, 1, 1), 1);
	}

	public static void addOreRecipes(){
	}
	
	

}
