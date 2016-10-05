package net.teamio.taam.integration.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamMain;
import net.teamio.taam.recipes.ProcessingRegistry;
import net.teamio.taam.recipes.ProcessingRegistryEntry;

@JEIPlugin
public class TaamJEIPlugin extends BlankModPlugin {
	@Override
	public void register(IModRegistry registry) {
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();

		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(
				new GrinderCategory(guiHelper),
				new CrusherCategory(guiHelper),
				new FluidDrierCategory(guiHelper),
				new MixerCategory(guiHelper)
				);

		registry.addRecipeHandlers(
				new GrinderRecipeHander(),
				new CrusherRecipeHandler(),
				new FluidDrierRecipeHandler(),
				new MixerRecipeHandler()
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
