package net.teamio.taam.util;

import net.minecraft.util.EnumFacing;

/**
 * Util class for encoding/decoding boolean values for {@link EnumFacing}s on
 * bit level.
 * 
 * @author Oliver Kahrmann
 *
 */
public final class FaceBitmap {
	private FaceBitmap() {
		// Util Class
	}

	public static boolean isSideBitSet(byte bitField, EnumFacing side) {
		int oper = 1 << side.ordinal();
		return (bitField & oper) != 0;
	}

	public static byte setSideBit(byte bitField, EnumFacing side) {
		int oper = 1 << side.ordinal();
		return (byte) (bitField | oper);
	}

	public static byte unsetSideBit(byte bitField, EnumFacing side) {
		int oper = 63 - (1 << side.ordinal());
		return (byte) (bitField & oper);
	}
}
