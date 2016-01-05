package net.teamio.taam.content;

import net.minecraft.util.EnumFacing;

public interface IRotatable {
	EnumFacing getFacingDirection();
	EnumFacing getNextFacingDirection();
	void setFacingDirection(EnumFacing direction);
}
