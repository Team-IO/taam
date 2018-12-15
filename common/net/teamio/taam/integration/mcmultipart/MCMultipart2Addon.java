package net.teamio.taam.integration.mcmultipart;

import mcmultipart.api.addon.IMCMPAddon;
import mcmultipart.api.addon.MCMPAddon;
import mcmultipart.api.multipart.IMultipartRegistry;

/**
 * Created by oliver on 2018-12-13.
 */
@MCMPAddon
public class MCMultipart2Addon implements IMCMPAddon {
	@Override
	public void registerParts(IMultipartRegistry registry) {
		// TODO: this is not active currently, see #259
		//registry.registerPartWrapper(TaamMain.blockMachine, new MachineMultipart());
		//registry.registerStackWrapper(ForgeRegistries.ITEMS.getValue(TaamMain.blockMachine.getRegistryName()), TaamMain.blockMachine);
	}
}
