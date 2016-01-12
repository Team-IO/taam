package net.teamio.taam.content.conveyors;

import java.util.List;

import codechicken.lib.inventory.InventoryUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Config;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.content.BaseBlock;
import net.teamio.taam.content.common.TileEntityChute;

public class BlockProductionLine extends BaseBlock {
	
	public BlockProductionLine() {
		super(Material.iron);
		this.setHardness(3.5f);
		this.setStepSound(Block.soundTypeMetal);
		this.setHarvestLevel("pickaxe", 1);
	}

	public String getUnlocalizedName(ItemStack itemStack) {
		int i = itemStack.getItemDamage();
		Enum<?>[] values = Taam.BLOCK_PRODUCTIONLINE_META.values();

		if (i < 0 || i >= values.length) {
			i = 0;
		}

		return super.getUnlocalizedName() + "." + values[i].name();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List list) {
		Enum<?>[] values = Taam.BLOCK_PRODUCTIONLINE_META.values();
		for (int i = 0; i < values.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		Taam.BLOCK_PRODUCTIONLINE_META variant = Taam.BLOCK_PRODUCTIONLINE_META.values()[metadata % Taam.BLOCK_PRODUCTIONLINE_META.values().length];
		switch(variant) {
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
			// Chute
			return new TileEntityChute(true);
		}
		Log.error("Was not able to create a TileEntity for " + getClass().getName());
		return null;
	}
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world,
			BlockPos pos) {
		int meta = world.getBlockMetadata(pos);
		this.minX = 0;
		this.maxX = 1;
		this.minZ = 0;
		this.maxZ = 1;
		if(meta == 20) {
			// Standalone (not in use at the moment)
			this.maxY = 1;
		} else {
			// Conveyor Machinery
			this.maxY = 0.5f;
		}		
		super.setBlockBoundsBasedOnState(world, pos);
	}
//	@Override
//	public boolean canProvidePower() {
//		return true;
//	}

//	@Override
//	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z,
//			int side) {
//		int meta = world.getBlockMetadata(x, y, z);
//		int rotation = meta & 7;
//		EnumFacing dir = EnumFacing.getOrientation(rotation);
//		EnumFacing sideDir = EnumFacing.getOrientation(side);
//		if(dir == sideDir) {
//			TileEntitySensor te = ((TileEntitySensor) world.getTileEntity(x, y, z));
//			return te.isPowering();
//		} else {
//			return 0;
//		}
//	}
//	
//	@Override
//	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z,
//			int side) {
//		int meta = world.getBlockMetadata(x, y, z);
//		int rotation = meta & 7;
//		EnumFacing dir = EnumFacing.getOrientation(rotation);
//		EnumFacing sideDir = EnumFacing.getOrientation(side);
//		if(dir == sideDir) {
//			TileEntitySensor te = ((TileEntitySensor) world.getTileEntity(x, y, z));
//			return te.isPowering();
//		} else {
//			return 0;
//		}
//	}
	
	@Override
	public boolean isBlockSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}
	
	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(World world, BlockPos pos) {
		IInventory inventory = InventoryUtils.getInventory(world, pos);
		if(inventory == null) {
			return 0;
		} else {
			return Container.calcRedstoneFromInventory(inventory);
		}
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		TileEntity ent = world.getTileEntity(pos);
		
		EnumFacing myDir = null;
		if(ent instanceof TileEntityConveyor) {
			myDir = ((TileEntityConveyor) ent).getFacingDirection();
		}
		return canBlockStay(world, pos, myDir);
	}
	
	public static boolean canBlockStay(World world, int x, int y, int z, EnumFacing myDir) {
		return  checkSupport(world, x, y, z, EnumFacing.DOWN, myDir, Config.pl_conveyor_supportrange, false) ||
				checkSupport(world, x, y, z, EnumFacing.UP, myDir, Config.pl_conveyor_supportrange, false) ||
				checkSupport(world, x, y, z, EnumFacing.NORTH, myDir, Config.pl_conveyor_supportrange, false) ||
				checkSupport(world, x, y, z, EnumFacing.SOUTH, myDir, Config.pl_conveyor_supportrange, false) ||
				checkSupport(world, x, y, z, EnumFacing.WEST, myDir, Config.pl_conveyor_supportrange, false) ||
				checkSupport(world, x, y, z, EnumFacing.EAST, myDir, Config.pl_conveyor_supportrange, false);
	}
	
	public static boolean checkSupport(World world, int x, int y, int z, EnumFacing side, EnumFacing myDir, int supportCount, boolean conveyorOnly) {
		EnumFacing otherDir = null;
		
		if(checkDirectSupport(world, x, y, z)) {
			return true;
		} else {
			TileEntity ent = world.getTileEntity(x + side.offsetX, y + side.offsetY, z + side.offsetZ);
			if(ent instanceof TileEntityConveyor) {
				
				boolean checkFurther = false;
				
				// The other is a conveyor and we are not a conveyor (no myDir)
				if(myDir == null && !conveyorOnly) {
					switch(side) {
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
				} else {
					if(side == EnumFacing.UP || side == EnumFacing.DOWN) {
						// Up and Down are connected by the supports
						checkFurther = true;
					} else {
						// Only connect conveyors directly working with each other (no sidealongs)
						otherDir = ((TileEntityConveyor) ent).getFacingDirection();
						checkFurther = myDir == otherDir && (myDir == side || myDir == side.getOpposite());
					}
				}
				if(checkFurther && supportCount > 0) {
					if(checkDirectSupport(world, x + side.offsetX, y + side.offsetY, z + side.offsetZ)) {
						return true;
					} else {
						if(checkSupport(world, x + side.offsetX, y + side.offsetY, z + side.offsetZ, side, myDir, supportCount - 1, conveyorOnly)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public static boolean checkDirectSupport(World world, int x, int y, int z) {
		for(EnumFacing side : EnumFacing.VALID_DIRECTIONS) {
			if(world.isSideSolid(x + side.offsetX, y + side.offsetY, z + side.offsetZ, side.getOpposite())) {
				return true;
			}
		}
		return false;
	}
}
