package net.teamio.taam.util;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.FluidHandlerWrapper;

@SuppressWarnings("deprecation") // Deprecation because of the net.minecraftforge.fluids.IFluidHandler
public final class FluidUtils {
	private FluidUtils() {
		// Util Class
	}

	public static FluidStack getFluidFromItem(ItemStack stack) {
		if(stack == null) {
			return null;
		}
		IFluidHandler fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP);
		if(fluidHandler == null) {
			return null;
		}
		return fluidHandler.drain(Integer.MAX_VALUE, false);
	}

	public static IFluidHandler getFluidHandlerForItem(ItemStack stack) {
		return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP);
	}

	public static IFluidHandler getFluidHandler(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return FluidUtils.getFluidHandler(world.getTileEntity(pos), side);
	}

	public static IFluidHandler getFluidHandler(TileEntity tileEntity, EnumFacing side) {
		if (tileEntity == null) {
			return null;
		}
		IFluidHandler fluidHandler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
		// Wrapper for the old fluid handlers for now - once the old system is removed, this can be removed as well.
		if(fluidHandler == null && tileEntity instanceof net.minecraftforge.fluids.IFluidHandler) {
			fluidHandler = new FluidHandlerWrapper((net.minecraftforge.fluids.IFluidHandler)tileEntity, side);
		}
		return fluidHandler;
	}
}
