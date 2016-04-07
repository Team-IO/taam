package net.teamio.taam.content.piping;

import java.util.ArrayList;
import java.util.List;

import mcmultipart.block.BlockMultipart;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.content.BaseBlock;
import net.teamio.taam.content.MaterialMachinesTransparent;

public class BlockPipe extends BaseBlock implements ITileEntityProvider {

	private static final float pipeWidth = 0.25f;
	private static final float fromBorder = (1f-pipeWidth) / 2;
	
	public static AxisAlignedBB bbCenter = new AxisAlignedBB(fromBorder, fromBorder, fromBorder, 1-fromBorder, 1-fromBorder, 1-fromBorder);
	public static final AxisAlignedBB[] bbFaces = new AxisAlignedBB[6];
	private AxisAlignedBB closestBB;
	
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
	
	@Override
	protected BlockState createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] {}, new IUnlistedProperty[]{BlockMultipart.properties[0], OBJModel.OBJProperty.instance});
	}
	
	public BlockPipe() {
		super(MaterialMachinesTransparent.INSTANCE);
		this.fullBlock = false;
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		return true;
	}

	
	@Override
	public MovingObjectPosition collisionRayTraceDefault(World world, BlockPos pos, Vec3 start, Vec3 end) {
		start = start.subtract(pos.getX(), pos.getY(), pos.getZ());
		end = end.subtract(pos.getX(), pos.getY(), pos.getZ());

		List<AxisAlignedBB> boxes = getBoxes(world, pos, world.getBlockState(pos));

		MovingObjectPosition closestHit = null;
		double dist = Double.MAX_VALUE;
		closestBB = null;
		
		for (AxisAlignedBB box : boxes) {

			MovingObjectPosition newHit = box.calculateIntercept(start, end);
			if (newHit != null) {
				double newDist = newHit.hitVec.distanceTo(start);
				if (newDist < dist) {
					closestHit = newHit;
					dist = newDist;
					closestBB = box;
				}
			}
		}
		if (closestHit == null) {
			return null;
		} else {
			return new MovingObjectPosition(closestHit.hitVec, closestHit.sideHit, pos);
		}
	}
 
	/**
	 * Get all boxes in block-space coordinates
	 * 
	 * @param world
	 * @param pos
	 * @param state
	 * @return
	 */
	public List<AxisAlignedBB> getBoxes(IBlockAccess world, BlockPos pos, IBlockState state) {
		List<AxisAlignedBB> collisionBoxes = new ArrayList<AxisAlignedBB>(7);

		TileEntity te = world.getTileEntity(pos);

		// Fallback
		if (!(te instanceof TileEntityPipe)) {
			collisionBoxes.add(bbCenter);
			return collisionBoxes;
		}

		// Check pipe connections
		TileEntityPipe pipe = (TileEntityPipe) te;

		collisionBoxes.add(bbCenter);

		for (EnumFacing side : EnumFacing.VALUES) {
			if (pipe.isSideConnected(side)) {
				collisionBoxes.add(bbFaces[side.ordinal()]);
			}
		}

		return collisionBoxes;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
		// Get player position + look vector
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		Vec3 eyes = player.getPositionEyes(0);
		Vec3 look = player.getLook(0);
		float reach = Minecraft.getMinecraft().playerController.getBlockReachDistance();
		Vec3 dest = eyes.addVector(look.xCoord * reach, look.yCoord * reach, look.zCoord * reach);
		
		// in that method, we update the closestBB
		collisionRayTrace(world, pos, eyes, dest);
		
		// Return the box that is hovered, or the default if nothing could be determined (edge cases)
		if(closestBB == null) {
			return bbCenter.offset(pos.getX(), pos.getY(), pos.getZ());
		} else {
			return closestBB.offset(pos.getX(), pos.getY(), pos.getZ());
		}
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
		// For this block, this method is only used when placing a new block in the world.
		// Return only the center to allow placig if the player is half way into the block
		return bbCenter.offset(pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public void addCollisionBoxesToListDefault(World world, BlockPos pos, IBlockState state, AxisAlignedBB mask,
			List<AxisAlignedBB> list, Entity par7Entity) {
		if (!isCollidable()) {
			return;
		} else {
			for (AxisAlignedBB bb : getBoxes(world, pos, state)) {
				bb = bb.offset(pos.getX(), pos.getY(), pos.getZ());
				if (mask.intersectsWith(bb)) {
					list.add(bb);
				}
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityPipe();
	}

}
