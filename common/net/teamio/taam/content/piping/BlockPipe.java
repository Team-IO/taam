package net.teamio.taam.content.piping;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.teamio.taam.content.BaseBlock;
import net.teamio.taam.content.MaterialMachinesTransparent;

public class BlockPipe extends BaseBlock implements ITileEntityProvider {

	public BlockPipe() {
		super(MaterialMachinesTransparent.INSTANCE);
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityPipe();
	}

}
