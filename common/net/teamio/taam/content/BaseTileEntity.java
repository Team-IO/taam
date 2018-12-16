package net.teamio.taam.content;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.piping.IPipePos;
import net.teamio.taam.util.TaamUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Base class for Taam's TileEntities. Keeps track of the block owner, manages
 * network updates and saving to/loading from disk.
 *
 * @author oliverkahrmann
 */
public abstract class BaseTileEntity extends TileEntity implements IWorldNameable, IPipePos {

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

	/**
	 * Separate method as due to obfuscation issues we cannot use getPos from TileEntity
	 * @return
	 */
	@Override
	public BlockPos getPipePos() {
		return pos;
	}

	/**
	 * Separate method as due to obfuscation issues we cannot use getWorld from TileEntity
	 * @return
	 */
	@Override
	public IBlockAccess getPipeWorld() {
		return getWorld();
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
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(getName());
	}

	/**
	 * Updates block info & marks the containing block for update when on the
	 * server.
	 *
	 * @param worldUpdate  Send update to client (notify block change / send via network). Re-render is only requested if renderUpdate is true as well.
	 * @param renderUpdate Update rendering (client only)
	 * @param blockUpdate  Notify neighbor blocks (block update, also notifies observers)
	 */
	public final void updateState(boolean worldUpdate, boolean renderUpdate, boolean blockUpdate) {
		if (world == null) {
			return;
		}
		markDirty();
		if (worldUpdate) {
			// Server -> Client
			TaamUtil.updateBlock(world, pos, renderUpdate);
		}
		if (renderUpdate) {
			// Only client?
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
		if (blockUpdate) {
			world.notifyNeighborsOfStateChange(pos, blockType, true);
		}
	}

	/**
	 * Called inside {@link BaseBlock#neighborChanged(IBlockState, World, BlockPos, Block, BlockPos)}. (On server side)
	 */
	public void blockUpdate() {
	}

	/**
	 * Called within {@link BaseBlock#getActualState(IBlockState, IBlockAccess, BlockPos)}
	 * to update render state in the tile entity.
	 */
	public void renderUpdate() {
	}

	/*
	 * Networking
	 */

	@Override
	@SideOnly(Side.CLIENT)
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.getNbtCompound();

		readPropertiesFromNBTInternal(nbt);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writePropertiesToNBTInternal(nbt);
		return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), nbt);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		readFromNBT(tag);
	}

	/*
	 * NBT
	 */

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		writePropertiesToNBTInternal(tag);
		return tag;
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
			tag.setUniqueId("owner", owner);
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
		if (tag.getBoolean("owner")) {
			owner = tag.getUniqueId("owner");
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
