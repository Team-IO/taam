package net.teamio.taam.conveyors;

import net.minecraft.util.EnumFacing;

/**
 * Base class for non-conveyor-like slot sets.
 *
 * @author Oliver Kahrmann
 *
 */
public abstract class ConveyorSlotsBase implements IConveyorSlots {

	protected SlotMatrix slotMatrix = SlotMatrix.ALL;
	public EnumFacing rotation = EnumFacing.NORTH;
	public double insertMaxY = 0.9;
	public double insertMinY = 0.3;
	public float verticalPosition = 0.51f;

	private ConveyorSlotsItemHandler handler;

	public ConveyorSlotsItemHandler getItemHandler(EnumFacing side) {
		if(handler == null || handler.slot != ConveyorUtil.getSlot(side)) {
			handler = new ConveyorSlotsItemHandler(this, side);
		}
		return handler;
	}

	/**
	 * This method should be called by implementations when content changes,
	 * that needs to be saved / updated.
	 *
	 * Implementations further down can then call their own logic in here.
	 */
	public void onChangeHook() {
	}

	@Override
	public boolean canSlotMove(int slot) {
		return false;
	}

	@Override
	public boolean isSlotAvailable(int slot) {
		return slotMatrix.isSlotAvailable(slot, rotation);
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

	@Override
	public float getVerticalPosition(int slot) {
		return verticalPosition;
	}

}