package founderio.taam;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import founderio.taam.blocks.TileEntityConveyor;
import founderio.taam.blocks.TileEntityConveyorHopper;
import founderio.taam.blocks.TileEntityConveyorProcessor;
import founderio.taam.blocks.TileEntityLogisticsManager;
import founderio.taam.blocks.TileEntityLogisticsStation;
import founderio.taam.blocks.TileEntitySensor;
import founderio.taam.entities.EntityLogisticsCart;
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
