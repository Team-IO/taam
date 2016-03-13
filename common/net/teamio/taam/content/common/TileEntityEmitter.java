package net.teamio.taam.content.common;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.teamio.taam.content.BaseTileEntity;

public class TileEntityEmitter extends BaseTileEntity implements ITickable {

	private String sound = "default";
	private int count = 0;
	private int timeout = 600;

	@Override
	public void update() {
		count++;
		if (count >= timeout) {
			count = 0;

			worldObj.playSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, sound, 1f, 1f, true);
		}
	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {

	}

}
