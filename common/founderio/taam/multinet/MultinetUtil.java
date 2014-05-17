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
import net.minecraftforge.common.util.ForgeDirection;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import founderio.taam.blocks.multinet.MultinetCable;

public final class MultinetUtil {
	/**
	 * Cables per block side
	 */
	public static final int layerCount = 6;
	/**
	 * Width of cables (in times of block size -> 1/16 is one "pixel")
	 */
	public static final float cableWidth = 2f / 16f;

	private MultinetUtil() {
		// Util Class
	}

	public static void addToNetwork(IMultinetAttachment attachment) {
		List<IMultinetAttachment> surrounding = MultinetUtil.findNeighbors(attachment);
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
			netToAdd = new Multinet(attachment.getCableType());
		}
		netToAdd.addCable(attachment);
	}

	public static void removeFromNetwork(IMultinetAttachment attachment) {
		if(attachment.getNetwork() != null) {
			Multinet network = attachment.getNetwork();
			network.removeCable(attachment);
	
			
			List<IMultinetAttachment> neighbors = MultinetUtil.findNeighbors(attachment);
			if(neighbors.size() > 1) {
				IMultinetAttachment first = neighbors.get(0);
				
				for(int i = 1; i < neighbors.size(); i++) {
					MultinetUtil.splitNetworks(first, neighbors.get(i));
				}
			}
			
			if(network.isEmpty()) {
				network.destroy();
			}
		}
	}

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
		return MultinetUtil.astar(a, b) != null;
	}

	static AStarNode<IMultinetAttachment> astar(IMultinetAttachment origin, IMultinetAttachment target) {
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
			
			for(IMultinetAttachment successor : MultinetUtil.findNeighbors(current.object)) {
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

	static boolean splitNetworks(IMultinetAttachment a, IMultinetAttachment b) {
		if(a.getNetwork() != b.getNetwork()) {
			return false;
		}
		if(findConnection(a, b)) {
			return false;
		} else {
			
			Multinet netToAdd = new Multinet(a.getCableType());
			
			Queue<IMultinetAttachment> cables = new ArrayDeque<IMultinetAttachment>();
			cables.add(a);
			
			do {
				IMultinetAttachment check = cables.remove();
				
				if(!netToAdd.contains(check)) {
					netToAdd.addCable(check);
					
					cables.addAll(MultinetUtil.findNeighbors(check));
				}
				
			} while(!cables.isEmpty());
			
			return true;
		}
	}

	public static List<IMultinetAttachment> findNeighbors(IMultinetAttachment attachment) {
		List<IMultinetAttachment> cbl = new ArrayList<IMultinetAttachment>();
		World world = attachment.getDimension();
		ForgeDirection dir = attachment.getFace();
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
		// UNKWOWN == cable rack
		if(dir != ForgeDirection.UNKNOWN) {
			// Adjacent cables on the same wall, one block offset
			for(ForgeDirection od : otherDirs) {
				cbl.addAll(MultinetUtil.getMultinetAttachments(world, attachment.getCoordinates().add(od.offsetX, od.offsetY, od.offsetZ), attachment.getLayer(), attachment.getFace(), od.getOpposite(), attachment.getCableType(), false));
			}
		}
		// Adjacent cables in the same block, on adjacent walls
		for(ForgeDirection od : otherDirs) {
			cbl.addAll(MultinetUtil.getMultinetAttachments(world, attachment.getCoordinates(), attachment.getLayer(), od, attachment.getFace(), attachment.getCableType(), false));
		}
		// Adjacent cables in the same block, on the same face (multitronix & Co.)
		cbl.addAll(MultinetUtil.getMultinetAttachments(world, attachment.getCoordinates(), attachment.getLayer(), attachment.getFace(), ForgeDirection.UNKNOWN, attachment.getCableType(), false));
		
		// Irregular connections (teleport, cross-layer, ...)
		List<IMultinetAttachment> irregular = attachment.getIrregularAttachments();
		if(irregular != null) {
			for(IMultinetAttachment att : irregular) {
				if(att.isAvailable() && att.getCableType().equals(attachment.getCableType())) {
					cbl.add(att);
				}
			}
		}
		// Just to be sure not to cause loops in the code ;)
		cbl.remove(attachment);
		return cbl;
	}

	public static List<IMultinetAttachment> getMultinetAttachments(World world, BlockCoord pos, int layer, ForgeDirection face, ForgeDirection dir, String type, boolean includeUnavailable) {
		List<IMultinetAttachment> attachments = new ArrayList<IMultinetAttachment>(2);
		TileEntity te = world.getTileEntity(pos.x, pos.y, pos.z);
		if(te instanceof TileMultipart) {
			List<TMultiPart> multiParts = ((TileMultipart) te).jPartList();
			
			for(TMultiPart part : multiParts) {
				if(part instanceof IMultinetAttachment) {
					IMultinetAttachment attach = (IMultinetAttachment)part;
					if((includeUnavailable || attach.isAvailable()) && attach.canAttach(pos, face, dir, layer, type)) {
						attachments.add(attach);
					}
				}
			}
		}
		return attachments;
	}

	public static MultinetCable getCable(World world, BlockCoord pos, int layer, ForgeDirection face, String type) {
		
		TileEntity te = world.getTileEntity(pos.x, pos.y, pos.z);
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
	
	public static boolean canCableStay(World world, int x, int y, int z, ForgeDirection side) {
		return world.isSideSolid(x + side.offsetX, y + side.offsetY, z + side.offsetZ, side.getOpposite(), false);
	}
}
