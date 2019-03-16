package net.teamio.taam.conveyors;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

/**
 * Meta information for appliances
 */
public interface IConveyorApplianceMetaInfo extends IStringSerializable {
	/**
	 * Return true if this appliance can be oriented in the requested direction.
	 * Used to prevent using an unsupported orientation before placing a block.
	 * Checked in {@link net.teamio.taam.content.conveyors.ItemAppliance}.
	 *
	 * @param dir
	 * @return
	 */
	boolean isDirectionSupported(EnumFacing dir);

	int metaData();

	String unlocalizedName();

	String[] getTooltip();

}
