package net.teamio.taam.content.conveyors;

import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Config;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.Taam.BLOCK_PRODUCTIONLINE_META;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.BaseBlock;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.MaterialMachinesTransparent;
import net.teamio.taam.content.common.TileEntityChute;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.rendering.obj.OBJModel;

public class BlockProductionLine extends BaseBlock {

	public static final PropertyEnum<Taam.BLOCK_PRODUCTIONLINE_META> VARIANT = PropertyEnum.create("variant", Taam.BLOCK_PRODUCTIONLINE_META.class);
	public static final PropertyEnum<EnumFacing> DIRECTION = PropertyEnum.create("direction", EnumFacing.class);

	public static final AxisAlignedBB BOUNDS_CONVEYORS = new AxisAlignedBB(0, 0, 0, 1, 0.5f, 1);
	public static final AxisAlignedBB BOUNDS_ELEVATOR_X = new AxisAlignedBB(ConveyorUtil.oneThird, 0, 0, 1 - ConveyorUtil.oneThird, 1, 1);
	public static final AxisAlignedBB BOUNDS_ELEVATOR_Z = new AxisAlignedBB(0, 0, ConveyorUtil.oneThird, 1, 1, 1 - ConveyorUtil.oneThird);

	public BlockProductionLine() {
		super(MaterialMachinesTransparent.INSTANCE);
		setHardness(3.5f);
		setSoundType(SoundType.METAL);
		this.setHarvestLevel("pickaxe", 1);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] { DIRECTION, VARIANT },
				new IUnlistedProperty[] { OBJModel.OBJProperty.instance });
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		Taam.BLOCK_PRODUCTIONLINE_META meta = state.getValue(VARIANT);
		return meta.ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		Taam.BLOCK_PRODUCTIONLINE_META[] values = Taam.BLOCK_PRODUCTIONLINE_META.values();
		if (meta < 0 || meta > values.length) {
			return getDefaultState();
		}
		return getDefaultState().withProperty(VARIANT, values[meta]);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {

		// Let the tile entity update anything that is required for rendering
		BaseTileEntity te = (BaseTileEntity) worldIn.getTileEntity(pos);
		if(te.getWorld().isRemote) {
			te.renderUpdate();
		}

		// This makes the state shows up in F3. Previously it was not actually applied on the rendering, though.
		// Rendering Transform was applied in getExtendedState
		// Since 1.9 this seems to work, though

		// Add rotation to state
		if(te instanceof IRotatable) {
			return state.withProperty(DIRECTION, ((IRotatable)te).getFacingDirection());
		}
		return state.withProperty(DIRECTION, EnumFacing.DOWN);
	}

	public String getUnlocalizedName(ItemStack itemStack) {
		int i = itemStack.getItemDamage();
		Enum<?>[] values = Taam.BLOCK_PRODUCTIONLINE_META.values();

		if (i < 0 || i >= values.length) {
			i = 0;
		}

		return super.getTranslationKey() + "." + values[i].name();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		Enum<?>[] values = Taam.BLOCK_PRODUCTIONLINE_META.values();
		for (int i = 0; i < values.length; i++) {
			items.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		Taam.BLOCK_PRODUCTIONLINE_META variant = state.getValue(VARIANT);
		switch (variant) {
		case conveyor1:
			// Plain Conveyor, Tier 1
			return new TileEntityConveyor(0);
		case conveyor2:
			// Plain Conveyor, Tier 2
			return new TileEntityConveyor(1);
		case conveyor3:
			// Plain Conveyor, Tier 2
			return new TileEntityConveyor(2);
		case hopper:
			// Hopper, Regular
			return new TileEntityConveyorHopper(false);
		case hopper_hs:
			// Hopper, High-Speed
			return new TileEntityConveyorHopper(true);
		case sieve:
			// Sieve
			return new TileEntityConveyorSieve();
		case shredder:
			// Shredder
			return new TileEntityConveyorProcessor(TileEntityConveyorProcessor.Shredder);
		case grinder:
			// Grinder
			return new TileEntityConveyorProcessor(TileEntityConveyorProcessor.Grinder);
		case crusher:
			// Crusher
			return new TileEntityConveyorProcessor(TileEntityConveyorProcessor.Crusher);
		case chute:
			// Conveyor Chute
			return new TileEntityChute(true);
		case elevator:
			// Elevator
			return new TileEntityConveyorElevator();
		default:
			Log.error("Was not able to create a TileEntity for " + getClass().getName());
			return null;
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		if(state.getBlock() != TaamMain.blockProductionLine) {
			// Can only work for actual production line, else we crash with wrong variant
			return BOUNDS_CONVEYORS;
		}
		if(state.getValue(VARIANT) == BLOCK_PRODUCTIONLINE_META.elevator) {
			TileEntityConveyorElevator te = (TileEntityConveyorElevator) source.getTileEntity(pos);
			if(te != null && te.getFacingDirection().getAxis() == Axis.X) {
				return BOUNDS_ELEVATOR_X;
			}
			return BOUNDS_ELEVATOR_Z;
		}
		return BOUNDS_CONVEYORS;
	}

	@Override
	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
		if(state.getBlock() != TaamMain.blockProductionLine) {
			// Can only work for actual production line, else we crash with wrong variant
			return false;
		}
		return state.getValue(VARIANT) == BLOCK_PRODUCTIONLINE_META.elevator;
	}

	@Override
	public boolean causesSuffocation(IBlockState state) {
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		if(base_state.getBlock() != TaamMain.blockProductionLine) {
			// Can only work for actual production line, else we crash with wrong variant
			return false;
		}
		BLOCK_PRODUCTIONLINE_META variant = base_state.getValue(VARIANT);
		if(variant == BLOCK_PRODUCTIONLINE_META.elevator) {
			return side == EnumFacing.UP;
		}
		if(variant == BLOCK_PRODUCTIONLINE_META.conveyor1 ||
				variant == BLOCK_PRODUCTIONLINE_META.conveyor2 ||
				variant == BLOCK_PRODUCTIONLINE_META.conveyor3) {
			return side == EnumFacing.DOWN;
		}
		return false;
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		TileEntity te = worldIn.getTileEntity(pos);
		return Container.calcRedstone(te);
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		return canPlaceBlockAt(worldIn, pos);
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		TileEntity ent = world.getTileEntity(pos);

		EnumFacing myDir = null;
		if (ent instanceof TileEntityConveyor) {
			myDir = ((TileEntityConveyor) ent).getFacingDirection();
		}
		return canBlockStay(world, pos, myDir);
	}

	public static boolean canBlockStay(World world, BlockPos pos, EnumFacing myDir) {
		return checkSupport(world, pos, EnumFacing.DOWN, myDir, Config.pl_conveyor_supportrange, false)
				|| checkSupport(world, pos, EnumFacing.UP, myDir, Config.pl_conveyor_supportrange, false)
				|| checkSupport(world, pos, EnumFacing.NORTH, myDir, Config.pl_conveyor_supportrange, false)
				|| checkSupport(world, pos, EnumFacing.SOUTH, myDir, Config.pl_conveyor_supportrange, false)
				|| checkSupport(world, pos, EnumFacing.WEST, myDir, Config.pl_conveyor_supportrange, false)
				|| checkSupport(world, pos, EnumFacing.EAST, myDir, Config.pl_conveyor_supportrange, false);
	}

	public static boolean checkSupport(World world, BlockPos pos, EnumFacing side, EnumFacing myDir, int supportCount,
			boolean conveyorOnly) {
		EnumFacing otherDir = null;

		if (checkDirectSupport(world, pos)) {
			return true;
		}
		TileEntity ent = world.getTileEntity(pos.offset(side));
		if (ent instanceof TileEntityConveyor) {

			boolean checkFurther = false;

			// The other is a conveyor and we are not a conveyor (no myDir)
			if (myDir == null && !conveyorOnly) {
				switch (side) {
				case UP:
				default:
					// Up is usually not connected
					return false;
				case NORTH:
				case EAST:
				case SOUTH:
				case WEST:
				case DOWN:
					// Attach to the side of or above a conveyor
					return true;
				}
			}
			if (side == EnumFacing.UP || side == EnumFacing.DOWN) {
				// Up and Down are connected by the supports
				checkFurther = true;
			} else {
				// Only connect conveyors directly working with each
				// other (no sidealongs)
				otherDir = ((TileEntityConveyor) ent).getFacingDirection();
				checkFurther = myDir == otherDir && (myDir == side || myDir == side.getOpposite());
			}
			if (checkFurther && supportCount > 0) {
				if (checkDirectSupport(world, pos.offset(side))) {
					return true;
				}
				if (checkSupport(world, pos.offset(side), side, myDir, supportCount - 1, conveyorOnly)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean checkDirectSupport(World world, BlockPos pos) {
		for (EnumFacing side : EnumFacing.VALUES) {
			if (world.isSideSolid(pos.offset(side), side.getOpposite())) {
				return true;
			}
		}
		return false;
	}
}
