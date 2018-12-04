package net.teamio.taam.piping;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.teamio.taam.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Pipe end, used in machines to connect to a pipe "network". This delegates any
 * addFluid or getFluids to the IFluidHandler used when creating this pipe end.
 *
 * Always has pressure 0, it remains "neutral".
 *
 * @author Oliver Kahrmann
 */
public class PipeEndFluidHandler implements IPipe {
	protected final IPipePos pos;
	private EnumFacing side;
	private final IFluidHandler fluidHandler;
	public boolean occluded;

	public PipeEndFluidHandler(IPipePos pos, IFluidHandler fluidHandler, EnumFacing side) {
		this.pos = pos;
		this.fluidHandler = fluidHandler;
		this.side = side;
	}

	public IFluidHandler getFluidHandler() {
		return fluidHandler;
	}

	public EnumFacing getSide() {
		return side;
	}

	public void setSide(EnumFacing side) {
		this.side = side;
	}

	@Override
	public int getCapacity() {
		IFluidTankProperties[] tankInfo = fluidHandler.getTankProperties();
		int capacity = 0;
		for (IFluidTankProperties tank : tankInfo) {
			capacity += tank.getCapacity();
		}
		return capacity;
	}

	@Override
	public int addFluid(FluidStack stack) {
		if(stack == null || stack.amount == 0) {
			return 0;
		}
		if(fluidHandler.fill(stack, false) == 0) {
			return 0;
		} else {
			return fluidHandler.fill(stack, true);
		}
	}

	@Override
	public int removeFluid(FluidStack like) {
		if(like == null || like.amount == 0) {
			return 0;
		}
		FluidStack drained = fluidHandler.drain(like, true);
		return drained == null ? 0 : drained.amount;
	}

	@Override
	public int getFluidAmount(FluidStack like) {
		if(like == null) {
			return 0;
		}
		IFluidTankProperties[] tankInfo = fluidHandler.getTankProperties();
		int amount = 0;
		for (IFluidTankProperties tank : tankInfo) {
			FluidStack contents = tank.getContents();
			if (contents != null && contents.isFluidEqual(like)) {
				amount += contents.amount;
			}
		}
		return amount;
	}

	@Override
	public List<FluidStack> getFluids() {
		IFluidTankProperties[] tankInfo = fluidHandler.getTankProperties();
		ArrayList<FluidStack> content = new ArrayList<FluidStack>(tankInfo.length);
		for (int i = 0; i < tankInfo.length; i++) {
			FluidStack inTank = tankInfo[i].getContents();
			if (inTank != null) {
				content.add(inTank);
			}
		}
		return content;
	}

	@Override
	public int getPressure() {
		return 0;
	}

	@Override
	public int applyPressure(int amount) {
		return 0;
	}

	@Override
	public IPipe[] getInternalPipes() {
		return null;
	}

	@Override
	public boolean isSideAvailable(EnumFacing side) {
		return !occluded && this.side == side;
	}

	@Override
	public BlockPos getPos() {
		if (pos == null) {
			return BlockPos.ORIGIN;
		}
		return pos.getPos();
	}

	@Override
	public IBlockAccess getWorld() {
		if (pos == null) {
			return null;
		}
		return pos.getWorld();
	}

	@Override
	public boolean isNeutral() {
		return true;
	}
}
