package net.teamio.taam.content.conveyors;

import net.minecraft.util.EnumFacing;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.SlotMatrix;
import net.teamio.taam.util.TaamUtil;

public abstract class ATileEntityAttachable extends BaseTileEntity implements IRotatable {

	public static final SlotMatrix SLOT_MATRIX = new SlotMatrix(true, false, false, true, false, false, true, false, false);
	
	protected EnumFacing direction = EnumFacing.NORTH;

	public ATileEntityAttachable() {
		super();
	}

	/*
	 * IRotatable implementation
	 */

	@Override
	public EnumFacing getFacingDirection() {
		return direction;
	}

	@Override
	public EnumFacing getNextFacingDirection() {
		EnumFacing dir = direction;
		for (int i = 0; i < 3; i++) {
			dir = dir.rotateY();
			if (TaamUtil.canAttach(world, pos, dir)) {
				return dir;
			}
		}
		return direction;
	}

	@Override
	public void setFacingDirection(EnumFacing direction) {
		if(this.direction != direction) {
			// Only update if necessary
			this.direction = direction;
			// Also do a block update, in case neighboring machines need to know...
			updateState(false, true, true);
		}
	}

}