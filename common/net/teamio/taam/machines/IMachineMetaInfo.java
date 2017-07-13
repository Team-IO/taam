package net.teamio.taam.machines;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import java.util.List;

public interface IMachineMetaInfo extends IStringSerializable {
	IMachine createMachine();

	int metaData();

	String unlocalizedName();

	void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced);
}
