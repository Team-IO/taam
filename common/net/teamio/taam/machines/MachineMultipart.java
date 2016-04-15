package net.teamio.taam.machines;

import java.util.List;

import mcmultipart.MCMultiPartMod;
import mcmultipart.block.BlockMultipart;
import mcmultipart.multipart.IOccludingPart;
import mcmultipart.multipart.Multipart;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.teamio.taam.Taam;

public class MachineMultipart extends Multipart implements IOccludingPart {
	private IMachine machine;

	public MachineMultipart() {
	}
	
	public MachineMultipart(IMachine machine) {
		this.machine = machine;
	}

	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		machine.addCollisionBoxes(mask, list, collidingEntity);
	}

	@Override
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		machine.addSelectionBoxes(list);
	}

	@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {
		machine.addOcclusionBoxes(list);
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state) {
		return machine.getExtendedState(state, getWorld(), getPos());
	}
	
	@Override
	public BlockState createBlockState() {
		return new ExtendedBlockState(MCMultiPartMod.multipart, new IProperty[0], new IUnlistedProperty[]{BlockMultipart.properties[0], OBJModel.OBJProperty.instance});
	}
	
	@Override
	public String getModelPath() {
		return machine.getModelPath();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		//TODO: Multipartfactory
		String machineID = tag.getString("machine");
		System.err.println("Reading nbt: " + machineID);
		IMachineMetaInfo meta = Taam.MACHINE_META.fromId(machineID);
		machine = meta.createMachine();
		machine.readPropertiesFromNBT(tag, false);
	}
	
	@Override
	public void readUpdatePacket(PacketBuffer buf) {
		String machineID = buf.readStringFromBuffer(30);
		System.err.println("Reading buf: " + machineID);
		IMachineMetaInfo meta = Taam.MACHINE_META.fromId(machineID);
		machine = meta.createMachine();
		//TODO: machine.readPropertiesFromBuf(tag, false);
	}
	
	@Override
	public void writeUpdatePacket(PacketBuffer buf) {
		buf.writeString("pipe");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		//TODO: write actual ID
		tag.setString("machine", "pipe");
		machine.writePropertiesToNBT(tag, false);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return machine.hasCapability(capability, facing);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return machine.getCapability(capability, facing);
	}

}
