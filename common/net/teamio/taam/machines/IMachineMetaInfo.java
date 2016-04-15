package net.teamio.taam.machines;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IMachineMetaInfo {
	IMachine createMachine();

	int metaData();

	String unlocalizedName();

	void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced);
}
