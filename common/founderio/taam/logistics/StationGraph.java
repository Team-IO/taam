package founderio.taam.logistics;

import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import founderio.taam.TaamMain;
import founderio.taam.content.logistics.TileEntityLogisticsStation;
import founderio.taam.util.AStar;
import founderio.taam.util.WorldCoord;
import founderio.taam.util.AStar.Node;

public class StationGraph {

	public class Navigator implements AStar.Navigator<WorldCoord> {

		@Override
		public WorldCoord getCoords(WorldCoord object) {
			return object;
		}

		@Override
		public List<WorldCoord> findNeighbors(WorldCoord object, Node<WorldCoord> predecessor) {
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

	public WorldCoord getTrackForStation(IStation station) {
		//TODO: Cleanup this mess
		TileEntityLogisticsStation te = (TileEntityLogisticsStation)station;
		ForgeDirection right = te.getFacingDirection().getRotation(ForgeDirection.UP);
		WorldCoord trackCoords = new WorldCoord(te).getDirectionalOffset(right);
		boolean isRail = LogisticsUtil.isMagnetRail(world, trackCoords.x, trackCoords.y, trackCoords.z);
		if(isRail) {
			return trackCoords;
		} else {
			return null;
		}
	}

	public Node<WorldCoord> astar(WorldCoord origin, WorldCoord target) {
		return AStar.astar(origin, target, navigator);
	}
}
