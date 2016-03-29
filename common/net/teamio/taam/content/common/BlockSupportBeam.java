package net.teamio.taam.content.common;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.teamio.taam.content.MaterialMachinesTransparent;

public class BlockSupportBeam extends Block {

	protected String baseName;

	public BlockSupportBeam() {
		super(MaterialMachinesTransparent.INSTANCE);
		this.setSoundType(SoundType.METAL);
		this.setHarvestLevel("pickaxe", 1);
		this.setResistance(3.7f);
		this.setHardness(2);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side){
		return true;
	}

}