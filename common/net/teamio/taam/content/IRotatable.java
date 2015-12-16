package net.teamio.taam.content;

import net.minecraftforge.common.util.ForgeDirection;

public interface IRotatable {
	ForgeDirection getFacingDirection();
	ForgeDirection getNextFacingDirection();
	void setFacingDirection(ForgeDirection direction);
}
