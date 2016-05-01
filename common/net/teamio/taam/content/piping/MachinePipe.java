package net.teamio.taam.content.piping;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.teamio.taam.Taam;
import net.teamio.taam.machines.IMachine;
import net.teamio.taam.piping.IPipe;
import net.teamio.taam.piping.PipeEndFluidHandler;
import net.teamio.taam.piping.PipeInfo;
import net.teamio.taam.piping.PipeUtil;

public class MachinePipe implements IMachine, IPipe {

	private static final float pipeWidth = 0.25f;
	private static final float fromBorder = (1f - pipeWidth) / 2;

	public static AxisAlignedBB bbCenter = new AxisAlignedBB(fromBorder, fromBorder, fromBorder, 1-fromBorder, 1-fromBorder, 1-fromBorder);
	public static final AxisAlignedBB[] bbFaces = new AxisAlignedBB[6];

	static {
		System.out.println(fromBorder);
		bbFaces[EnumFacing.EAST.ordinal()]	= new AxisAlignedBB(1-fromBorder,	fromBorder,		fromBorder,
																1,				1-fromBorder,	1-fromBorder);
		bbFaces[EnumFacing.WEST.ordinal()]	= new AxisAlignedBB(0,				fromBorder,		fromBorder,
																fromBorder,		1-fromBorder,	1-fromBorder);
		bbFaces[EnumFacing.SOUTH.ordinal()]	= new AxisAlignedBB(fromBorder,		fromBorder,		1-fromBorder,
																1-fromBorder,	1-fromBorder,	1);
		bbFaces[EnumFacing.NORTH.ordinal()]	= new AxisAlignedBB(fromBorder,		fromBorder,		0,
																1-fromBorder,	1-fromBorder,	fromBorder);
		bbFaces[EnumFacing.UP.ordinal()]	= new AxisAlignedBB(fromBorder,		1-fromBorder,	fromBorder,
																1-fromBorder,	1,				1-fromBorder);
		bbFaces[EnumFacing.DOWN.ordinal()]	= new AxisAlignedBB(fromBorder,		0,				fromBorder,
																1-fromBorder,	fromBorder,		1-fromBorder);
	}
	
	private final PipeInfo info;
	/**
	 * Bitmap containing the surrounding pipes Runtime-only, required for
	 * rendering. This is updated in the {@link #renderUpdate()} method, called
	 * from
	 * {@link net.minecraft.block.Block#getActualState(net.minecraft.block.state.IBlockState, IBlockAccess, BlockPos)}
	 * just before rendering.
	 */
	private byte adjacentPipes;
	
	private PipeEndFluidHandler[] adjacentFluidHandlers;
	/**
	 * ThreadLocal storage for the list of visible parts (required due to some
	 * concurrency issues, See issue #194) TODO: central location for one list?
	 * Not one per entity type.. Adjust getVisibleParts
	 */
	private static final ThreadLocal<List<String>> visibleParts = new ThreadLocal<List<String>>() {
		@Override
		protected List<String> initialValue() {
			return new ArrayList<String>(7);
		}
	};

	public MachinePipe() {
		info = new PipeInfo(500);
	}

	public List<String> getVisibleParts() {
		List<String> visibleParts = MachinePipe.visibleParts.get();

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
		if (te instanceof IFluidHandler) {
			IFluidHandler fh = (IFluidHandler) te;
			FluidTankInfo[] info = fh.getTankInfo(mySide.getOpposite());
			if (info != null && info.length > 0) {
				return fh;
			}
		}
		return null;
	}

	@Override
	public boolean renderUpdate(World world, BlockPos pos) {
		int old = adjacentPipes;
		adjacentPipes = 0;
		for (EnumFacing side : EnumFacing.VALUES) {
			IPipe pipeOnSide = PipeUtil.getConnectedPipe(world, pos, side);
			if (pipeOnSide != null) {
				adjacentPipes |= 1 << side.ordinal();
				continue;
			}
			TileEntity te = world.getTileEntity(pos.offset(side));
			if (getFluidHandler(te, side) != null) {
				adjacentPipes |= 1 << side.ordinal();
			}
		}
		return old != adjacentPipes;
	};

