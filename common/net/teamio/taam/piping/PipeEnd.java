package net.teamio.taam.piping;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.taam.Config;

import java.util.List;

public class PipeEnd implements IPipe {
	protected final IPipePos pos;
	protected EnumFacing side;
	public final PipeInfo info;
	public boolean occluded;

	public PipeEnd(IPipePos pos, EnumFacing side, PipeInfo info) {
		this.pos = pos;
		this.side = side;
		this.info = info;
	}

	public PipeEnd(IPipePos pos, EnumFacing side, int capacity) {
		this(pos, side, new PipeInfo(capacity));
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
	public int getPressure() {
		return info.pressure;
	}

	@Override
	public int applyPressure(int amount) {
		return info.applyPressure(amount, Config.pl_pipe_max_pressure);
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
	public List<FluidStack> getFluids() {
		return info.getFluids();
	}

	@Override
	public int getCapacity() {
		return info.capacity;
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
		return pos.getPipePos();
	}

	@Override
	public IBlockAccess getWorld() {
		if (pos == null) {
			return null;
		}
		return pos.getPipeWorld();
	}

	@Override
	public boolean isNeutral() {
		return false;
	}

}
