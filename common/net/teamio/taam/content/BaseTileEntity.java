package net.teamio.taam.content;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.IWorldNameable;
import net.teamio.taam.util.TaamUtil;

/**
 * Base class for Taam's TileEntities. Keeps track of the block owner, manages
 * network updates and saving to/loading from disk.
 *
 * @author oliverkahrmann
 *
 */
public abstract class BaseTileEntity extends TileEntity implements IWorldNameable {

	private UUID owner = null;
	/**
	 * ThreadLocal storage for the list of visible parts (required due to some
	 * concurrency issues, See issue #194)
	 */
	public static final ThreadLocal<List<String>> visibleParts = new ThreadLocal<List<String>>() {
		@Override
		protected List<String> initialValue() {
			return new ArrayList<String>(14);
		}
	};

	public void setOwner(EntityPlayer player) {
		if (player == null) {
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

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public IChatComponent getDisplayName() {
		return new ChatComponentTranslation(getName());
	}

	/**
	 * Updates block info & marks the containing block for update when on the
	 * server.
	 *
	 * @param worldUpdate
	 *            Send update to client (notify block change / send via network)
	 * @param renderUpdate
	 *            Update rendering (client only)
	 * @param blockUpdate
	 *            Notify neighbor blocks (block update)
	 */
	public final void updateState(boolean worldUpdate, boolean renderUpdate, boolean blockUpdate) {
		if (worldObj == null) {
			return;
		}
		markDirty();
		if (worldUpdate) {
			TaamUtil.updateBlock(worldObj, pos);
		}
		if (renderUpdate) {
			worldObj.markBlockRangeForRenderUpdate(pos, pos);
		}
		if (blockUpdate) {
			worldObj.notifyNeighborsOfStateChange(pos, blockType);
		}
	}

	/**
	 * Called inside
	 * {@link BaseBlock#onNeighborBlockChange(net.minecraft.world.World, net.minecraft.util.BlockPos, net.minecraft.block.state.IBlockState, net.minecraft.block.Block)}
	 * . (On server side)
	 */
	public void blockUpdate() {
	}

	/**
	 * Called within
	 * {@link BaseBlock#getActualState(net.minecraft.block.state.IBlockState, net.minecraft.world.IBlockAccess, net.minecraft.util.BlockPos)}
	 * to update render state in the tile entity.
	 */
	public void renderUpdate() {
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
		return new S35PacketUpdateTileEntity(getPos(), getBlockMetadata(), nbt);
	}

	/*
	 * NBT
	 */

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		writePropertiesToNBTInternal(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
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
		if (owner != null) {
			tag.setBoolean("owner", true);
			TaamUtil.setUniqueId(tag, "owner", owner);
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
		if(tag.getBoolean("owner")) {
			owner = TaamUtil.getUniqueId(tag, "owner");
		} else {
			owner = null;
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
