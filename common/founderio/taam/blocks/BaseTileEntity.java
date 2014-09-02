package founderio.taam.blocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

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
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.func_148857_g();

		readPropertiesFromNBTInternal(nbt);
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writePropertiesToNBTInternal(nbt);
		
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public final void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		writePropertiesToNBTInternal(tag);
	}
	
	private void writePropertiesToNBTInternal(NBTTagCompound tag) {
		writePropertiesToNBT(tag);
	}

	protected abstract void writePropertiesToNBT(NBTTagCompound tag);

	@Override
	public final void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		readPropertiesFromNBTInternal(tag);
	}
	
	private void readPropertiesFromNBTInternal(NBTTagCompound tag) {
		readPropertiesFromNBT(tag);
	}

	protected abstract void readPropertiesFromNBT(NBTTagCompound tag);
	
}
