package net.teamio.taam.piping;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.util.FluidUtils;
import net.teamio.taam.util.InventoryUtils;
import net.teamio.taam.util.TaamUtil;

public final class PipeUtil {
	private PipeUtil() {
		// Util Class
	}

	/**
	 * Factor used when transferring fluid between pipes, tanks, etc.
	 */
	public static final float pipeTransferFactor = 0.5f;

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
	 * Returns a pipe connected to a side of a block. Looks for a TileEntity
	 * in the direction of side, then asks that tile for a pipe in direction of
	 * side.getOpposite().
	 *
	 * Checks for blocked/disabled pipes. To get any pipe, regardless of blocked/disabled state, use {@link #getPipe(IBlockAccess, BlockPos, EnumFacing)}.
	 *
	 * @param world
	 * @param pos
	 * @param side
	 * @return An IPipe or null.
	 */
	public static IPipe getConnectedPipe(IBlockAccess world, BlockPos pos, EnumFacing side) {
		BlockPos offsetPos = pos.offset(side);
		TileEntity ent = world.getTileEntity(offsetPos);
		if(ent == null) {
			return null;
		}
		EnumFacing opposite = side.getOpposite();
		IPipe candidate = TaamUtil.getCapability(Taam.CAPABILITY_PIPE, ent, opposite);
		if(candidate != null && candidate.isSideAvailable(opposite)) {
			return candidate;
		}
		return null;
	}

	/**
	 * Returns a pipe connected to a side of a block. Looks for a TileEntity in
	 * the direction of side, then asks that tile for a pipe in direction of
	 * side.getOpposite().
	 *
	 * Does not check for blocked/disabled pipes! For that, use
	 * {@link #getConnectedPipe(IBlockAccess, BlockPos, EnumFacing)}.
	 *
	 * @param world
	 * @param pos
	 * @param side
	 * @return An IPipe or null.
	 */
	public static IPipe getPipe(IBlockAccess world, BlockPos pos, EnumFacing side) {
		BlockPos offsetPos = pos.offset(side);
		TileEntity ent = world.getTileEntity(offsetPos);
		if(ent == null) {
			return null;
		}
		EnumFacing opposite = side.getOpposite();
		return TaamUtil.getCapability(Taam.CAPABILITY_PIPE, ent, opposite);
	}

	private static final ThreadLocal<ArrayList<IPipe>> connected = new ThreadLocal<ArrayList<IPipe>>() {
		@Override
		protected ArrayList<IPipe> initialValue() {
			return new ArrayList<IPipe>(6);
		}
	};

	public static void processPipes(IPipe pipe, IBlockAccess world, BlockPos pos) {

		if (pipe == null) {
			Log.warn("null pipe requested for processing at {} in {}", pos, world);
		}

		ArrayList<IPipe> connected = PipeUtil.connected.get();

		connected.clear();

		IPipe[] internal = pipe.getInternalPipes(world, pos);

		if(internal != null) {
			for(IPipe intPipe : internal) {
				if(intPipe != null) {
					connected.add(intPipe);
				}
			}
		}

		for(EnumFacing side : EnumFacing.VALUES) {
			if(pipe.isSideAvailable(side)) {
				IPipe external = PipeUtil.getConnectedPipe(world, pos, side);
				if(external != null) {
					connected.add(external);
				}
			}
		}

		if (connected.isEmpty()) {
			return;
		}
		/*
		 * Update Pressure from surrounding pipes
		 */

		if (!pipe.isActive()) {
			int maxPressure = 0;
			int maxSuction = 0;

			for (int i = 0; i < connected.size(); i++) {
				IPipe other = connected.get(i);
				if(other == null) {
					continue;
				}
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
		List<FluidStack> pipeFluids = pipe.getFluids();
		if(pipeFluids == null) {
			Log.warn("Pipe returned null fluid list, requested for processing at {} in {}", pos, world);
		}
		for (FluidStack fs : pipeFluids) {
			if (fs == null) {
				continue;
			}
			totalAmount += fs.amount;
		}
		if (totalAmount == 0) {
			return;
		}

		int effectivePressure = pipe.getPressure() == 0 ? -pipe.getSuction() : pipe.getPressure();

		/*
		 * Transfer based on the share
		 */

		for (int i = 0; i < connected.size(); i++) {
			IPipe other = connected.get(i);
			if(other == null) {
				continue;
			}
			int otherPressure = other.getPressure() == 0 ? -other.getSuction() : other.getPressure();
			if(effectivePressure <= otherPressure) {
				// No transfer without pressure
				continue;
			}

			int share = (int) Math.ceil(totalAmount * pipeTransferFactor);
			pipeFluids = pipe.getFluids();
			if(pipeFluids == null) {
				Log.warn("Pipe returned null fluid array, requested for processing at {} in {}", pos, world);
			}
			for (FluidStack fs : pipeFluids) {
				if(fs == null) {
					continue;
				}
				FluidStack transfer = fs.copy();
				transfer.amount = Math.min(transfer.amount, share);

				// "Simulate" drain
				int simuDrain = pipe.getFluidAmount(transfer);
				// Limit to what we can actually pull
				if(simuDrain < transfer.amount) {
					transfer.amount = simuDrain;
				}
				int actualFill = other.addFluid(transfer);
				// Limit to what was actually pushed into the next pipe
				if(actualFill < simuDrain) {
					transfer.amount = actualFill;
				}
				// Remove fluid from previous pipe
				int actualDrain = pipe.removeFluid(transfer);
				if(actualDrain != actualFill) {
					// This should not happen.
					Log.error("Transferring from pipe {} to pipe {} yielded inconsistent results (actual drain != actual fill). Simulated drain: {} Fill: {} Actual Drain: {}. Fluid was potentially lost or duplicated. This is an issue.",
							pipe, other, simuDrain, actualFill, actualDrain);
				}
				share -= actualFill;
				if (share <= 0) {
					break;
				}
			}
		}
	}

	/**
	 * The default interaction for tanks, usually fills/drains a selected fluid container.
	 * @param player
	 * @param tank
	 * @return
	 */
	public static boolean defaultPlayerInteraction(EntityPlayer player, IFluidTank tank) {

		Log.debug("Beginning fluid interaction.");
		ItemStack playerStack = player.inventory.getCurrentItem();
		ItemStack handlingStack = playerStack;
		if(handlingStack == null) {
			return false;
		}
		boolean isPartialStack = false;
		if(handlingStack.stackSize > 1) {
			isPartialStack = true;
			handlingStack = InventoryUtils.copyStack(handlingStack, 1);
		}

		IFluidHandler itemFH = FluidUtils.getFluidHandlerForItem(handlingStack);

		boolean success = false;

		if(itemFH != null) {
			FluidStack inTank = tank.getFluid();
			if(inTank != null) {
				Log.debug("Attempting to fill {}x{} into item.", inTank.amount, inTank.getFluid());
				// Fill into the item
				int fill = itemFH.fill(inTank, true);
				Log.debug("Filled {} into item.", fill);
				if(fill > 0) {
					// Drain from the tank
					FluidStack drained = tank.drain(fill, true);
					// Failsave check
					if(drained == null || drained.amount != fill) {
						Log.error("Error filling item. Drained amount {} was not the expected amount {} filled into the item. This should not happen.", drained == null ? 0 : drained.amount, fill);
					}
					success = true;
				} else {
					Log.debug("Attempting to drain {}x{} from item.", inTank.amount, inTank.getFluid());
					int capa = tank.getCapacity();
					if(inTank.amount < capa) {
						// Get the remaining capacity
						FluidStack toDrain = inTank.copy();
						toDrain.amount = capa - inTank.amount;
						// Drain maximum of that from the item
						FluidStack drain = itemFH.drain(toDrain, true);
						if(drain != null && drain.amount > 0) {
							Log.debug("Drained {}x{} from item.", drain.amount, drain.getFluid());
							// Fill into the tank
							int filled = tank.fill(drain, true);
							// Failsave check
							if(filled != drain.amount) {
								Log.error("Error draining item. Filled amount {} was not the expected amount {} drained from the item. This should not happen.", filled, drain.amount);
							}
							success = true;
						} else {
							Log.debug("Drained nothing from item.");
						}
					}
				}
			} else {
				Log.debug("Attempting to drain anything from item.");
				// Drain maximum of tank capacity from the item
				FluidStack drain = itemFH.drain(tank.getCapacity(), true);
				if(drain != null && drain.amount > 0) {
					Log.debug("Drained {}x{} from item.", drain.amount, drain.getFluid());
					// Fill into the tank
					int filled = tank.fill(drain, true);
					// Failsave check
					if(filled != drain.amount) {
						Log.error("Error draining item. Filled amount {} was not the expected amount {} drained from the item. This should not happen.", filled, drain.amount);
					}
					success = true;
				} else {
					Log.debug("Drained nothing from item.");
				}
			}
		}

		if(success && isPartialStack) {
			playerStack.stackSize--;
			InventoryUtils.tryDropToInventory(player, handlingStack, player.getPosition());
		}
		return success;
	}
}
