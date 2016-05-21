package net.teamio.taam.integration.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IItemRegistry;
import mezz.jei.api.IModRegistry;

public class TaamJEIPlugin extends BlankModPlugin {
	@Override
	public void register(IModRegistry registry) {
		IItemRegistry itemRegistry = registry.getItemRegistry();
	}
}
