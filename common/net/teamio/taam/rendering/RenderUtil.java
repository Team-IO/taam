package net.teamio.taam.rendering;

import net.minecraft.util.EnumFacing;
import net.teamio.taam.content.IRotatable;

/**
 * Utility methods used in the {@link TaamRenderer}
 *
 * @author Oliver Kahrmann
 */
public final class RenderUtil {
	private RenderUtil() {
		// Util Class
	}

	/**
	 * Maps the given enum facing to full 90 degrees steps.
	 * South is the native rotation.
	 *
	 * @param direction One of the horizontal facings, unknown values are mapped to 0 degrees.
	 * @return 0, 90, 180 or 270 degrees as float. Defaults to 0 degrees.
	 */
	public static float getRotationDegrees(EnumFacing direction) {
		if (direction == null) return 0;
		float rotationDegrees;
		switch (direction) {
			case WEST:
				rotationDegrees = 270;
				break;
			case NORTH:
				rotationDegrees = 180;
				break;
			case EAST:
				rotationDegrees = 90;
				break;
			default:
				rotationDegrees = 0;
				break;
		}
		return rotationDegrees;
	}

	/**
	 * Gets the facing from the given rotatable
	 * and then maps to degrees using {@link #getRotationDegrees(EnumFacing)}.
	 * South is the native rotation.
	 *
	 * @param rotatable An IRotatable. null values are mapped to 0 degrees.
	 * @return 0, 90, 180 or 270 degrees as float. Defaults to 0 degrees.
	 */
	public static float getRotationDegrees(IRotatable rotatable) {
		if (rotatable == null) {
			return 0;
		}
		EnumFacing direction = rotatable.getFacingDirection();
		return getRotationDegrees(direction);
	}

	/**
	 * Tries to get a facing from the given object (cast to {@link IRotatable})
	 * and then maps to degrees using {@link #getRotationDegrees(EnumFacing)}.
	 * South is the native rotation.
	 *
	 * @param tileEntity An object that hopefully implements {@link IRotatable}.
	 *                   Other values are mapped to 0 degrees.
	 * @return 0, 90, 180 or 270 degrees as float. Defaults to 0 degrees.
	 */
	public static float getRotationDegrees(Object tileEntity) {
		EnumFacing direction = getDirection(tileEntity);
		return getRotationDegrees(direction);
	}

	/**
	 * Tries to get a facing from the given object (cast to {@link IRotatable})
	 *
	 * @param tileEntity An object that hopefully implements {@link IRotatable}.
	 *                   Other values are mapped to {@link EnumFacing#SOUTH}.
	 * @return an EnumFacing, potentially non-horizontal. Defaults to {@link EnumFacing#SOUTH}.
	 */
	public static EnumFacing getDirection(Object tileEntity) {
		EnumFacing direction;
		if (tileEntity instanceof IRotatable) {
			direction = ((IRotatable) tileEntity).getFacingDirection();
		} else {
			direction = EnumFacing.SOUTH;
		}
		return direction;
	}
}
