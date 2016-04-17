package net.teamio.taam.machines;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.teamio.taam.Taam;
import net.teamio.taam.content.BaseTileEntity;

public class MachineTileEntity extends BaseTileEntity implements ITickable {

	public IMachine machine;
	
	public MachineTileEntity() {
	}
	
	public MachineTileEntity(IMachine machine) {
		this.machine = machine;
	}
	
	@Override
	public void update() {
		machine.update(worldObj, pos);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		String machineID = tag.getString("machine");
		IMachineMetaInfo meta = Taam.MACHINE_META.fromId(machineID);
		machine = meta.createMachine();
		super.readFromNBT(tag);
	}
	
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		//TODO: write actual ID
		tag.setString("machine", "pipe");
	};
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		machine.writePropertiesToNBT(tag);
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		machine.readPropertiesFromNBT(tag);
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
