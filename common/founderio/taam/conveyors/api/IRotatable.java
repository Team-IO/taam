package founderio.taam.conveyors.api;

import net.minecraftforge.common.util.ForgeDirection;

public interface IRotatable {
	ForgeDirection getFacingDirection();
	ForgeDirection getMountDirection();
	ForgeDirection getNextFacingDirection();
	ForgeDirection getNextMountDirection();
	void setFacingDirection(ForgeDirection direction);
	void setMountDirection(ForgeDirection direction);
}