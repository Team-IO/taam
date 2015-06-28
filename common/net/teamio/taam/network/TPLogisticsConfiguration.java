package net.teamio.taam.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.teamio.taam.content.logistics.EntityLogisticsCart;
import net.teamio.taam.content.logistics.TileEntityLogisticsStation;
import net.teamio.taam.util.WorldCoord;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public final class TPLogisticsConfiguration implements IMessage {

	public static final class Handler implements IMessageHandler<TPLogisticsConfiguration, IMessage> {

		@Override
		public IMessage onMessage(TPLogisticsConfiguration message, MessageContext ctx) {
			WorldServer world = MinecraftServer.getServer().worldServerForDimension(message.entityDimensionID);
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
	
	public static TPLogisticsConfiguration newConnectManager(WorldCoord station, WorldCoord manager) {
		TPLogisticsConfiguration pack = new TPLogisticsConfiguration();
		pack.mode = Action.ConnectManager;
		pack.station = station;
		pack.manager = manager;
		return pack;
	}
	
	public static TPLogisticsConfiguration newDisconnectManager(WorldCoord station) {
		TPLogisticsConfiguration pack = new TPLogisticsConfiguration();
		pack.mode = Action.DisconnectManager;
		pack.station = station;
		return pack;
	}
	
	public static TPLogisticsConfiguration newConnectManagerVehicle(int world, int entityID, WorldCoord manager) {
		TPLogisticsConfiguration pack = new TPLogisticsConfiguration();
		pack.mode = Action.ConnectManagerVehicle;
		pack.entityDimensionID = world;
		pack.entityID = entityID;
		pack.manager = manager;
		return pack;
	}
	
	public static TPLogisticsConfiguration newDisconnectManagerVehicle(int world, int entityID) {
		TPLogisticsConfiguration pack = new TPLogisticsConfiguration();
		pack.mode = Action.DisconnectManagerVehicle;
		pack.entityDimensionID = world;
		pack.entityID = entityID;
		return pack;
	}
	
	public TPLogisticsConfiguration() {
		// Serialization only.
	}

	int entityDimensionID;
	int entityID;
	private WorldCoord station;
	private WorldCoord manager;
	private Action mode;
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int modeOrd = buf.readInt();
		//TODO: Check Range
		mode = Action.values()[modeOrd];
		switch(mode) {
		case ConnectManager:
			station = readCoords(buf);
			manager = readCoords(buf);
			break;
		case DisconnectManager:
			station = readCoords(buf);
			break;
		case ConnectManagerVehicle:
			entityDimensionID = buf.readInt();
			entityID = buf.readInt();
			manager = readCoords(buf);
			break;
		case DisconnectManagerVehicle:
			entityDimensionID = buf.readInt();
			entityID = buf.readInt();
			break;
		}
	}
	
	private WorldCoord readCoords(ByteBuf buf) {
		int world = buf.readInt();
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		return new WorldCoord(world, x, y, z);
	}
	
	private void writeCoords(ByteBuf buf, WorldCoord coords) {
		buf.writeInt(coords.world);
		buf.writeInt(coords.x);
		buf.writeInt(coords.y);
		buf.writeInt(coords.z);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(mode.ordinal());
		switch(mode) {
		case ConnectManager:
			writeCoords(buf, station);
			writeCoords(buf, manager);
			return;
		case DisconnectManager:
			writeCoords(buf, station);
			break;
		case ConnectManagerVehicle:
			buf.writeInt(entityDimensionID);
			buf.writeInt(entityID);
			writeCoords(buf, manager);
			return;
		case DisconnectManagerVehicle:
			buf.writeInt(entityDimensionID);
			buf.writeInt(entityID);
			break;
		}
	}

}
