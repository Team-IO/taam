package net.teamio.taam.piping;

import net.minecraft.util.EnumFacing;

/**
 * Base interface for all tile entities that have pipe connectors.
 * 
 * @author Oliver Kahrmann
 *
 */
public interface IPipeTE {
	public IPipe[] getPipesForSide(EnumFacing side);
}
