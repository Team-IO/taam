package net.teamio.taam.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.teamio.taam.content.IRedstoneControlled;
import net.teamio.taam.content.conveyors.TileEntityConveyorHopper;
import net.teamio.taam.util.WorldCoord;

public final class TPMachineConfiguration implements IMessage {

	public static final class Handler implements IMessageHandler<TPMachineConfiguration, IMessage> {

		@Override
		public IMessage onMessage(TPMachineConfiguration message, MessageContext ctx) {
			WorldServer world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.tileEntity.world);
			if(ctx.side == Side.SERVER) {
				TileEntity te = world.getTileEntity(message.tileEntity.pos());
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
						default:
							//TODO: Log Error
							break;
						}
					} else {
						//TODO: Log Error
					}
					break;
				default:
				case ChangeInteger:
					if(te instanceof IRedstoneControlled) {
						switch(message.id) {
						case 1:
							((IRedstoneControlled) te).setRedstoneMode((byte)message.intValue);
							break;
						default:
							//TODO: Log Error
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


	public enum Action {
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
		Action[] values = Action.values();
		mode = values[MathHelper.clamp(modeOrd, 0, values.length - 1)];
		tileEntity = WorldCoord.readCoords(buf);
		switch (mode) {
			case ChangeBoolean:
				id = buf.readByte();
				boolValue = buf.readBoolean();
			break;
		default:
		case ChangeInteger:
			id = buf.readByte();
			intValue = buf.readInt();
			break;
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(mode.ordinal());
		WorldCoord.writeCoords(buf, tileEntity);
		switch (mode) {
			case ChangeBoolean:
				buf.writeByte(id);
				buf.writeBoolean(boolValue);
			break;
		default:
		case ChangeInteger:
			buf.writeByte(id);
			buf.writeInt(intValue);
			break;
		}
	}

}
