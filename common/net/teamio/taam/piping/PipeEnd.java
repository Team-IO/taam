package net.teamio.taam.piping;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;

public class PipeEnd extends PipeInfo implements IPipe {

	private final EnumFacing side;
	
	public PipeEnd(int capacity, EnumFacing side) {
		super(capacity);
		this.side = side;
	}

	@Override
	public int getPressure() {
		return pressure;
	}
	
	@Override
	public void setPressure(int pressure) {
		this.pressure = pressure;
	}
	
	@Override
	public void setSuction(int suction) {
		this.suction = suction;	
	}
	
	@Override
	public int getSuction() {
		return suction;
	}
	
	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public FluidStack[] getFluids() {
		return content;
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

	@Override
	public IPipe[] getConnectedPipes(IBlockAccess world, BlockPos pos) {
		TileEntity ent = world.getTileEntity(pos.offset(side));
		if(ent instanceof IPipeTE) {
			IPipeTE pipeTE = (IPipeTE)ent;
			return pipeTE.getPipesForSide(side.getOpposite());
		} else {
			return null;
		}
	}

}
