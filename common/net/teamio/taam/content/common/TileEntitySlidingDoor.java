package net.teamio.taam.content.common;

import net.minecraft.nbt.NBTTagCompound;
import net.teamio.taam.content.BaseTileEntity;

public class TileEntitySlidingDoor extends BaseTileEntity {

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
	}
	
	@Override
	public String getName() {
		return "tile.taam.sliding_door.name";
	}

}
