package founderio.taam.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import founderio.taam.multinet.logistics.WorldCoord;
import founderio.taam.rendering.TaamRenderer;

public class BlockMagnetRail extends Block {

	public IIcon connRight;
	public IIcon connLeft;
	public IIcon connForward;
	public IIcon connBase;
	
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
        routesForward[0] = new InBlockRoute(ForgeDirection.NORTH, ForgeDirection.SOUTH, new float[] {
        		0.5f, 0, 1,
        		0.5f, 0, 0
        });
        routesLeft[0] = new InBlockRoute(ForgeDirection.NORTH, ForgeDirection.EAST, new float[] {
        		0.5f, 0, 1,
        		0.5f, 0, 0.5f,
        		1, 0, 0.5f
        });
        routesRight[0] = new InBlockRoute(ForgeDirection.NORTH, ForgeDirection.WEST, new float[] {
        		0.5f, 0, 1,
        		0.5f, 0, 0.5f,
        		0, 0, 0.5f
        });
        // Create rotated variants for each direction
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
		connForward = register.registerIcon(Taam.MOD_ID + ":magnet_rail_forward");
		connBase = register.registerIcon(Taam.MOD_ID + ":magnet_rail_base");
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
		return blockIcon;
	}
	
	public List<InBlockRoute> getInBlockRoutes(IBlockAccess world, int x, int y, int z) {
		//TODO: Extract method
		if(world.getBlock(x, y, z) != this) {
			return Collections.emptyList();
		}
		
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
		
		
		//TODO: Optimize
		List<InBlockRoute> routes = new ArrayList<InBlockRoute>(3);
		
		if(rightConnected && leftConnected) {
			routes.add(routesForward[rotation]);
			routes.add(routesLeft[rotation]);
			routes.add(routesRight[rotation]);
		} else if(rightConnected) {
			routes.add(routesForward[rotation]);
			routes.add(routesRight[rotation]);
		} else if(leftConnected) {
			routes.add(routesForward[rotation]);
			routes.add(routesLeft[rotation]);
		} else {
			routes.add(routesForward[rotation]);
		}
		return routes;
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
				updateConnectionState(world, x, y, z);
			}
		}
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
	
	public void updateConnectionState(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		int rotation = getRotation(meta);

		// Get directions
		ForgeDirection direction = ForgeDirection.SOUTH;
		for (int i = 0; i < rotation; i++) {
			direction = direction.getRotation(ForgeDirection.UP);
		}
		ForgeDirection left = direction.getRotation(ForgeDirection.DOWN);
		ForgeDirection right = direction.getRotation(ForgeDirection.UP);

		// Check for neighbors
		boolean leftConnected = isNeighborConnected(world, x + left.offsetX, y + left.offsetY, z + left.offsetZ, 1, rotation);
		boolean rightConnected = isNeighborConnected(world, x + right.offsetX, y + right.offsetY, z + right.offsetZ, 2, rotation);

		// Correct metadata (change rendering & logic)
		int calculatedMeta = rotation + (leftConnected ? 4 : 0)
				+ (rightConnected ? 8 : 0);

		if (meta != calculatedMeta) {
			world.setBlockMetadataWithNotify(x, y, z, calculatedMeta, 3);
		}
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z,
			Block block) {
		if (!world.isRemote) {
			boolean flag = this.canPlaceBlockAt(world, x, y, z);

			if (!flag) {
				this.dropBlockAsItem(world, x, y, z, 0, 0);
				world.setBlockToAir(x, y, z);
			}
			
			updateConnectionState(world, x, y, z);

			super.onNeighborBlockChange(world, x, y, z, block);
		}
	}
	
	private boolean isNeighborConnected(IBlockAccess world, int nX, int nY, int nZ, int direction, int rotation) {
		Block neighborBlock = world.getBlock(nX, nY, nZ);
		if(neighborBlock != this) {
			return false;
		}
		int otherRotation = getRotation(world.getBlockMetadata(nX, nY, nZ));
		switch(direction) {
		case 0://Forward
			default:
			return rotation == otherRotation;
		case 1://Left
			return otherRotation == rotation - 1 || otherRotation == rotation + 3;
		case 2://Right
			return otherRotation == rotation + 1 || otherRotation == rotation - 3;
		}
	}

	public List<WorldCoord> getConnectedTracks(World world, int x, int y, int z, int meta) {
		
		int rotation = getRotation(world.getBlockMetadata(x, y, z));
		ForgeDirection direction = ForgeDirection.SOUTH;
		for(int i = 0; i < rotation; i++) {
			direction = direction.getRotation(ForgeDirection.UP);
		}
		ForgeDirection left = direction.getRotation(ForgeDirection.DOWN);
		ForgeDirection right = direction.getRotation(ForgeDirection.UP);
		
		// Check for neighbors
		boolean forwardConnected = isNeighborConnected(world, x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ, 0, rotation);
		boolean leftConnected = isNeighborConnected(world, x + left.offsetX, y + left.offsetY, z + left.offsetZ, 1, rotation);
		boolean rightConnected = isNeighborConnected(world, x + right.offsetX, y + right.offsetY, z + right.offsetZ, 2, rotation);
		
		WorldCoord leftC = new WorldCoord(world, x, y, z).getDirectionalOffset(left);
		WorldCoord forwardC = new WorldCoord(world, x, y, z).getDirectionalOffset(direction);
		WorldCoord rightC = new WorldCoord(world, x, y, z).getDirectionalOffset(right);
		
		//TODO: Optimize
		
		List<WorldCoord> coords = new ArrayList<WorldCoord>(3);
		
		if(rightConnected) {
			coords.add(rightC);
		}
		if(leftConnected) {
			coords.add(leftC);
		}
		if(forwardConnected) {
			coords.add(forwardC);
		}
		return coords;
		
	}
}
