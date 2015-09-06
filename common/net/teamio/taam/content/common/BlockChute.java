package net.teamio.taam.content.common;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.teamio.taam.content.BaseBlock;

public class BlockChute extends BaseBlock {

	public BlockChute() {
		super(Material.rock);
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
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntityChute();
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world,
			int x, int y, int z) {
		this.minX = 0.10;
		this.minY = 0;
		this.minZ = 0.10;
		this.maxX = 0.9;
		this.maxY = 1;
		this.maxZ = 0.9;
	}

}
