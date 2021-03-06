package net.teamio.taam.integration.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.teamio.taam.Log;
import net.teamio.taam.recipes.ProcessingRegistry;
import net.teamio.taam.recipes.ProcessingRegistryEntry;

@JEIPlugin
public class TaamJEIPlugin extends BlankModPlugin {
	@Override
	public void register(IModRegistry registry) {
		Log.debug("Registering JEI Categories + RecipeHandlers");

		IJeiHelpers jeiHelpers = registry.getJeiHelpers();

		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(
				new GrinderCategory(guiHelper),
				new CrusherCategory(guiHelper),
				new FluidDrierCategory(guiHelper),
				new MixerCategory(guiHelper),
				new SprayerCategory(guiHelper)
				);

		/*registry.addRecipeCategoryCraftingItem(
				new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.grinder.ordinal()),
				Taam.INTEGRATION_JEI_CAT_GRINDER);
		registry.addRecipeCategoryCraftingItem(
				new ItemStack(TaamMain.blockProductionLine, 1, Taam.BLOCK_PRODUCTIONLINE_META.crusher.ordinal()),
				Taam.INTEGRATION_JEI_CAT_CRUSHER);
		registry.addRecipeCategoryCraftingItem(
				new ItemStack(TaamMain.itemMachine, 1, Taam.MACHINE_META.fluid_drier.metaData()),
				Taam.INTEGRATION_JEI_CAT_FLUIDDRIER);
		registry.addRecipeCategoryCraftingItem(
				new ItemStack(TaamMain.itemMachine, 1, Taam.MACHINE_META.mixer.metaData()),
				Taam.INTEGRATION_JEI_CAT_MIXER);
		registry.addRecipeCategoryCraftingItem(
				new ItemStack(TaamMain.blockProductionLineAppliance, 1, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.sprayer.metaData()),
				Taam.INTEGRATION_JEI_CAT_SPRAYER);*/

		registry.addRecipeHandlers(
				new GrinderRecipeHander(),
				new CrusherRecipeHandler(),
				new FluidDrierRecipeHandler(),
				new MixerRecipeHandler(),
				new SprayerRecipeHandler()
				);
		/*
		 * Add all recipes from the registry, for all machines we have
		 */
		Log.debug("Beginning to add recipes");
		for (ProcessingRegistryEntry entry : ProcessingRegistry.getRegistryEntries()) {
			Log.debug("Adding {} recipes", entry.getAllRecipes().size());
			registry.addRecipes(entry.getAllRecipes());
		}
		Log.debug("Finished adding recipes");
	}
}
