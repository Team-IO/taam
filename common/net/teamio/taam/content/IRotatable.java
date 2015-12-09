package net.teamio.taam.content;

import net.minecraft.util.EnumFacing;

public interface IRotatable {
	EnumFacing getFacingDirection();
	EnumFacing getMountDirection();
	EnumFacing getNextFacingDirection();
	EnumFacing getNextMountDirection();
	void setFacingDirection(EnumFacing direction);
	void setMountDirection(EnumFacing direction);
}
