package net.teamio.taam.rendering;

import net.minecraft.util.EnumFacing;
import net.teamio.taam.content.IRotatable;

/**
 * Created by oliver on 2018-12-03.
 */
public class RenderUtil {
	public static float getRotationDegrees(EnumFacing direction) {
		float rotationDegrees = 0;
		if (direction == EnumFacing.WEST) {
			rotationDegrees = 270;
		} else if (direction == EnumFacing.NORTH) {
			rotationDegrees = 180;
		} else if (direction == EnumFacing.EAST) {
			rotationDegrees = 90;
		}
		return rotationDegrees;
	}

	public static EnumFacing getDirection(Object tileEntity) {
		EnumFacing direction;
		if (tileEntity instanceof IRotatable) {
			direction = ((IRotatable) tileEntity).getFacingDirection();
		} else {
			direction = EnumFacing.SOUTH;
		}
		return direction;
	}

	public static float getRotationDegrees(IRotatable rotatable) {
		if (rotatable == null) {
			return 0;
		}
		EnumFacing direction = rotatable.getFacingDirection();
		return getRotationDegrees(direction);
	}

	public static float getRotationDegrees(Object tileEntity) {
		EnumFacing direction = getDirection(tileEntity);
		return getRotationDegrees(direction);
	}
}
