package net.teamio.taam;

import mcmultipart.multipart.MultipartRegistry;
import net.minecraft.item.Item;
import net.teamio.taam.machines.IMachineMetaInfo;
import net.teamio.taam.machines.MachineItemMultipart;
import net.teamio.taam.machines.MachineMultipart;

public class MultipartHandler {
	
	public static void registerMultipartStuff() {
		MultipartRegistry.registerPart(MachineMultipart.class, "taam.machine");
	}
	
	public static Item registerMultipartItem(String unlocalizedName, IMachineMetaInfo[] meta) {
		MachineItemMultipart item = new MachineItemMultipart(meta);
		item.setUnlocalizedName(unlocalizedName);
		item.setCreativeTab(TaamMain.creativeTab);
		return item;
	}
}
