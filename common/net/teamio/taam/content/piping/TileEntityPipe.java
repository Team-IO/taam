package net.teamio.taam.content.piping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.piping.IPipe;
import net.teamio.taam.piping.IPipeTE;
import net.teamio.taam.piping.PipeInfo;
import net.teamio.taam.piping.PipeUtil;

public class TileEntityPipe extends BaseTileEntity implements IPipe, IPipeTE, ITickable {

	private final PipeInfo info;
	
	public TileEntityPipe() {
		info = new PipeInfo(500);
	}
	
	@Override
	public void update() {
		PipeUtil.processPipes(this, worldObj, pos);
		
		updateState();
	}
	
	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		info.readFromNBT(tag);
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		info.writeToNBT(tag);
	}
	
	@Override
	public void applyTickPressure(int pressure, int limit) {
		info.applyTickPressure(pressure, limit);
	}

	@Override
	public int getPressure() {
		return info.pressure;
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
	public void setPressure(int pressure) {
		info.pressure = pressure;
	}
	
	@Override
	public void setSuction(int suction) {
		info.suction = suction;	
	}
	
	@Override
	public int getSuction() {
		return info.suction;
	}
	
	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public IPipe[] getConnectedPipes(IBlockAccess world, BlockPos pos) {
		List<IPipe> pipes = new ArrayList<IPipe>(6);
		for(EnumFacing side : EnumFacing.values()) {
			TileEntity ent = world.getTileEntity(pos.offset(side));
			if(ent instanceof IPipeTE) {
				IPipeTE pipeTE = (IPipeTE)ent;
				Collections.addAll(pipes, pipeTE.getPipesForSide(side.getOpposite()));
			}
		}
		return pipes.toArray(new IPipe[pipes.size()]);
	}

	@Override
	public IPipe[] getPipesForSide(EnumFacing side) {
		return new IPipe[] { this };
	}

}
