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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Base class for Taam's TileEntities. Keeps track of the block owner, manages
 * network updates and saving to/loading from disk.
 *
 * @author Oliver Kahrmann
 */
public abstract class BaseTileEntity extends TileEntity implements IWorldNameable, IPipePos {

	private UUID owner;
	/**
	 * ThreadLocal storage for the list of visible parts (required due to some
	 * concurrency issues, See issue #194)
	 */
	public static final ThreadLocal<List<String>> visibleParts = ThreadLocal.withInitial(() -> new ArrayList<>(14));

	/**
	 * Separate method as due to obfuscation issues we cannot use getPos from TileEntity
	 *
	 * @return the BlockPos of this entity
	 */
	@Override
	public BlockPos getPipePos() {
		return pos;
	}

	/**
	 * Separate method as due to obfuscation issues we cannot use getWorld from TileEntity
	 *
	 * @return the world instance related to this entity
	 */
	@Override
	public IBlockAccess getPipeWorld() {
		return getWorld();
	}

	/**
	 * Set the owner of the block to the given UUID.
	 *
	 * @param owner The player ID. Pass null to clear the owner.
	 */
	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	/**
	 * Set the owner of the block to the given player's UUID.
	 *
	 * @param player The player from which to ge the ID. Pass null to clear the owner.
	 */
	public void setOwner(EntityPlayer player) {
		if (player == null) {
			owner = null;
		} else {
			owner = player.getUniqueID();
		}
	}

	/**
	 * Get the owner's UUID.
	 *
	 * @return A UUID belonging to a player profile, or null, if the block does not have an owner.
	 */
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
	 * Override as needed.
	 */
	public void blockUpdate() {
		// Default implementation
	}

	/**
	 * Called within {@link BaseBlock#getActualState(IBlockState, IBlockAccess, BlockPos)}
	 * to update render state in the tile entity.
	 * Override as needed.
	 */
	public void renderUpdate() {
		// Default implementation
	}

	/*
	 * Networking
	 */

	@Override
	@SideOnly(Side.CLIENT)
	public void onDataPacket(@Nonnull NetworkManager net, @Nonnull SPacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.getNbtCompound();

		readPropertiesFromNBTInternal(nbt);
	}

	@Override
	@Nonnull
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writePropertiesToNBTInternal(nbt);
		return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), nbt);
	}

	@Nonnull
	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
		readFromNBT(tag);
	}

	/*
	 * NBT
	 */

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tag) {
		super.writeToNBT(tag);

		writePropertiesToNBTInternal(tag);
		return tag;
	}

	@Override
	public void readFromNBT(@Nonnull NBTTagCompound tag) {
		super.readFromNBT(tag);

		readPropertiesFromNBTInternal(tag);
	}

	/**
	 * Internal, writes common properties to NBT & calls the write method on the
	 * subclass.
	 *
	 * @param tag The destination tag, properties are written directly into this tag
	 */
	private void writePropertiesToNBTInternal(@Nonnull NBTTagCompound tag) {
		if (owner != null) {
			tag.setBoolean("owner", true);
			tag.setUniqueId("owner", owner);
		}
		writePropertiesToNBT(tag);
	}

	/**
	 * Write-method for subclasses to store their properties easily.
	 * This is automatically called during save and during network updates.
	 * Everything in this tag is written to the network update package and written to disk.
	 *
	 * @param tag The destination tag, properties are written directly into this tag
	 */
	protected abstract void writePropertiesToNBT(@Nonnull NBTTagCompound tag);

	/**
	 * Internal, reads common properties from NBT & calls the read method on the
	 * subclass.
	 *
	 * @param tag The source tag, properties are read directly from this tag
	 */
	private void readPropertiesFromNBTInternal(@Nonnull NBTTagCompound tag) {
		if (tag.getBoolean("owner")) {
			owner = tag.getUniqueId("owner");
		} else {
			owner = null;
		}
		readPropertiesFromNBT(tag);
	}

	/**
	 * Write-method for subclasses to read their properties easily.
	 * This is automatically called during load and during network updates.
	 *
	 * @param tag The source tag, properties are read directly from this tag
	 */
	protected abstract void readPropertiesFromNBT(@Nonnull NBTTagCompound tag);

}
