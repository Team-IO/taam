package net.teamio.taam.integration.mcmultipart;

import mcmultipart.api.addon.IMCMPAddon;
import mcmultipart.api.addon.MCMPAddon;
import mcmultipart.api.multipart.IMultipartRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.teamio.taam.TaamMain;

/**
 * Created by oliver on 2018-12-13.
 */
@MCMPAddon
public class MCMultipart2Addon implements IMCMPAddon {
	@Override
	public void registerParts(IMultipartRegistry registry) {
		registry.registerPartWrapper(TaamMain.blockMachine, new MachineMultipart());
		registry.registerStackWrapper(ForgeRegistries.ITEMS.getValue(TaamMain.blockMachine.getRegistryName()), TaamMain.blockMachine);
	}
}
