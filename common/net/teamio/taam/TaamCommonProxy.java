package net.teamio.taam;

import net.teamio.taam.network.TPMachineConfiguration;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class TaamCommonProxy {
	public void registerRenderStuff() {

	}

	public void registerPackets(SimpleNetworkWrapper network) {
		network.registerMessage(TPMachineConfiguration.Handler.class, TPMachineConfiguration.class, 1, Side.SERVER);
	}
}
