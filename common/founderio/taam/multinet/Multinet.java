package founderio.taam.multinet;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import codechicken.lib.vec.BlockCoord;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import founderio.taam.blocks.multinet.MultinetCable;

public class Multinet {
	
	static List<Multinet> networks;
	
	static {
		networks = new ArrayList<Multinet>();
	}
	
	public static void addNetwork(MultinetCable cable) {
		List<MultinetCable> surrounding = findNeighbors(cable);
		Multinet netToAdd = null;
		if(!surrounding.isEmpty()) {
			for(int i = 0; i < surrounding.size(); i++) {
				if(netToAdd == null) {
					netToAdd = surrounding.get(i).network;
				}
				if(surrounding.get(i).network != null) {
					netToAdd.mergeOtherMultinet(surrounding.get(i).network);
				}
			}
			
		}
		if(netToAdd == null) {
			netToAdd = new Multinet();
			netToAdd.dimension = cable.world().provider.dimensionId;
			netToAdd.cableType = cable.getType();
			networks.add(netToAdd);
		}
		
		netToAdd.cables.add(cable);
		System.out.println("Cable added!");
		cable.network = netToAdd;
	}
	
	private String cableType = "";
	private List<MultinetCable> cables;
	private int dimension;
	
	public Multinet() {
		cables = new ArrayList<MultinetCable>();
		System.out.println("Multinet created!");
	}
	
	public void addCable(MultinetCable cable) {
		cables.add(cable);
		System.out.println("Cable added! (Manually)");
		cable.network = this;
	}
	
	public void removeCable(MultinetCable cable) {
		cables.remove(cable);
		System.out.println("Cable removed");
		cable.network = null;
	}
	
	public boolean mergeOtherMultinet(Multinet net) {
		if(this == net) {
			return false;
		}
		if(!net.cableType.equals(cableType)) {
			return false;
		}
		if(net.dimension != this.dimension) {
			return false;
		}
		System.out.println("Multinet merged!");
		
		for(MultinetCable cable : net.cables) {
			cable.network = this;
			cables.add(cable);
		}
		net.cables.clear();
		
		return true;
	}
	
	public boolean findConnection(MultinetCable a, MultinetCable b) {
		if(a.world().provider.dimensionId != b.world().provider.dimensionId) {
			return false;
		}
		if(a.world().provider.dimensionId != this.dimension) {
			return false;
		}
		//TODO: actually check if it still connects
		return false;
	}
	
	public boolean splitNetworks(MultinetCable a, MultinetCable b) {
		if(a.network != b.network) {
			return false;
		}
		if(findConnection(a, b)) {
			return false;
		} else {
			
			//TODO: actually split networks
			
			
			return true;
		}
	}
	
	public static List<MultinetCable> findNeighbors(MultinetCable cable) {
		List<MultinetCable> cbl = new ArrayList<MultinetCable>();
		World world = cable.world();
		ForgeDirection dir = ForgeDirection.getOrientation(cable.getFace());
		ForgeDirection[] otherDirs;
		switch(dir) {
		case DOWN:
		case UP:
			otherDirs = new ForgeDirection[] { ForgeDirection.EAST, ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST };
			break;
		case WEST:
		case EAST:
			otherDirs = new ForgeDirection[] { ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.DOWN };
			break;
		case NORTH:
		case SOUTH:
			otherDirs = new ForgeDirection[] { ForgeDirection.UP, ForgeDirection.WEST, ForgeDirection.EAST, ForgeDirection.DOWN };
			break;
		default:
			otherDirs = ForgeDirection.VALID_DIRECTIONS;
			break;
		}
		if(dir != ForgeDirection.UNKNOWN) {
			// Adjacent cables on the same wall, one block offset
			for(ForgeDirection od : otherDirs) {
				MultinetCable nc = getCable(world, new BlockCoord(cable.x() + od.offsetX, cable.y() + od.offsetY, cable.z() + od.offsetZ), cable.getLayer(), cable.getFace(), cable.getType());
				if(nc != null) {
					cbl.add(nc);
				}
			}
		}
		// Adjacent cables in the same block, on adjacent walls
		for(ForgeDirection od : otherDirs) {
			MultinetCable nc = getCable(world, new BlockCoord(cable.x(), cable.y(), cable.z()), cable.getLayer(), od.ordinal(), cable.getType());
			if(nc != null) {
				cbl.add(nc);
			}
		}
		
		return cbl;
	}
	
	public static MultinetCable getCable(World world, BlockCoord pos, int layer, int face, String type) {
		TileEntity te = world.getBlockTileEntity(pos.x, pos.y, pos.z);
		if(te instanceof TileMultipart) {
			List<TMultiPart> multiParts = ((TileMultipart) te).jPartList();
			
			for(TMultiPart part : multiParts) {
				if(part instanceof MultinetCable) {
					MultinetCable cable = (MultinetCable)part;
					if(cable.getType().equals(type) && cable.getLayer() == layer && cable.getFace() == face) {
						return cable;
					}
				}
			}
		}
		return null;
	}
}
