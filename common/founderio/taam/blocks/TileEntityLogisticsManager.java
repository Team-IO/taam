package founderio.taam.blocks;

import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.taam.multinet.logistics.IStation;
import founderio.taam.multinet.logistics.LogisticsManager;

public class TileEntityLogisticsManager extends BaseTileEntity {
	
	public TileEntityLogisticsManager() {
	}
	
	@SideOnly(Side.SERVER)
	private void initServerside() {
		manager = new LogisticsManager();
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
		//TODO: save registered stations
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		
	}
	
	private LogisticsManager manager;

	//TODO: Keep list around / fetch from manager to store in NBT
	public void stationRegister(IStation station) {
		System.out.println("Station added " + station.getName());
		manager.addStation(station);
	}
	
	public void stationUnregister(IStation station) {
		System.out.println("Station removed " + station.getName());
		manager.removeStation(station);
	}
}
