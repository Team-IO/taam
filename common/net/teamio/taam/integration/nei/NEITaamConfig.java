package net.teamio.taam.integration.nei;

import net.teamio.taam.Taam;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEITaamConfig implements IConfigureNEI {

	@Override
	public String getName() {
		return Taam.MOD_NAME + " NEI Integration";
	}

	@Override
	public String getVersion() {
		return Taam.MOD_VERSION;
	}

	@Override
	public void loadConfig() {
		API.registerRecipeHandler(new ProcessingRecipeHandler.Crusher());
		API.registerUsageHandler(new ProcessingRecipeHandler.Crusher());

		API.registerRecipeHandler(new ProcessingRecipeHandler.Grinder());
		API.registerUsageHandler(new ProcessingRecipeHandler.Grinder());
	}

}
