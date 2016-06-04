package net.teamio.taam.conveyors;

import net.minecraft.util.EnumFacing;

/**
 * Abstract class for implementing inside a conveyor-like machine, as the
 * movementDirection is overridden.
 * 
 * @author Oliver Kahrmann
 *
 */
public abstract class ConveyorSlotsMoving extends ConveyorSlotsStandard {
	
	
	public ConveyorSlotsMoving() {
		super();
	}

	@Override
	public boolean shouldRenderItemsDefault() {
		return true;
	}

	@Override
	public boolean canSlotMove(int slot) {
		ItemWrapper slotObject = slots[slot];
		return !slotObject.isBlocked();
	};
	
	@Override
	public EnumFacing getMovementDirection() {
		return rotation;
	}
	
	public abstract EnumFacing getNextSlot(int slot);

}
