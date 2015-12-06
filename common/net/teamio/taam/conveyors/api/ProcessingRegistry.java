package net.teamio.taam.conveyors.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.teamio.taam.Log;

@SuppressWarnings("unchecked")
public final class ProcessingRegistry {
	private ProcessingRegistry() {
		// Util Class
	}

	private static Map<Item, IProcessingRecipe[]>[] recipes;
	private static Map<String, IProcessingRecipe[]>[] recipesOreDict;
	
	public static final int GRINDER = 0;
	public static final int CRUSHER = 1;
	
	static {
		recipes = new Map[2];
		recipes[GRINDER] = new HashMap<Item, IProcessingRecipe[]>();
		recipes[CRUSHER] = new HashMap<Item, IProcessingRecipe[]>();
		recipesOreDict = new Map[2];
		recipesOreDict[GRINDER] = new HashMap<String, IProcessingRecipe[]>();
		recipesOreDict[CRUSHER] = new HashMap<String, IProcessingRecipe[]>();
	}
	
	public static IProcessingRecipe getRecipe(int machine, ItemStack input) {
		Map<Item, IProcessingRecipe[]> recipes = ProcessingRegistry.recipes[machine];
		
		IProcessingRecipe[] matches = recipes.get(input.getItem());

		if(matches != null) {
			Log.debug("Fetching recipe for machine " + machine + ": " + input + "->" + matches.length + " matches");
			for(IProcessingRecipe recipe : matches) {
				if(recipe != null && recipe.inputMatches(input)) {
					Log.debug("Matching recipe " + recipe);
					return recipe;
				}
			}
		}
		
		/*
		 * Ore Dictionary Recipes
		 */
		
		int[] oreIDs = OreDictionary.getOreIDs(input);
		Log.debug("Fetching ore dict recipes for machine " + machine + ": " + input + "->" + oreIDs.length + " matches");
		
		Map<String, IProcessingRecipe[]> recipesOreDict = ProcessingRegistry.recipesOreDict[machine];
		
		for(int oreID : oreIDs) {
			String oreName = OreDictionary.getOreName(oreID);
			matches = recipesOreDict.get(oreName);
			if(matches != null) {
				for(IProcessingRecipe recipe : matches) {
					if(recipe != null && recipe.inputMatches(input)) {
						Log.debug("Matching recipe " + recipe);
						return recipe;
					}
				}
			}
		}
		
		return null;
	}
	
	public static void registerRecipe(int machine, IProcessingRecipe recipe) {
		Map<Item, IProcessingRecipe[]> recipes = ProcessingRegistry.recipes[machine];
		Map<String, IProcessingRecipe[]> recipesOreDict = ProcessingRegistry.recipesOreDict[machine];

		Item key = null;
		{
			ItemStack inputStack = recipe.getInput();
			if(inputStack != null) {
				key = inputStack.getItem();
			}
		}
		String keyOreDict = recipe.getInputOreDict();

		Log.debug("Registering recipe for machine " + machine + ": " + (key == null ? keyOreDict : key) + "->" + recipe);
		
		IProcessingRecipe[] matches;
		
		if(key == null) {
			if(keyOreDict == null) {
				throw new RuntimeException("Error registering recipe " + recipe + " for machine " + machine + ". Both keys (item and ore dict) were null.");
			}
			matches = recipesOreDict.get(keyOreDict);
		} else {
			matches = recipes.get(key);
		}
		
		
		if(matches == null) {
			Log.debug("First recipe for this item.");
			matches = new IProcessingRecipe[1];
		} else {
			Log.debug((matches.length + 1) + ". recipe for this item.");
			matches = Arrays.copyOf(matches, matches.length + 1);
		}
		matches[matches.length - 1] = recipe;
		if(key == null) {
			recipesOreDict.put(keyOreDict, matches);
		} else {
			recipes.put(key, matches);
		}
	}

	public static Collection<IProcessingRecipe> getRecipes(int machine) {
		return getRecipes(machine, null);
	}

	public static Collection<IProcessingRecipe> getRecipes(int machine, ItemStack result) {
		Map<Item, IProcessingRecipe[]> recipes = ProcessingRegistry.recipes[machine];
		Map<String, IProcessingRecipe[]> recipesOreDict = ProcessingRegistry.recipesOreDict[machine];
		
		ArrayList<IProcessingRecipe> matching = new ArrayList<IProcessingRecipe>();

		filter(matching, recipes.values(), result);
		filter(matching, recipesOreDict.values(), result);
		
		return matching;
	}
	
	public static void filter(Collection<IProcessingRecipe> outputMatching, Collection<IProcessingRecipe[]> candidates, ItemStack filterFor) {
		for(IProcessingRecipe[] list : candidates) {
			if(filterFor == null) {
				Collections.addAll(outputMatching, list);
			} else {
				for(IProcessingRecipe recipe : list) {
					ChancedOutput[] output = recipe.getOutput();
					for(ChancedOutput co : output) {
						if(OreDictionary.itemMatches(filterFor, co.output, false)) {
							outputMatching.add(recipe);
							break;
						}
					}
				}
			}
		}
	}
}
