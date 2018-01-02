package net.teamio.taam.content;


public interface IRedstoneControlled {
	byte MODE_IGNORE = 0;
	byte MODE_ACTIVE_ON_HIGH = 1;
	byte MODE_ACTIVE_ON_LOW = 2;
	byte MODE_ACTIVE_ON_HIGH_PULSE = 3;
	byte MODE_ACTIVE_ON_LOW_PULSE = 4;

	boolean isPulsingSupported();

	byte getRedstoneMode();

	void setRedstoneMode(byte mode);
}
