package net.teamio.taam.machines;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public interface IMachineMetaInfo extends IStringSerializable {
	IMachine createMachine(IMachineWrapper wrapper);

	int metaData();

	String unlocalizedName();

	void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn);
}
