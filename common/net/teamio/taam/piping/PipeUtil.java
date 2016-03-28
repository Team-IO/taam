package net.teamio.taam.piping;

import java.util.Collections;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import scala.actors.threadpool.Arrays;

public final class PipeUtil {
	private PipeUtil() {
		// Util Class
	}

	public static int calculateAppliedPressure(int current, int change, int limit) {
		if (change > 0) {
			int diff = limit - current;
			if (diff > 0) {
				return current + Math.min(change, diff);
			}
		} else {
			int diff = limit - current;
			if (diff < 0) {
				return current + Math.max(change, diff);
			}
		}
		return current;
	}

	/**
	 * Returns all pipes connected to a side of a block. (Looks for an IPipeTE
	 * in the direction of side, the asks that block for pipes in direction of
	 * side.getOpposite().
	 * 
	 * @param world
	 * @param pos
	 * @param side
	 * @return An array of IPipe (may be empty) or null.
	 */
	public static IPipe[] getConnectedPipes(IBlockAccess world, BlockPos pos, EnumFacing side) {
		TileEntity ent = world.getTileEntity(pos.offset(side));
		if (ent instanceof IPipeTE) {
			IPipeTE pipeTE = (IPipeTE) ent;
			return pipeTE.getPipesForSide(side.getOpposite());
		}
		return null;
	}

	public static void processPipes(IPipe pipe, IBlockAccess world, BlockPos pos) {

		IPipe[] connected = pipe.getConnectedPipes(world, pos);
		if (connected == null || connected.length == 0) {
			return;
		}
		/*
		 * Update Pressure from surrounding pipes
		 */

		if (!pipe.isActive()) {
			int maxPressure = 0;
			int maxSuction = 0;

			for (int i = 0; i < connected.length; i++) {
				IPipe other = connected[i];
				int otherPressure = other.getPressure();

				if (otherPressure > maxPressure) {
					maxPressure = otherPressure;
				}
				int otherSuction = other.getSuction();
				if (otherSuction > maxSuction) {
					maxSuction = otherSuction;
				}
			}
			pipe.setPressure(Math.max(0, maxPressure - 1));
			pipe.setSuction(Math.max(0, maxSuction - 2));
		}

		/*
		 * Calculate total amount of fluid
		 */

		int totalAmount = 0;
		for (FluidStack fs : pipe.getFluids()) {
			if (fs == null) {
				continue;
			}
			totalAmount += fs.amount;
		}
		if (totalAmount == 0) {
			return;
		}

		int effectivePressure = pipe.getPressure() - pipe.getSuction();

		/*
		 * Transfer based on the share
		 */

		for (int i = 0; i < connected.length; i++) {
			IPipe other = connected[i];
			int otherPressure = other.getPressure() - other.getSuction();
			float factor = 0;
			if (effectivePressure > otherPressure + 10) {
				factor = 1;
			} else if (effectivePressure > otherPressure + 5) {
				factor = 0.75f;
			} else if (effectivePressure > otherPressure) {
				factor = 0.5f;
			}

			int share = (int) Math.ceil(totalAmount * factor);
			for (FluidStack fs : pipe.getFluids()) {
				// TODO: Simulate Drain.
				FluidStack transfer = fs;
				if (fs.amount > share) {
					transfer = fs.copy();
					transfer.amount = share;
				}
				int transferred = connected[i].addFluid(transfer);
				// TODO: Drain.
				fs.amount -= transferred;
				share -= transferred;
				if (share <= 0) {
					break;
				}
			}
		}
	}
}
