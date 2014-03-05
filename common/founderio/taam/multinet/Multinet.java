package founderio.taam.multinet;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import founderio.taam.blocks.multinet.MultinetCable;

public class Multinet {
	
	static List<Multinet> networks;
	
	static {
		networks = new ArrayList<Multinet>();
	}
	
	public static void addCableToNetwork(IMultinetAttachment cable) {
		List<IMultinetAttachment> surrounding = findNeighbors(cable);
		Multinet netToAdd = null;
		if(!surrounding.isEmpty()) {
			for(int i = 0; i < surrounding.size(); i++) {
				if(netToAdd == null) {
					netToAdd = surrounding.get(i).getNetwork();
				}
				if(surrounding.get(i).getNetwork() != null) {
					netToAdd.mergeOtherMultinet(surrounding.get(i).getNetwork());
				}
			}
			
		}
		if(netToAdd == null) {
			netToAdd = new Multinet();
			netToAdd.cableType = cable.getCableType();
			networks.add(netToAdd);
		}
		netToAdd.addCable(cable);
	}
	
	public static void removeFromNetwork(MultinetCable cable) {
		if(cable.getNetwork() != null) {
			Multinet network = cable.getNetwork();
			network.removeCable(cable);

			
			List<IMultinetAttachment> neighbors = findNeighbors(cable);
			if(neighbors.size() > 1) {
				IMultinetAttachment first = neighbors.get(0);
				
				for(int i = 1; i < neighbors.size(); i++) {
					splitNetworks(first, neighbors.get(i));
				}
			}
			
			if(network.cables.isEmpty()) {
				networks.remove(network);
				System.out.println("Multinet destroyed!");
				//TOO: invalidate net, that people referencing it do not use it anymore
			}
		}
	}
	
	private String cableType = "";
	private List<IMultinetAttachment> cables;
	
	private Multinet() {
		cables = new ArrayList<IMultinetAttachment>();
		System.out.println("Multinet created");
	}
	
	private void addCable(IMultinetAttachment cable) {
		cables.add(cable);
		cable.setNetwork(this);
		System.out.println("Cable added");
	}
	
	private void removeCable(IMultinetAttachment cable) {
		cables.remove(cable);
		cable.setNetwork(null);
		System.out.println("Cable removed");
	}
	
	private boolean mergeOtherMultinet(Multinet net) {
		if(this == net) {
			return false;
		}
		if(!net.cableType.equals(cableType)) {
			return false;
		}

		System.out.println("Merging Multinet...");
		
		for(IMultinetAttachment cable : net.cables) {
			addCable(cable);
		}
		net.cables.clear();
		networks.remove(net);
		System.out.println("Multinet merged");
		
		return true;
	}

	/**
	 * Cables per block side
	 */
	public static final int layerCount = 6;
	/**
	 * Width of cables (in times of block size -> 1/16 is one "pixel")
	 */
	public static final float cableWidth = 2f / 16f;
	
	public static int getHitLayer(ForgeDirection dir, Vector3 hit) {
		int layer = -1;
		switch(dir) {
		case DOWN:
		case UP:
			if(hit.x > hit.z) {
				layer = (int)(hit.x * layerCount);
			} else {
				layer = (int)(hit.z * layerCount);
			}
			break;
		case NORTH:
		case SOUTH:
			if(hit.y > hit.x) {
				layer = (int)(hit.y * layerCount);
			} else {
				layer = (int)(hit.x * layerCount);
			}
			break;
		case WEST:
		case EAST:
			if(hit.y > hit.z) {
				layer = (int)(hit.y * layerCount);
			} else {
				layer = (int)(hit.z * layerCount);
			}
			break;
		default:
			break;
		}
		return layer;
	}
	
	public static boolean findConnection(IMultinetAttachment a, IMultinetAttachment b) {
		if(!a.getCableType().equals(b.getCableType())) {
			return false;
		}
		return astar(a, b) != null;
	}
	
	public static class AStarNode<T> implements Comparable<AStarNode<T>> {
		public final T object;
		protected Integer dist = Integer.MAX_VALUE;
		protected Double value = 0d;
		protected AStarNode<T> predecessor;
		
		public AStarNode(T object, Double value) {
			this.object = object;
			this.value = value;
		}
		
		@Override
		public int compareTo(AStarNode<T> o) {
			return value.compareTo(o.value);
		}
		
		public AStarNode<T> getPredecessor() {
			return predecessor;
		}
		
		public int getStepDistance() {
			return dist;
		}
	}
	
