package founderio.taam.multinet.logistics;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import founderio.taam.blocks.BlockMagnetRail;

public final class LogisticsUtil {
	private LogisticsUtil() {
		//Util Class
	}
	
	public static boolean isMagnetRail(IBlockAccess blockAccess, int x, int y, int z) {
		Block block = blockAccess.getBlock(x, y, z);
		return block instanceof BlockMagnetRail;
	}
}
