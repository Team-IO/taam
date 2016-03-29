package net.teamio.taam.piping;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;

public class PipeEndPump implements IPipe {
	private EnumFacing side;
	private final PipeInfo info;

	private int pressure;
	private int suction;

	public PipeEndPump(EnumFacing side, PipeInfo pipeInfo) {
		this.side = side;
		this.info = pipeInfo;
	}

	public EnumFacing getSide() {
		return side;
	}

	public void setSide(EnumFacing side) {
		this.side = side;
	}

	/*
	 * IPipe implementation
	 */

	@Override
	public int getPressure() {
		return pressure;
	}

	@Override
	public void setPressure(int pressure) {
		this.pressure = pressure;
	}

	@Override
	public int getSuction() {
		return suction;
	}

	@Override
	public void setSuction(int suction) {
		this.suction = suction;
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public int addFluid(FluidStack stack) {
		return info.addFluid(stack);
	}

	@Override
	public FluidStack[] getFluids() {
		return info.content;
	}

	@Override
	public int getCapacity() {
		return info.capacity;
	}

	@Override
	public IPipe[] getConnectedPipes(IBlockAccess world, BlockPos pos) {
		return PipeUtil.getConnectedPipes(world, pos, side);
	}
}
