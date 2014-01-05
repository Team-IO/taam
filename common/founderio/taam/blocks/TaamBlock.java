package founderio.taam.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import founderio.taam.Taam;

public class TaamBlock extends Block {

	public static final String[] metaList = new String[] {
		Taam.BLOCK_SENSOR_MOTION,
		Taam.BLOCK_SENSOR_MINECT
	};
	
	public TaamBlock(int par1) {
		super(par1, Material.iron);
		this.setHardness(2);
		this.setResistance(6f);
		this.setStepSound(Block.soundMetalFootstep);
		MinecraftForge.setBlockHarvestLevel(this, "pickaxe", 0);
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

	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntitySensor();
		
	}
	
	@Override
	public boolean canProvidePower() {
		return true;
	}
	
	@Override
	public int isProvidingWeakPower(IBlockAccess par1iBlockAccess, int par2,
			int par3, int par4, int par5) {
		TileEntitySensor te = ((TileEntitySensor) par1iBlockAccess.getBlockTileEntity(par2, par3, par4));
		System.out.println(te.isPowering());
		return te.isPowering();
	}
	
	@Override
	public int isProvidingStrongPower(IBlockAccess par1iBlockAccess, int par2,
			int par3, int par4, int par5) {
		TileEntitySensor te = ((TileEntitySensor) par1iBlockAccess
				.getBlockTileEntity(par2, par3, par4));
		System.out.println(te.isPowering());
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
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4,
			EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
		// Update Owner
		if (par5EntityLivingBase instanceof EntityPlayer) {
			TileEntitySensor te = ((TileEntitySensor) par1World
					.getBlockTileEntity(par2, par3, par4));
			te.setOwner(((EntityPlayer) par5EntityLivingBase).username);
		}
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
