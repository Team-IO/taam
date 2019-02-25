package net.teamio.taam.machines;

import net.minecraft.util.IStringSerializable;

public interface IMachineMetaInfo extends IStringSerializable {
	IMachine createMachine(IMachineWrapper wrapper);

	int metaData();

	String unlocalizedName();

	String[] getTooltip();
}
