package net.teamio.taam.machines;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.content.BaseTileEntity;

public class MachineTileEntity extends BaseTileEntity implements ITickable, IMachineWrapper {

	public IMachine machine;
	public IMachineMetaInfo meta;

	public MachineTileEntity() {
		machine = new MachineDummy();
	}

	public MachineTileEntity(IMachineMetaInfo meta) {
		this.meta = meta;
		machine = meta.createMachine(this);
	}

	@Override
	public void markAsDirty() {
		markDirty();
	}

	@Override
	public void onLoad() {
		if (machine == null) {
			Log.error("MachineTileEntity at {} is missing machine instance.", getPos());
			return;
		}
		machine.onCreated(world, pos);
	}

	@Override
	public void onChunkUnload() {
		if (machine == null) {
			Log.error("MachineTileEntity at {} is missing machine instance.", getPos());
			return;
		}
		machine.onUnload(world, pos);
	}

	@Override
	public String getName() {
		return "tile.taam.machine." + meta.getName() + ".name";
	}

	@Override
	public void update() {
		if (machine == null) {
			// DO NOT LOG, this will definitely lead to log spamming.
			return;
		}
		if(machine.update(world, pos)) {
			markDirty();
		}
	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		if (meta != null) {
			tag.setString("machine", meta.unlocalizedName());
		}
		if (machine == null) {
			Log.error("MachineTileEntity at {} is missing machine instance.", pos);
			return;
		}
		machine.writePropertiesToNBT(tag);
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		String machineID = tag.getString("machine");
		IMachineMetaInfo meta = Taam.MACHINE_META.fromId(machineID);
		if (meta != null && this.meta != meta) {
			this.meta = meta;
			machine = meta.createMachine(this);
			updateState(false, true, false);
		}
		if (machine == null) {
			Log.error("MachineTileEntity at {} is missing machine instance.", pos);
			return;
		}
		machine.readPropertiesFromNBT(tag);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (machine == null) {
			Log.error("MachineTileEntity at {} is missing machine instance.", pos);
			return false;
		}
		return machine.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (machine == null) {
			Log.error("MachineTileEntity at {} is missing machine instance.", pos);
			return null;
		}
		return machine.getCapability(capability, facing);
	}

	@Override
	public void sendPacket() {
		updateState(true, false, false);
		//TODO: send update packet
	}
}
