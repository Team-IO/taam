package net.teamio.taam;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.obj.OBJModel.OBJBakedModel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.content.IRenderableItem;
import net.teamio.taam.content.common.TileEntityChute;
import net.teamio.taam.content.common.TileEntityCreativeCache;
import net.teamio.taam.content.common.TileEntitySensor;
import net.teamio.taam.content.conveyors.TileEntityConveyor;
import net.teamio.taam.content.conveyors.TileEntityConveyorHopper;
import net.teamio.taam.content.conveyors.TileEntityConveyorItemBag;
import net.teamio.taam.content.conveyors.TileEntityConveyorProcessor;
import net.teamio.taam.content.conveyors.TileEntityConveyorSieve;
import net.teamio.taam.content.conveyors.TileEntityConveyorTrashCan;
import net.teamio.taam.content.piping.TileEntityTank;
import net.teamio.taam.conveyors.appliances.ApplianceSprayer;
import net.teamio.taam.rendering.TaamRenderer;

@SuppressWarnings("deprecation")
public class TaamClientProxy extends TaamCommonProxy {

	public static int blockRendererId;

	public static TaamRenderer taamRenderer;

	@Override
	public void registerRenderStuff() {

		OBJLoader.instance.addDomain("taam");

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

//		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPipe.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTank.class, taamRenderer);

		ClientRegistry.bindTileEntitySpecialRenderer(ApplianceSprayer.class, taamRenderer);

		// Receive event for Client Ticks
		MinecraftForge.EVENT_BUS.register(taamRenderer);

		ItemModelMesher modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

		/*
		 * Ores, Ingots, Dusts
		 */

		Item itemOre = GameRegistry.findItem(Taam.MOD_ID, Taam.BLOCK_ORE);
		Item itemIngot = GameRegistry.findItem(Taam.MOD_ID, Taam.ITEM_INGOT);
		Item itemDust = GameRegistry.findItem(Taam.MOD_ID, Taam.ITEM_DUST);

		ModelBakery.registerItemVariants(itemOre, new ResourceLocation(Taam.MOD_ID, "ore.impossible"));
		ModelBakery.registerItemVariants(itemIngot, new ResourceLocation(Taam.MOD_ID, "ingot.impossible"));
		ModelBakery.registerItemVariants(itemDust, new ResourceLocation(Taam.MOD_ID, "dust.impossible"));

		for (Taam.BLOCK_ORE_META meta : Taam.BLOCK_ORE_META.values()) {
			int metaInt = meta.ordinal();
			String metaName = meta.name();
			if (meta.ore) {
				ModelBakery.registerItemVariants(itemOre, new ResourceLocation(Taam.MOD_ID, "ore." + metaName));
				modelMesher.register(itemOre, metaInt,
						new ModelResourceLocation(Taam.MOD_ID + ":ore." + metaName, "inventory"));
			} else {
				modelMesher.register(itemOre, metaInt,
						new ModelResourceLocation(Taam.MOD_ID + ":ore.impossible", "inventory"));
			}
			if (meta.ingot) {
				ModelBakery.registerItemVariants(itemIngot, new ResourceLocation(Taam.MOD_ID, "ingot." + metaName));
				modelMesher.register(itemIngot, metaInt,
						new ModelResourceLocation(Taam.MOD_ID + ":ingot." + metaName, "inventory"));
			} else {
				modelMesher.register(itemIngot, metaInt,
						new ModelResourceLocation(Taam.MOD_ID + ":ingot.impossible", "inventory"));
			}
			if (meta.dust) {
				ModelBakery.registerItemVariants(itemDust, new ResourceLocation(Taam.MOD_ID, "dust." + metaName));
				modelMesher.register(itemDust, meta.ordinal(),
						new ModelResourceLocation(Taam.MOD_ID + ":dust." + metaName, "inventory"));
			} else {
				modelMesher.register(itemDust, meta.ordinal(),
						new ModelResourceLocation(Taam.MOD_ID + ":dust.impossible", "inventory"));
			}
		}

		/*
		 * Concrete
		 */

		Item itemToRegister = GameRegistry.findItem(Taam.MOD_ID, Taam.BLOCK_CONCRETE);

		for (Taam.BLOCK_CONCRETE_META meta : Taam.BLOCK_CONCRETE_META.values()) {
			int metaInt = meta.ordinal();
			String metaName = meta.name();
			ModelBakery.registerItemVariants(itemToRegister, new ResourceLocation(Taam.MOD_ID, "concrete." + metaName));
			modelMesher.register(itemToRegister, metaInt,
					new ModelResourceLocation(Taam.MOD_ID + ":concrete." + metaName, "inventory"));
		}

		/*
		 * Materials
		 */

		itemToRegister = TaamMain.itemMaterial;

		for (Taam.ITEM_MATERIAL_META meta : Taam.ITEM_MATERIAL_META.values()) {
			int metaInt = meta.ordinal();
			String metaName = meta.name();
			ModelBakery.registerItemVariants(itemToRegister, new ResourceLocation(Taam.MOD_ID, "material." + metaName));
			modelMesher.register(itemToRegister, metaInt, new ModelResourceLocation(Taam.MOD_ID + ":material." + metaName, "inventory"));
		}

		/*
		 * Parts
		 */

		itemToRegister = TaamMain.itemPart;

		for (Taam.ITEM_PART_META meta : Taam.ITEM_PART_META.values()) {
			int metaInt = meta.ordinal();
			String metaName = meta.name();
			ModelBakery.registerItemVariants(itemToRegister, new ResourceLocation(Taam.MOD_ID, "part." + metaName));
			modelMesher.register(itemToRegister, metaInt, new ModelResourceLocation(Taam.MOD_ID + ":part." + metaName, "inventory"));
		}

		/*
		 * Other Items
		 */

		registerItemDefault(modelMesher, TaamMain.itemWrench, 0, Taam.MOD_ID + ":wrench");
		registerItemDefault(modelMesher, TaamMain.itemSaw, 0, Taam.MOD_ID + ":tool.saw");
		registerItemDefault(modelMesher, TaamMain.itemDebugTool, 0, Taam.MOD_ID + ":debugger");

		/*
		 * OBJ Models
		 */

		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_SENSOR, 0, "sensor.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_SUPPORT_BEAM, 0, "support_beam.obj");

//		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PIPE, 0, "pipes.obj");

		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_MACHINES, Taam.BLOCK_MACHINES_META.chute.ordinal(), "chute.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_MACHINES, Taam.BLOCK_MACHINES_META.creativecache.ordinal(), "creative_cache.obj");
		
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.sprayer.ordinal(), "sprayer.obj");

		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PIPEMACHINES, Taam.BLOCK_PIPEMACHINES_META.tank.ordinal(), "tank.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PIPEMACHINES, Taam.BLOCK_PIPEMACHINES_META.creativewell.ordinal(), "creative_well.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PIPEMACHINES, Taam.BLOCK_PIPEMACHINES_META.pump.ordinal(), "pump.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PIPEMACHINES, Taam.BLOCK_PIPEMACHINES_META.mixer.ordinal(), "mixer.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PIPEMACHINES, Taam.BLOCK_PIPEMACHINES_META.fluid_drier.ordinal(), "fluid_drier.obj");

		registerItemOBJ(modelMesher, Taam.BLOCK_PRODUCTIONLINE, Taam.BLOCK_PRODUCTIONLINE_META.values().length, "conveyor.obj");
		registerItemOBJ(modelMesher, Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE, Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.values().length, "conveyor_attachables.obj");
	}

	private final List<ModelResourceLocation> locationsToReplace = new ArrayList<ModelResourceLocation>();

	/**
	 * Registers & remembers a model location for inventory rendering for the
	 * given item, for every meta value from 0 to metaCount-1.
	 * 
	 * Specific for items using OBJ models.
	 * 
	 * @param modelMesher
	 * @param itemId
	 * @param metaCount
	 * @param modelFile
	 *            Expects the model file to be a something.obj
	 */
	private void registerItemOBJ(ItemModelMesher modelMesher, String itemId, int metaCount, String modelFile) {

		// Find item to register
		Item item = GameRegistry.findItem(Taam.MOD_ID, itemId);

		// Create & remember model location
		final ModelResourceLocation resourceLocation = new ModelResourceLocation(Taam.MOD_ID + ":" + modelFile, "inventory");
		locationsToReplace.add(resourceLocation);

		ItemMeshDefinition meshDef = new ItemMeshDefinition() {

			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				return resourceLocation;
			}
		};

		// Register the variants
		modelMesher.register(item, meshDef);
		// Register the model location
		for (int meta = 0; meta < metaCount; meta++) {
			ModelLoader.setCustomModelResourceLocation(item, meta, resourceLocation);
		}
	}

	/**
	 * Registers & remembers a model location for inventory rendering for the
	 * given item, for a single meta value.
	 * 
	 * Specific for items using OBJ models.
	 * 
	 * @param modelMesher
	 * @param itemId
	 * @param metaValue
	 * @param modelFile
	 */
	private void registerItemOBJSingleMeta(ItemModelMesher modelMesher, String itemId, int metaValue, String modelFile) {
		// Find item to register
		Item item = GameRegistry.findItem(Taam.MOD_ID, itemId);

		// Create & remember model location
		final ModelResourceLocation resourceLocation = new ModelResourceLocation(Taam.MOD_ID + ":" + modelFile, "inventory");
		locationsToReplace.add(resourceLocation);

		// Register the variant
		modelMesher.register(item, metaValue, resourceLocation);
		// Register the model location
		ModelLoader.setCustomModelResourceLocation(item, metaValue, resourceLocation);
	}

	/**
	 * Registers a model for inventory rendering for a single item.
	 * 
	 * Default rendering, not for OBJ models.
	 * 
	 * @param modelMesher
	 * @param item
	 * @param meta
	 * @param name
	 */
	private void registerItemDefault(ItemModelMesher modelMesher, Item item, int meta, String name) {
		modelMesher.register(item, meta, new ModelResourceLocation(name, "inventory"));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void textureStitchPre(TextureStitchEvent.Pre event) {
		for (Fluid fluid : TaamMain.fluidsDye) {
			textureStitchPre(fluid, event);
		}
		for (Fluid fluid : TaamMain.fluidsMaterial) {
			textureStitchPre(fluid, event);
		}
	}

	private void textureStitchPre(Fluid fluid, TextureStitchEvent.Pre event) {
		TextureAtlasSprite still = event.map.getTextureExtry(fluid.getStill().toString());
		if (still == null) {
			event.map.registerSprite(fluid.getStill());
		}

		TextureAtlasSprite flow = event.map.getTextureExtry(fluid.getFlowing().toString());
		if (flow == null) {
			event.map.registerSprite(fluid.getFlowing());
		}
	}

	@SubscribeEvent
	public void onModelBakeEvent(ModelBakeEvent event) {
		Log.debug("Beginning onModelBakeEvent");

		/*
		 * We need to se the "flip-v" flag.. As the inventory-variant is
		 * "generated" above, MC will ignore what we have in the blockstates
		 * json & render the textures flipped in the inventory...
		 * 
		 * Doing it via reflection, as we'd need to redefine the original
		 * OBJModel somewhere (OBJModel.process() will do that) but I have no
		 * idea WHERE!
		 */

		Field customDataField = null;
		Field customDataFlipVField = null;
		try {
			customDataField = OBJModel.class.getDeclaredField("customData");
			customDataField.setAccessible(true);
			Class<?> customDataType = customDataField.getType();
			customDataFlipVField = customDataType.getDeclaredField("flipV");
			customDataFlipVField.setAccessible(true);
		} catch (Exception e) {
			Log.error(
					"Failed to make OBJModel.customData accessible or access other reflection stuff. Inventory items will have wrong textures.",
					e);
		}

		/*
		 * Go through all registered locations from above & replace the baked
		 * model with one that understands our items
		 */

		for (ModelResourceLocation resourceLocation : locationsToReplace) {
			IBakedModel bakedModel = event.modelRegistry.getObject(resourceLocation);
			if (bakedModel instanceof OBJBakedModel) {
				Log.debug("Replacing " + resourceLocation);

				OBJBakedModel bakedAsObj = (OBJBakedModel) bakedModel;
				OBJModel obj = bakedAsObj.getModel();

				/*
				 * Set flip-v flag
				 */

				try {
					Object customData = customDataField.get(obj);
					customDataFlipVField.set(customData, true);
				} catch (Exception e) {
					Log.error("Failed to adjust custom data. Inventory items will have wrong textures.", e);
				}

				/*
				 * Create custom baked model as replacement
				 */

				bakedModel = new ItemAwareOBJBakedModel(bakedAsObj);
				event.modelRegistry.putObject(resourceLocation, bakedModel);
			}
		}

		Log.debug("Completed onModelBakeEvent");
	}

	/**
	 * Baked model implementation that checks with the item type for a list of
	 * parts to render using an OBJBakedModel as parent.
	 * 
	 * Customized: item rendering. The rest of the implementation just relays to
	 * the parent model.
	 * 
	 * @author Oliver Kahrmann
	 *
	 */
	public static class ItemAwareOBJBakedModel
			implements IFlexibleBakedModel, ISmartBlockModel, ISmartItemModel, IPerspectiveAwareModel {

		private OBJBakedModel original;

		public ItemAwareOBJBakedModel(OBJBakedModel original) {
			this.original = original;
		}

		/*
		 * IFlexibleBakedModel
		 */

		@Override
		public VertexFormat getFormat() {
			return original.getFormat();
		}

		/*
		 * ISmartItemModel
		 */

		@Override
		public IBakedModel handleItemState(ItemStack stack) {
			if (stack != null && stack.getItem() instanceof IRenderableItem) {
				// Ask what to render
				List<String> visibleParts = ((IRenderableItem) stack.getItem()).getVisibleParts(stack);
				// Create matching state
				OBJModel.OBJState retState = new OBJModel.OBJState(visibleParts, true);
				return original.getCachedModel(retState);
			}
			// Not one of ours, whatever, just render everything...
			return original;
		}

		/*
		 * IPerspectiveAwareModel
		 */

		@Override
		public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
			return original.handlePerspective(cameraTransformType);
		}

		/*
		 * ISmartBlockModel
		 */

		@Override
		public IBakedModel handleBlockState(IBlockState state) {
			return original.handleBlockState(state);
		}

		/*
		 * IBakedModel
		 */

		@Override
		public List<BakedQuad> getFaceQuads(EnumFacing side) {
			return original.getFaceQuads(side);
		}

		@Override
		public List<BakedQuad> getGeneralQuads() {
			return original.getGeneralQuads();
		}

		@Override
		public boolean isAmbientOcclusion() {
			return original.isAmbientOcclusion();
		}

		@Override
		public boolean isGui3d() {
			return original.isGui3d();
		}

		@Override
		public boolean isBuiltInRenderer() {
			return original.isBuiltInRenderer();
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return original.getParticleTexture();
		}

		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return original.getItemCameraTransforms();
		}

	}
}
