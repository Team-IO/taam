package net.teamio.taam.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public final class FluidUtils {
	private FluidUtils() {
		// Util Class
	}

	public static FluidStack getFluidFromItem(ItemStack stack) {
		if(stack == null) {
			return null;
		}
		IFluidHandler fluidHandler = getFluidHandlerForItem(stack);
		if(fluidHandler == null) {
			return null;
		}
		return fluidHandler.drain(Integer.MAX_VALUE, false);
	}

	public static IFluidHandler getFluidHandlerForItem(ItemStack stack) {
		return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP);
	}

	public static IFluidHandler getFluidHandler(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return getFluidHandler(world.getTileEntity(pos), side);
	}

	// Deprecation because of the net.minecraftforge.fluids.IFluidHandler
	@SuppressWarnings("deprecation")
	public static IFluidHandler getFluidHandler(ICapabilityProvider tileEntity, EnumFacing side) {
		if (tileEntity == null) {
			return null;
		}
		IFluidHandler fluidHandler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);

		// Fallback if someone directly implemented the capability interface - not recommended, this is just for compatibility
		if(fluidHandler == null && tileEntity instanceof IFluidHandler) {
			fluidHandler = (IFluidHandler)tileEntity;
		}
		return fluidHandler;
	}
}
