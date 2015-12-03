package net.teamio.taam.content.conveyors;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.teamio.taam.Config;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.content.BaseBlock;
import net.teamio.taam.content.common.TileEntityChute;
import codechicken.lib.inventory.InventoryUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockProductionLine extends BaseBlock {
	
	public BlockProductionLine() {
		super(Material.iron);
		this.setHardness(3.5f);
		this.setStepSound(Block.soundTypeMetal);
		this.setHarvestLevel("pickaxe", 1);
		this.setBlockTextureName(Taam.MOD_ID + ":tech_block");
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
		if(metadata == 0) {
			// Plain Conveyor, Tier 1
			return new TileEntityConveyor(0);
		} else if(metadata == 1) {
			// Plain Conveyor, Tier 2
			return new TileEntityConveyor(1);
		} else if(metadata == 2) {
			// Plain Conveyor, Tier 2
			return new TileEntityConveyor(2);
		} else if(metadata == 3) {
			// Hopper, Regular
			return new TileEntityConveyorHopper(false);
		} else if(metadata == 4) {
			// Hopper, High-Speed
			return new TileEntityConveyorHopper(true);
		} else if(metadata == 5) {
			// Sieve
			return null;
		} else if(metadata == 6) {
			// Shredder
			return new TileEntityConveyorProcessor(TileEntityConveyorProcessor.Shredder);
		} else if(metadata == 7) {
			// Grinder
			return new TileEntityConveyorProcessor(TileEntityConveyorProcessor.Grinder);
		} else if(metadata == 8) {
			// Crusher
			return new TileEntityConveyorProcessor(TileEntityConveyorProcessor.Crusher);
		} else if(metadata == 9) {
			// Chute
			return new TileEntityChute();
		}
		Log.error("Was not able to create a TileEntity for " + getClass().getName());
		return null;
	}
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world,
			int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
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
		super.setBlockBoundsBasedOnState(world, x, y, z);
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
//		ForgeDirection dir = ForgeDirection.getOrientation(rotation);
//		ForgeDirection sideDir = ForgeDirection.getOrientation(side);
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
//		ForgeDirection dir = ForgeDirection.getOrientation(rotation);
//		ForgeDirection sideDir = ForgeDirection.getOrientation(side);
//		if(dir == sideDir) {
//			TileEntitySensor te = ((TileEntitySensor) world.getTileEntity(x, y, z));
//			return te.isPowering();
//		} else {
//			return 0;
//		}
//	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isBlockNormalCube() {
		return false;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess world, int x,
			int y, int z, int side) {
		return false;
	}
	
	@Override
	public void onPostBlockPlaced(World par1World, int par2, int par3,
			int par4, int par5) {
//		updateBlocksAround(par1World, par2, par3, par4);
	}
	
	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int meta) {
		IInventory inventory = InventoryUtils.getInventory(world, x, y, z);
		if(inventory == null) {
			return 0;
		} else {
			return Container.calcRedstoneFromInventory(inventory);
		}
	}
	
	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		TileEntity ent = world.getTileEntity(x, y, z);
		
		ForgeDirection myDir = null;
		if(ent instanceof TileEntityConveyor) {
			myDir = ((TileEntityConveyor) ent).getFacingDirection();
		}
		return canBlockStay(world, x, y, z, myDir);
	}
	
	public static boolean canBlockStay(World world, int x, int y, int z, ForgeDirection myDir) {
		return checkSupport(world, x, y, z, ForgeDirection.DOWN, myDir, Config.pl_conveyor_supportrange, false) ||
				checkSupport(world, x, y, z, ForgeDirection.UP, myDir, Config.pl_conveyor_supportrange, false) ||
				checkSupport(world, x, y, z, ForgeDirection.NORTH, myDir, Config.pl_conveyor_supportrange, false) ||
				checkSupport(world, x, y, z, ForgeDirection.SOUTH, myDir, Config.pl_conveyor_supportrange, false) ||
				checkSupport(world, x, y, z, ForgeDirection.WEST, myDir, Config.pl_conveyor_supportrange, false) ||
				checkSupport(world, x, y, z, ForgeDirection.EAST, myDir, Config.pl_conveyor_supportrange, false);
	}
	
	public static boolean checkSupport(World world, int x, int y, int z, ForgeDirection side, ForgeDirection myDir, int supportCount, boolean conveyorOnly) {
		ForgeDirection otherDir = null;
		
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
					if(side == ForgeDirection.UP || side == ForgeDirection.DOWN) {
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
		for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
			if(world.isSideSolid(x + side.offsetX, y + side.offsetY, z + side.offsetZ, side.getOpposite())) {
				return true;
			}
		}
		return false;
	}
}
