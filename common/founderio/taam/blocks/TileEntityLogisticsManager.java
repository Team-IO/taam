package founderio.taam.blocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import founderio.taam.multinet.logistics.Demand;
import founderio.taam.multinet.logistics.IStation;
import founderio.taam.multinet.logistics.IVehicle;
import founderio.taam.multinet.logistics.LogisticsConfiguration;
import founderio.taam.multinet.logistics.LogisticsManager;
import founderio.taam.multinet.logistics.Transport;

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
		if(worldObj.isRemote) {
			return null;
		}
		if(manager == null) {
			manager = new LogisticsManager(worldObj);
		}
		return manager;
	}

	public TileEntityLogisticsManager() {
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
		
		if(!worldObj.isRemote) {
			
			// Preload Manager if not available yet
			getManager();
			

			Collection<Demand> pendingDemands = manager.pendingDemands;
			Collection<Demand> processingDemands = manager.processingDemands;
			Collection<Transport> pendingTransports = manager.pendingTransport;
			
			Iterator<Demand> iter = pendingDemands.iterator();
			
			while(iter.hasNext()) {
				Demand demand = iter.next();
				
				boolean demandFinished = false;
				
				//TODO: only check every demand several ticks apart, as there may be a lot of them
				//TODO: Limit amount of demands processed? Limit time taken?
				//TODO: check if there is a route from source station to target station
				
				//TODO: Extract methods & optimize
				for(IStation station : manager.getStations()) {
					if(station instanceof TileEntityLogisticsStation) {
						TileEntityLogisticsStation tels = (TileEntityLogisticsStation)station;
						for(LogisticsConfiguration config : tels.getConfigurations()) {
							if(config instanceof LogisticsConfiguration.ProvideStock) {
								LogisticsConfiguration.ProvideStock provideStock = (LogisticsConfiguration.ProvideStock) config;
								boolean equal = provideStock.what.isItemEqual((ItemStack)demand.goods.type);
								
								if(equal) {
									int availableStock = provideStock.getAvailableAmount();
									if(availableStock < 1) {
										// Can not be fulfilled
										// No break, we will continue to check and try to fulfill the demand.
										continue;
									} else if(availableStock < demand.goods.amount) {
										// Can be partially fulfilled
										//TODO: Prioritization, transport minimization (find full match first), configuration not to allow partial requests
										Demand partial = demand.copy();
										partial.goods.amount = availableStock;
										demand.goods.amount -= availableStock;
										processingDemands.add(partial);
										Transport t = new Transport();
										t.from = tels.getStationID();
										t.to = partial.station;
										t.goods = partial.goods;
										t.demand = partial;
										t.created = 0;//TODO time created
										
										pendingTransports.add(t);
										System.out.println("Scheduled transport " + t);
										// No break, we will continue to check and try to fulfill the rest of the demand.
										continue;
									} else {
										// Can be fulfilled
										iter.remove();
										processingDemands.add(demand);
										demandFinished = true;
										Transport t = new Transport();
										t.from = tels.getStationID();
										t.to = demand.station;
										t.goods = demand.goods;
										t.demand = demand;
										t.created = 0;//TODO time created
										
										pendingTransports.add(t);
										System.out.println("Scheduled transport " + t);
										break;
									}
								}
							}
						}
						if(demandFinished) {
							demandFinished = false;
							break;//Next Demand
						}
					}
				}
			}
			
			manager.scheduleTransports();
			
			// Content changed, send Network update.
			if(changed) {
				updateState();
			}
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
	public int stationRegister(IStation station) {
		System.out.println("Station added " + station.getName());
		return getManager().addStation(station);
	}
	
	public void stationUnregister(IStation station) {
		System.out.println("Station removed " + station.getName());
		getManager().removeStation(station);
	}
	
	public int vehicleRegister(IVehicle vehicle) {
		System.out.println("Station added " + vehicle.getName());
		return getManager().addVehicle(vehicle);
	}
	
	public void vehicleUnregister(IVehicle vehicle) {
		System.out.println("Station removed " + vehicle.getName());
		getManager().removeVehicle(vehicle);
	}
}
