package net.teamio.taam;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
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
//		taamBlockRenderer = new TaamBlockRenderer();
//		blockRendererId = RenderingRegistry.getNextAvailableRenderId();
//		RenderingRegistry.registerBlockHandler(blockRendererId, taamBlockRenderer);
		
		// Receive event for Client Ticks
		FMLCommonHandler.instance().bus().register(taamRenderer);
		
		ItemModelMesher modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		
		Item itemOre = GameRegistry.findItem(Taam.MOD_ID, Taam.BLOCK_ORE);
		Item itemIngot = GameRegistry.findItem(Taam.MOD_ID, Taam.ITEM_INGOT);
		Item itemDust = GameRegistry.findItem(Taam.MOD_ID, Taam.ITEM_DUST);
		
		ModelBakery.addVariantName(itemOre, Taam.MOD_ID + ":ore.impossible");
		ModelBakery.addVariantName(itemIngot, Taam.MOD_ID + ":ingot.impossible");
		ModelBakery.addVariantName(itemDust, Taam.MOD_ID + ":dust.impossible");
		
		for(Taam.BLOCK_ORE_META meta : Taam.BLOCK_ORE_META.values()) {
			int metaInt = meta.ordinal();
			String metaName = meta.name();
			if(meta.ore) {
				ModelBakery.addVariantName(itemOre, Taam.MOD_ID + ":ore." + metaName);
				modelMesher.register(itemOre, metaInt, new ModelResourceLocation(Taam.MOD_ID + ":ore." + metaName, "inventory"));
			} else {
				modelMesher.register(itemOre, metaInt, new ModelResourceLocation(Taam.MOD_ID + ":ore.impossible", "inventory"));
			}
			if(meta.ingot) {
				ModelBakery.addVariantName(itemIngot, Taam.MOD_ID + ":ingot." + metaName);
				modelMesher.register(itemIngot, metaInt, new ModelResourceLocation(Taam.MOD_ID + ":ingot." + metaName, "inventory"));
			} else {
				modelMesher.register(itemIngot, metaInt, new ModelResourceLocation(Taam.MOD_ID + ":ingot.impossible", "inventory"));
			}
			if(meta.dust) {
				ModelBakery.addVariantName(itemDust, Taam.MOD_ID + ":dust." + metaName);
				modelMesher.register(itemDust, meta.ordinal(), new ModelResourceLocation(Taam.MOD_ID + ":dust." + metaName, "inventory"));
			} else {
				modelMesher.register(itemDust, meta.ordinal(), new ModelResourceLocation(Taam.MOD_ID + ":dust.impossible", "inventory"));
			}
		}

		Item itemConcrete = GameRegistry.findItem(Taam.MOD_ID, Taam.BLOCK_CONCRETE);
		
		for(Taam.BLOCK_CONCRETE_META meta : Taam.BLOCK_CONCRETE_META.values()) {
			int metaInt = meta.ordinal();
			String metaName = meta.name();
			ModelBakery.addVariantName(itemConcrete, Taam.MOD_ID + ":concrete." + metaName);
			modelMesher.register(itemConcrete, metaInt, new ModelResourceLocation(Taam.MOD_ID + ":concrete." + metaName, "inventory"));
		}

		registerItemDefault(modelMesher, TaamMain.itemWrench, 0, Taam.MOD_ID + ":wrench");
		registerItemDefault(modelMesher, TaamMain.itemSaw, 0, Taam.MOD_ID + ":tool.saw");
		registerItemDefault(modelMesher, TaamMain.itemDebugTool, 0, Taam.MOD_ID + ":coffee");
	}
	
	private void registerItemDefault(ItemModelMesher modelMesher, Item item, int meta, String name) {
		ModelBakery.addVariantName(item, name);
		modelMesher.register(item, meta, new ModelResourceLocation(name, "inventory"));
	}
}
