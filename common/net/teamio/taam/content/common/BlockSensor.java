package net.teamio.taam.content.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
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
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return null;
    }

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntitySensor(ForgeDirection.getOrientation(metadata));
		
	}
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world,
			int x, int y, int z) {
		// Type is determined by the tile entity, we just need the rotation here
		int meta = world.getBlockMetadata(x, y, z);
		ForgeDirection dir = ForgeDirection.getOrientation(meta);
		
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
		case UNKNOWN:
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
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
		return getRedstoneLevel(world, x, y, z);
	}
	
	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
		int meta = world.getBlockMetadata(x, y, z);
		ForgeDirection dir = ForgeDirection.getOrientation(meta);
		ForgeDirection sideDir = ForgeDirection.getOrientation(side);
		if(dir == sideDir) {
			return getRedstoneLevel(world, x, y, z);
		} else {
			return 0;
		}
	}
	
	public int getRedstoneLevel(IBlockAccess world, int x, int y, int z) {
		TileEntitySensor te = ((TileEntitySensor) world.getTileEntity(x, y, z));
		return te == null ? 0 : te.getRedstoneLevel();
	}
	
	@Override
	public boolean isBlockSolid(IBlockAccess world, int x, int y, int z, int side) {
		return false;
	}

	@Override
	public void onPostBlockPlaced(World world, int x, int y, int z, int meta) {
		world.notifyBlocksOfNeighborChange(x, y, z, this);
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z,
			int side, float hitx, float hity, float hitz, int meta) {
        return side;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		BaseBlock.updateBlocksAround(world, x, y, z);
		super.breakBlock(world, x, y, z, block, meta);
	}
	
	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		ForgeDirection dir = ForgeDirection.getOrientation(meta).getOpposite();
		return world.isSideSolid(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir);
	}
	
	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
		ForgeDirection dir = ForgeDirection.getOrientation(side).getOpposite();
		return world.isSideSolid(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir);
	}
	
	
}
