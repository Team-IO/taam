package founderio.taam.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.taam.multinet.logistics.IStation;
import founderio.taam.multinet.logistics.LogisticsManager;

public class TileEntityLogisticsManager extends BaseTileEntity {

	private static List<TileEntityLogisticsManager> activeManagers;
	
	static {
		//TODO: Make this list + the updating routines server-only (the client does not need to know and does not have all entities loaded anyways)
		activeManagers = new ArrayList<TileEntityLogisticsManager>();
	}
	
	public static List<TileEntityLogisticsManager> getActiveManagers() {
		return Collections.unmodifiableList(activeManagers);
	}
	
	private LogisticsManager manager;
	
	public LogisticsManager getManager() {
		return manager;
	}
	
	public TileEntityLogisticsManager() {
	}
	
	@SideOnly(Side.SERVER)
	private void initServerside() {
		manager = new LogisticsManager();
	}
	
	@Override
	public void onChunkUnload() {
		activeManagers.remove(this);
		super.onChunkUnload();
	}
	
	@Override
	public void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();
		if(!activeManagers.contains(this)) {
			activeManagers.add(this);
		}
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
