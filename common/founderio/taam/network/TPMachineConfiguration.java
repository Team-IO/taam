package founderio.taam.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import founderio.taam.content.conveyors.TileEntityConveyorHopper;
import founderio.taam.conveyors.api.IRedstoneControlled;
import founderio.taam.util.WorldCoord;

public final class TPMachineConfiguration implements IMessage {

	public static final class Handler implements IMessageHandler<TPMachineConfiguration, IMessage> {

		@Override
		public IMessage onMessage(TPMachineConfiguration message, MessageContext ctx) {
			WorldServer world = MinecraftServer.getServer().worldServerForDimension(message.tileEntity.world);
			if(ctx.side == Side.SERVER) {
				TileEntity te = world.getTileEntity(message.tileEntity.x, message.tileEntity.y, message.tileEntity.z);
				switch(message.mode) {
				case ChangeBoolean:
					
					if(te instanceof TileEntityConveyorHopper) {
						switch(message.id) {
						case 1:
							((TileEntityConveyorHopper) te).setEject(message.boolValue);
							break;
						case 2:
							((TileEntityConveyorHopper) te).setStackMode(message.boolValue);
							break;
						case 3:
							((TileEntityConveyorHopper) te).setLinearMode(message.boolValue);
							break;
						}
					} else {
						//TODO: Log Error
					}
					break;
				case ChangeInteger: 
					if(te instanceof IRedstoneControlled) {
						switch(message.id) {
						case 1:
							((IRedstoneControlled) te).setRedstoneMode((byte)message.intValue);
							break;
						}
					} else {
						//TODO: Log Error
					}
				}
			}
			return null;
		}

	}
	
	
	public static enum Action {
		ChangeBoolean,
		ChangeInteger
	}
	
	public static TPMachineConfiguration newChangeBoolean(WorldCoord tileEntity, byte id, boolean value) {
		TPMachineConfiguration pack = new TPMachineConfiguration();
		pack.mode = Action.ChangeBoolean;
		pack.tileEntity = tileEntity;
		pack.boolValue = value;
		pack.id = id;
		return pack;
	}
	
	public static TPMachineConfiguration newChangeInteger(WorldCoord tileEntity, byte id, int value) {
		TPMachineConfiguration pack = new TPMachineConfiguration();
		pack.mode = Action.ChangeInteger;
		pack.tileEntity = tileEntity;
		pack.intValue = value;
		pack.id = id;
		return pack;
	}
	
	public TPMachineConfiguration() {
		// Serialization only.
	}

	private WorldCoord tileEntity;
	private boolean boolValue;
	private int intValue;
	private Action mode;
	private byte id;
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int modeOrd = buf.readInt();
		//TODO: Check Range
		mode = Action.values()[modeOrd];
		tileEntity = readCoords(buf);
		switch(mode) {
		case ChangeBoolean:
			id = buf.readByte();
			boolValue = buf.readBoolean();
			break;
		case ChangeInteger:
			id = buf.readByte();
			intValue = buf.readInt();
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
		writeCoords(buf, tileEntity);
		switch(mode) {
		case ChangeBoolean:
			buf.writeByte(id);
			buf.writeBoolean(boolValue);
			return;
		case ChangeInteger:
			buf.writeByte(id);
			buf.writeInt(intValue);
			return;
		}
	}

}
