package founderio.taam.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import codechicken.lib.vec.BlockCoord;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import founderio.taam.blocks.TileEntityLogisticsStation;
import founderio.taam.entities.EntityLogisticsCart;

public final class TPLogisticsConfiguration implements IMessage {

	public static final class Handler implements IMessageHandler<TPLogisticsConfiguration, IMessage> {

		@Override
		public IMessage onMessage(TPLogisticsConfiguration message, MessageContext ctx) {
			WorldServer world = MinecraftServer.getServer().worldServerForDimension(message.world);
			if(ctx.side == Side.SERVER) {
				switch(message.mode) {
				case ConnectManager:
					
					//TODO: Move to central function?
					TileEntity te = world.getTileEntity(message.station.x, message.station.y, message.station.z);
					if(te instanceof TileEntityLogisticsStation) {
						((TileEntityLogisticsStation) te).linkToManager(message.manager);
					} else {
						//TODO: Log Error
					}
				case ConnectManagerVehicle:
					//TODO: Move to central function?
					Entity ent = world.getEntityByID(message.entityID);
					if(ent instanceof EntityLogisticsCart) {
						((EntityLogisticsCart) ent).linkToManager(message.manager);
					} else {
						//TODO: Log Error
					}
				case DisconnectManager:
					//TODO: disconnect.
					break;
				case DisconnectManagerVehicle:
					//TODO: disconnect.
					break;
				}
			}
			return null;
		}

	}
	
	
	public static enum Action {
		ConnectManager,
		DisconnectManager,
		ConnectManagerVehicle,
		DisconnectManagerVehicle
	}
	
	public static TPLogisticsConfiguration newConnectManager(int world, BlockCoord station, BlockCoord manager) {
		TPLogisticsConfiguration pack = new TPLogisticsConfiguration();
		pack.mode = Action.ConnectManager;
		pack.world = world;
		pack.station = station;
		pack.manager = manager;
		return pack;
	}
	
	public static TPLogisticsConfiguration newDisconnectManager(int world, BlockCoord station) {
		TPLogisticsConfiguration pack = new TPLogisticsConfiguration();
		pack.mode = Action.DisconnectManager;
		pack.world = world;
		pack.station = station;
		return pack;
	}
	
	public static TPLogisticsConfiguration newConnectManagerVehicle(int world, int entityID, BlockCoord manager) {
		TPLogisticsConfiguration pack = new TPLogisticsConfiguration();
		pack.mode = Action.ConnectManagerVehicle;
		pack.world = world;
		pack.entityID = entityID;
		pack.manager = manager;
		return pack;
	}
	
	public static TPLogisticsConfiguration newDisconnectManagerVehicle(int world, int entityID) {
		TPLogisticsConfiguration pack = new TPLogisticsConfiguration();
		pack.mode = Action.DisconnectManagerVehicle;
		pack.world = world;
		pack.entityID = entityID;
		return pack;
	}
	
	public TPLogisticsConfiguration() {
		// Serialization only.
	}

	int world;
	int entityID;
	private BlockCoord station;
	private BlockCoord manager;
	private Action mode;
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int modeOrd = buf.readInt();
		//TODO: Check Range
		mode = Action.values()[modeOrd];
		switch(mode) {
		case ConnectManager:
			world = buf.readInt();
			station = readCoords(buf);
			manager = readCoords(buf);
			break;
		case DisconnectManager:
			world = buf.readInt();
			station = readCoords(buf);
			break;
		case ConnectManagerVehicle:
			world = buf.readInt();
			entityID = buf.readInt();
			manager = readCoords(buf);
			break;
		case DisconnectManagerVehicle:
			world = buf.readInt();
			entityID = buf.readInt();
			break;
		}
	}
	
	private BlockCoord readCoords(ByteBuf buf) {
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		return new BlockCoord(x, y, z);
	}
	
	private void writeCoords(ByteBuf buf, BlockCoord coords) {
		buf.writeInt(coords.x);
		buf.writeInt(coords.y);
		buf.writeInt(coords.z);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(mode.ordinal());
		switch(mode) {
		case ConnectManager:
			buf.writeInt(world);
			writeCoords(buf, station);
			writeCoords(buf, manager);
			return;
		case DisconnectManager:
			buf.writeInt(world);
			writeCoords(buf, station);
			break;
		case ConnectManagerVehicle:
			buf.writeInt(world);
			buf.writeInt(entityID);
			writeCoords(buf, manager);
			return;
		case DisconnectManagerVehicle:
			buf.writeInt(world);
			buf.writeInt(entityID);
			break;
		}
	}

}
