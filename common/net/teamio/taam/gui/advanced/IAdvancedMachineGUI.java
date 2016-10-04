package net.teamio.taam.gui.advanced;

import net.minecraft.world.IWorldNameable;

public interface IAdvancedMachineGUI extends IWorldNameable {
	void setup(ContainerAdvancedMachine container);

	void markDirty();
}
