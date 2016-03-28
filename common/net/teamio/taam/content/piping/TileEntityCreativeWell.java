package net.teamio.taam.content.piping;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.piping.IPipe;
import net.teamio.taam.piping.IPipeTE;
import net.teamio.taam.piping.PipeEnd;
import net.teamio.taam.piping.PipeUtil;

public class TileEntityCreativeWell extends BaseTileEntity implements IFluidHandler, IPipeTE, ITickable, IWorldInteractable {

	private final PipeEnd[] pipeEnds;

	public TileEntityCreativeWell() {
		pipeEnds = new PipeEnd[6];
		for (EnumFacing side : EnumFacing.VALUES) {
			int index = side.ordinal();
			pipeEnds[index] = new PipeEnd(50, side);
			pipeEnds[index].setPressure(20);
		}
	}

	@Override
	public void update() {
		for (EnumFacing side : EnumFacing.VALUES) {
			int index = side.ordinal();
			pipeEnds[index].addFluid(new FluidStack(FluidRegistry.WATER, 50));
			PipeUtil.processPipes(pipeEnds[index], worldObj, pos);
		}

	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * IPipeTE implementation
	 */
	
	@Override
	public IPipe[] getPipesForSide(EnumFacing side) {
		int index = side.ordinal();
		return new IPipe[] { pipeEnds[index] };
	}

	/*
	 * IFluidHandler implementation
	 */
	
	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		if(resource != null && resource.getFluid() == FluidRegistry.WATER) {
			return resource;
		} else {
			return null;
		}
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		return new FluidStack(FluidRegistry.WATER, maxDrain);
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return fluid == FluidRegistry.WATER;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return new FluidTankInfo[0];
	}
	
	/*
	 * IWorldInteractable implementation
	 */
	
	@Override
	public boolean onBlockActivated(World world, EntityPlayer player, boolean hasWrench, EnumFacing side, float hitX,
			float hitY, float hitZ) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean onBlockHit(World world, EntityPlayer player, boolean hasWrench) {
		return false;
	}
}
