package founderio.taam.blocks;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import founderio.taam.Taam;
import founderio.taam.conveyors.IRotatable;
import founderio.taam.multinet.logistics.IStation;
import founderio.taam.multinet.logistics.ITrack;

public class BlockMagnetRail extends Block implements ITrack, IRotatable {

	public BlockMagnetRail() {
		super(Material.circuits);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
        this.setBlockTextureName(Taam.MOD_ID + ":magnet_rail");
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public int getRenderType() {
		// Render Type for Lily Pad
		return 23;
	}
	
	@Override
	public IIcon getIcon(IBlockAccess p_149673_1_, int p_149673_2_,
			int p_149673_3_, int p_149673_4_, int p_149673_5_) {
		// TODO Auto-generated method stub
		return super.getIcon(p_149673_1_, p_149673_2_, p_149673_3_, p_149673_4_,
				p_149673_5_);
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)
        		|| world.getBlock(x, y - 1, z) == Blocks.glowstone
        		|| world.getBlock(x, y - 1, z) == Blocks.glass;
    }
	
	@Override
	public void onNeighborBlockChange(World world, int x,
			int y, int z, Block block) {
		 if (!world.isRemote)
	        {
	            boolean flag = this.canPlaceBlockAt(world, x, y, z);

	            if (!flag)
	            {
	                this.dropBlockAsItem(world, x, y, z, 0, 0);
	                world.setBlockToAir(x, y, z);
	            }

	            super.onNeighborBlockChange(world, x, y, z, block);
	        }
	}

	@Override
	public ForgeDirection getFacingDirection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ForgeDirection getMountDirection() {
		return ForgeDirection.DOWN;
	}

	@Override
	public ITrack[] getConnectedTracks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IStation, Integer> getLocatedStations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ForgeDirection getNextFacingDirection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ForgeDirection getNextMountDirection() {
		return ForgeDirection.DOWN;
	}

	@Override
	public void setFacingDirection(ForgeDirection direction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMountDirection(ForgeDirection direction) {
		return;
	}
}
