package net.teamio.taam.content.conveyors;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;
import net.teamio.taam.util.TaamUtil;

public abstract class ATileEntityAttachable extends BaseTileEntity implements IConveyorAwareTE, IRotatable {

	protected EnumFacing direction = EnumFacing.NORTH;

	public ATileEntityAttachable() {
		super();
	}

	@Override
	public boolean isSlotAvailable(int slot) {
		switch(direction) {
		default:
		case SOUTH:
			return slot == 2 || slot == 5 || slot == 8;
		case EAST:
			return slot == 6 || slot == 7 || slot == 8;
		case NORTH:
			return slot == 0 || slot == 3 || slot == 6;
		case WEST:
			return slot == 0 || slot == 1 || slot == 2;
		}
	}

	public EnumFacing getNextSlot(int slot) {
		return null;
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
		for(int i = 0; i < 3; i++) {
			dir = dir.rotateY();
			if(TaamUtil.canAttach(worldObj, pos, dir)) {
				return dir;
			}
		}
		return direction;
	}

	@Override
	public void setFacingDirection(EnumFacing direction) {
		this.direction = direction;
		IBlockState state = worldObj.getBlockState(pos);
		worldObj.setBlockState(pos, state.withProperty(BlockProductionLineAttachable.FACING, direction));
		updateState();
	}

}