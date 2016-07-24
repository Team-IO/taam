package net.teamio.taam;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.content.conveyors.TileEntityConveyor;
import net.teamio.taam.content.conveyors.TileEntityConveyorElevator;
import net.teamio.taam.content.conveyors.TileEntityConveyorItemBag;
import net.teamio.taam.content.conveyors.TileEntityConveyorProcessor;
import net.teamio.taam.content.conveyors.TileEntityConveyorSieve;
import net.teamio.taam.content.conveyors.TileEntityConveyorTrashCan;
import net.teamio.taam.conveyors.appliances.ApplianceAligner;
import net.teamio.taam.conveyors.appliances.ApplianceSprayer;
import net.teamio.taam.machines.MachineTileEntity;
import net.teamio.taam.network.TPAdvancedGuiAppData;
import net.teamio.taam.rendering.TaamRenderer;
import net.teamio.taam.rendering.obj.OBJCustomData;
import net.teamio.taam.rendering.obj.OBJLoader;
import net.teamio.taam.rendering.obj.OBJModel;
import net.teamio.taam.rendering.obj.OBJModel.OBJBakedModel;

@SuppressWarnings("deprecation")
public class TaamClientProxy extends TaamCommonProxy {

	public static int blockRendererId;

	public static TaamRenderer taamRenderer;

	private final List<ModelResourceLocation> locationsToReplace = new ArrayList<ModelResourceLocation>();

	@Override
	public void registerPackets(SimpleNetworkWrapper network) {
		super.registerPackets(network);

		network.registerMessage(TPAdvancedGuiAppData.Handler.class, TPAdvancedGuiAppData.class, 2, Side.CLIENT);
	}

	@Override
	public void registerRenderStuff() {
		ModelLoaderRegistry.registerLoader(OBJLoader.INSTANCE);
		OBJLoader.INSTANCE.addDomain(Taam.MOD_ID.toLowerCase());

		taamRenderer = new TaamRenderer();

		// Tile Entity Rendering
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyor.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorProcessor.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorSieve.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorItemBag.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorTrashCan.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyorElevator.class, taamRenderer);

