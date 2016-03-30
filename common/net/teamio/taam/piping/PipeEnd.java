package net.teamio.taam.piping;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;

public class PipeEnd implements IPipe {

	protected EnumFacing side;
	public final PipeInfo info;
	private final boolean active;

	public PipeEnd(EnumFacing side, PipeInfo info, boolean active) {
		this.side = side;
		this.info = info;
		this.active = active;
	}

	public PipeEnd(EnumFacing side, int capacity, boolean active) {
		this(side, new PipeInfo(capacity), active);
	}

	public EnumFacing getSide() {
		return side;
	}

	public void setSide(EnumFacing side) {
		this.side = side;
	}

	public void writeToNBT(NBTTagCompound tag) {
		info.writeToNBT(tag);
	}

	public void readFromNBT(NBTTagCompound tag) {
		info.readFromNBT(tag);
	}

	/*
	 * IPipe implementation
	 */

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public int getPressure() {
		return info.pressure;
	}

	@Override
	public void setPressure(int pressure) {
		info.pressure = pressure;
	}

	@Override
	public int getSuction() {
		return info.suction;
	}

	@Override
	public void setSuction(int suction) {
		info.suction = suction;
	}

	@Override
	public int addFluid(FluidStack stack) {
		return info.addFluid(stack);
	}
	
	@Override
	public int removeFluid(FluidStack like) {
		return info.removeFluid(like);
	}
	
	@Override
	public int getFluidAmount(FluidStack like) {
		return info.getFluidAmount(like);
	}

	@Override
	public FluidStack[] getFluids() {
		return info.getFluids();
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
