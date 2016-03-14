package net.teamio.taam.content;

import java.util.UUID;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.teamio.taam.Log;

/**
 * Base class for Taam's TileEntities. Keeps track of the block owner, manages
 * network updates and saving to/loading from disk.
 * 
 * @author oliverkahrmann
 *
 */
public abstract class BaseTileEntity extends TileEntity {

	private UUID owner = null;

	public void setOwner(EntityPlayer player) {
		if(player == null) {
			owner = null;
		} else {
			owner = player.getUniqueID();
		}
	}
	
	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	public UUID getOwner() {
		return owner;
	}

	/**
	 * Updates block info & marks the containing block for update when on the
	 * server.
	 */
	public final void updateState() {
		if (worldObj.isRemote) {
			return;
		}
		markDirty();
		this.worldObj.markBlockForUpdate(pos);
	}
	
	public void updateRenderingInfo() {
		worldObj.markBlockRangeForRenderUpdate(pos, pos);
	}

	/*
	 * Networking
	 */

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.getNbtCompound();

		readPropertiesFromNBTInternal(nbt);
	}

	@Override
	public S35PacketUpdateTileEntity getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writePropertiesToNBTInternal(nbt);

		return new S35PacketUpdateTileEntity(pos, 0, nbt);
	}

	/*
	 * NBT
	 */

	@Override
	public final void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		writePropertiesToNBTInternal(tag);
	}

	@Override
	public final void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		readPropertiesFromNBTInternal(tag);
	}

	/**
	 * Internal, writes common properties to NBT & calls the write method on the
	 * subclass.
	 * 
	 * @param tag
	 */
	private void writePropertiesToNBTInternal(NBTTagCompound tag) {
		if(owner != null) {
			tag.setString("owner", owner.toString());
		}
		writePropertiesToNBT(tag);
	}

	/**
	 * Write-method for subclasses to store their properties easily.
	 * 
	 * @param tag
	 */
	protected abstract void writePropertiesToNBT(NBTTagCompound tag);

	/**
	 * Internal, reads common properties from NBT & calls the read method on the
	 * subclass.
	 * 
	 * @param tag
	 */
	private void readPropertiesFromNBTInternal(NBTTagCompound tag) {
		String ownerString = tag.getString("owner");
		if(ownerString != null && !ownerString.isEmpty()) {
			try {
				owner = UUID.fromString(ownerString);
			} catch (IllegalArgumentException e) {
				Log.warn("Trouble reading owner UUID. This might not be an issue. (Owner will be set to null. If this issue keeps reappering for the same blocks, notify the authors!");
				Log.LOGGER.catching(Level.DEBUG, e);
				owner = null;
			}
		}
		readPropertiesFromNBT(tag);
	}

	/**
	 * Write-method for subclasses to read their properties easily.
	 * 
	 * @param tag
	 */
	protected abstract void readPropertiesFromNBT(NBTTagCompound tag);

}
