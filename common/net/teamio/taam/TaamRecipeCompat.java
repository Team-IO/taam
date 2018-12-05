package net.teamio.taam;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.teamio.taam.recipes.ChancedOutput;
import net.teamio.taam.recipes.ProcessingRegistry;
import net.teamio.taam.recipes.impl.CrusherRecipe;
import net.teamio.taam.util.InventoryUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Compatibility recipes, using ore dictionary
 */
public class TaamRecipeCompat {

	private static class OreDictCompatRecipe {
		public final String input;
		public final String output;
		public final int amount;

		public OreDictCompatRecipe(String input, String output, int amount) {
			this.input = input;
			this.output = output;
			this.amount = amount;
		}
	}

	public static void registerRecipes() {
		ArrayList<OreDictCompatRecipe> oreDictCompat = new ArrayList<OreDictCompatRecipe>();

		String[] defaultOres = {
				// "Defaults"
				"Nickel",
				"Lead",
				"Silver",

				// Specialties
				"Uranium",
				"Titanium",
				"Glowstone",
				"Cobalt",
				"Ardite",
				"Manyullyn",
				"Alubrass",
				"Osmium",

				// Alloys & etc
				"Electrum",
				"Constantan",
				"Steel",
				"Bronze",
				"RefinedObsidian",
		};

		String[] gemOres = {
				"Apatite",
				"Amber",
				"Sapphire",
				"Malachite",
				"Tanzanite",
				"Topaz",
				"Peridot",
				"Ruby"
		};

		for (String regName : defaultOres) {
			String ingot = "ingot" + regName;
			String dust = "dust" + regName;
			String ore = "ore" + regName;
			String block = "block" + regName;

			ItemStack ingotStack = getOredict(ingot);
			ItemStack dustStack = getOredict(dust);
			ItemStack oreStack = getOredict(ore);
			ItemStack blockStack = getOredict(block);

			// Block -> Ingot
			if(blockStack != null && ingotStack != null) {
				ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
						new CrusherRecipe(block,
								new ChancedOutput(InventoryUtils.copyStack(ingotStack, 9), 1.0f)
						));
			}

			// Ingot -> Dust
			if(ingotStack != null && dustStack != null) {
				ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
						new CrusherRecipe(ingot,
								new ChancedOutput(dustStack, 1.0f)
						));
			}

			// Ore -> Dust
			if(oreStack != null && dustStack != null) {
				ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
						new CrusherRecipe(ore,
								new ChancedOutput(InventoryUtils.copyStack(dustStack, 2), 1.0f),
								new ChancedOutput(new ItemStack(Blocks.COBBLESTONE), 0.4f),
								new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, Taam.BLOCK_ORE_META.stone.ordinal()), 0.3f),
								new ChancedOutput(new ItemStack(Blocks.MOSSY_COBBLESTONE), 0.0001f)
						));
			}
		}


		for (String regName : gemOres) {
			String ore = "ore" + regName;
			String gem = "gem" + regName;

			ItemStack oreStack = getOredict(ore);
			ItemStack gemStack = getOredict(gem);

			// Ore -> Gem
			if(oreStack != null && gemStack != null) {
				ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
						new CrusherRecipe(ore,
								new ChancedOutput(gemStack, 1.0f),
								new ChancedOutput(gemStack, 0.01f),
								new ChancedOutput(new ItemStack(Blocks.COBBLESTONE), 0.4f),
								new ChancedOutput(new ItemStack(TaamMain.itemDust, 1, Taam.BLOCK_ORE_META.stone.ordinal()), 0.3f),
								new ChancedOutput(new ItemStack(Blocks.MOSSY_COBBLESTONE), 0.0001f)
						));
			}
		}

		oreDictCompat.add(new OreDictCompatRecipe("obsidian", "dustObsidian", 2));
		oreDictCompat.add(new OreDictCompatRecipe("gemDiamond", "dustDiamond", 1));
		oreDictCompat.add(new OreDictCompatRecipe("gemPsi", "dustPsi", 1));
		oreDictCompat.add(new OreDictCompatRecipe("blockPsiDust", "dustPsi", 9));
		oreDictCompat.add(new OreDictCompatRecipe("ingotPsi", "dustPsi", 1));
		oreDictCompat.add(new OreDictCompatRecipe("fuelCoke", "dustCoke", 1));

		for (OreDictCompatRecipe compat : oreDictCompat) {
			ItemStack inputStack = getOredict(compat.input);
			ItemStack outputStack = getOredict(compat.output);

			if(inputStack != null && outputStack != null) {
				ProcessingRegistry.registerRecipe(ProcessingRegistry.CRUSHER,
						new CrusherRecipe(compat.input,
								new ChancedOutput(InventoryUtils.copyStack(outputStack, compat.amount), 1.0f)
						));
			}
		}
	}

	private static ItemStack getOredict(String name) {
		if(!OreDictionary.doesOreNameExist(name)) {
			return null;
		}
		List<ItemStack> stacks = OreDictionary.getOres(name);
		if(stacks == null || stacks.isEmpty()) {
			return null;
		}
		return InventoryUtils.copyStack(stacks.get(0), 1);
	}
}
