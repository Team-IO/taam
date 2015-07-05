package net.teamio.taam;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.MinecraftForgeClient;
import net.teamio.taam.content.common.TileEntitySensor;
import net.teamio.taam.content.conveyors.TileEntityConveyor;
import net.teamio.taam.content.conveyors.TileEntityConveyorHopper;
import net.teamio.taam.content.conveyors.TileEntityConveyorProcessor;
import net.teamio.taam.rendering.TaamRenderer;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

public class TaamClientProxy extends TaamCommonProxy {
	
	public TaamRenderer taamRenderer;
	
	@Override
	public void registerRenderStuff() {
		taamRenderer = new TaamRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySensor.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyor.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorHopper.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorProcessor.class, taamRenderer);
		
		MinecraftForgeClient.registerItemRenderer(ItemBlock.getItemFromBlock(TaamMain.blockSensor), taamRenderer);
		MinecraftForgeClient.registerItemRenderer(ItemBlock.getItemFromBlock(TaamMain.blockProductionLine), taamRenderer);
		// Receive event for Client Ticks
		FMLCommonHandler.instance().bus().register(taamRenderer);
		
		int id = RenderingRegistry.getNextAvailableRenderId();
	}
}