	private static AStarNode<IMultinetAttachment> astar(IMultinetAttachment origin, IMultinetAttachment target) {
		PriorityQueue<AStarNode<IMultinetAttachment>> openlist = new PriorityQueue<AStarNode<IMultinetAttachment>>();
		Set<AStarNode<IMultinetAttachment>> closedlist = new HashSet<AStarNode<IMultinetAttachment>>();
		openlist.add(new AStarNode<IMultinetAttachment>(origin, 0d));
		
		AStarNode<IMultinetAttachment> current;
		
		BlockCoord bctarget = target.getCoordinates();
		BlockCoord bccurrent = new BlockCoord();
		
		do {
			current = openlist.remove();
			
			if(current.object == target) {
				return current;
			}
			
			closedlist.add(current);
			
			for(IMultinetAttachment successor : findNeighbors(current.object)) {
				// skip attachments that are already being processed
				boolean found = false;
				for(AStarNode<IMultinetAttachment> op : openlist) {
					if(op.object == successor) {
						found = true;
						break;
					}
				}
				for(AStarNode<IMultinetAttachment> op : closedlist) {
					if(op.object == successor) {
						found = true;
						break;
					}
				}
				if(found) {
					continue;
				}
				
				int tentative_g = current.dist + 1;
				
				AStarNode<IMultinetAttachment> foundS = null;
				for(AStarNode<IMultinetAttachment> op : closedlist) {
					if(op.object == successor) {
						found = true;
						foundS = op;
						break;
					}
				}
				if(found && tentative_g >= foundS.dist) {
					continue;
				}
				
				if(!found) {
					foundS = new AStarNode<IMultinetAttachment>(successor, 0d);
				}
				
				foundS.predecessor = current;
				foundS.dist = tentative_g;
				
				double f = tentative_g + bccurrent.set(current.object.getCoordinates()).sub(bctarget).mag();
				foundS.value = f;
				if(found) {
					openlist.remove(foundS);
				}
				openlist.add(foundS);
				
			}
			
			
		} while(!openlist.isEmpty());
		return null;
	}
	
	
	private static boolean splitNetworks(IMultinetAttachment a, IMultinetAttachment b) {
		if(a.getNetwork() != b.getNetwork()) {
			return false;
		}
		if(findConnection(a, b)) {
			return false;
		} else {
			
			Multinet netToAdd = new Multinet();
			netToAdd.cableType = a.getCableType();
			networks.add(netToAdd);
			
			Queue<IMultinetAttachment> cables = new ArrayDeque<IMultinetAttachment>();
			cables.add(a);
			
			do {
				IMultinetAttachment check = cables.remove();
				
				if(!netToAdd.cables.contains(check)) {
					netToAdd.addCable(check);
					
					cables.addAll(findNeighbors(check));
				}
				
			} while(!cables.isEmpty());
			
			return true;
		}
	}
	
	public static List<IMultinetAttachment> findNeighbors(IMultinetAttachment cable) {
		//TODO: Move to cable implementation (and create static method here to find cables or other machines connecting to a specific side)
		List<IMultinetAttachment> cbl = new ArrayList<IMultinetAttachment>();
		World world = cable.getDimension();
		ForgeDirection dir = cable.getFace();
		ForgeDirection[] otherDirs;
		/*
		 * Get valid connection sides
		 * (adjacent walls in same block and adjacent blocks on the same wall
		 * all use the same direction, just handling is different)
		 */
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
				cbl.addAll(getMultinetAttachments(world, cable.getCoordinates().add(od.offsetX, od.offsetY, od.offsetZ), cable.getLayer(), cable.getFace(), od.getOpposite(), cable.getCableType()));
			}
		}
		// Adjacent cables in the same block, on adjacent walls
		for(ForgeDirection od : otherDirs) {
			cbl.addAll(getMultinetAttachments(world, cable.getCoordinates(), cable.getLayer(), od, cable.getFace(), cable.getCableType()));
		}
		
		List<IMultinetAttachment> irregular = cable.getIrregularAttachments();
		if(irregular != null) {
			for(IMultinetAttachment att : irregular) {
				if(att.getCableType().equals(cable.getCableType())) {
					cbl.add(att);
				}
			}
		}
		
		return cbl;
	}
	
	public static List<IMultinetAttachment> getMultinetAttachments(World world, BlockCoord pos, int layer, ForgeDirection face, ForgeDirection dir, String type) {
		List<IMultinetAttachment> attachments = new ArrayList<IMultinetAttachment>(2);
		TileEntity te = world.getBlockTileEntity(pos.x, pos.y, pos.z);
		if(te instanceof TileMultipart) {
			List<TMultiPart> multiParts = ((TileMultipart) te).jPartList();
			
			for(TMultiPart part : multiParts) {
				if(part instanceof IMultinetAttachment) {
					IMultinetAttachment attach = (IMultinetAttachment)part;
					if(attach.canAttach(face, dir, layer, type)) {
						attachments.add(attach);
					}
				}
			}
		}
		return attachments;
	}
	
	public static MultinetCable getCable(World world, BlockCoord pos, int layer, ForgeDirection face, String type) {
		TileEntity te = world.getBlockTileEntity(pos.x, pos.y, pos.z);
		if(te instanceof TileMultipart) {
			List<TMultiPart> multiParts = ((TileMultipart) te).jPartList();
			
			for(TMultiPart part : multiParts) {
				if(part instanceof MultinetCable) {
					MultinetCable cable = (MultinetCable)part;
					if((type == null || cable.getType().equals(type))
							&& cable.getLayer() == layer
							&& cable.getFace() == face) {
						return cable;
					}
				}
			}
		}
		return null;
	}
}
