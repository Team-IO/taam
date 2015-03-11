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
		//TODO: Remove Bauxite and Kaolinit ore furnace recipes
		for(int meta = 0; meta < Taam.BLOCK_ORE_META.length; meta++) {
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
				'I', Items.iron_ingot,
				'R', Items.redstone));
		
//		hs hopper standalone
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, 3),
				"C C", " H ", 
				'C', new ItemStack(TaamMain.blockProductionLine , 1, 0),
				'H', Blocks.hopper));
		
//		hs hopper
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TaamMain.blockProductionLine, 1, 2), 
				new Object[] { new ItemStack(TaamMain.itemPart, 1, 2),
				new ItemStack(TaamMain.blockProductionLine, 1, 3)}));
//		logistics chip
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TaamMain.itemPart, 1, 5),
				"PKP", "HCH", "PVP",
				'P', "materialPlastic",
				'K', Blocks.chest,
				'H', Blocks.hopper,
				'V', Items.comparator,
				'C', new ItemStack(TaamMain.itemPart, 1, 4)));

	}
	
	

}