	@Override
	public void blockUpdate(World world, BlockPos pos) {
		// Check surrounding blocks for IFluidHandler implementations that don't use the pipe system
		// and create wrappers accordingly
		boolean wrappersRequired = false;
		for (EnumFacing side : EnumFacing.VALUES) {
			int sideIdx = side.ordinal();
			IPipe pipeOnSide = PipeUtil.getConnectedPipe(world, pos, side);
			if (pipeOnSide == null) {
				TileEntity te = world.getTileEntity(pos.offset(side));
				if (te != null && !te.hasCapability(Taam.CAPABILITY_PIPE, side)) {
					IFluidHandler fh = getFluidHandler(te, side);
					if (fh != null) {
						wrappersRequired = true;
						// Fluid handler here, we need a wrapper.
						if (adjacentFluidHandlers == null) {
							adjacentFluidHandlers = new PipeEndFluidHandler[6];
							adjacentFluidHandlers[sideIdx] = new PipeEndFluidHandler(fh, side.getOpposite(), false);
						} else {
							// Not yet known or a different TileEntity, we need a new wrapper.
							if (adjacentFluidHandlers[sideIdx] == null
									|| adjacentFluidHandlers[sideIdx].getOwner() != te) {
								adjacentFluidHandlers[sideIdx] = new PipeEndFluidHandler(fh, side.getOpposite(), false);
							}
						}
					}
				}
			} else {
				// We have a regular pipe there, no need for a wrapper
				if (adjacentFluidHandlers != null) {
					adjacentFluidHandlers[sideIdx] = null;
				}
			}
		}
		// No wrappers required, delete the array
		if (!wrappersRequired) {
			adjacentFluidHandlers = null;
		}
	}

	@Override
	public void update(World world, BlockPos pos) {
		// Process "this"
		PipeUtil.processPipes(this, world, pos);
		// Process the fluid handlers for adjecent non-pipe-machines (implementing IFluidHandler)
		if (adjacentFluidHandlers != null) {
			for (EnumFacing side : EnumFacing.VALUES) {
				PipeEndFluidHandler handler = adjacentFluidHandlers[side.ordinal()];
				if (handler != null) {
					PipeUtil.processPipes(handler, world, pos.offset(side));
				}
			}
		}
		
		//TODO: updateState(false, false, false);
	}

	@Override
	public void writePropertiesToNBT(NBTTagCompound tag) {
		info.writeToNBT(tag);
	}

	@Override
	public void readPropertiesFromNBT(NBTTagCompound tag) {
		info.readFromNBT(tag);
	}

	@Override
	public void writeUpdatePacket(PacketBuffer buf) {
		info.writeUpdatePacket(buf);
	}

	@Override
	public void readUpdatePacket(PacketBuffer buf) {
		info.readUpdatePacket(buf);
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, World world, BlockPos blockPos) {
		renderUpdate(world, blockPos);
		// Apply rotation to the model
		OBJModel.OBJState retState = new OBJModel.OBJState(getVisibleParts(), true, new TRSRTransformation(EnumFacing.SOUTH));

		IExtendedBlockState extendedState = (IExtendedBlockState) state;
		return extendedState.withProperty(OBJModel.OBJProperty.instance, retState);
	}

	@Override
	public String getModelPath() {
		return "taam:machine";
	}

	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		if (mask.intersectsWith(bbCenter)) {
			list.add(bbCenter);
		}
		for (EnumFacing side : EnumFacing.VALUES) {
			if (isSideConnected(side)) {
				AxisAlignedBB box = bbFaces[side.ordinal()];
				if (mask.intersectsWith(box)) {
					list.add(box);
				}
			}
		}
	}

	@Override
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		list.add(bbCenter);
		for (EnumFacing side : EnumFacing.VALUES) {
			if (isSideConnected(side)) {
				AxisAlignedBB box = bbFaces[side.ordinal()];
				list.add(box);
			}
		}
	}

	@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {
		list.add(bbCenter);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == Taam.CAPABILITY_PIPE) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == Taam.CAPABILITY_PIPE) {
			return (T) this;
		}
		return null;
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
		//TODO: markDirty();
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
	public IPipe[] getInternalPipes(IBlockAccess world, BlockPos pos) {
		List<IPipe> pipes = new ArrayList<IPipe>(6);
		for (EnumFacing side : EnumFacing.values()) {
			IPipe pipeOnSide = PipeUtil.getConnectedPipe(world, pos, side);
			if (pipeOnSide != null) {
				pipes.add(pipeOnSide);
			} else {
				int sideIdx = side.ordinal();
				if(adjacentFluidHandlers != null && adjacentFluidHandlers[sideIdx] != null) {
					pipes.add(adjacentFluidHandlers[sideIdx]);
				}
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
	
	@Override
	public boolean isSideAvailable(EnumFacing side) {
		//TODO: Check multipart occlusion?
		return true;
	}

}