		ClientRegistry.bindTileEntitySpecialRenderer(ApplianceSprayer.class, taamRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(ApplianceAligner.class, taamRenderer);

		ClientRegistry.bindTileEntitySpecialRenderer(MachineTileEntity.class, taamRenderer);

		// If we load multipart, register multipart things, too
		if(Config.multipart_load) {
			MultipartHandlerClient.registerRenderStuff();
		}

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
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_LAMP, 0, "industrial_lamp.obj");

		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_MACHINES, Taam.BLOCK_MACHINES_META.chute.ordinal(), "chute.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_MACHINES, Taam.BLOCK_MACHINES_META.creativecache.ordinal(), "creative_cache.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_MACHINES, Taam.BLOCK_MACHINES_META.creativewell.ordinal(), "creative_well.obj");

		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.sprayer.ordinal(), "sprayer.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.aligner.ordinal(), "appliance_aligner.obj");

		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_MACHINE_WRAPPER, Taam.MACHINE_META.pipe.ordinal(), "pipes.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_MACHINE_WRAPPER, Taam.MACHINE_META.tank.ordinal(), "tank.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_MACHINE_WRAPPER, Taam.MACHINE_META.pump.ordinal(), "pump.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_MACHINE_WRAPPER, Taam.MACHINE_META.mixer.ordinal(), "mixer.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_MACHINE_WRAPPER, Taam.MACHINE_META.fluid_drier.ordinal(), "fluid_drier.obj");

		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE, Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.itembag.ordinal(), "itembag.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE, Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.trashcan.ordinal(), "trashcan.obj");


		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PRODUCTIONLINE, Taam.BLOCK_PRODUCTIONLINE_META.conveyor1.ordinal(), "conveyor_wood.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PRODUCTIONLINE, Taam.BLOCK_PRODUCTIONLINE_META.conveyor2.ordinal(), "conveyor_alu.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PRODUCTIONLINE, Taam.BLOCK_PRODUCTIONLINE_META.conveyor3.ordinal(), "conveyor_hs.obj");

		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PRODUCTIONLINE, Taam.BLOCK_PRODUCTIONLINE_META.chute.ordinal(), "conveyor_chute.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PRODUCTIONLINE, Taam.BLOCK_PRODUCTIONLINE_META.crusher.ordinal(), "conveyor_processor_crusher.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PRODUCTIONLINE, Taam.BLOCK_PRODUCTIONLINE_META.grinder.ordinal(), "conveyor_processor_grinder.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PRODUCTIONLINE, Taam.BLOCK_PRODUCTIONLINE_META.shredder.ordinal(), "conveyor_processor_shredder.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PRODUCTIONLINE, Taam.BLOCK_PRODUCTIONLINE_META.hopper.ordinal(), "conveyor_hopper.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PRODUCTIONLINE, Taam.BLOCK_PRODUCTIONLINE_META.hopper_hs.ordinal(), "conveyor_hopper_hs.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PRODUCTIONLINE, Taam.BLOCK_PRODUCTIONLINE_META.sieve.ordinal(), "conveyor_sieve.obj");
		registerItemOBJSingleMeta(modelMesher, Taam.BLOCK_PRODUCTIONLINE, Taam.BLOCK_PRODUCTIONLINE_META.elevator.ordinal(), "conveyor_elevator.obj");
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
	private static void registerItemDefault(ItemModelMesher modelMesher, Item item, int meta, String name) {
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

	private static void textureStitchPre(Fluid fluid, TextureStitchEvent.Pre event) {
		TextureMap map = event.getMap();
		TextureAtlasSprite still = map.getTextureExtry(fluid.getStill().toString());
		if (still == null) {
			map.registerSprite(fluid.getStill());
		}

		TextureAtlasSprite flow = map.getTextureExtry(fluid.getFlowing().toString());
		if (flow == null) {
			map.registerSprite(fluid.getFlowing());
		}
	}

	@SubscribeEvent
	public void onModelBakeEvent(ModelBakeEvent event) {
		Log.debug("Beginning onModelBakeEvent");

		/*
		 * We need to set the "flip-v" flag.. As the inventory-variant is
		 * "generated" above, MC will ignore what we have in the blockstates
		 * json & render the textures flipped in the inventory...
		 *
		 * Doing it via reflection, as we'd need to redefine the original
		 * OBJModel somewhere (OBJModel.process() will do that) but I have no
		 * idea WHERE!
		 */

		// Currently not required due to custom replacement of OBJModel (Hacky workaround replaces Hacky Workaround)
//		Field customDataField = null;
//		Field customDataFlipVField = null;
//		try {
//			customDataField = OBJModel.class.getDeclaredField("customData");
//			customDataField.setAccessible(true);
//			Class<?> customDataType = customDataField.getType();
//			customDataFlipVField = customDataType.getDeclaredField("flipV");
//			customDataFlipVField.setAccessible(true);
//		} catch (Exception e) {
//			Log.error(
//					"Failed to make OBJModel.customData accessible or access other reflection stuff. Inventory items will have wrong textures.",
//					e);
//		}

		/*
		 * Go through all registered locations from above & replace the baked
		 * model with one that understands our items
		 */

		IRegistry<ModelResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
		for (ModelResourceLocation resourceLocation : locationsToReplace) {
			IBakedModel bakedModel = modelRegistry.getObject(resourceLocation);
			if (bakedModel instanceof OBJBakedModel) {
				Log.debug("Replacing " + resourceLocation);

				OBJBakedModel bakedAsObj = (OBJBakedModel) bakedModel;
				OBJModel obj = bakedAsObj.getModel();

				/*
				 * Set flip-v flag
				 */
				obj.customData.processUVData.put(OBJCustomData.Keys.FLIP_UVS, Pair.of(false, true));
				obj.customData.hasProcessed = true;
//				try {
//					Object customData = customDataField.get(obj);
//					customDataFlipVField.set(customData, true);
//				} catch (Exception e) {
//					Log.error("Failed to adjust custom data. Inventory items will have wrong textures.", e);
//				}

				/*
				 * Create custom baked model as replacement
				 */

				bakedModel = new ItemAwareOBJBakedModel(bakedAsObj);
				modelRegistry.putObject(resourceLocation, bakedModel);
			}
		}

		//		if(Config.multipart_load) {
		//			MultipartHandlerClient.onModelBakeEvent(event);
		//		}

		Log.debug("Completed onModelBakeEvent");
	}

	/**
	 * Original: {@link net.minecraftforge.client.model.ForgeBlockStateV1.Variant.Deserializer.get(float, float, float, float, float, float, float)}
	 * @param tx
	 * @param ty
	 * @param tz
	 * @param ax
	 * @param ay
	 * @param az
	 * @param s
	 * @return
	 */
	public static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s)
	{
		return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
				new Vector3f(tx / 16, ty / 16, tz / 16),
				TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)),
				new Vector3f(s, s, s),
				null));
	}

	/**
	 * Original: {@link net.minecraftforge.client.model.ForgeBlockStateV1.Variant.Deserializer.flipX}
	 */
	public static final TRSRTransformation flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1), null);

	/**
	 * Original: {@link net.minecraftforge.client.model.ForgeBlockStateV1.Variant.Deserializer.leftify(TRSRTransformation)}
	 * @param transform
	 * @return
	 */
	public static TRSRTransformation leftify(TRSRTransformation transform)
	{
		return TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX));
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
	public static class ItemAwareOBJBakedModel implements IPerspectiveAwareModel {

		public static final IModelState defaultBlockTransform;

		/**
		 * Original: {@link net.minecraftforge.client.model.ForgeBlockStateV1.Variant.Deserializer.deserialize(JsonElement, Type, JsonDeserializationContext)}
		 *
		 * Load the default transform for block models to rotate the models in GUI & Co accordingly.
		 * These are the same transforms that are applied by forge when loaded from the blockstates using "forge:default-block".
		 */
		static {
			TRSRTransformation thirdperson = get(0, 2.5f, 0, 75, 45, 0, 0.375f);
			ImmutableMap.Builder<TransformType, TRSRTransformation> builder = ImmutableMap.builder();
			builder.put(TransformType.GUI,                     get(0, 0, 0, 30, 225, 0, 0.625f));
			builder.put(TransformType.GROUND,                  get(0, 3, 0, 0, 0, 0, 0.25f));
			builder.put(TransformType.FIXED,                   get(0, 0, 0, 0, 0, 0, 0.5f));
			builder.put(TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
			builder.put(TransformType.THIRD_PERSON_LEFT_HAND,  leftify(thirdperson));
			builder.put(TransformType.FIRST_PERSON_RIGHT_HAND, get(0, 0, 0, 0, 45, 0, 0.4f));
			builder.put(TransformType.FIRST_PERSON_LEFT_HAND,  get(0, 0, 0, 0, 225, 0, 0.4f));
			defaultBlockTransform = new SimpleModelState(builder.build());
		}

		private OBJBakedModel original;

		public ItemAwareOBJBakedModel(OBJBakedModel original) {
			this.original = original;
		}

		/*
		 * IPerspectiveAwareModel
		 */

		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
			// Use forge default block transform
			return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, defaultBlockTransform, cameraTransformType);
		}

		/*
		 * IBakedModel
		 */

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			return original.getQuads(state, side, rand);
		}

		@Override
		public boolean isAmbientOcclusion() {
			return original.isAmbientOcclusion();
		}

		@Override
		public boolean isGui3d() {
			return true;
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
			return ItemCameraTransforms.DEFAULT;
		}

		@Override
		public ItemOverrideList getOverrides() {
			return original.getOverrides();
		}

	}
}
