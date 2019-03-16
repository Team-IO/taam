package net.teamio.taam.piping;

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

import java.util.ArrayList;
import java.util.List;

public final class PipeUtil {
	private PipeUtil() {
		// Util Class
	}

	/**
	 * Returns a pipe connected to a side of a block. Looks for a TileEntity
	 * in the direction of side, then asks that tile for a pipe in direction of
	 * side.getOpposite().
	 * <p>
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
		if (ent == null) {
			return null;
		}
		EnumFacing opposite = side.getOpposite();
		IPipe candidate = TaamUtil.getCapability(Taam.CAPABILITY_PIPE, ent, opposite);
		if (candidate != null && candidate.isSideAvailable(opposite)) {
			return candidate;
		}
		return null;
	}

	/**
	 * Returns a pipe connected to a side of a block. Looks for a TileEntity in
	 * the direction of side, then asks that tile for a pipe in direction of
	 * side.getOpposite().
	 * <p>
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
		if (ent == null) {
			return null;
		}
		EnumFacing opposite = side.getOpposite();
		return TaamUtil.getCapability(Taam.CAPABILITY_PIPE, ent, opposite);
	}

	private static final ThreadLocal<ArrayList<FluidStack>> pipeFluidsList = new ThreadLocal<ArrayList<FluidStack>>() {
		@Override
		protected ArrayList<FluidStack> initialValue() {
			return new ArrayList<FluidStack>(6);
		}
	};

	/**
	 * Transfers content between pipes, up to the specified amount.
	 *
	 * @param source
	 * @param destination
	 * @param amount
	 * @return The actual amount of content transferred
	 */
	public static int transferContent(IPipe source, IPipe destination, int amount) {
		int share = amount;
		List<FluidStack> pipeFluids = pipeFluidsList.get();

		// Get fluids from pipe
		pipeFluids.clear();
		List<FluidStack> fromPipe = source.getFluids();
		if (fromPipe == null) {
			Log.warn("Pipe returned null fluid array, requested for processing at {} in {}", source.getPos(), source.getWorld());
		} else {
			pipeFluids.addAll(fromPipe);
		}

		for (FluidStack fs : pipeFluids) {
			if (fs == null) {
				continue;
			}
			FluidStack transfer = fs.copy();
			transfer.amount = Math.min(transfer.amount, share);

			// "Simulate" drain
			int simuDrain = source.getFluidAmount(transfer);
			// Limit to what we can actually pull
			if (simuDrain < transfer.amount) {
				transfer.amount = simuDrain;
			}
			int actualFill = destination.addFluid(transfer);
			// Limit to what was actually pushed into the next pipe
			if (actualFill < simuDrain) {
				transfer.amount = actualFill;
			}
			// Remove fluid from previous pipe
			int actualDrain = source.removeFluid(transfer);
			if (actualDrain != actualFill) {
				// This should not happen.
				Log.error("Transferring from pipe {} to pipe {} yielded inconsistent results (actual drain != actual fill). Simulated drain: {} Fill: {} Actual Drain: {}. Fluid was potentially lost or duplicated. This is an issue.",
						share, destination, simuDrain, actualFill, actualDrain);
			}
			share -= actualFill;
			if (share <= 0) {
				break;
			}
		}
		return amount - share;
	}

	/**
	 * The default interaction for tanks, usually fills/drains a selected fluid container.
	 *
	 * @param player
	 * @param tank
	 * @return
	 */
	public static boolean defaultPlayerInteraction(EntityPlayer player, IFluidTank tank) {

		Log.debug("Beginning fluid interaction.");
		ItemStack playerStack = player.inventory.getCurrentItem();
		ItemStack handlingStack = playerStack;
		if (InventoryUtils.isEmpty(handlingStack)) {
			return false;
		}
		if(handlingStack.stackSize > 1) {
			handlingStack = InventoryUtils.copyStack(handlingStack, 1);
		}

		IFluidHandler itemFH = FluidUtils.getFluidHandlerForItem(handlingStack);

		boolean success = false;
		boolean modifyInventory = !player.capabilities.isCreativeMode;

		if (itemFH != null) {
			FluidStack inTank = tank.getFluid();
			if (inTank != null) {
				Log.debug("Attempting to fill {}x{} into item.", inTank.amount, inTank.getFluid());
				// Fill into the item
				int fill = itemFH.fill(inTank, modifyInventory);
				Log.debug("Filled {} into item.", fill);
				if (fill > 0) {
					// Drain from the tank
					FluidStack drained = tank.drain(fill, true);
					// Failsave check
					if (drained == null || drained.amount != fill) {
						Log.error("Error filling item. Drained amount {} was not the expected amount {} filled into the item. This should not happen.", drained == null ? 0 : drained.amount, fill);
					}
					success = true;
				} else {
					Log.debug("Attempting to drain {}x{} from item.", inTank.amount, inTank.getFluid());
					int capa = tank.getCapacity();
					if (inTank.amount < capa) {
						// Get the remaining capacity
						FluidStack toDrain = inTank.copy();
						toDrain.amount = capa - inTank.amount;
						// Drain maximum of that from the item
						FluidStack drain = itemFH.drain(toDrain, modifyInventory);
						if (drain != null && drain.amount > 0) {
							Log.debug("Drained {}x{} from item.", drain.amount, drain.getFluid());
							// Fill into the tank
							int filled = tank.fill(drain, true);
							// Failsave check
							if (filled != drain.amount) {
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
				FluidStack drain = itemFH.drain(tank.getCapacity(), modifyInventory);
				if (drain != null && drain.amount > 0) {
					Log.debug("Drained {}x{} from item.", drain.amount, drain.getFluid());
					// Fill into the tank
					int filled = tank.fill(drain, true);
					// Failsave check
					if (filled != drain.amount) {
						Log.error("Error draining item. Filled amount {} was not the expected amount {} drained from the item. This should not happen.", filled, drain.amount);
					}
					success = true;
				} else {
					Log.debug("Drained nothing from item.");
				}
			}
		}

		if(success && modifyInventory) {
			// Adjust stack that was clicked
			playerStack.stackSize--;
			// Drop empty or filled container into inventory
			if(!InventoryUtils.isEmpty(handlingStack)) {
				InventoryUtils.tryDropToInventory(player, handlingStack, player.getPosition());
			}
		}
		return success;
	}
}
