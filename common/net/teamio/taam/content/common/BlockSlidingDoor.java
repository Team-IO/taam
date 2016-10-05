package net.teamio.taam.content.common;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.teamio.taam.content.BaseBlock;

public class BlockSlidingDoor extends BaseBlock {

	public BlockSlidingDoor() {
		super(Material.iron);
		setHardness(3.5f);
		setStepSound(soundTypeMetal);
		this.setHarvestLevel("pickaxe", 1);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntitySlidingDoor();
	}

	@Override
	public boolean isNormalCube(IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		// TODO Auto-generated method stub
		return true;
	}

}