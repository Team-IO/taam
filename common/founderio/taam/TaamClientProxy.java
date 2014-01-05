package founderio.taam;

import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import founderio.taam.blocks.TileEntitySensor;
import founderio.taam.rendering.TaamRenderer;

public class TaamClientProxy extends TaamCommonProxy {
	
	public TaamRenderer taamRenderer;
	
	@Override
	public void registerRenderStuff() {
		taamRenderer = new TaamRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySensor.class, taamRenderer);
		MinecraftForgeClient.registerItemRenderer(TaamMain.blockSensor.blockID, taamRenderer);
		TickRegistry.registerTickHandler(taamRenderer, Side.CLIENT);
	}
}
