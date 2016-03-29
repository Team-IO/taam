package net.teamio.taam.content.piping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRenderable;
import net.teamio.taam.piping.IPipe;
import net.teamio.taam.piping.IPipeTE;
import net.teamio.taam.piping.PipeInfo;
import net.teamio.taam.piping.PipeUtil;

public class TileEntityPipe extends BaseTileEntity implements IPipe, IPipeTE, ITickable, IRenderable {

	private final PipeInfo info;
	
	private static final List<String> visibleParts = new ArrayList<String>(7);
	
	private int adjacentPipes;
	
	public TileEntityPipe() {
		info = new PipeInfo(500);
	}
	
	@Override
	public List<String> getVisibleParts() {
		visibleParts.clear();
		visibleParts.add("Center_pcmdl");
		if(isSideConnected(EnumFacing.EAST))
			visibleParts.add("FlangeMX_pfmdl");

		if(isSideConnected(EnumFacing.WEST))
			visibleParts.add("FlangePX_pfmdl");
		
		if(isSideConnected(EnumFacing.NORTH))
			visibleParts.add("FlangeMY_pfmdl");
		if(isSideConnected(EnumFacing.SOUTH))
			visibleParts.add("FlangePY_pfmdl");

		if(isSideConnected(EnumFacing.DOWN))
			visibleParts.add("FlangeMZ_pfmdl");
		if(isSideConnected(EnumFacing.UP))
			visibleParts.add("FlangePZ_pfmdl");
		return visibleParts;
	}
	
	public boolean isSideConnected(EnumFacing side) {
		return (adjacentPipes & (1 << side.ordinal())) != 0;
	}
	
	public void renderUpdate() {
		adjacentPipes = 0;
		for(EnumFacing side : EnumFacing.VALUES) {
			IPipe[] pipesOnSide = PipeUtil.getConnectedPipes(worldObj, pos, side);
			if(pipesOnSide != null && pipesOnSide.length != 0) {
				adjacentPipes |= 1 << side.ordinal();
			}
		}
	};
	
	@Override
	public void blockUpdate() {
		super.blockUpdate();
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
	public int getPressure() {
		return info.pressure;
	}

	@Override
	public int addFluid(FluidStack stack) {
		markDirty();
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
			IPipe[] pipesOnSide = PipeUtil.getConnectedPipes(world, pos, side);
			if(pipesOnSide != null) {
				Collections.addAll(pipes, pipesOnSide);
			}
		}
		return pipes.toArray(new IPipe[pipes.size()]);
	}

	@Override
	public IPipe[] getPipesForSide(EnumFacing side) {
		return new IPipe[] { this };
	}

}
