package net.teamio.taam.machines;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.teamio.taam.content.BaseTileEntity;

public class MachineTileEntity extends BaseTileEntity implements ITickable {

	public final IMachine machine;
	
	public MachineTileEntity(IMachine machine) {
		this.machine = machine;
	}
	
	@Override
	public void update() {
		machine.update();
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		machine.writePropertiesToNBT(tag, false);
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		machine.readPropertiesFromNBT(tag, false);
	}

}
