package net.teamio.taam.content.common;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.teamio.taam.content.MaterialMachinesTransparent;

public class BlockSupportBeam extends Block {

	protected String baseName;

	public BlockSupportBeam() {
		super(MaterialMachinesTransparent.INSTANCE);
		setStepSound(Block.soundTypeMetal);
		this.setHarvestLevel("pickaxe", 1);
		setResistance(3.7f);
		setHardness(2);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side){
		return true;
	}

}