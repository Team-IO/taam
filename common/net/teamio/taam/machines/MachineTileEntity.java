package net.teamio.taam.machines;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.teamio.taam.Taam;
import net.teamio.taam.content.BaseTileEntity;

public class MachineTileEntity extends BaseTileEntity implements ITickable {

	public IMachine machine;
	public IMachineMetaInfo meta;

	public MachineTileEntity() {
		machine = new MachineDummy();
	}

	public MachineTileEntity(IMachineMetaInfo meta) {
		this.meta = meta;
		this.machine = meta.createMachine();
	}

	@Override
	public void update() {
		machine.update(worldObj, pos);
	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		if (meta != null) {
			tag.setString("machine", meta.unlocalizedName());
		}
		machine.writePropertiesToNBT(tag);
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		String machineID = tag.getString("machine");
		IMachineMetaInfo meta = Taam.MACHINE_META.fromId(machineID);
		if (this.meta != meta) {
			this.meta = meta;
			if(meta != null) {
				machine = meta.createMachine();
			}
			updateState(false, true, false);
		}
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
