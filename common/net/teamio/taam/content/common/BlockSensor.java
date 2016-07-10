package net.teamio.taam.content.common;

import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.teamio.taam.Taam;
import net.teamio.taam.content.BaseBlock;
import net.teamio.taam.content.MaterialMachinesTransparent;
import net.teamio.taam.rendering.obj.OBJModel;

public class BlockSensor extends BaseBlock {

	public static PropertyEnum<EnumFacing> DIRECTION = PropertyEnum.create("direction", EnumFacing.class, EnumFacing.VALUES);

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
		super(MaterialMachinesTransparent.INSTANCE);
		setHardness(3.5f);
		setSoundType(SoundType.METAL);
		this.setHarvestLevel("pickaxe", 1);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] { DIRECTION },
				new IUnlistedProperty[] { OBJModel.OBJProperty.instance });
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		EnumFacing meta = state.getValue(DIRECTION);
		return meta.ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(DIRECTION, EnumFacing.getFront(meta));
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		return null;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntitySensor(state.getValue(DIRECTION));

	}
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		// Type is determined by the tile entity, we just need the rotation here

		EnumFacing dir = state.getValue(DIRECTION);
		float minX, minY, minZ, maxX, maxY, maxZ;
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
		return new AxisAlignedBB(minX,minY,minZ, maxX, maxY,maxZ);
	}
	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return getRedstoneLevel(blockAccess, pos);
	}

	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		EnumFacing dir = blockState.getValue(DIRECTION);
		if(dir == side) {
			return getRedstoneLevel(blockAccess, pos);
		} else {
			return 0;
		}
	}

	public int getRedstoneLevel(IBlockAccess world, BlockPos pos) {
		TileEntitySensor te = (TileEntitySensor) world.getTileEntity(pos);
		return te == null ? 0 : te.getRedstoneLevel();
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
		return getDefaultState().withProperty(DIRECTION, facing);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		BaseBlock.updateBlocksAround(worldIn, pos);
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState blockState = worldIn.getBlockState(pos);
		return canBlockStay(worldIn, pos, blockState);
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		EnumFacing dir = state.getValue(DIRECTION);
		EnumFacing side = dir.getOpposite();

		return worldIn.isSideSolid(pos.offset(side), dir);
	}

	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
		//return worldIn.isSideSolid(pos.offset(side), side);
		return true;
	}

}
