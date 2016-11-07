package net.teamio.taam.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.teamio.taam.Log;

public final class ProcessingRegistryEntry {
	public final Map<Item, IProcessingRecipe[]> recipes;
	public final Map<String, IProcessingRecipe[]> recipesOreDict;
	public final Map<Fluid, IProcessingRecipeFluidBased[]> recipesFluid;
	public final Class<? extends IProcessingRecipe> recipeClass;
	public final String machineName;

	public ProcessingRegistryEntry(Class<? extends IProcessingRecipe> recipeClass, String machineName) {
		this.recipeClass = recipeClass;
		this.machineName = machineName;
		recipes = new HashMap<Item, IProcessingRecipe[]>();
		recipesOreDict = new HashMap<String, IProcessingRecipe[]>();
		recipesFluid = new HashMap<Fluid, IProcessingRecipeFluidBased[]>();
	}

	/**
	 * Returns the first {@link IProcessingRecipe} that has a matching input.
	 * Searches for an exact {@link Item} match first, then the ore dictionary
	 * entries.
	 *
	 * @param input
	 * @return
	 */
	public IProcessingRecipe getRecipe(ItemStack input) {

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
	 * @param input
	 * @return
	 */
	public IProcessingRecipe[] getRecipes(ItemStack input) {
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

		return actualMatches.toArray(new IProcessingRecipe[actualMatches.size()]);
	}

	/**
	 * Returns the first {@link IProcessingRecipeFluidBased} that has a matching
	 * input.
	 *
	 * @param input
	 * @return
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
	 * @param input
	 * @return
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
	 *
	 * Special handling: {@link IProcessingRecipeFluidBased} will be indexed for
	 * input fluid and can be searched using {@link #getRecipe(FluidStack)}.
	 *
	 * @param recipe
	 */
	public void registerRecipe(IProcessingRecipe recipe) {
		if (!recipeClass.isInstance(recipe)) {
			throw new RuntimeException(String.format(
					"Error registering recipe %o for machine %s. Recipe is not an instance of the designated recipe class %o.",
					recipe, machineName, recipeClass));
		}
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
			throw new RuntimeException("Error registering recipe " + recipe + " for machine " + machineName
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
	 * @param recipe
	 */
	private void registerRecipeItemBased(IProcessingRecipe recipe) {
		Item key = null;
		{
			ItemStack inputStack = recipe.getInput();
			if (inputStack != null) {
				key = inputStack.getItem();
			}
		}
		String keyOreDict = recipe.getInputOreDict();

		Log.debug("Registering item  recipe for machine {}: {}->{}", machineName, key == null ? keyOreDict : key.getUnlocalizedName(), recipe);

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
	 * @param recipe
	 */
	private void registerRecipeFluidBased(IProcessingRecipeFluidBased recipe) {
		Fluid key = recipe.getInputFluid().getFluid();

		Log.debug("Registering fluid recipe for machine {}: {}->{}", machineName, recipe.getInputFluid().getUnlocalizedName(), recipe);

		IProcessingRecipeFluidBased[] matches;

		if (key == null) {
			throw new RuntimeException("Error registering fluid recipe " + recipe + " for machine " + machineName
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

	public List<IProcessingRecipe> getAllRecipes() {
		Set<IProcessingRecipe> recipes = new HashSet<IProcessingRecipe>();
		for(IProcessingRecipe[] list : this.recipes.values()) {
			for(IProcessingRecipe recipe : list) {
				recipes.add(recipe);
			}
		}
		for(IProcessingRecipe[] list : recipesFluid.values()) {
			for(IProcessingRecipe recipe : list) {
				recipes.add(recipe);
			}
		}
		for(IProcessingRecipe[] list : recipesOreDict.values()) {
			for(IProcessingRecipe recipe : list) {
				recipes.add(recipe);
			}
		}
		return new ArrayList<IProcessingRecipe>(recipes);
	}
}
