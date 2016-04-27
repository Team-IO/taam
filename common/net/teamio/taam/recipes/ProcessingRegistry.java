package net.teamio.taam.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.teamio.taam.Log;

/**
 * Registry for all processing machines. Provides indexed access to all recipes.
 * 
 * Supported recipes have to extend {@link IProcessingRecipe}, special handling
 * for {@link IProcessingRecipeFluidBased} exists.
 * 
 * For every machine there is an integer constant e.g. {@link #GRINDER}, that
 * can be used for querying. Currently, it is not easily possible to add
 * additional machines.
 * 
 * @author Oliver Kahrmann
 *
 */
// Required, as we can't create arrays of generic maps:
@SuppressWarnings("unchecked")
public final class ProcessingRegistry {
	private ProcessingRegistry() {
		// Util Class
	}

	private static Map<Item, IProcessingRecipe[]>[] recipes;
	private static Map<String, IProcessingRecipe[]>[] recipesOreDict;
	private static Map<Fluid, IProcessingRecipeFluidBased[]>[] recipesFluid;

	public static final int count = 5;
	public static final int GRINDER = 0;
	public static final int CRUSHER = 1;
	public static final int SPRAYER = 2;
	public static final int MIXER = 3;
	public static final int FLUIDDRIER = 4;

	static {
		recipes = new Map[count];
		recipesOreDict = new Map[count];
		recipesFluid = new Map[count];
		for (int i = 0; i < count; i++) {
			recipes[i] = new HashMap<Item, IProcessingRecipe[]>();
			recipesOreDict[i] = new HashMap<String, IProcessingRecipe[]>();
			recipesFluid[i] = new HashMap<Fluid, IProcessingRecipeFluidBased[]>();
		}
	}

	/**
	 * Returns the first {@link IProcessingRecipe} that has a matching input.
	 * Searches for an exact {@link Item} match first, then the ore dictionary entries.
	 * @param machine
	 * @param input
	 * @return
	 */
	public static IProcessingRecipe getRecipe(int machine, ItemStack input) {
		Map<Item, IProcessingRecipe[]> recipes = ProcessingRegistry.recipes[machine];

		IProcessingRecipe[] matches = recipes.get(input.getItem());

		if (matches != null) {
			Log.debug("Fetching recipes for machine {}, {}: {} matches.", machine, input, matches.length);
			for (IProcessingRecipe recipe : matches) {
				if (recipe != null && recipe.inputMatches(input)) {
					Log.debug("Matching recipe {}", recipe);
					return recipe;
				}
			}
		}

		/*
		 * Ore Dictionary Recipes
		 */

		int[] oreIDs = OreDictionary.getOreIDs(input);
		Log.debug("Fetching ore dict recipes for machine {}, {}: {} matches.", machine, input, oreIDs.length);

		Map<String, IProcessingRecipe[]> recipesOreDict = ProcessingRegistry.recipesOreDict[machine];

		for (int oreID : oreIDs) {
			String oreName = OreDictionary.getOreName(oreID);
			matches = recipesOreDict.get(oreName);
			if (matches != null) {
				for (IProcessingRecipe recipe : matches) {
					if (recipe != null && recipe.inputMatches(input)) {
						Log.debug("Matching recipe; {}", recipe);
						return recipe;
					}
				}
			}
		}

		return null;
	}
	
	/**
	 * Returns all {@link IProcessingRecipe} that have a matching input.
	 * Exact {@link Item} matches will appear sorted before ore dictionary matches.
	 * @param machine
	 * @param input
	 * @return
	 */
	public static IProcessingRecipe[] getRecipes(int machine, ItemStack input) {
		ArrayList<IProcessingRecipe> actualMatches = new ArrayList<IProcessingRecipe>();
		
		Map<Item, IProcessingRecipe[]> recipes = ProcessingRegistry.recipes[machine];

		IProcessingRecipe[] matches = recipes.get(input.getItem());

		if (matches != null) {
			Log.debug("Fetching recipes for machine {}, {}: {} matches.", machine, input, matches.length);
			for (IProcessingRecipe recipe : matches) {
				if (recipe != null && recipe.inputMatches(input)) {
					Log.debug("Matching recipe {}", recipe);
					actualMatches.add(recipe);
				}
			}
		}

		/*
		 * Ore Dictionary Recipes
		 */

		int[] oreIDs = OreDictionary.getOreIDs(input);
		Log.debug("Fetching ore dict recipes for machine {}, {}: {} matches.", machine, input, oreIDs.length);

		Map<String, IProcessingRecipe[]> recipesOreDict = ProcessingRegistry.recipesOreDict[machine];

		for (int oreID : oreIDs) {
			String oreName = OreDictionary.getOreName(oreID);
			matches = recipesOreDict.get(oreName);
			if (matches != null) {
				for (IProcessingRecipe recipe : matches) {
					if (recipe != null && recipe.inputMatches(input)) {
						Log.debug("Matching recipe; {}", recipe);
						actualMatches.add(recipe);
					}
				}
			}
		}

		return actualMatches.toArray(new IProcessingRecipe[actualMatches.size()]);
	}

