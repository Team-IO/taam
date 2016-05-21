package net.teamio.taam.integration.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IItemRegistry;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import net.teamio.taam.recipes.ProcessingRegistry;

public class TaamJEIPlugin extends BlankModPlugin {
	@Override
	public void register(IModRegistry registry) {
		IItemRegistry itemRegistry = registry.getItemRegistry();
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		

		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		
		registry.addRecipeCategories(new ProcessingCategory(ProcessingRegistry.GRINDER, guiHelper));
	}
}
