package net.teamio.taam.conveyors.api;

import net.minecraft.util.EnumFacing;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.SlotMatrix;

/**
 * Base class for non-conveyor-like slot sets.
 * 
 * @author Oliver Kahrmann
 *
 */
public abstract class ConveyorSlotsStatic implements IConveyorSlots {

	protected SlotMatrix slotMatrix = SlotMatrix.ALL;
	public EnumFacing rotation = EnumFacing.NORTH;
	public double insertMaxY = 0.9;
	public double insertMinY = 0.3;
	
	@Override
	public boolean canSlotMove(int slot) {
		return false;
	}

	@Override
	public boolean isSlotAvailable(int slot) {
		return slotMatrix.isSlotAvailable(slot, rotation);
	}

	@Override
	public int getMovementProgress(int slot) {
		return 0;
	}

	@Override
	public byte getSpeedsteps() {
		return 1;
	}

	@Override
	public EnumFacing getMovementDirection() {
		return EnumFacing.DOWN;
	}

	@Override
	public ItemWrapper getSlot(int slot) {
		return ItemWrapper.EMPTY;
	}

	@Override
	public EnumFacing getNextSlot(int slot) {
		return EnumFacing.DOWN;
	}

	@Override
	public boolean shouldRenderItemsDefault() {
		return false;
	}

	@Override
	public double getInsertMaxY() {
		return insertMaxY;
	}

	@Override
	public double getInsertMinY() {
		return insertMinY;
	}

}