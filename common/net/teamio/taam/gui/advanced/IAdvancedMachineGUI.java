package net.teamio.taam.gui.advanced;

import net.minecraft.world.IWorldNameable;

/**
 * Capability interface for the advanced gui.
 * Machines expose an implementation of this via {@link net.teamio.taam.Taam#CAPABILITY_ADVANCED_GUI}
 * to configure the apps they support.
 *
 * @author Oliver Kahrmann
 */
public interface IAdvancedMachineGUI extends IWorldNameable {
	void setup(ContainerAdvancedMachine container);

	void markDirty();
}
