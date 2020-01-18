package net.teamio.taam.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public final class FluidUtils {
	private FluidUtils() {
		// Util Class
	}

	public static FluidStack getFluidFromItem(ItemStack stack) {
		if (stack == null) {
			return null;
		}
		IFluidHandler fluidHandler = getFluidHandlerForItem(stack);
		if (fluidHandler == null) {
			return null;
		}
		return fluidHandler.drain(Integer.MAX_VALUE, false);
	}

	public static IFluidHandlerItem getFluidHandlerForItem(ItemStack stack) {
		return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP);
	}

	public static IFluidHandler getFluidHandler(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return getFluidHandler(world.getTileEntity(pos), side);
	}

	public static IFluidHandler getFluidHandler(ICapabilityProvider tileEntity, EnumFacing side) {
		if (tileEntity == null) {
			return null;
		}
		IFluidHandler fluidHandler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
		if (fluidHandler != null) return fluidHandler;

		// Fallback if someone directly implemented the capability interface - not recommended, this is just for compatibility
		if (tileEntity instanceof IFluidHandler) {
			return (IFluidHandler) tileEntity;
		}
		return null;
	}

	/**
	 * Calculate fill level of a tank in percent.
	 *
	 * @param tank A tank implementation. Only {@link IFluidTank#getFluidAmount()} and {@link IFluidTank#getCapacity()} are used.
	 * @return Percentage, 0..1
	 */
	public static float getTankFillLevel(IFluidTank tank) {
		int content = tank.getFluidAmount();
		int capacity = tank.getCapacity();
		if (capacity <= 0) {
			return 0;
		}
		if (content >= capacity) {
			return 1;
		}
		return content / (float) capacity;
	}

	/**
	 * Calculate the comparator value based on the given percentage.
	 *
	 * @param fillLevel Percentage, 0..1
	 * @return A redstone level
	 */
	public static int getComparatorValueFromFillLevel(float fillLevel) {
		return MathHelper.floor(fillLevel * 14) + (fillLevel > 0 ? 1 : 0);
	}
}
