package net.teamio.taam.piping;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * Pipe end, used in machines to connect to a pipe "network".
 * This delegates any addFluid or getFluids to the IFluidHandler used when creating this pipe end.
 * 
 * @author Oliver Kahrmann
 *
 */
public class PipeEndFluidHandler implements IPipe {
	
	private EnumFacing side;
	private IFluidHandler owner;
	private int pressure;
	private int suction;
	
	public PipeEndFluidHandler(IFluidHandler owner, EnumFacing side) {
		this.owner = owner;
		this.side = side;
	}
	
	public IFluidHandler getOwner() {
		return owner;
	}
	
	@Override
	public int getCapacity() {
		FluidTankInfo[] tankInfo = owner.getTankInfo(side);
		int capacity = 0;
		for(FluidTankInfo tank : tankInfo) {
			capacity += tank.capacity;
		}
		return capacity;
	}
	
	@Override
	public int addFluid(FluidStack stack) {
		if(!owner.canFill(side, stack.getFluid())) {
			return 0;
		}
		return owner.fill(side, stack, true);
	}
	
	@Override
	public int removeFluid(FluidStack like) {
		FluidStack drained = owner.drain(side, like, true);
		if(drained != null) {
			return drained.amount;
		}
		return 0;
	}
	
	@Override
	public int getFluidAmount(FluidStack like) {
		FluidStack drained = owner.drain(side, like, false);
		if(drained != null) {
			return drained.amount;
		}
		return 0;
	}
	
	@Override
	public FluidStack[] getFluids() {
		FluidTankInfo[] tankInfo = owner.getTankInfo(side);
		FluidStack[] content = new FluidStack[tankInfo.length];
		for(int i = 0; i < tankInfo.length; i++) {
			content[i] = tankInfo[i].fluid;
		}
		return content;
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
