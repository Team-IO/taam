package net.teamio.taam.content.conveyors;

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
			dir = dir.getRotation(EnumFacing.UP);
			if(TaamUtil.canAttach(worldObj, xCoord, yCoord, zCoord, dir)) {
				return dir;
			}
		}
		return direction;
	}

	@Override
	public void setFacingDirection(EnumFacing direction) {
		this.direction = direction;
		//if(!worldObj.isRemote) {
			int dir;
			switch(direction) {
			default:
			case NORTH:
				dir = 0;
				break;
			case SOUTH:
				dir = 1;
				break;
			case EAST:
				dir = 2;
				break;
			case WEST:
				dir = 3;
				break;
			}
			int worldMeta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, (worldMeta & 3) + (dir << 2), 3);
			updateState();
		//}
	}

	public EnumFacing getNextSlot(int slot) {
		return null;
	}

}