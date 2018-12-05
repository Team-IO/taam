package net.teamio.taam.conveyors.filters;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class HidableSlot extends Slot {

	public final int xPosBackup;
	public final int yPosBackup;

	public HidableSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
		this.xPosBackup = xPosition;
		this.yPosBackup = yPosition;
	}

	public void setEnabled(boolean enable) {
		if (enable) {
			this.xPos = xPosBackup;
			this.yPos = yPosBackup;
		} else {
			this.xPos = -500;
			this.yPos = -500;
		}
	}

}