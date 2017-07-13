package net.teamio.taam;

import com.google.common.collect.Lists;
import mcmultipart.client.multipart.MultipartRegistryClient;
import net.teamio.taam.machines.MachineMultipart;
import net.teamio.taam.rendering.TaamMultipartRenderer;

import java.util.List;

public class MultipartHandlerClient {

	public static TaamMultipartRenderer taamMultipartRenderer;

	public static List<String> multipartSpecialModels = Lists.newArrayList("taam:machine#variant=pipe");

	/**
	 * Called in {@link TaamClientProxy#registerRenderStuff()}
	 */
	public static void registerRenderStuff() {
		taamMultipartRenderer = new TaamMultipartRenderer(TaamClientProxy.taamRenderer);

		MultipartRegistryClient.bindMultipartSpecialRenderer(MachineMultipart.class, taamMultipartRenderer);
	}

}
