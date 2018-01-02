package net.teamio.taam.piping;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Created by oliver on 2017-12-07.
 */
public interface IPipePos {

	public static class Constant implements IPipePos {

		public BlockPos pos;
		public IBlockAccess world;

		public Constant(IBlockAccess world, BlockPos pos) {
			this.world = world;
			this.pos = pos;
		}

		@Override
		public BlockPos getPos() {
			return pos;
		}

		@Override
		public IBlockAccess getWorld() {
			return world;
		}
	}

	BlockPos getPos();

	IBlockAccess getWorld();
}
