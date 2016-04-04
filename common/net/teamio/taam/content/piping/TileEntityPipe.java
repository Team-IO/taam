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
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRenderable;
import net.teamio.taam.piping.IPipe;
import net.teamio.taam.piping.IPipeTE;
import net.teamio.taam.piping.PipeEndFluidHandler;
import net.teamio.taam.piping.PipeInfo;
import net.teamio.taam.piping.PipeUtil;

public class TileEntityPipe extends BaseTileEntity implements IPipe, IPipeTE, ITickable, IRenderable {

	private final PipeInfo info;

	/**
	 * ThreadLocal storage for the list of visible parts (required due to some concurrency issues, See issue #194)
	 * TODO: central location for one list? Not one per entity type.. Adjust getVisibleParts
	 */
	private static final ThreadLocal<List<String>> visibleParts = new ThreadLocal<List<String>>() {
		@Override
		protected List<String> initialValue() {
			return new ArrayList<String>(7);
		}
	};

	/**
	 * Bitmap containing the surrounding pipes Runtime-only, required for
	 * rendering. This is updated in the {@link #renderUpdate()} method, called
	 * from
	 * {@link net.minecraft.block.Block#getActualState(net.minecraft.block.state.IBlockState, IBlockAccess, BlockPos)}
	 * just before rendering.
	 */
	private byte adjacentPipes;
	
	private PipeEndFluidHandler[] adjacentFluidHandlers;

	public TileEntityPipe() {
		info = new PipeInfo(500);
	}

	@Override
	public List<String> getVisibleParts() {
		List<String> visibleParts = TileEntityPipe.visibleParts.get();
		
		// Visible parts list is re-used to reduce object creation
		visibleParts.clear();
		visibleParts.add("Center_pcmdl");
		if (isSideConnected(EnumFacing.EAST))
			visibleParts.add("FlangeMX_pfmdl");

		if (isSideConnected(EnumFacing.WEST))
			visibleParts.add("FlangePX_pfmdl");

		if (isSideConnected(EnumFacing.NORTH))
			visibleParts.add("FlangeMY_pfmdl");
		if (isSideConnected(EnumFacing.SOUTH))
			visibleParts.add("FlangePY_pfmdl");

		if (isSideConnected(EnumFacing.DOWN))
			visibleParts.add("FlangeMZ_pfmdl");
		if (isSideConnected(EnumFacing.UP))
			visibleParts.add("FlangePZ_pfmdl");
		return visibleParts;
	}

	public boolean isSideConnected(EnumFacing side) {
		return (adjacentPipes & (1 << side.ordinal())) != 0;
	}
	
	private IFluidHandler getFluidHandler(TileEntity te, EnumFacing mySide) {
		if(te instanceof IFluidHandler) {
			IFluidHandler fh = (IFluidHandler)te;
			FluidTankInfo[] info = fh.getTankInfo(mySide.getOpposite());
			if(info != null && info.length > 0) {
				return fh;
			}
		}
		return null;
	}

	public void renderUpdate() {
		adjacentPipes = 0;
		for (EnumFacing side : EnumFacing.VALUES) {
			IPipe[] pipesOnSide = PipeUtil.getConnectedPipes(worldObj, pos, side);
			if (pipesOnSide != null && pipesOnSide.length != 0) {
				adjacentPipes |= 1 << side.ordinal();
				continue;
			}
			TileEntity te = worldObj.getTileEntity(pos.offset(side));
			if(getFluidHandler(te, side) != null) {
				adjacentPipes |= 1 << side.ordinal();
			}
		}
	};

	@Override
	public void blockUpdate() {
		// Check surrounding blocks for IFluidHandler implementations that don't use the pipe system
		// and create wrappers accordingly
		boolean wrappersRequired = false;
		for (EnumFacing side : EnumFacing.VALUES) {
			int sideIdx = side.ordinal();
			IPipe[] pipesOnSide = PipeUtil.getConnectedPipes(worldObj, pos, side);
			if (pipesOnSide == null || pipesOnSide.length == 0) {
				TileEntity te = worldObj.getTileEntity(pos.offset(side));
				IFluidHandler fh = getFluidHandler(te, side);
				if(fh != null) {
					wrappersRequired = true;
					// Fluid handler here, we need a wrapper.
					if(adjacentFluidHandlers == null) {
						adjacentFluidHandlers = new PipeEndFluidHandler[6];
						adjacentFluidHandlers[sideIdx] = new PipeEndFluidHandler(fh, side.getOpposite(), false);
					} else {
						// Not yet known or a different TileEntity, we need a new wrapper.
						if(adjacentFluidHandlers[sideIdx] == null || adjacentFluidHandlers[sideIdx].getOwner() != te) {
							adjacentFluidHandlers[sideIdx] = new PipeEndFluidHandler(fh, side.getOpposite(), false);
						}
					}
					
				}
			} else {
				// We have a regular pipe there, no need for a wrapper
				if(adjacentFluidHandlers != null) {
					adjacentFluidHandlers[sideIdx] = null;
				}
			}
		}
		// No wrappers required, delete the array
		if(!wrappersRequired) {
			adjacentFluidHandlers = null;
		}
		super.blockUpdate();
	}

	@Override
	public void update() {
		// Process "this"
		PipeUtil.processPipes(this, worldObj, pos);

		//Process the fluid handlers for adjecent non-pipe-machines (implementing IFluidHandler)
		if(adjacentFluidHandlers != null) {
			for (EnumFacing side : EnumFacing.VALUES) {
				PipeEndFluidHandler handler = adjacentFluidHandlers[side.ordinal()];
				if(handler != null) {
					PipeUtil.processPipes(handler, worldObj, pos.offset(side));
				}
			}
		}
		
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
	
	public int getFillLevel() {
		return info.fillLevel;
	}

	/*
	 * IPipeTE implementation
	 */
	@Override
	public IPipe[] getPipesForSide(EnumFacing side) {
		return new IPipe[] { this };
	}

	/*
	 * IPipe implementation
	 */

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
		return info.getFluids();
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
		for (EnumFacing side : EnumFacing.values()) {
			IPipe[] pipesOnSide = PipeUtil.getConnectedPipes(world, pos, side);
			if (pipesOnSide != null) {
				Collections.addAll(pipes, pipesOnSide);
			}
			int sideIdx = side.ordinal();
			if(adjacentFluidHandlers != null && adjacentFluidHandlers[sideIdx] != null) {
				pipes.add(adjacentFluidHandlers[sideIdx]);
			}
		}
		return pipes.toArray(new IPipe[pipes.size()]);
	}

	@Override
	public int removeFluid(FluidStack like) {
		return info.removeFluid(like);
	}

	@Override
	public int getFluidAmount(FluidStack like) {
		return info.getFluidAmount(like);
	}

}
