package net.teamio.taam;

import mcmultipart.capabilities.ISlottedCapabilityProvider;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.IPartFactory.IAdvancedPartFactory;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.MultipartHelper;
import mcmultipart.multipart.MultipartRegistry;
import mcmultipart.multipart.PartSlot;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.teamio.taam.machines.IMachineMetaInfo;
import net.teamio.taam.machines.MachineItemMultipart;
import net.teamio.taam.machines.MachineMultipart;

public class MultipartHandler {

	public static void registerMultipartStuff() {
		
		MultipartRegistry.registerPartFactory(new IAdvancedPartFactory() {
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
		}, Taam.MACHINE_META.valuesAsString());

		//MultipartRegistry.registerPart(MachineMultipart.class, "taam.machine");
	}

	public static Item createMultipartItem(String unlocalizedName, IMachineMetaInfo[] meta) {
		MachineItemMultipart item = new MachineItemMultipart(meta);
		return item;
	}

	public static <T> T getCapabilityForCenter(Capability<T> capability, IBlockAccess world, BlockPos pos, EnumFacing facing) {
		IMultipartContainer container = MultipartHelper.getPartContainer(world, pos);
		if(container == null) {
			return null;
		}
		ISlottedPart part = container.getPartInSlot(PartSlot.CENTER);
		if(part instanceof ISlottedCapabilityProvider) {
			return ((ISlottedCapabilityProvider) part).getCapability(capability, PartSlot.getFaceSlot(facing), facing);
		} else if(part instanceof ICapabilityProvider) {
			return ((ICapabilityProvider) part).getCapability(capability, facing);
		} else {
			return container.getCapability(capability, PartSlot.CENTER, facing);
		}
	}
}
