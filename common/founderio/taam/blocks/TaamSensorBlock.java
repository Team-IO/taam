package founderio.taam.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import founderio.taam.Taam;

public class TaamSensorBlock extends BaseBlock {

	public static final String[] metaList = new String[] {
		Taam.BLOCK_SENSOR_MOTION,
		Taam.BLOCK_SENSOR_MINECT
	};
	
	public TaamSensorBlock(int par1) {
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
	
	//TODO: Adjust Hitbox!

	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntitySensor();
		
	}
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world,
			int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		int rotation = meta & 7;
		ForgeDirection dir = ForgeDirection.getOrientation(rotation);
		
		
		
		/*
		 * Größen des Sensors
		 */
		// Tiefe (Verankerung -> Sensor)
		final float depth = 0.25f;
		// Breite (Block Außenseite -> Aufhängung)
		final float width = 0.25f;
		// höhe (Block Unter-/Oberseite -> Aufhängung
		final float height = 0.45f;
		
		switch(dir) {
		case DOWN:
			minX = depth;
			maxX = 1f-depth;
			maxY = 1f;
			minY = 1f - width;
			minZ = height;
			maxZ = 1f - height;
			break;
		case UP:
			minX = 0f;
			maxX = depth;
			maxY = 1f;
			minY = 1f - width;
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
	public int isProvidingWeakPower(IBlockAccess par1iBlockAccess, int par2,
			int par3, int par4, int par5) {
		TileEntitySensor te = ((TileEntitySensor) par1iBlockAccess.getBlockTileEntity(par2, par3, par4));
		//System.out.println(te.isPowering());
		return te.isPowering();
	}
	
	@Override
	public int isProvidingStrongPower(IBlockAccess par1iBlockAccess, int par2,
			int par3, int par4, int par5) {
		TileEntitySensor te = ((TileEntitySensor) par1iBlockAccess
				.getBlockTileEntity(par2, par3, par4));
		//System.out.println(te.isPowering());
		return te.isPowering();
	}
	
	@Override
	public boolean isBlockNormalCube(World world, int x, int y, int z) {
		return false;
	}
	
	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, net.minecraftforge.common.ForgeDirection side) {
		return true;
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
}
