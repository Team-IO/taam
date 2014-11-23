package founderio.taam.blocks;

import codechicken.lib.vec.BlockCoord;
import founderio.taam.multinet.logistics.IStation;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityLogisticsStation extends BaseTileEntity implements IStation {
	
	private String name = "";
	
	private BlockCoord coordsManager = null;
	
	public TileEntityLogisticsStation() {
	}
	
	public void linkToManager() {
		//TODO: find nearby manager (traverse tracks?)
	}
	
	@Override
	public void updateEntity() {

		boolean changed = false;
		
		// Content changed, send Network update.
		if(changed && !worldObj.isRemote) {
			updateState();
		}
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		if(name != null && !name.trim().isEmpty()) {
			tag.setString("name", name);
		}
		if(coordsManager != null) {
			//TODO: Write manager coords
			//TODO: Write if we are linked to a manager
		}
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		name = tag.getString("name");
		//TODO: read manager coords if manager is linked
	}

	@Override
	public String getName() {
		if(name == null || name.trim().isEmpty()) {
			return "Station at x" + xCoord + " y" + yCoord + " z" + zCoord;
		} else {
			return name;
		}
	}

}