	/**
	 * Returns the first {@link IProcessingRecipeFluidBased} that has a matching input.
	 * @param machine
	 * @param input
	 * @return
	 */
	public static IProcessingRecipeFluidBased getRecipe(int machine, FluidStack input) {
		Map<Fluid, IProcessingRecipeFluidBased[]> recipes = ProcessingRegistry.recipesFluid[machine];

		IProcessingRecipeFluidBased[] matches = recipes.get(input.getFluid());
		if (matches != null) {
			Log.debug("Fetching fluid recipes for machine {}, {}: {} matches.", machine, input, matches.length);
			for (IProcessingRecipeFluidBased recipe : matches) {
				if (recipe != null && recipe.inputFluidMatches(input)) {
					Log.debug("Matching recipe {}", recipe);
					return recipe;
				}
			}
		}

		return null;
	}

	/**
	 * Returns all {@link IProcessingRecipeFluidBased} that have a matching input.
	 * @param machine
	 * @param input
	 * @return
	 */
	public static IProcessingRecipeFluidBased[] getRecipes(int machine, FluidStack input) {
		Map<Fluid, IProcessingRecipeFluidBased[]> recipes = ProcessingRegistry.recipesFluid[machine];

		
		IProcessingRecipeFluidBased[] matches = recipes.get(input.getFluid());

		if(matches == null) {
			return null;
		}
		
		IProcessingRecipeFluidBased[] actualMatches = new IProcessingRecipeFluidBased[matches.length];
		int idx = 0;
		
		if (matches != null) {
			Log.debug("Fetching fluid recipes for machine {}, {}: {} matches.", machine, input, matches.length);
			for (IProcessingRecipeFluidBased recipe : matches) {
				if (recipe != null && recipe.inputFluidMatches(input)) {
					Log.debug("Matching recipe {}", recipe);
					actualMatches[idx] = recipe;
					idx++;
				}
			}
		}
		
		return Arrays.copyOf(actualMatches, idx);
	}

	/**
	 * Registers a recipe for a specific machine. Regular
	 * {@link IProcessingRecipe} are indexed for input item or ore dictionary
	 * name. All recipes can then be searched using
	 * {@link #getRecipe(int, ItemStack)}.
	 * 
	 * Special handling: {@link IProcessingRecipeFluidBased} will be indexed for
	 * input fluid and can be searched using {@link #getRecipe(int, FluidStack)}
	 * .
	 * 
	 * @param machine
	 * @param recipe
	 */
	public static void registerRecipe(int machine, IProcessingRecipe recipe) {
		
		Item key = null;
		{
			ItemStack inputStack = recipe.getInput();
			if (inputStack != null) {
				key = inputStack.getItem();
			}
		}
		String keyOreDict = recipe.getInputOreDict();

		boolean isFluidBased = recipe instanceof IProcessingRecipeFluidBased;
		boolean isItemBased = key != null || keyOreDict != null;
		
		if (!isItemBased && !isFluidBased) {
			throw new RuntimeException("Error registering recipe " + recipe + " for machine " + machine
					+ ". Both keys (item and ore dict) were null, and not a fluid recipe.");
		}
		
		if(isItemBased) {
			registerRecipeItemBased(machine, recipe);
		}

		if (isFluidBased) {
			registerRecipeFluidBased(machine, (IProcessingRecipeFluidBased) recipe);
		}
	}
	
