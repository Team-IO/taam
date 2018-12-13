package net.teamio.taam.conveyors;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Meta information for appliances
 */
public interface IConveyorApplianceMetaInfo extends IStringSerializable  {
	/**
	 * Return true if this appliance can be oriented in the requested direction.
	 * Used to prevent using an unsupported orientation before placing a block.
	 * Checked in {@link net.teamio.taam.content.conveyors.ItemAppliance}.
	 * @param dir
	 * @return
	 */
	boolean isDirectionSupported(EnumFacing dir);

	int metaData();

	String unlocalizedName();

	void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn);


}
