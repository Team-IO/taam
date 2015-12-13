package net.teamio.taam.content.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Taam;
import net.teamio.taam.content.BaseBlock;

public class BlockSensor extends BaseBlock {
	
	/**
	 * Hitbox "offset" depth (attaching side -> sensor front)
	 */
	private static final float depth = 0.30f;
	/**
	 * Hitbox "offset" depth (block side -> sensor base)
	 */
	private static final float width = 0.23f;
	/**
	 * Hitbox "offset" depth (block bottom/top -> sensor base)
	 */
	private static final float height = 0.43f;
	
	public static final String[] metaList = new String[] {
		Taam.BLOCK_SENSOR_MOTION,
		Taam.BLOCK_SENSOR_MINECT
	};
	
	public BlockSensor() {
		super(Material.iron);
		this.setHardness(3.5f);
		this.setStepSound(Block.soundTypeMetal);
		this.setHarvestLevel("pickaxe", 1);
		this.setBlockTextureName(Taam.MOD_ID + ":tech_block");
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
		return null;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntitySensor();
		
	}
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
		int meta = world.getBlockMetadata(pos);
		int rotation = meta & 7;
		EnumFacing dir = EnumFacing.getFront(rotation);
		
		switch (dir) {
		case DOWN:
			minX = width;
			maxX = 1f - width;
			minY = 1f - depth;
			maxY = 1f;
			minZ = height;
			maxZ = 1f - height;
			break;
		case UP:
			minX = width;
			maxX = 1f - width;
			minY = 0f;
			maxY = depth;
			minZ = height;
			maxZ = 1f - height;
			break;
		case NORTH:
			minX = width;
			maxX = 1f - width;
			maxY = 1f - height;
			minY = height;
			minZ = 1f - depth;
			maxZ = 1f;
			break;
		case SOUTH:
			minX = width;
			maxX = 1f - width;
			maxY = 1f - height;
			minY = height;
			minZ = 0f;
			maxZ = depth;
			break;
		case WEST:
			minX = 1f - depth;
			maxX = 1f;
			maxY = 1f - height;
			minY = height;
			minZ = width;
			maxZ = 1f - width;
			break;
		case EAST:
			minX = 0f;
			maxX = depth;
			maxY = 1f - height;
			minY = height;
			minZ = width;
			maxZ = 1f - width;
			break;
		default:
			minX = 0;
			maxX = 1;
			minY = 0;
			maxY = 1;
			minZ = 0;
			maxZ = 1;
			break;
		}
	}
	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
		int meta = state;
		int rotation = meta & 7;
		EnumFacing dir = EnumFacing.getFront(rotation);
		if(dir == side) {
			TileEntitySensor te = ((TileEntitySensor) world.getTileEntity(pos));
			return te.isPowering();
		} else {
			return 0;
		}
	}
	
	@Override
	public int isProvidingStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
		int meta = state;
		int rotation = meta & 7;
		EnumFacing dir = EnumFacing.getFront(rotation);
		if(dir == side) {
			TileEntitySensor te = ((TileEntitySensor) worldIn.getTileEntity(pos));
			return te.isPowering();
		} else {
			return 0;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isBlockNormalCube() {
		return false;
	}

	
	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		worldIn.notifyNeighborsOfStateChange(pos, this);
	}
	
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer) {
		int metaPart = meta & 8;
        int resultingRotation = facing.getIndex();
        // metaPart | resultingRotation;
        //TODO actually use IBlockState...
		return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, metaPart | resultingRotation, placer);
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		BaseBlock.updateBlocksAround(worldIn, pos);
		super.breakBlock(worldIn, pos, state);
	}
	
	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		int meta = worldIn.getBlockMetadata(pos);
		int rotation = meta & 7;
		EnumFacing side = EnumFacing.getFront(rotation).getOpposite();

		return worldIn.isSideSolid(pos.offset(side), side.getOpposite());
	}
	
	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
		return worldIn.isSideSolid(pos.offset(side), side.getOpposite());
	}
	
}
