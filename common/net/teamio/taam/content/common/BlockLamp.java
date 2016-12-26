package net.teamio.taam.content.common;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Taam;
import net.teamio.taam.content.MaterialMachinesTransparent;
import net.teamio.taam.rendering.obj.OBJModel;

public class BlockLamp extends Block {

	public static PropertyEnum<EnumFacing> DIRECTION = PropertyEnum.create("direction", EnumFacing.class, EnumFacing.VALUES);
	public static PropertyBool POWERED = PropertyBool.create("powered");
	public static PropertyBool ATTACHED = PropertyBool.create("attached");

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
		return new ExtendedBlockState(this, new IProperty[] { DIRECTION, POWERED, ATTACHED },
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
		return getDefaultState().withProperty(DIRECTION, EnumFacing.getFront(meta & 7)).withProperty(POWERED, (meta & 8) != 0).withProperty(ATTACHED, false);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
		return null;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
		return getBoundingBox(worldIn.getBlockState(pos), worldIn, pos).offset(pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end) {
		AxisAlignedBB selBox = getBoundingBox(worldIn.getBlockState(pos), worldIn, pos);
		this.minX = selBox.minX;
		this.maxX = selBox.maxX;
		this.minY = selBox.minY;
		this.maxY = selBox.maxY;
		this.minZ = selBox.minZ;
		this.maxZ = selBox.maxZ;
		return super.collisionRayTrace(worldIn, pos, start, end);
	}

	public AxisAlignedBB getBoundingBox(IBlockState state, World source, BlockPos pos) {
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
		boolean attached = isAttached(source, pos, dir);
		if(attached) {
			minY -= 0.25f;
			maxY -= 0.25f;
		}
		return new AxisAlignedBB(minX,minY,minZ, maxX, maxY,maxZ);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		EnumFacing dir = state.getValue(DIRECTION);
		boolean attached = isAttached(worldIn, pos, dir);
		return super.getActualState(state, worldIn, pos).withProperty(ATTACHED, attached);
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		EnumFacing dir = state.getValue(DIRECTION);
		boolean attached = isAttached(world, pos, dir);
		return super.getExtendedState(state, world, pos).withProperty(ATTACHED, attached);
	}

	public static boolean isAttached(IBlockAccess world, BlockPos pos, EnumFacing dir) {
		if (dir.getAxis() == EnumFacing.Axis.Y) {
			return false;
		}

		// Check for a conveyor or other compatible machine
		EnumFacing opposite = dir.getOpposite();
		BlockPos offsetPos = pos.offset(opposite);
		TileEntity ent = world.getTileEntity(offsetPos);

		// Attached means we move down the lamp so it can sit on the side of the conveyor instead of floating
		return ent != null && ent.hasCapability(Taam.CAPABILITY_CONVEYOR, opposite);
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
		return getDefaultState().withProperty(DIRECTION, facing).withProperty(POWERED, isInverted).withProperty(ATTACHED, false);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState blockState = worldIn.getBlockState(pos);
		return canBlockStay(worldIn, pos, blockState);
	}

	public static boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		EnumFacing dir = state.getValue(DIRECTION);

		return canPlaceBlock(worldIn, pos, dir);
	}

	public static boolean canPlaceBlock(World worldIn, BlockPos pos, EnumFacing dir) {
		EnumFacing opposite = dir.getOpposite();
		BlockPos offsetPos = pos.offset(opposite);

		if (worldIn.isSideSolid(offsetPos, dir)) {
			return true;
		}
		// Only do special handling for conveyors if on the sides
		if (dir.getAxis() == EnumFacing.Axis.Y) {
			return false;
		}
		TileEntity ent = worldIn.getTileEntity(offsetPos);
		return ent != null && ent.hasCapability(Taam.CAPABILITY_CONVEYOR, opposite);
	}

	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
		return canPlaceBlock(worldIn, pos, side);
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
