package net.teamio.taam;

import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.capabilities.Capability;
import net.teamio.taam.machines.IMachineMetaInfo;

public class MultipartHandler {

	public static void registerMultipartStuff() {

		/*MultipartRegistry.registerPartFactory(new IAdvancedPartFactory() {
			@Override
			public IMultipart createPart(ResourceLocation type, NBTTagCompound tag) {
				IMachineMetaInfo meta = Taam.MACHINE_META.fromId(type.getResourcePath());
				MachineMultipart multipart = new MachineMultipart(meta);
				multipart.readFromNBT(tag);
				return multipart;
			}

			@Override
			public IMultipart createPart(ResourceLocation type, PacketBuffer buf) {
				IMachineMetaInfo meta = Taam.MACHINE_META.fromId(type.getResourcePath());
				MachineMultipart multipart = new MachineMultipart(meta);
				multipart.readUpdatePacket(buf);
				return multipart;
			}
		}, Taam.MACHINE_META.valuesAsString());*/
	}

	public static Item createMultipartItem(IMachineMetaInfo[] meta) {
		return null;
		//return new MachineItemMultipart(meta);
	}

	public static <T> T getCapabilityForCenter(Capability<T> capability, IBlockAccess world, BlockPos pos, EnumFacing facing) {
		/*IMultipartContainer container = MultipartHelper.getPartContainer(world, pos);
		if(container == null) {
			return null;
		}
		ISlottedPart part = container.getPartInSlot(PartSlot.CENTER);
		// Slotted capability prodiver
		if(part instanceof ISlottedCapabilityProvider) {
			ISlottedCapabilityProvider capProvider = (ISlottedCapabilityProvider) part;
			PartSlot faceSlot = PartSlot.getFaceSlot(facing);
			if(capProvider.hasCapability(capability, faceSlot, facing))
				return capProvider.getCapability(capability, faceSlot, facing);
		}
		// "Regular" capability prodiver
		if(part instanceof ICapabilityProvider) {
			ICapabilityProvider capProvider = (ICapabilityProvider) part;
			if(capProvider.hasCapability(capability, facing))
				return capProvider.getCapability(capability, facing);
		}
		// Fallback, ask the container
		return container.getCapability(capability, PartSlot.CENTER, facing);*/
		return null;
	}
}
