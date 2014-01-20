package founderio.taam.blocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.PacketDispatcher;

public abstract class BaseTileEntity extends TileEntity {


	private String owner = "";
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public final void updateState() {
		if (worldObj.isRemote) {
			return;
		}
		PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 128,
				worldObj.provider.dimensionId, getDescriptionPacket());
	}

	@Override
	public final Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writePropertiesToNBTInternal(nbt);

		Packet132TileEntityData packet = new Packet132TileEntityData(xCoord,
				yCoord, zCoord, 0, nbt);

		return packet;
	}

	@Override
	public final void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		NBTTagCompound nbt = pkt.data;

		readPropertiesFromNBTInternal(nbt);
	}

	@Override
	public final void writeToNBT(NBTTagCompound par1nbtTagCompound) {
		super.writeToNBT(par1nbtTagCompound);

		writePropertiesToNBTInternal(par1nbtTagCompound);
	}
	
	private void writePropertiesToNBTInternal(NBTTagCompound par1nbtTagCompound) {
		writePropertiesToNBT(par1nbtTagCompound);
	}

	protected abstract void writePropertiesToNBT(NBTTagCompound par1nbtTagCompound);

	@Override
	public final void readFromNBT(NBTTagCompound par1nbtTagCompound) {
		super.readFromNBT(par1nbtTagCompound);

		readPropertiesFromNBTInternal(par1nbtTagCompound);
	}
	
	private void readPropertiesFromNBTInternal(NBTTagCompound par1nbtTagCompound) {
		readPropertiesFromNBT(par1nbtTagCompound);
	}

	protected abstract void readPropertiesFromNBT(NBTTagCompound par1nbtTagCompound);
	
}
