package net.teamio.taam;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.teamio.taam.network.TPMachineConfiguration;

public class TaamCommonProxy {
	public void registerRenderStuff() {

	}

	public void registerPackets(SimpleNetworkWrapper network) {
		network.registerMessage(TPMachineConfiguration.Handler.class, TPMachineConfiguration.class, 1, Side.SERVER);
	}
}
