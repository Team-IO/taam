package net.teamio.taam.conveyors;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.MathHelper;

/**
 * Helper to get conveyor slot availability, with automatic calculation of rotations.
 * This is used by machines to determine if their slots are available based on the machine rotation.
 *
 * @author Oliver Kahrmann
 */
public class SlotMatrix {

	public static final SlotMatrix ALL = new SlotMatrix();

	private final boolean[] unrotated;
	private boolean[][] rotated;

	public SlotMatrix() {
		unrotated = new boolean[]{
				true, true, true,
				true, true, true,
				true, true, true
		};
	}

	public SlotMatrix(boolean one, boolean two, boolean three, boolean four, boolean five, boolean six, boolean seven,
	                  boolean eight, boolean nine) {
		unrotated = new boolean[]{one, two, three, four, five, six, seven, eight, nine};
	}

	/**
	 * Checks the unrotated state of this matrix. By convention this means
	 * facing North.
	 *
	 * @param slot A slot id (0-8). This is automatically clamped, so be careful not to overshoot or the results are undefined.
	 * @return True if the slot is available
	 */
	public boolean isSlotAvailable(int slot) {
		slot = MathHelper.clamp(slot, 0, 8);

		return unrotated[slot];
	}

	/**
	 * Checks a rotated state of this matrix. Vertical Axis is ignored &
	 * considered unrotated.
	 *
	 * @param slot     A slot id (0-8). This is automatically clamped, so be careful not to overshoot or the results are undefined.
	 * @param rotation The rotation for which the slot should be checked. Vertical rotations are considered unrotated.
	 * @return True if the slot is available
	 */
	public boolean isSlotAvailable(int slot, EnumFacing rotation) {
		// Non-Rotation can be skipped, also if we are the ALL instance.
		if (rotation.getAxis() == Axis.Y || this == ALL) {
			return isSlotAvailable(slot);
		}

		if (rotated == null) {
			calculateRotations();
		}
		// Horizontal Index: S-W-N-E
		boolean[] slots = rotated[rotation.getHorizontalIndex()];

		slot = MathHelper.clamp(slot, 0, 8);

		return slots[slot];
	}

	private void calculateRotations() {
		rotated = new boolean[4][];
		// North
		rotated[2] = unrotated;
		// West
		rotated[1] = rotate(unrotated);
		// South
		rotated[0] = rotate(rotated[1]);
		// East
		rotated[3] = rotate(rotated[0]);
	}

	/**
	 * Rotates the given source array counter-clockwise
	 *
	 * @param source An array of 9 booleans
	 * @return A new array of 9 booleans with the contents rotated according to the default slot layout
	 */
	public static boolean[] rotate(boolean[] source) {
		return new boolean[]{
				source[6], source[3], source[0],
				source[7], source[4], source[1],
				source[8], source[5], source[2]
		};
	}
}
