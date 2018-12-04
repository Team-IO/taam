package net.teamio.taam.machines;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by oliver on 2017-12-31.
 */
public interface IMachineWrapper {
	void sendPacket();

	void markAsDirty();
}
