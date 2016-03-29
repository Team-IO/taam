package net.teamio.taam.content.common;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.teamio.taam.content.BaseBlock;

public class BlockSlidingDoor extends BaseBlock {

	public BlockSlidingDoor() {
		super(Material.iron);
		this.setHardness(3.5f);
		this.setSoundType(SoundType.METAL);
		this.setHarvestLevel("pickaxe", 1);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntitySlidingDoor();
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}
	
	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		// TODO Auto-generated method stub
		return true;
	}

}