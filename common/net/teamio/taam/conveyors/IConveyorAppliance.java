package net.teamio.taam.conveyors;

import net.minecraft.util.EnumFacing;
import net.teamio.taam.content.IRotatable;

import javax.annotation.Nonnull;

public interface IConveyorAppliance extends IRotatable {

	/**
	 * Asks the appliance to process one slot on the host.
	 *
	 * @param host
	 *            The machine hosting the appliance.
	 * @param slot
	 *            Slot to process (0-8)
	 * @param wrapper
	 *            The wrapper located at that slot. The wrapper will never be
	 *            null or empty at this point.
	 * @return If true is returned, this indicates that something on the
	 *         appliance host was changed an needs to be communicated to clients
	 *         or saved to disk. (TileEntity will be marked as dirty)
	 */
	boolean processItem(IConveyorApplianceHost host, int slot, ItemWrapper wrapper);

	/**
	 * Allows the appliance to override the direction a slot travels next.
	 *
	 * Don't call any {@link IConveyorSlots#getNextSlot(int)} (if available) inside this method!
	 *
	 * @param host The host tile entity for this conveyor appliance.
	 * @param slot The slot currently being processed for which the next slot needs to be determined.
	 * @param wrapper The current content of the slot.
	 * @param beforeOverride The original next slot direction as determined by the host.
	 * @return either the direction passed in to {@code beforeOverride} or a
	 *         changed direction. Never return null!
	 */
	@Nonnull
	EnumFacing overrideNextSlot(IConveyorApplianceHost host, int slot, ItemWrapper wrapper, EnumFacing beforeOverride);
}
