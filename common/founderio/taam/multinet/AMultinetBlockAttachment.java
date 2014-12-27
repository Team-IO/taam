package founderio.taam.multinet;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import founderio.taam.multinet.logistics.WorldCoord;

public abstract class AMultinetBlockAttachment implements IMultinetAttachment {

	private Multinet network;
	
	private int x;
	private int y;
	private int z;
	
	private World world;
	
	private ForgeDirection face;
	private ForgeDirection dir;
	private int layer;
	private String type;
	
	private boolean available = false;
	
	public AMultinetBlockAttachment(World world, int x, int y, int z, ForgeDirection face, ForgeDirection dir,
			int layer, String type) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.face = face;
		this.dir = dir;
		this.type = type;
		this.layer = layer;
	}
	
	
	@Override
	public boolean canAttach(WorldCoord coords, ForgeDirection face, ForgeDirection dir,
			int layer, String type) {
		return face == this.face && dir == this.dir && layer == this.layer && this.type.equals(type);
	}

	@Override
	public WorldCoord getCoordinates() {
		return new WorldCoord(world, x, y, z);
	}

	@Override
	public int getLayer() {
		return layer;
	}

	@Override
	public ForgeDirection getFace() {
		return face;
	}

	@Override
	public String getCableType() {
		return type;
	}

	@Override
	public World getDimension() {
		return world;
	}

	@Override
	public void setNetwork(Multinet network) {
		this.network = network;
	}

	@Override
	public Multinet getNetwork() {
		return network;
	}

	public void connect() {
		available = true;
		MultinetUtil.addToNetwork(this);
	}

	public void disconnect() {
		available = false;
		MultinetUtil.removeFromNetwork(this);
	}
	
	@Override
	public boolean isAvailable() {
		return available;
	}
	
}
