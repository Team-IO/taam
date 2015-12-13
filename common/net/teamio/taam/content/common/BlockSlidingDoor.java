package net.teamio.taam.content.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.teamio.taam.content.BaseBlock;

public class BlockSlidingDoor extends BaseBlock {

	public BlockSlidingDoor() {
		super(Material.iron);
		this.setHardness(3.5f);
		this.setStepSound(Block.soundTypeMetal);
		this.setHarvestLevel("pickaxe", 1);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntitySlidingDoor();
	}

	@Override
	public boolean isNormalCube() {
		return false;
	}

}