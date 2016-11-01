package net.teamio.taam;

import java.util.List;

import com.google.common.collect.Lists;

import mcmultipart.client.multipart.ISmartMultipartModel;
import mcmultipart.client.multipart.MultipartRegistryClient;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.teamio.taam.machines.MachineMultipart;
import net.teamio.taam.rendering.TaamMultipartRenderer;
import net.teamio.taam.rendering.obj.OBJModel;

public class MultipartHandlerClient {

	public static TaamMultipartRenderer taamMultipartRenderer;

	public static List<String> multipartSpecialModels = Lists.newArrayList("taam:machine#direction=down,variant=pipe");
	/**
	 * Called in {@link TaamClientProxy#registerRenderStuff()}
	 */
	public static void registerRenderStuff() {
		taamMultipartRenderer = new TaamMultipartRenderer(TaamClientProxy.taamRenderer);

		MultipartRegistryClient.bindMultipartSpecialRenderer(MachineMultipart.class, taamMultipartRenderer);
	}

	public static class SmartBlockPartModel
			extends TaamClientProxy.ItemAwareOBJBakedModel
			implements ISmartMultipartModel {

		public SmartBlockPartModel(OBJModel.OBJBakedModel original) {
			super(original);
		}

		@Override
		public IBakedModel handlePartState(IBlockState state) {
			OBJModel.OBJBakedModel baked = (OBJModel.OBJBakedModel)original.handleBlockState(state);
			return new SmartBlockPartModel(baked);
		}

	}

	/**
	 * Called in {@link TaamClientProxy#onModelBakeEvent(ModelBakeEvent)}
	 *
	 * Replaces some specific {@link ISmartMultipartModel}s with versions that
	 * understand OBJState ({@link SmartBlockPartModel}.
	 *
	 * @param event
	 */
	public static void onModelBakeEvent(ModelBakeEvent event) {

		for (String modelName : multipartSpecialModels) {
			// Get the existing model
			ModelResourceLocation resourceLocation = new ModelResourceLocation(modelName);
			IBakedModel bakedModel = event.modelRegistry.getObject(resourceLocation);

			if (bakedModel instanceof OBJModel.OBJBakedModel) {
				Log.debug("Replacing (multipart) " + resourceLocation);

				OBJModel.OBJBakedModel bakedAsObj = (OBJModel.OBJBakedModel) bakedModel;

				/*
				 * Create custom baked model as replacement
				 */

				bakedModel = new MultipartHandlerClient.SmartBlockPartModel(bakedAsObj);
				event.modelRegistry.putObject(resourceLocation, bakedModel);
			}
		}

	}

}
