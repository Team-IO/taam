package founderio.taam.blocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.vec.BlockCoord;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import founderio.taam.conveyors.IRotatable;
import founderio.taam.multinet.logistics.Demand;
import founderio.taam.multinet.logistics.Demand.DemandCategory;
import founderio.taam.multinet.logistics.IStation;
import founderio.taam.multinet.logistics.LogisticsConfiguration;
import founderio.taam.multinet.logistics.LogisticsManager;

public class TileEntityLogisticsStation extends BaseTileEntity implements IStation, IRotatable {
	
	private String name = "";
	private ForgeDirection direction = ForgeDirection.NORTH;
	
	private BlockCoord coordsManager = null;
	
	private int stationID = -1;
	
	private List<LogisticsConfiguration> configurations;
	
	private transient boolean changed = false;
	
	public static enum StationStatus {
		Idle,
		Restocking,
		Producing,
		ManualOrder
	}
	
	public StationStatus getStatus() {
		return StationStatus.Idle;
	}
	
	public boolean isManagerConnected() {
		return coordsManager != null;
	}
	
	public TileEntityLogisticsStation() {
		configurations = new ArrayList<LogisticsConfiguration>();
	}
	
	public void linkToManager(TileEntityLogisticsManager manager) {
		//TODO for later: once cross-dimensional support is a topic, update this.
		if(manager.getWorldObj() != worldObj) {
			return;
		}
		//TODO: fetch station ID from manager
		this.coordsManager = new BlockCoord(manager);
	}
	
	public void unlinkFromManager() {
		this.coordsManager = null;
		this.stationID = -1;
	}
	
	public TileEntityLogisticsManager getManagerTE() {
		if(coordsManager == null) {
			return null;
		}
		TileEntity te = worldObj.getTileEntity(coordsManager.x, coordsManager.y, coordsManager.z);
		if(te instanceof TileEntityLogisticsManager) {
			return (TileEntityLogisticsManager) te;
		} else {
			coordsManager = null;
			stationID = -1;
			changed = true;
			return null;
		}
	}
	
	public void placeDemand(Demand demand) {
		Collection<Demand> currentDemands = getCurrentDemands();
		demand.station = stationID;
		currentDemands.add(demand);
	}
	
	public void placeDemand(ItemStack stack) {
		Collection<Demand> currentDemands = getCurrentDemands();
		Demand demand = new Demand();
		demand.goods.amount = stack.stackSize;
		demand.goods.type = stack;
		demand.category = DemandCategory.FillStock;
		demand.station = stationID;
		currentDemands.add(demand);
	}
	
	public static class PredictedInventory {
		//TODO: Respect already scheduled transports
		
		private List<ItemStack> projected;
		
		public PredictedInventory(IInventory inventory, Collection<Demand> demands) {
			int size = 5;
			if(inventory != null) {
				size = inventory.getSizeInventory();
			}
			projected = new ArrayList<ItemStack>(size);
			if(inventory != null) {
				for(int i = 0; i < size; i++) {
					changeStock(inventory.getStackInSlot(i), false);
				}
			}
			for(Demand demand : demands) {
				//TODO: change once demands & goods are up to speed.
				changeStock((ItemStack) demand.goods.type, false);
			}
		}
		
		public ItemStack findSameItem(ItemStack stack) {
			for(ItemStack projectedStack : projected) {
				if(projectedStack.isItemEqual(stack)) {
					return projectedStack;
				}
			}
			return null;
		}
		
		private void changeStock(ItemStack stack, boolean subtract) {
			Iterator<ItemStack> iter = projected.iterator();
			while(iter.hasNext()) {
				ItemStack projectedStack = iter.next();
				if(projectedStack.isItemEqual(stack)) {
					if(subtract) {
						projectedStack.stackSize -= stack.stackSize;
						if(projectedStack.stackSize == 0) {
							iter.remove();
						}
					} else {
						projectedStack.stackSize += stack.stackSize;
					}
					return;
				}
			}
		}
		
	}
	
	public IInventory getControlledInventory() {
		return InventoryUtils.getInventory(worldObj,
				xCoord + direction.offsetX,
				yCoord + direction.offsetY,
				zCoord + direction.offsetZ);
	}
	
	public PredictedInventory getProjectedInventory() {
		Collection<Demand> demands = getCurrentDemands();
		return new PredictedInventory(getControlledInventory(), demands);
	}
	
	//private Collection<Demand> currentDemands;
	
	public Collection<Demand> getCurrentDemands() {
		//TODO: Chaching somehow? without causing problems when this list is loaded from outside our own update method
		TileEntityLogisticsManager teManager = getManagerTE();
		if(teManager == null) {
			return Collections.emptyList();
		}
		LogisticsManager manager = teManager.getManager();
		return Collections2.filter(manager.pendingDemands, new Predicate<Demand>() {
			@Override
			public boolean apply(Demand input) {
				return input.station == stationID;
			}
		});
	}
	
	@Override
	public void updateEntity() {

		
		if(!worldObj.isRemote) {
			changed = false;
			
			TileEntityLogisticsManager teManager = getManagerTE();
			if(teManager != null) {
				//LogisticsManager manager = teManager.getManager();
						
				
				for(LogisticsConfiguration config : configurations) {
					if(config.enabled) {
						config.process(this);
					}
				}
			}
			
			// Content changed, send Network update.
			if(changed) {
				updateState();
			}
		}
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		if(name != null && !name.trim().isEmpty()) {
			tag.setString("name", name);
		}
		tag.setInteger("direction", direction.ordinal());
		if(coordsManager != null) {
			tag.setIntArray("coordsManager", coordsManager.intArray());
		}
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		name = tag.getString("name");
		direction = ForgeDirection.getOrientation(tag.getInteger("direction"));
		int[] coords = tag.getIntArray("coordsManager");
		if(coords == null || coords.length != 3) {
			coordsManager = null;
		} else {
			coordsManager = BlockCoord.fromAxes(coords);
		}
	}

	@Override
	public String getName() {
		if(name == null || name.trim().isEmpty()) {
			return "Station at x" + xCoord + " y" + yCoord + " z" + zCoord;
		} else {
			return name;
		}
	}

	
	@Override
	public ForgeDirection getFacingDirection() {
		return direction;
	}

	@Override
	public ForgeDirection getMountDirection() {
		return ForgeDirection.DOWN;
	}

	@Override
	public ForgeDirection getNextFacingDirection() {
		return direction.getRotation(ForgeDirection.UP);
	}

	@Override
	public ForgeDirection getNextMountDirection() {
		return ForgeDirection.DOWN;
	}

	@Override
	public void setFacingDirection(ForgeDirection direction) {
		this.direction = direction;
	}

	@Override
	public void setMountDirection(ForgeDirection direction) {
		return;
	}

}
