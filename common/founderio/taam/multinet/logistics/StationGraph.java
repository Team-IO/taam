package founderio.taam.multinet.logistics;

import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import codechicken.lib.vec.BlockCoord;
import founderio.taam.TaamMain;
import founderio.taam.blocks.TileEntityLogisticsStation;
import founderio.taam.multinet.AStar;
import founderio.taam.multinet.AStar.Node;

public class StationGraph {

	public class Navigator implements AStar.Navigator<BlockCoord> {

		private Navigator() {

		}

		@Override
		public BlockCoord getCoords(BlockCoord object) {
			return object;
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<BlockCoord> findNeighbors(BlockCoord object) {
			// TODO: Use InBlockRoutes
			// TODO: We need the previous route here...
			return TaamMain.blockMagnetRail.getConnectedTracks(world, object.x, object.y, object.z, world.getBlockMetadata(object.x, object.y, object.z));
		}

	}

	private Navigator navigator = new Navigator();
	private World world;
	
	public StationGraph(World world) {
		this.world = world;
	}


	public BlockCoord getTrackForStation(IStation station) {
		//TODO: Cleanup this mess
		TileEntityLogisticsStation te = (TileEntityLogisticsStation)station;
		ForgeDirection right = te.getFacingDirection().getRotation(ForgeDirection.UP);
		BlockCoord trackCoords = new BlockCoord(te.xCoord + right.offsetX, te.yCoord + right.offsetY, te.zCoord + right.offsetZ);
		boolean isRail = LogisticsUtil.isMagnetRail(world, trackCoords.x, trackCoords.y, trackCoords.z);
		if(isRail) {
			return trackCoords;
		} else {
			return null;
		}
	}

	public Node<BlockCoord> astar(BlockCoord origin, BlockCoord target) {
		return AStar.astar(origin, target, navigator);
	}
}
