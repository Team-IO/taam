package net.teamio.taam.conveyors.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.teamio.taam.Log;

@SuppressWarnings("unchecked")
public final class ProcessingRegistry {
	private ProcessingRegistry() {
		// Util Class
	}

	private static Map<Item, IProcessingRecipe[]>[] recipes;
	
	public static final int GRINDER = 0;
	public static final int CRUSHER = 1;
	
	static {
		recipes = new Map[2];
		recipes[GRINDER] = new HashMap<Item, IProcessingRecipe[]>();
		recipes[CRUSHER] = new HashMap<Item, IProcessingRecipe[]>();
	}
	
	public static IProcessingRecipe getRecipe(int machine, ItemStack input) {
		Map<Item, IProcessingRecipe[]> recipes = ProcessingRegistry.recipes[machine];
		
		IProcessingRecipe[] matches = recipes.get(input.getItem());
		Log.debug("Fetching recipe for machine " + machine + ": " + input + "->" + matches);

		if(matches != null) {
			for(IProcessingRecipe recipe : matches) {
				if(recipe != null && recipe.inputMatches(input)) {
					Log.debug("Matching recipe " + recipe);
					return recipe;
				}
			}
		}
		
		return null;
	}
	
	public static void registerRecipe(int machine, IProcessingRecipe recipe) {
		Map<Item, IProcessingRecipe[]> recipes = ProcessingRegistry.recipes[machine];

		Item key = recipe.getInput().getItem();
		
		Log.debug("Registering recipe for machine " + machine + ": " + recipe.getInput().getItem() + "->" + recipe);
		
		IProcessingRecipe[] matches = recipes.get(key);
		
		if(matches == null) {
			Log.debug("First recipe for this item.");
			matches = new IProcessingRecipe[1];
		} else {
			Log.debug(matches.length + ". recipe for this item.");
			matches = Arrays.copyOf(matches, matches.length + 1);
		}
		matches[matches.length - 1] = recipe;
		recipes.put(key, matches);
	}
}
