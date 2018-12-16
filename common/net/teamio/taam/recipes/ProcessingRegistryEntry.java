package net.teamio.taam.recipes;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.teamio.taam.Log;
import net.teamio.taam.util.InventoryUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ProcessingRegistryEntry {
	public final Map<Item, IProcessingRecipe[]> recipes;
	public final Map<String, IProcessingRecipe[]> recipesOreDict;
	public final Map<Fluid, IProcessingRecipeFluidBased[]> recipesFluid;
	public final Class<? extends IProcessingRecipe> recipeClass;
	public final String machineName;

	public ProcessingRegistryEntry(Class<? extends IProcessingRecipe> recipeClass, String machineName) {
		this.recipeClass = recipeClass;
		this.machineName = machineName;
		recipes = new HashMap<>();
		recipesOreDict = new HashMap<>();
		recipesFluid = new HashMap<>();
	}

	/**
	 * Returns the first {@link IProcessingRecipe} that has a matching input.
	 * Searches for an exact {@link Item} match first, then the ore dictionary
	 * entries.
	 *
	 * @param input The input item to search for
	 * @return The first matching recipe, or null if none matched.
	 */
	public IProcessingRecipe getRecipe(ItemStack input) {
		if (InventoryUtils.isEmpty(input)) return null;

		IProcessingRecipe[] matches = recipes.get(input.getItem());

		if (matches != null) {
			Log.debug("Fetching recipes for machine {}, {}: {} matches.", machineName, input, matches.length);
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
		Log.debug("Fetching ore dict recipes for machine {}, {}: {} matches.", machineName, input, oreIDs.length);

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
	 * Returns all {@link IProcessingRecipe} that have a matching input. Exact
	 * {@link Item} matches will appear sorted before ore dictionary matches.
	 *
	 * @param input The input item to search for
	 * @return An array of matching recipes. May return null or an empty array.
	 */
	public IProcessingRecipe[] getRecipes(ItemStack input) {
		if (InventoryUtils.isEmpty(input)) return new IProcessingRecipe[0];
		ArrayList<IProcessingRecipe> actualMatches = new ArrayList<IProcessingRecipe>();

		IProcessingRecipe[] matches = recipes.get(input.getItem());

		if (matches != null) {
			Log.debug("Fetching recipes for machine {}, {}: {} matches.", machineName, input, matches.length);
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
		Log.debug("Fetching ore dict recipes for machine {}, {}: {} matches.", machineName, input, oreIDs.length);

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

		return actualMatches.toArray(new IProcessingRecipe[0]);
	}

	/**
	 * Returns the first {@link IProcessingRecipeFluidBased} that has a matching
	 * input.
	 *
	 * @param input The input fluid to search for
	 * @return The first matching recipe, or null if none matched.
	 */
	public IProcessingRecipeFluidBased getRecipe(FluidStack input) {

		IProcessingRecipeFluidBased[] matches = recipesFluid.get(input.getFluid());
		if (matches != null) {
			Log.debug("Fetching fluid recipes for machine {}, {}: {} matches.", machineName, input, matches.length);
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
	 * Returns all {@link IProcessingRecipeFluidBased} that have a matching
	 * input.
	 *
	 * @param input The input fluid to search for
	 * @return An array of matching recipes. May return null or an empty array.
	 */
	public IProcessingRecipeFluidBased[] getRecipes(FluidStack input) {
		IProcessingRecipeFluidBased[] matches = recipesFluid.get(input.getFluid());

		if (matches == null) {
			return null;
		}

		IProcessingRecipeFluidBased[] actualMatches = new IProcessingRecipeFluidBased[matches.length];
		int idx = 0;

		Log.debug("Fetching fluid recipes for machine {}, {}: {} matches.", machineName, input, matches.length);
		for (IProcessingRecipeFluidBased recipe : matches) {
			if (recipe != null && recipe.inputFluidMatches(input)) {
				Log.debug("Matching recipe {}", recipe);
				actualMatches[idx] = recipe;
				idx++;
			}
		}

		return Arrays.copyOf(actualMatches, idx);
	}

	/**
	 * Registers a recipe for a specific machine. Regular
	 * {@link IProcessingRecipe} are indexed for input item or ore dictionary
	 * name. All recipes can then be searched using
	 * {@link #getRecipe(ItemStack)}.
	 * <p>
	 * Special handling: {@link IProcessingRecipeFluidBased} will be indexed for
	 * input fluid and can be searched using {@link #getRecipe(FluidStack)}.
	 *
	 * @param recipe A recipe of the correct class that was given in the constructor.
	 */
	public void registerRecipe(IProcessingRecipe recipe) {
		if (!recipeClass.isInstance(recipe)) {
			throw new IllegalArgumentException(String.format(
					"Error registering recipe %o for machine %s. Recipe is not an instance of the designated recipe class %o.",
					recipe, machineName, recipeClass));
		}
		Item key = null;
		{
			ItemStack inputStack = recipe.getInput();
			if (!InventoryUtils.isEmpty(inputStack)) {
				key = inputStack.getItem();
			}
		}
		String keyOreDict = recipe.getInputOreDict();

		boolean isFluidBased = recipe instanceof IProcessingRecipeFluidBased;
		boolean isItemBased = key != null || keyOreDict != null;

		if (!isItemBased && !isFluidBased) {
			throw new IllegalArgumentException("Error registering recipe " + recipe + " for machine " + machineName
					+ ". Both keys (item and ore dict) were null, and not a fluid recipe.");
		}

		if (isItemBased) {
			registerRecipeItemBased(recipe);
		}

		if (isFluidBased) {
			registerRecipeFluidBased((IProcessingRecipeFluidBased) recipe);
		}
	}

	/**
	 * Registers an {@link IProcessingRecipe} for search via {@link ItemStack}.
	 *
	 * @param recipe The recipe to index, already checked for the correct class.
	 */
	private void registerRecipeItemBased(IProcessingRecipe recipe) {
		Item key = null;
		{
			ItemStack inputStack = recipe.getInput();
			if (!InventoryUtils.isEmpty(inputStack)) {
				key = inputStack.getItem();
			}
		}
		String keyOreDict = recipe.getInputOreDict();

		Log.debug("Registering item  recipe for machine {}: {}->{}", machineName, key == null ? keyOreDict : key.getRegistryName(), recipe);

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
	 * Registers an {@link IProcessingRecipeFluidBased} for search via {@link FluidStack}.
	 *
	 * @param recipe The recipe to index, already checked for the correct class.
	 */
	private void registerRecipeFluidBased(IProcessingRecipeFluidBased recipe) {
		Fluid key = recipe.getInputFluid().getFluid();

		Log.debug("Registering fluid recipe for machine {}: {}->{}", machineName, recipe.getInputFluid().getUnlocalizedName(), recipe);

		IProcessingRecipeFluidBased[] matches;

		if (key == null) {
			throw new IllegalArgumentException("Error registering fluid recipe " + recipe + " for machine " + machineName
					+ ". Key (fluid) was null.");
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
	 * Combines all registered recipes into one list.
	 *
	 * @return A single ArrayList of all recipes, without duplicates, but not in a guaranteed order.
	 */
	public List<IProcessingRecipe> getAllRecipes() {
		Set<IProcessingRecipe> recipes = new HashSet<>();
		for (IProcessingRecipe[] list : this.recipes.values()) {
			Collections.addAll(recipes, list);
		}
		for (IProcessingRecipe[] list : recipesFluid.values()) {
			Collections.addAll(recipes, list);
		}
		for (IProcessingRecipe[] list : recipesOreDict.values()) {
			Collections.addAll(recipes, list);
		}
		return new ArrayList<>(recipes);
	}
}
