package net.teamio.taam.content.common;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.teamio.taam.content.BaseBlock;

public class BlockSoundEmitter extends BaseBlock {

	public BlockSoundEmitter(Material par2Material) {
		super(par2Material);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		// TODO Auto-generated method stub
		return true;
	}

}
