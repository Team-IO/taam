package founderio.taam.content.multinet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.TMultiPart;
import founderio.taam.multinet.IMultinetAttachment;
import founderio.taam.multinet.Multinet;
import founderio.taam.multinet.MultinetUtil;
import founderio.taam.util.WorldCoord;

public abstract class MultinetMultipart extends TMultiPart implements IMultinetAttachment {

	public MultinetMultipart(String cableType) {
		this.cableType = cableType;
	}

	/**
	 * 0-5 == Block Sides, 6 == Cable Rack
	 */
	protected ForgeDirection face;
	/**
	 * Layer index, 0-layerCount
	 */
	protected int layer = -1;
	protected String cableType;
	protected Multinet network;

	public boolean available = false;
	
	/*
	 * TMultiPart implementation
	 */

	@Override
	public void onWorldJoin() {
		if(world().isRemote) {
			return;
		}
		available = true;
		MultinetUtil.addToNetwork(this);
		sendDescUpdate();
	}
	
	@Override
	public void onWorldSeparate() {
		if(world().isRemote) {
			return;
		}
		available = false;
		MultinetUtil.removeFromNetwork(this);
	}
	
	@Override
	public void onNeighborChanged() {
		if(world().isRemote) {
			return;
		}
		if(!canStay(world(), x(), y(), z(), face)) {
			tile().dropItems(getDrops());
			tile().remPart(this);
		}
	}
	
	@Override
	public void save(NBTTagCompound tag) {
		super.save(tag);
		
		tag.setInteger("face", face.ordinal());
		tag.setInteger("layer", layer);
		tag.setString("type", cableType);
		
		saveProperties(tag);
	}
	
	@Override
	public void load(NBTTagCompound tag) {
		super.load(tag);
		
		face = ForgeDirection.getOrientation(tag.getInteger("face"));
		layer = tag.getInteger("layer");
		cableType = tag.getString("type");
		
		loadProperties(tag);
	}
	
	@Override
	public void writeDesc(MCDataOutput packet) {
		packet.writeInt(face.ordinal());
		packet.writeInt(layer);
		saveProperties(packet);
	}
	
	@Override
	public void readDesc(MCDataInput packet) {
		face = ForgeDirection.getOrientation(packet.readInt());
		layer = packet.readInt();
		loadProperties(packet);
	}
	
	protected abstract void saveProperties(NBTTagCompound tag);
	protected abstract void loadProperties(NBTTagCompound tag);
	protected abstract void saveProperties(MCDataOutput packet);
	protected abstract void loadProperties(MCDataInput packet);
	
	public abstract boolean canStay(World world, int x, int y, int z, ForgeDirection side);
	/**
	 * 
	 * @param blockCoords
	 * @param blockface
	 * @param hit The face of the block that was hit, on which the part should be placed on. (Actual face will be opposite of this!)
	 */
	public void init(BlockCoord blockCoords, int hitface, Vector3 hit) {
		ForgeDirection dir = ForgeDirection.getOrientation(hitface).getOpposite();
		this.face = dir;
		this.layer = MultinetUtil.getHitLayer(dir, hit);
	}
	
//	@Override
//	public String getType() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	/*
	 * IMultinetAttachment implementation
	 */
	
	@Override
	public WorldCoord getCoordinates() {
		return new WorldCoord(getTile());
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
		return cableType;
	}

	@Override
	public World getDimension() {
		return world();
	}

	@Override
	public void setNetwork(Multinet network) {
		this.network = network;
	}

	@Override
	public Multinet getNetwork() {
		return this.network;
	}

	@Override
	public boolean isAvailable() {
		return available;
	}

}
