package net.teamio.taam.logistics;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.logistics.TileEntityLogisticsManager;
import net.teamio.taam.util.WorldCoord;

public final class LogisticsUtil {
	private LogisticsUtil() {
		//Util Class
	}
	
	public static boolean isMagnetRail(IBlockAccess blockAccess, int x, int y, int z) {
		Block block = blockAccess.getBlock(x, y, z);
		return block == TaamMain.blockMagnetRail;
	}

	public static LogisticsManager getManager(IBlockAccess blockAccess, WorldCoord coordsManager) {
		TileEntity te = blockAccess.getTileEntity(coordsManager.x, coordsManager.y, coordsManager.z);
		if(te == null) {
			return null;
		}
		if(te instanceof TileEntityLogisticsManager) {
			return ((TileEntityLogisticsManager) te).getManager();
		} else {
			return null;
		}
	}
}
