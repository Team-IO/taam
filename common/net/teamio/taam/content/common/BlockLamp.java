package net.teamio.taam.content.common;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.content.MaterialMachinesTransparent;
import net.teamio.taam.rendering.obj.OBJModel;

public class BlockLamp extends Block {

	public static PropertyEnum<EnumFacing> DIRECTION = PropertyEnum.create("direction", EnumFacing.class, EnumFacing.VALUES);
	public static PropertyBool POWERED = PropertyBool.create("powered");

	/**
	 * Hitbox "offset" depth (attaching side -> sensor front)
	 */
	private static final float depth = 2/16f;
	/**
	 * Hitbox "offset" width (block side -> sensor base)
	 */
	private static final float width = 0.25f;
	/**
	 * Hitbox "offset" height (block bottom/top -> sensor base)
	 */
	private static final float height = 6/16f;

	public final boolean isInverted;

	public BlockLamp(boolean isInverted) {
		super(MaterialMachinesTransparent.INSTANCE);
		this.isInverted = isInverted;
		setHardness(3.5f);
		setStepSound(Block.soundTypeMetal);
		this.setHarvestLevel("pickaxe", 1);
	}

	@Override
	protected BlockState createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] { DIRECTION, POWERED },
				new IUnlistedProperty[] { OBJModel.OBJProperty.instance });
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		EnumFacing meta = state.getValue(DIRECTION);
		boolean powered = state.getValue(POWERED);
		return meta.ordinal() + (powered ? 8 : 0);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(DIRECTION, EnumFacing.getFront(meta & 7)).withProperty(POWERED, (meta & 8) != 0);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
		return getBoundingBox(worldIn.getBlockState(pos), worldIn, pos);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
		return null;
	}

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
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		boolean isOn = state.getValue(POWERED);

		boolean powered = worldIn.getStrongPower(pos, state.getValue(DIRECTION)) > 0;

		if(isInverted) {
			powered = !powered;
		}

		if(isOn != powered) {
			worldIn.setBlockState(pos, state.withProperty(POWERED, powered), 2);
		}
	}

	@Override
	public int getLightValue(IBlockAccess world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isFullCube() {
		// Required false to prevent suffocation
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isBlockNormalCube() {
		return false;
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(DIRECTION, facing).withProperty(POWERED, isInverted);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState blockState = worldIn.getBlockState(pos);
		return canBlockStay(worldIn, pos, blockState);
	}

	public static boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		EnumFacing dir = state.getValue(DIRECTION);

		return worldIn.isSideSolid(pos.offset(dir.getOpposite()), dir);
	}

	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
		return worldIn.isSideSolid(pos.offset(side.getOpposite()), side);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		IBlockState state = world.getBlockState(pos);
		EnumFacing currentDirection = state.getValue(DIRECTION);
		EnumFacing previousRotation = currentDirection;
		for(int i = 0; i < 6; i++) {
			state = state.cycleProperty(DIRECTION);
			currentDirection = state.getValue(DIRECTION);
			if(canPlaceBlockOnSide(world, pos, currentDirection)) {
				break;
			}
		}
		if(currentDirection != previousRotation) {
			world.setBlockState(pos, state, 2);
			return true;
		}
		return false;
	}

}
