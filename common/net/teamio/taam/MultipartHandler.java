package net.teamio.taam;

import mcmultipart.client.multipart.MultipartRegistryClient;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IPartFactory.IAdvancedPartFactory;
import mcmultipart.multipart.MultipartRegistry;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.teamio.taam.machines.IMachine;
import net.teamio.taam.machines.IMachineMetaInfo;
import net.teamio.taam.machines.MachineItemMultipart;
import net.teamio.taam.machines.MachineMultipart;

public class MultipartHandler {
	
	public static void registerMultipartStuff() {
		MultipartRegistry.registerPartFactory(new IAdvancedPartFactory() {
			
			@Override
			public IMultipart createPart(String type, NBTTagCompound tag) {
				IMachineMetaInfo meta = Taam.MACHINE_META.fromId(type);
				MachineMultipart multipart = new MachineMultipart(meta);
				multipart.readFromNBT(tag);
				return multipart;
			}
			
			@Override
			public IMultipart createPart(String type, PacketBuffer buf) {
				IMachineMetaInfo meta = Taam.MACHINE_META.fromId(type);
				MachineMultipart multipart = new MachineMultipart(meta);
				multipart.readUpdatePacket(buf);
				return multipart;
			}
		}, Taam.MACHINE_META.valuesAsString());
		
		//MultipartRegistry.registerPart(MachineMultipart.class, "taam.machine");
	}
	
	public static Item registerMultipartItem(String unlocalizedName, IMachineMetaInfo[] meta) {
		MachineItemMultipart item = new MachineItemMultipart(meta);
		item.setUnlocalizedName(unlocalizedName);
		item.setCreativeTab(TaamMain.creativeTab);
		return item;
	}
}
