package net.teamio.taam;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.teamio.taam.network.TPAdvancedGuiAppData;
import net.teamio.taam.network.TPMachineConfiguration;
import net.teamio.taam.piping.PipeNetwork;
import net.teamio.taam.piping.PressureSimulator;

public class TaamCommonProxy {
	public void registerRenderStuff() {

	}

	public void registerPackets(SimpleNetworkWrapper network) {
		network.registerMessage(TPMachineConfiguration.Handler.class, TPMachineConfiguration.class, 1, Side.SERVER);
		network.registerMessage(TPAdvancedGuiAppData.Handler.class, TPAdvancedGuiAppData.class, 2, Side.SERVER);
	}

	public void registerModelLoader() {
	}

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		PressureSimulator.simulate(PipeNetwork.NET);
	}
}
