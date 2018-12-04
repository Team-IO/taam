package net.teamio.taam.recipes;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.taam.recipes.impl.CrusherRecipe;
import net.teamio.taam.recipes.impl.FluidDrierRecipe;
import net.teamio.taam.recipes.impl.GrinderRecipe;
import net.teamio.taam.recipes.impl.MixerRecipe;
import net.teamio.taam.recipes.impl.SprayerRecipe;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for all processing machines. Provides indexed access to all recipes.
 *
 * Supported recipes have to extend {@link IProcessingRecipe}, special handling
 * for {@link IProcessingRecipeFluidBased} exists.
 *
 * For every machine there is an integer constant e.g. {@link #GRINDER}, that
 * can be used for querying. New machines can be added via
 * {@link #registerMachine(ProcessingRegistryEntry)} which returns a new integer
 * "constant".
 *
 * @author Oliver Kahrmann
 *
 */
public final class ProcessingRegistry {
	private ProcessingRegistry() {
		// Util Class
	}

	private static final Map<Integer, ProcessingRegistryEntry> entries;

	private static int count = 0;

	public static final Integer GRINDER;
	public static final Integer CRUSHER;
	public static final Integer SPRAYER;
	public static final Integer MIXER;
	public static final Integer FLUIDDRIER;

	static {
		entries = new HashMap<Integer, ProcessingRegistryEntry>();

		GRINDER = registerMachine(new ProcessingRegistryEntry(GrinderRecipe.class, "Grinder"));
		CRUSHER = registerMachine(new ProcessingRegistryEntry(CrusherRecipe.class, "Crusher"));
		SPRAYER = registerMachine(new ProcessingRegistryEntry(SprayerRecipe.class, "Sprayer"));
		MIXER = registerMachine(new ProcessingRegistryEntry(MixerRecipe.class, "Mixer"));
		FLUIDDRIER = registerMachine(new ProcessingRegistryEntry(FluidDrierRecipe.class, "Fluid Drier"));
	}

	/**
	 * Returns the number of registered machines. Since the machines are
	 * numbered sequentially, this can be used to iterate over all machines.
	 *
	 * See also {@link #getRegistryEntries()}.
	 *
	 * @return The number of registered machines, also used as the next index
	 *         for registering new machines.
	 */
	public static int getCount() {
		return count;
	}

	/**
	 * Returns all registry entries.
	 *
	 * @return All registry entries.
	 */
	public static Collection<ProcessingRegistryEntry> getRegistryEntries() {
		return entries.values();
	}

	/**
	 * Returns the registry entry for a machine.
	 *
	 * @param machine
	 * @return
	 */
	public static ProcessingRegistryEntry getRegistryEntry(Integer machine) {
		return entries.get(machine);
	}

	/**
	 * Registers a new registry entry.
	 *
	 * @param entry
	 * @return The integer value that can be used to query the registry later.
	 *         Alternatively, the registry entry itself can be used, as the
	 *         registry simply delegates to the correct entry per machine.
	 */
	public static Integer registerMachine(ProcessingRegistryEntry entry) {
		Integer id = count;
		count++;
		entries.put(id, entry);
		return id;
	}

	/**
	 * Returns the first {@link IProcessingRecipe} that has a matching input.
	 * Searches for an exact {@link Item} match first, then the ore dictionary
	 * entries.
	 *
	 * @param machine
	 * @param input
	 * @return
	 */
	public static IProcessingRecipe getRecipe(int machine, ItemStack input) {
		ProcessingRegistryEntry entry = getRegistryEntry(machine);

		return entry.getRecipe(input);
	}

	/**
	 * Returns all {@link IProcessingRecipe} that have a matching input. Exact
	 * {@link Item} matches will appear sorted before ore dictionary matches.
	 *
	 * @param machine
	 * @param input
	 * @return
	 */
	public static IProcessingRecipe[] getRecipes(int machine, ItemStack input) {
		ProcessingRegistryEntry entry = getRegistryEntry(machine);

		return entry.getRecipes(input);
	}

	/**
	 * Returns the first {@link IProcessingRecipeFluidBased} that has a matching
	 * input.
	 *
	 * @param machine
	 * @param input
	 * @return
	 */
	public static IProcessingRecipeFluidBased getRecipe(int machine, FluidStack input) {
		ProcessingRegistryEntry entry = getRegistryEntry(machine);

		return entry.getRecipe(input);
	}

	/**
	 * Returns all {@link IProcessingRecipeFluidBased} that have a matching
	 * input.
	 *
	 * @param machine
	 * @param input
	 * @return
	 */
	public static IProcessingRecipeFluidBased[] getRecipes(int machine, FluidStack input) {
		ProcessingRegistryEntry entry = getRegistryEntry(machine);

		return entry.getRecipes(input);
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
		ProcessingRegistryEntry entry = getRegistryEntry(machine);

		entry.registerRecipe(recipe);
	}

	/**
	 * Fetch all recipes for a machine.
	 *
	 * @param machine
	 * @return
	 */
	public static List<IProcessingRecipe> getAllRecipes(int machine) {
		ProcessingRegistryEntry entry = getRegistryEntry(machine);

		return entry.getAllRecipes();
	}
}
