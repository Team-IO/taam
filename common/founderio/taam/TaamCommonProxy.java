package founderio.taam;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import founderio.taam.network.TPLogisticsConfiguration;
public class TaamCommonProxy {
	public void registerRenderStuff() {
		
	}
	
	public void registerPackets(SimpleNetworkWrapper network) {
		network.registerMessage(TPLogisticsConfiguration.Handler.class, TPLogisticsConfiguration.class, 0, Side.SERVER);
	}
}
