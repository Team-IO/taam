package founderio.taam.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.taam.Taam;

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
	
	public BlockSensor(int par1) {
		super(par1, Material.iron);
		this.setHardness(3.5f);
		this.setStepSound(Block.soundMetalFootstep);
		MinecraftForge.setBlockHarvestLevel(this, "pickaxe", 1);
	}
	
	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}
	
	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void addCollisionBoxesToList(World par1World, int par2, int par3,
			int par4, AxisAlignedBB par5AxisAlignedBB, List par6List,
			Entity par7Entity) {
		return;
	}

	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntitySensor();
		
	}
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world,
			int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		int rotation = meta & 7;
		ForgeDirection dir = ForgeDirection.getOrientation(rotation);
		
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
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z,
			int side) {
		int meta = world.getBlockMetadata(x, y, z);
		int rotation = meta & 7;
		ForgeDirection dir = ForgeDirection.getOrientation(rotation);
		ForgeDirection sideDir = ForgeDirection.getOrientation(side);
		if(dir == sideDir) {
			TileEntitySensor te = ((TileEntitySensor) world.getBlockTileEntity(x, y, z));
			return te.isPowering();
		} else {
			return 0;
		}
	}
	
	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z,
			int side) {
		int meta = world.getBlockMetadata(x, y, z);
		int rotation = meta & 7;
		ForgeDirection dir = ForgeDirection.getOrientation(rotation);
		ForgeDirection sideDir = ForgeDirection.getOrientation(side);
		if(dir == sideDir) {
			TileEntitySensor te = ((TileEntitySensor) world.getBlockTileEntity(x, y, z));
			return te.isPowering();
		} else {
			return 0;
		}
	}

	@Override
	public boolean isBlockNormalCube(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z,
			ForgeDirection side) {
		return false;
	};
	
	public void updateBlocksAround(World par1World, int par2, int par3, int par4) {
		par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
		par1World.notifyBlocksOfNeighborChange(par2 + 1, par3, par4, this.blockID);
        par1World.notifyBlocksOfNeighborChange(par2 - 1, par3, par4, this.blockID);
        par1World.notifyBlocksOfNeighborChange(par2, par3, par4 + 1, this.blockID);
        par1World.notifyBlocksOfNeighborChange(par2, par3, par4 - 1, this.blockID);
        par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
        par1World.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, this.blockID);
	}

	@Override
	public void onPostBlockPlaced(World par1World, int par2, int par3,
			int par4, int par5) {
		updateBlocksAround(par1World, par2, par3, par4);
	}

	@Override
	public int onBlockPlaced(World par1World, int x, int y, int z,
			int side, float hitx, float hity, float hitz, int meta) {
		int metaPart = meta & 8;
        int resultingRotation = side;
        return metaPart | resultingRotation;
	}
	
	@Override
	public void breakBlock(World par1World, int par2, int par3, int par4,
			int par5, int par6) {
		updateBlocksAround(par1World, par2, par3, par4);
		
		super.breakBlock(par1World, par2, par3, par4, par5, par6);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.blockIcon = par1IconRegister.registerIcon(Taam.MOD_ID + ":tech_block");
	}
	
	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		int rotation = meta & 7;
		ForgeDirection dir = ForgeDirection.getOrientation(rotation).getOpposite();
		
		return world.isBlockSolidOnSide(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, ForgeDirection.getOrientation(rotation));
	}
	
	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y,
			int z, int side) {
		ForgeDirection dir = ForgeDirection.getOrientation(side).getOpposite();
		return world.isBlockSolidOnSide(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir);
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y,
			int z, int neighborId) {
		if(!canBlockStay(world, x, y, z)) {
			int meta = world.getBlockMetadata(x, y, z);
            dropBlockAsItem(world, x, y, z, meta, 0);
            world.setBlock(x, y, z, 0, 0, 3);
		}
	}
	
	
}
