package net.teamio.taam.content.piping;

import java.util.ArrayList;
import java.util.List;

import mcmultipart.block.BlockMultipart;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.teamio.taam.machines.IMachine;
import net.teamio.taam.piping.IPipe;
import net.teamio.taam.piping.PipeEndFluidHandler;
import net.teamio.taam.piping.PipeInfo;
import net.teamio.taam.piping.PipeUtil;

public class MachinePipe implements IMachine {

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
	 * ThreadLocal storage for the list of visible parts (required due to some concurrency issues, See issue #194)
	 * TODO: central location for one list? Not one per entity type.. Adjust getVisibleParts
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
		if(te instanceof IFluidHandler) {
			IFluidHandler fh = (IFluidHandler)te;
			FluidTankInfo[] info = fh.getTankInfo(mySide.getOpposite());
			if(info != null && info.length > 0) {
				return fh;
			}
		}
		return null;
	}

	public void renderUpdate(World world, BlockPos pos) {
		adjacentPipes = 0;
		for (EnumFacing side : EnumFacing.VALUES) {
			IPipe[] pipesOnSide = PipeUtil.getConnectedPipes(world, pos, side);
			if (pipesOnSide != null && pipesOnSide.length != 0) {
				adjacentPipes |= 1 << side.ordinal();
				continue;
			}
			TileEntity te = world.getTileEntity(pos.offset(side));
			if(getFluidHandler(te, side) != null) {
				adjacentPipes |= 1 << side.ordinal();
			}
		}
	};
	
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writePropertiesToNBT(NBTTagCompound tag, boolean isNetwork) {
		info.writeToNBT(tag);
	}

	@Override
	public void readPropertiesFromNBT(NBTTagCompound tag, boolean isNetwork) {
		info.readFromNBT(tag);
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, World world, BlockPos blockPos) {
		renderUpdate(world, blockPos);
		// Apply rotation to the model
		OBJModel.OBJState retState = new OBJModel.OBJState(getVisibleParts(), true);
		
		IExtendedBlockState extendedState = (IExtendedBlockState)state;
		
		return extendedState.withProperty(OBJModel.OBJProperty.instance, retState);
	}

	@Override
	public BlockState createBlockState(Block block) {
		return new ExtendedBlockState(block, new IProperty[] {}, new IUnlistedProperty[]{BlockMultipart.properties[0], OBJModel.OBJProperty.instance});
	}

	@Override
	public String getModelPath() {
		return "taam:pipe";
	}
	
	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		// TODO Auto-generated method stub
		list.add(BlockPipe.bbCenter);
	}

	@Override
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		// TODO Auto-generated method stub
		list.add(BlockPipe.bbCenter);
	}

	@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {
		// TODO Auto-generated method stub
		list.add(BlockPipe.bbCenter);
	}

}
