package net.teamio.taam.content.piping.multiparts;

import mcmultipart.multipart.MultipartRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.teamio.taam.TaamMain;

public class MultitpartHandlerPiping {
	public static ItemPartPipe itemPartPipe;
	
	public static void registerMultiparts() {
		itemPartPipe = new ItemPartPipe();
		itemPartPipe.setUnlocalizedName("multipartPipe");
		itemPartPipe.setCreativeTab(TaamMain.creativeTab);
		
		MultipartRegistry.registerPart(PipeMultipart.class, "multipartPipe");
		
		GameRegistry.registerItem(itemPartPipe, "multipartPipe");
	}
}
