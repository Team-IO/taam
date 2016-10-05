package net.teamio.taam.util;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

@SuppressWarnings("deprecation") // Deprecation because of the net.minecraftforge.fluids.IFluidHandler
public final class FluidUtils {
	private FluidUtils() {
		// Util Class
	}

	public static FluidStack getFluidFromItem(ItemStack stack) {
		if(stack == null) {
			return null;
		}
		/*IFluidHandler fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP);
		if(fluidHandler == null) {
			return null;
		}
		return fluidHandler.drain(Integer.MAX_VALUE, false);*/
		return null; //FIXME Make another wrapper!
	}

	public static IFluidHandler getFluidHandlerForItem(ItemStack stack) {
		return null; //FIXME Make another wrapper!
		//return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP);
	}

	public static IFluidHandler getFluidHandler(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return FluidUtils.getFluidHandler(world.getTileEntity(pos), side);
	}

	public static IFluidHandler getFluidHandler(TileEntity tileEntity, EnumFacing side) {
		if(tileEntity instanceof IFluidHandler) {
			return (IFluidHandler)tileEntity;
		}
		return null;
	}
}
