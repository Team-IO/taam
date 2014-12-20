package founderio.taam.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import founderio.taam.Taam;
import founderio.taam.conveyors.ConveyorUtil;
import founderio.taam.multinet.logistics.InBlockRoute;
import founderio.taam.rendering.TaamRenderer;

public class BlockMagnetRail extends Block {

	private IIcon connRight;
	private IIcon connLeft;
	private IIcon connBoth;
	
	private InBlockRoute[] routesRight;
	private InBlockRoute[] routesLeft;
	private InBlockRoute[] routesForward;
	
	public BlockMagnetRail() {
		super(Material.circuits);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
        this.setBlockTextureName(Taam.MOD_ID + ":magnet_rail");
        
        routesRight = new InBlockRoute[4];
        routesLeft = new InBlockRoute[4];
        routesForward = new InBlockRoute[4];
        
        //TODO: Validate these coordinates!
        routesForward[0] = new InBlockRoute(ForgeDirection.SOUTH, ForgeDirection.NORTH, new float[] {
        		0.5f, 0, 1,
        		0.5f, 0, 0
        });
        routesLeft[0] = new InBlockRoute(ForgeDirection.SOUTH, ForgeDirection.WEST, new float[] {
        		0.5f, 0, 1,
        		0.5f, 0, 0.5f,
        		1, 0, 0.5f
        });
        routesRight[0] = new InBlockRoute(ForgeDirection.SOUTH, ForgeDirection.EAST, new float[] {
        		0.5f, 0, 1,
        		0.5f, 0, 0.5f,
        		0, 0, 0.5f
        });
        for(int i = 1; i < 4; i++) {
        	routesForward[i] = routesForward[i-1].getRotated();
        	routesLeft[i] = routesLeft[i-1].getRotated();
        	routesRight[i] = routesRight[i-1].getRotated();
        }
	}
	
	@Override
	public void registerBlockIcons(IIconRegister register) {
		connRight = register.registerIcon(Taam.MOD_ID + ":magnet_rail_right");
		connLeft = register.registerIcon(Taam.MOD_ID + ":magnet_rail_left");
		connBoth = register.registerIcon(Taam.MOD_ID + ":magnet_rail_both");
		super.registerBlockIcons(register);
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
		return TaamRenderer.renderMagneticRailID;
	}
	
	@Override
	public IIcon getIcon(IBlockAccess world, int x,
			int y, int z, int meta) {
		int rotation = getRotation(world.getBlockMetadata(x, y, z));
		ForgeDirection direction = ForgeDirection.SOUTH;
		for(int i = 0; i < rotation; i++) {
			direction = direction.getRotation(ForgeDirection.UP);
		}
		ForgeDirection left = direction.getRotation(ForgeDirection.DOWN);
		ForgeDirection right = direction.getRotation(ForgeDirection.UP);
		boolean leftConnected = false;
		boolean rightConnected = false;
		
		Block block = world.getBlock(x + left.offsetX, y + left.offsetY, z + left.offsetZ);
		
		if(block == this) {
			int otherRotation = getRotation(world.getBlockMetadata(x + left.offsetX, y + left.offsetY, z + left.offsetZ));
			if(otherRotation == rotation - 1 || otherRotation == otherRotation + 3) {
				leftConnected = true;
			}
		}
		
		block = world.getBlock(x + right.offsetX, y + right.offsetY, z + right.offsetZ);
		
		if(block == this) {
			int otherRotation = getRotation(world.getBlockMetadata(x + right.offsetX, y + right.offsetY, z + right.offsetZ));
			if(otherRotation == rotation + 1 || otherRotation == otherRotation - 3) {
				rightConnected = true;
			}
		}
		
		if(rightConnected && leftConnected) {
			return connBoth;
		} else if(rightConnected) {
			return connRight;
		} else if(leftConnected) {
			return connLeft;
		} else {
			return blockIcon;
		}
	}
	
	public int getRotation(int meta) {
		return meta & 3;
	}
	
	public int setRotation(int meta, int rotation) {
		if(rotation > 3) {
			rotation = 0;
		}
		return ((meta | 3) - 3) | rotation;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x,
			int y, int z, EntityPlayer player,
			int side, float hitX, float hitY,
			float hitZ) {
		if(ConveyorUtil.playerHasWrench(player)) {
			if(player.isSneaking()) {
				//TODO: Drop Block
			} else {
				int meta = world.getBlockMetadata(x, y, z);
				int rotation = getRotation(meta);
				rotation ++;
				meta = setRotation(meta, rotation);
				world.setBlockMetadataWithNotify(x, y, z, meta, 1+2);
			}
		}
		// TODO Auto-generated method stub
		return super.onBlockActivated(world, x, y,
				z, player, side, hitX, hitY,
				hitZ);
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
}
