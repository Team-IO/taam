package founderio.taam;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import founderio.taam.blocks.TileEntitySensor;
import founderio.taam.rendering.TaamRenderer;

public class TaamClientProxy extends TaamCommonProxy {
	
	public TaamRenderer taamRenderer;
	
	@Override
	public void registerRenderStuff() {
		taamRenderer = new TaamRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySensor.class, taamRenderer);
		MinecraftForgeClient.registerItemRenderer(ItemBlock.getItemFromBlock(TaamMain.blockSensor), taamRenderer);
		MinecraftForge.EVENT_BUS.register(taamRenderer);
	}
}
