package net.teamio.taam.content.piping;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.piping.IPipe;
import net.teamio.taam.piping.IPipeTE;
import net.teamio.taam.piping.PipeEnd;
import net.teamio.taam.piping.PipeUtil;

public class TileEntityCreativeWell extends BaseTileEntity implements IFluidHandler, IPipeTE, ITickable {

	private final PipeEnd pipeEnd;
	
	public TileEntityCreativeWell() {
		pipeEnd = new PipeEnd(50, EnumFacing.UP);
	}

	@Override
	public void update() {
		pipeEnd.addFluid(new FluidStack(FluidRegistry.WATER, 50));
		pipeEnd.setPressure(20);
		
		PipeUtil.processPipes(pipeEnd, worldObj, pos);
	}
	
	@Override
	public IPipe[] getPipesForSide(EnumFacing side) {
		return new IPipe[] {pipeEnd};
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return new FluidTankInfo[0];
	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		// TODO Auto-generated method stub
		
	}
}
