package net.teamio.taam.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Coordinates for a block inside a world. Includes the dimension ID. Used for
 * routing over the network.
 *
 * @author Oliver Kahrmann
 */
public class WorldCoord {
	public int world;
	public int x;
	public int y;
	public int z;

	public WorldCoord() {
		this(0, 0, 0, 0);
	}

	public WorldCoord(int world, int x, int y, int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public WorldCoord(World world, int x, int y, int z) {
		this(world.provider.getDimension(), x, y, z);
	}

	public WorldCoord(World world, BlockPos pos) {
		this(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ());
	}

	public WorldCoord(TileEntity tile) {
		this(tile.getWorld(), tile.getPos());
	}

	public WorldCoord(int[] data) {
		this(data[0], data[1], data[2], data[3]);
	}

	public int[] toData() {
		return new int[]{world, x, y, z};
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("world", world);
		tag.setInteger("x", x);
		tag.setInteger("y", y);
		tag.setInteger("z", z);
	}

	public WorldCoord readFromNBT(NBTTagCompound tag) {
		world = tag.getInteger("world");
		x = tag.getInteger("x");
		y = tag.getInteger("y");
		z = tag.getInteger("z");
		return this;
	}

	public WorldServer getWorldServer() {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(world);
	}

	public WorldClient getWorldClient() {
		WorldClient worldClient = Minecraft.getMinecraft().world;
		if (worldClient.provider.getDimension() == world) {
			return worldClient;
		}
		return null;
	}

	public WorldCoord getDirectionalOffset(EnumFacing direction) {
		return new WorldCoord(world, x + direction.getXOffset(), y + direction.getYOffset(), z + direction.getZOffset());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + world;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		WorldCoord other = (WorldCoord) obj;
		if (world != other.world) {
			return false;
		}
		return x == other.x && z == other.z;
	}

	public WorldCoord set(WorldCoord coords) {
		world = coords.world;
		x = coords.x;
		y = coords.y;
		z = coords.z;
		return this;
	}

	public WorldCoord subtract(WorldCoord coords) {
		return subtract(coords.x, coords.y, coords.z);
	}

	public WorldCoord subtract(BlockPos coords) {
		return subtract(coords.getX(), coords.getY(), coords.getZ());
	}

	public WorldCoord subtract(int x, int y, int z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	public double mag() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public int mag2() {
		return x * x + y * y + z * z;
	}

	public boolean isZero() {
		return x == 0 && y == 0 && z == 0;
	}

	public BlockPos pos() {
		return new BlockPos(x, y, z);
	}

	public static void writeCoords(ByteBuf buf, WorldCoord coords) {
		buf.writeInt(coords.world);
		buf.writeInt(coords.x);
		buf.writeInt(coords.y);
		buf.writeInt(coords.z);
	}

	public static WorldCoord readCoords(ByteBuf buf) {
		int world = buf.readInt();
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		return new WorldCoord(world, x, y, z);
	}
}
