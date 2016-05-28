package net.teamio.taam.conveyors.api;

import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.ItemWrapper;

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
	public boolean processItem(IConveyorApplianceHost host, int slot, ItemWrapper wrapper);


}