	/**
	 * Registers an {@link IProcessingRecipe} for search via {@link ItemStack}.
	 * 
	 * @param machine
	 * @param recipe
	 */
	private static void registerRecipeItemBased(int machine, IProcessingRecipe recipe) {

		Map<Item, IProcessingRecipe[]> recipes = ProcessingRegistry.recipes[machine];
		Map<String, IProcessingRecipe[]> recipesOreDict = ProcessingRegistry.recipesOreDict[machine];

		Item key = null;
		{
			ItemStack inputStack = recipe.getInput();
			if (inputStack != null) {
				key = inputStack.getItem();
			}
		}
		String keyOreDict = recipe.getInputOreDict();

		Log.debug("Registering recipe for machine %d: %s->%s", machine, (key == null ? keyOreDict : key), recipe);
		
		IProcessingRecipe[] matches;
		
		if (key == null) {
			if (keyOreDict == null) {
				return;
			}
			matches = recipesOreDict.get(keyOreDict);
		} else {
			matches = recipes.get(key);
		}
		
		if (matches == null) {
			Log.debug("First recipe for this item.");
			matches = new IProcessingRecipe[1];
		} else {
			Log.debug("{}. recipe for this item.", matches.length + 1);
			matches = Arrays.copyOf(matches, matches.length + 1);
		}
		matches[matches.length - 1] = recipe;
		if (key == null) {
			recipesOreDict.put(keyOreDict, matches);
		} else {
			recipes.put(key, matches);
		}
	}

	/**
	 * Registers an {@link IProcessingRecipeFluidBased} for search via
	 * {@link FluidStack}.
	 * 
	 * @param machine
	 * @param recipe
	 */
	private static void registerRecipeFluidBased(int machine, IProcessingRecipeFluidBased recipe) {
		Map<Fluid, IProcessingRecipeFluidBased[]> recipesFluid = ProcessingRegistry.recipesFluid[machine];
		Fluid key = recipe.getInputFluid().getFluid();

		Log.debug("Registering fluid recipe for machine {}: {}->{}", machine, key, recipe);

		IProcessingRecipeFluidBased[] matches;

		if (key == null) {
			throw new RuntimeException(
					"Error registering fluid recipe " + recipe + " for machine " + machine + ". Key (fluid) was null.");
		}
		matches = recipesFluid.get(key);

		if (matches == null) {
			Log.debug("First recipe for this fluid.");
			matches = new IProcessingRecipeFluidBased[1];
		} else {
			Log.debug("{}. recipe for this fluid.", matches.length + 1);
			matches = Arrays.copyOf(matches, matches.length + 1);
		}
		matches[matches.length - 1] = recipe;
		recipesFluid.put(key, matches);
	}

	/**
	 * Fetch all recipes for a machine.
	 * 
	 * @param machine
	 * @return
	 */
	public static Collection<IProcessingRecipe> getAllRecipes(int machine) {
		return getRecipesMatchingOutput(machine, null);
	}

	/**
	 * Fetch all recipes for a machine that have a specific result. Respects ore
	 * dictionary.
	 * 
	 * @param machine
	 * @param result
	 *            The result to search. If null, returns all recipes.
	 * @return
	 */
	public static Collection<IProcessingRecipe> getRecipesMatchingOutput(int machine, ItemStack result) {
		Map<Item, IProcessingRecipe[]> recipes = ProcessingRegistry.recipes[machine];
		Map<String, IProcessingRecipe[]> recipesOreDict = ProcessingRegistry.recipesOreDict[machine];

		ArrayList<IProcessingRecipe> matching = new ArrayList<IProcessingRecipe>();

		filter(matching, recipes.values(), result);
		filter(matching, recipesOreDict.values(), result);

		return matching;
	}

	/**
	 * Add all candidates to outputMatching where filterFor is a possible
	 * output. Respects ore dictionary.
	 * 
	 * @param outputMatching
	 * @param candidates
	 * @param filterFor
	 *            The result to filter for. If null, all items will be added.
	 */
	public static void filter(Collection<IProcessingRecipe> outputMatching, Collection<IProcessingRecipe[]> candidates, ItemStack filterFor) {
		for (IProcessingRecipe[] list : candidates) {
			if (filterFor == null) {
				Collections.addAll(outputMatching, list);
			} else {
				for (IProcessingRecipe recipe : list) {
					ChancedOutput[] output = recipe.getOutput();
					for (ChancedOutput co : output) {
						if (OreDictionary.itemMatches(filterFor, co.output, false)) {
							outputMatching.add(recipe);
							break;
						}
					}
				}
			}
		}
	}
}
