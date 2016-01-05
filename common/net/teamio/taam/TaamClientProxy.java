package net.teamio.taam;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.teamio.taam.content.common.TileEntityChute;
import net.teamio.taam.content.common.TileEntityCreativeCache;
import net.teamio.taam.content.common.TileEntitySensor;
import net.teamio.taam.content.conveyors.TileEntityConveyor;
import net.teamio.taam.content.conveyors.TileEntityConveyorHopper;
import net.teamio.taam.content.conveyors.TileEntityConveyorItemBag;
import net.teamio.taam.content.conveyors.TileEntityConveyorProcessor;
import net.teamio.taam.content.conveyors.TileEntityConveyorSieve;
import net.teamio.taam.content.conveyors.TileEntityConveyorTrashCan;
import net.teamio.taam.rendering.TaamBlockRenderer;
import net.teamio.taam.rendering.TaamRenderer;

public class TaamClientProxy extends TaamCommonProxy {

	public static int blockRendererId;
	
	public static TaamRenderer taamRenderer;
	public static TaamBlockRenderer taamBlockRenderer;

	@Override
	public void registerRenderStuff() {
		taamRenderer = new TaamRenderer();
		
		// Tile Entity Rendering
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySensor.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChute.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCreativeCache.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyor.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorHopper.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorProcessor.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorSieve.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorItemBag.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorTrashCan.class, taamRenderer);
		
		// Item Rendering
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TaamMain.blockSensor), taamRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TaamMain.blockMachines), taamRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TaamMain.blockProductionLine), taamRenderer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TaamMain.blockProductionLineAttachable), taamRenderer);
		MinecraftForgeClient.registerItemRenderer(TaamMain.itemConveyorAppliance, taamRenderer);
		
		// Block Rendering
		taamBlockRenderer = new TaamBlockRenderer();
		blockRendererId = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(blockRendererId, taamBlockRenderer);
		
		// Receive event for Client Ticks
		FMLCommonHandler.instance().bus().register(taamRenderer);

	}
}
