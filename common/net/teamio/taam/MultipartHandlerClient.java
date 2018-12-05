package net.teamio.taam;

import com.google.common.collect.Lists;
import mcmultipart.client.multipart.MultipartRegistryClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.machines.MachineMultipart;
import net.teamio.taam.rendering.TaamMultipartRenderer;

import java.util.List;

@SideOnly(Side.CLIENT)
public class MultipartHandlerClient {

	public static TaamMultipartRenderer taamMultipartRenderer;

	// Previously used to fix some models for rending
	// public static List<String> multipartSpecialModels = Lists.newArrayList("taam:machine#variant=pipe");

	/**
	 * Called in {@link TaamClientProxy#registerRenderStuff()}
	 */
	public static void registerRenderStuff() {
		taamMultipartRenderer = new TaamMultipartRenderer(TaamClientProxy.taamRenderer);

		MultipartRegistryClient.bindMultipartSpecialRenderer(MachineMultipart.class, taamMultipartRenderer);
	}

}
