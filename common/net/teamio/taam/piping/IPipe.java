package net.teamio.taam.piping;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;

/**
 * Base interface for a pipe. Not necessarily a tile entity, as there can be
 * multiple pipes/pipe ends per block.
 *
 * Many of these methods are implemented in {@link PipeInfo} to ease
 * implementation.
 *
 * @author Oliver Kahrmann
 *
 */
public interface IPipe {

	/**
	 * Get the pressure currently on the pipe. Effective pressure is calculated
	 * using getPressure and getSuction.
	 *
	 * @return Should be > 0 and capped internally.
	 */
	int getPressure();

	/**
	 * Sets the pipe pressure. This method is also used by the pipe logic.
	 *
	 * @param pressure
	 *            Should be > 0 and capped internally.
	 */
	void setPressure(int pressure);

	/**
	 * Get the suction currently on the pipe. Effective pressure is calculated
	 * using getPressure and getSuction.
	 *
	 * @return
	 */
	int getSuction();

	/**
	 * Sets the pipe suction. This method is also used by the pipe logic. Should
	 * be > 0 and capped internally.
	 *
	 * @param suction
	 */
	void setSuction(int suction);

	/**
	 * Check if the pipe is active. On active pipes, the pressure will not be
	 * changed by the pipe logic. Use it for parts that affect the pressure in
	 * pipes (i.e. machines, tanks, pumps, ...).
	 *
	 * @return
	 */
	boolean isActive();

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
	 * @param like
	 *            The kind and amount to remove.
	 * @return the actual amount of fluid removed.
	 */
	int removeFluid(FluidStack like);

	/**
	 * returns the amount of a specific fluid in the pipe.
	 *
	 * @param like
	 *            The kind of fluid to check. Disregard like.amount.
	 * @return The amount of that fluid in the pipe.
	 */
	int getFluidAmount(FluidStack like);

	/**
	 * Get all fluids currently in the pipe.
	 *
	 * @return An array of {@link FluidStack} that are currently in the pipe.
	 */
	FluidStack[] getFluids();

	/**
	 * Get the capacity of this pipe. This is the total sum, so if there is
	 * specific limits imposed by the implementation, addFluid might fail to add
	 * a fluid while there is still "space".
	 *
	 * @return The total capacity of the pipe.
	 */
	int getCapacity();

	/**
	 * Ask the implementation to get all pipes this pipe is connected to. The do
	 * not necessarily have to be side-by-side, nor do they have to be in
	 * different blocks.
	 *
	 * @param world
	 *            World of this pipe.
	 * @param pos
	 *            Position of this pipe.
	 * @return An array of pipes. May return null if there are no connected
	 *         pipes.
	 */
	IPipe[] getInternalPipes(IBlockAccess world, BlockPos pos);

	/**
	 * Asks the pipe if it does connect on the given side.
	 *
	 * @param side
	 * @return
	 */
	boolean isSideAvailable(EnumFacing side);
}
