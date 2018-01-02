package net.teamio.taam.piping;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

/**
 * Base interface for a pipe. Not necessarily a tile entity, as there can be
 * multiple pipes/pipe ends per block.
 * <p>
 * Many of these methods are implemented in {@link PipeInfo} to ease
 * implementation.
 *
 * @author Oliver Kahrmann
 */
public interface IPipe {

	/**
	 * Get the pressure currently on the pipe.
	 * Adding fluid to a pipe increases pressure, pulling out fluid decreases it.
	 *
	 * @return A positive or negative integer describing the current pressure level
	 */
	int getPressure();

	/**
	 * Applies pressure or suction without changing the pipe content.
	 * Useful for pumps/tanks pumping dry - and for testing purposes.
	 * @param amount
	 * @return The real amount of pressure applied, in case an upper or lower boundary was reached.
	 */
	int applyPressure(int amount);

	/**
	 * Tries to add the given fluid to the pipe. (Does not change pressure)
	 *
	 * @param stack
	 * @return The actual amount that was added.
	 */
	int addFluid(FluidStack stack);

	/**
	 * Try to remove the given fluid from the pipe (drain). (Does not change
	 * pressure)
	 *
	 * @param like The kind and amount to remove.
	 * @return the actual amount of fluid removed.
	 */
	int removeFluid(FluidStack like);

	/**
	 * returns the amount of a specific fluid in the pipe.
	 *
	 * @param like The kind of fluid to check. Disregard like.amount.
	 * @return The amount of that fluid in the pipe.
	 */
	int getFluidAmount(FluidStack like);

	/**
	 * Get all fluids currently in the pipe.
	 *
	 * @return A list of {@link FluidStack} that are currently in the pipe.
	 */
	List<FluidStack> getFluids();

	/**
	 * Get the capacity of this pipe. This is the total sum, so if there is
	 * specific limits imposed by the implementation, addFluid might fail to add
	 * a fluid while there is still "space".
	 *
	 * @return The total capacity of the pipe.
	 */
	int getCapacity();

	/**
	 * Ask the implementation to get all pipes this pipe is connected to
	 * internally. Regular side-by-side connections are handled differently.
	 *
	 * @return An array of pipes. May return null if there are no connected
	 * pipes. May return a re-usable array, as this method has to be
	 * trimmed for performance!
	 */
	IPipe[] getInternalPipes();

	/**
	 * Asks the pipe if it does connect on the given side.
	 *
	 * @param side
	 * @return
	 */
	boolean isSideAvailable(EnumFacing side);

	/**
	 * Returns true if this pipe is neutral. Neutral pipes will not receive pure pressure transfer, only fluid transfer will affect connected pipes.
	 * Used for fluid handler pipe ends so they don't drain the whole network of pressure.
	 * @return
	 */
	boolean isNeutral();

	BlockPos getPos();

	IBlockAccess getWorld();
}
