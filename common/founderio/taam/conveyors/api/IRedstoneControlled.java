package founderio.taam.conveyors.api;

public interface IRedstoneControlled {
	public static final byte MODE_IGNORE = 0;
	public static final byte MODE_ACTIVE_ON_HIGH = 1;
	public static final byte MODE_ACTIVE_ON_LOW = 2;
	public static final byte MODE_ACTIVE_ON_HIGH_PULSE = 3;
	public static final byte MODE_ACTIVE_ON_LOW_PULSE = 4;

	boolean isPulsingSupported();

	byte getRedstoneMode();

	void setRedstoneMode(byte mode);
}
