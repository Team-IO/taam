package net.teamio.taam.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.teamio.taam.Log;
import net.teamio.taam.gui.advanced.ContainerAdvancedMachine;

import java.io.IOException;

public class TPAdvancedGuiAppData implements IMessage {

	public NBTTagCompound tag;
	public int appContainerId;

	public static final class Handler implements IMessageHandler<TPAdvancedGuiAppData, IMessage> {

		@Override
		public IMessage onMessage(TPAdvancedGuiAppData message, MessageContext ctx) {
			// route message to the open container of the player
			Container container;
			if (ctx.side == Side.CLIENT) {
				container = Minecraft.getMinecraft().player.openContainer;
			} else {
				container = ctx.getServerHandler().player.openContainer;
			}
			if (container instanceof ContainerAdvancedMachine) {
				ContainerAdvancedMachine containerAdvanced = (ContainerAdvancedMachine) container;
				containerAdvanced.onAppPacket(message);
			} else {
				Log.error("Error processing {}. Container {} is not a {}.",
						message, container, ContainerAdvancedMachine.class.getName());
			}
			return null;
		}
	}

	/**
	 * Constructor for serialization
	 */
	public TPAdvancedGuiAppData() {
	}

	/**
	 * Create a new packet with given tag and app container id.
	 *
	 * @param tag
	 * @param appContainerId
	 */
	public TPAdvancedGuiAppData(NBTTagCompound tag, int appContainerId) {
		super();
		this.tag = tag;
		this.appContainerId = appContainerId;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer packetBuffer = new PacketBuffer(buf);
		try {
			appContainerId = packetBuffer.readInt();
			tag = packetBuffer.readCompoundTag();
		} catch (IOException e) {
			Log.error("Error reading network packet", e);
		} finally {
			packetBuffer.release();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer packetBuffer = new PacketBuffer(buf);
		packetBuffer.writeInt(appContainerId);
		packetBuffer.writeCompoundTag(tag);
	}

}
