package net.teamio.taam.conveyors.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@SuppressWarnings("unchecked")
public final class ProcessingRegistry {
	private ProcessingRegistry() {
		// Util Class
	}

	private static Map<Integer, IProcessingRecipe[]>[] recipes;
	
	public static final int GRINDER = 0;
	public static final int CRUSHER = 1;
	
	static {
		recipes = new Map[2];
		recipes[GRINDER] = new HashMap<Integer, IProcessingRecipe[]>();
		recipes[CRUSHER] = new HashMap<Integer, IProcessingRecipe[]>();
	}
	
	public static IProcessingRecipe getRecipe(int machine, ItemStack input) {
		Map<Integer, IProcessingRecipe[]> recipes = ProcessingRegistry.recipes[machine];
		
		IProcessingRecipe[] matches = recipes.get(Item.getIdFromItem(input.getItem()));
		
		if(matches != null) {
			for(IProcessingRecipe recipe : matches) {
				if(recipe != null && recipe.inputMatches(input)) {
					return recipe;
				}
			}
		}
		
		return null;
	}
	
	public static void registerRecipe(int machine, IProcessingRecipe recipe) {
		Map<Integer, IProcessingRecipe[]> recipes = ProcessingRegistry.recipes[machine];

		Integer key = Item.getIdFromItem(recipe.getInput().getItem());
		
		IProcessingRecipe[] matches = recipes.get(key);
		
		if(matches == null) {
			matches = new IProcessingRecipe[1];
		} else {
			matches = Arrays.copyOf(matches, matches.length + 1);
		}
		matches[matches.length - 1] = recipe;
		recipes.put(key, matches);
	}
}
