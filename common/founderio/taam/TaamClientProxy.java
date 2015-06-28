package founderio.taam;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import founderio.taam.content.common.TileEntitySensor;
import founderio.taam.content.conveyors.TileEntityConveyor;
import founderio.taam.content.conveyors.TileEntityConveyorHopper;
import founderio.taam.content.conveyors.TileEntityConveyorProcessor;
import founderio.taam.content.logistics.EntityLogisticsCart;
import founderio.taam.content.logistics.TileEntityLogisticsManager;
import founderio.taam.content.logistics.TileEntityLogisticsStation;
import founderio.taam.rendering.TaamEntityRenderer;
import founderio.taam.rendering.TaamRenderer;

public class TaamClientProxy extends TaamCommonProxy {
	
	public TaamRenderer taamRenderer;
	public TaamEntityRenderer taamEntityRenderer;
	
	@Override
	public void registerRenderStuff() {
		taamRenderer = new TaamRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySensor.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyor.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorHopper.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorProcessor.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLogisticsStation.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLogisticsManager.class, taamRenderer);
		
		MinecraftForgeClient.registerItemRenderer(ItemBlock.getItemFromBlock(TaamMain.blockSensor), taamRenderer);
		MinecraftForgeClient.registerItemRenderer(ItemBlock.getItemFromBlock(TaamMain.blockProductionLine), taamRenderer);
		// Receive event for Client Ticks
		FMLCommonHandler.instance().bus().register(taamRenderer);
		
		int id = RenderingRegistry.getNextAvailableRenderId();
		TaamRenderer.renderMagneticRailID = id;
		RenderingRegistry.registerBlockHandler(id, TaamRenderer.renderMagneticRail);
		
		taamEntityRenderer = new TaamEntityRenderer();
		RenderingRegistry.registerEntityRenderingHandler(EntityLogisticsCart.class, taamEntityRenderer);
	}
}
