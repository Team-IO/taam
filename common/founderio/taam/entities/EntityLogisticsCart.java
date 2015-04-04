package founderio.taam.entities;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;
import codechicken.lib.inventory.InventorySimple;
import codechicken.lib.inventory.InventoryUtils;
import founderio.taam.TaamMain;
import founderio.taam.blocks.TileEntityLogisticsManager;
import founderio.taam.conveyors.ConveyorUtil;
import founderio.taam.multinet.logistics.IVehicle;
import founderio.taam.multinet.logistics.InBlockRoute;
import founderio.taam.multinet.logistics.LogisticsManager;
import founderio.taam.multinet.logistics.LogisticsUtil;
import founderio.taam.multinet.logistics.PredictedInventory;
import founderio.taam.multinet.logistics.Route;
import founderio.taam.multinet.logistics.StationGraph;
import founderio.taam.multinet.logistics.WorldCoord;
import founderio.taam.network.TPLogisticsConfiguration;

public class EntityLogisticsCart extends Entity implements IVehicle {

	public static final int inventory_size = 3;
	
	private InventorySimple inventory;
	
	private int vehicleID = -1;
	
	
	public boolean isOnRail = false;
	
	private int currentRailX;
	private int currentRailY;
	private int currentRailZ;
	
	private InBlockRoute ibr;
	private float ibrProgress;
	private Route route;
	private int routeProgress;
	
	private float currentSpeed = 0.001f;
	private WorldCoord coordsManager;
    private String name;
    
	public EntityLogisticsCart(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		inventory = new InventorySimple(inventory_size);
		dataWatcher.addObject(19, (byte)0);
		dataWatcher.addObject(20, 0);
		dataWatcher.addObject(21, 0);
		dataWatcher.addObject(22, 0);
		dataWatcher.addObject(23, 0);
		setCoordsManager(coordsManager);
		
		//TODO: Set BoundingBox
		this.boundingBox.maxY = 0.8;
	}
	
	private WorldCoord getCoordsManager() {
		boolean coordsPresent = dataWatcher.getWatchableObjectByte(19) != (byte)0;
		if(coordsPresent) {
			coordsManager = new WorldCoord();
			coordsManager.world = dataWatcher.getWatchableObjectInt(20);
			coordsManager.x = dataWatcher.getWatchableObjectInt(21);
			coordsManager.y = dataWatcher.getWatchableObjectInt(22);
			coordsManager.z = dataWatcher.getWatchableObjectInt(23);
		} else {
			coordsManager = null;
		}
		return coordsManager;
	}
	
	private void setCoordsManager(WorldCoord coords) {
		coordsManager = coords;
		dataWatcher.updateObject(19, coordsManager == null ? (byte)0 : (byte)1);
		if(coordsManager != null) {
			dataWatcher.updateObject(20, coordsManager.world);
			dataWatcher.updateObject(21, coordsManager.x);
			dataWatcher.updateObject(22, coordsManager.y);
			dataWatcher.updateObject(23, coordsManager.z);
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		currentRailX = tag.getInteger("currentRailX");
		currentRailY = tag.getInteger("currentRailY");
		currentRailZ = tag.getInteger("currentRailZ");
		
		vehicleID = tag.getInteger("vehicleID");
		

		ibrProgress = tag.getFloat("ibrProgress");
		currentSpeed = tag.getFloat("currentSpeed");
		
		name = tag.getString("name");
		NBTTagCompound coordsManager = tag.getCompoundTag("coordsManager");
		if(coordsManager == null) {
			this.coordsManager = null;
		} else {
			this.coordsManager = new WorldCoord().readFromNBT(coordsManager);
		}
		InventoryUtils.readItemStacksFromTag(inventory.items, tag.getTagList("inventory", NBT.TAG_COMPOUND));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		// TODO Auto-generated method stub
		tag.setInteger("currentRailX", currentRailX);
		tag.setInteger("currentRailY", currentRailY);
		tag.setInteger("currentRailZ", currentRailZ);
		
		tag.setInteger("vehicleID", vehicleID);
		
		//TODO: Somehow store ibr (unique id?)
		
		tag.setFloat("ibrProgress", ibrProgress);
		//TODO: Somehow store root (link to manager's ID for the route)
		
		tag.setFloat("currentSpeed", currentSpeed);
		
		if(coordsManager != null) {
			NBTTagCompound tagCoords = new NBTTagCompound();
			coordsManager.writeToNBT(tagCoords);
			tag.setTag("coordsManager", tagCoords);
		}
		if(name != null && !name.trim().isEmpty()) {
			tag.setString("name", name);
		}
		tag.setTag("inventory", InventoryUtils.writeItemStacksToTag(inventory.items));
	}
	
	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity other) {
		
		if (other == null) {
			return null;
		}
		return null;
		//return other.canBePushed() ? other.getBoundingBox() : null;
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return boundingBox;
	}
	
	@Override
	public boolean interactFirst(EntityPlayer player) {
		if(player.isSneaking() && ConveyorUtil.playerHasWrench(player)) {
			ItemStack stack = new ItemStack(TaamMain.itemLogisticsCart);
			ConveyorUtil.tryDropToInventory(player, stack, posX, posY, posZ);
			this.setDead();
		} else {
			player.openGui(TaamMain.instance, 1, worldObj, this.getEntityId(), 0, 0);
		}
		return true;
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return !isDead;
	}
	
	@Override
	public boolean canBePushed() {
		return !isOnRail;
	}
	
	@Override
	public double getMountedYOffset() {
		// TODO Auto-generated method stub
		return super.getMountedYOffset();
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource damageSource, float damage) {
		// TODO Auto-generated method stub
		return super.attackEntityFrom(damageSource, damage);
	}
	
	public int getVehicleID() {
		return vehicleID;
	}
	
	private void linkToManager(TileEntityLogisticsManager manager) {
		//TODO for later: once cross-dimensional support is a topic, update this.
		if(manager.getWorldObj() != worldObj) {
			return;
		}
		setCoordsManager(new WorldCoord(manager));
		this.vehicleID = manager.vehicleRegister(this);
	}

	@Override
	public void linkToManager(WorldCoord coords) {
		if(worldObj.isRemote) {
			//TODO: send packet
			TPLogisticsConfiguration config = TPLogisticsConfiguration.newConnectManagerVehicle(worldObj.provider.dimensionId, this.getEntityId(), coords);
			TaamMain.network.sendToServer(config);
		} else {
			TileEntity te = worldObj.getTileEntity(coords.x, coords.y, coords.z);
			if(te instanceof TileEntityLogisticsManager) {
				linkToManager((TileEntityLogisticsManager) te);
			} else {
				//TODO: Log Error
			}
		}
	}

	@Override
	public boolean isConnectedToManager() {
		return getCoordsManager() != null;
	}

	@Override
	public String getName() {
		return this.name != null ? this.name : "Vehicle";
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	private void snapToRail() {
		 int x = MathHelper.floor_double(this.posX);
         int y = MathHelper.floor_double(this.posY);
         int z = MathHelper.floor_double(this.posZ);
         
         boolean isActuallyOnRail = LogisticsUtil.isMagnetRail(worldObj, x, y, z);
         // Also check one block below if we are "floating"
         if(!isActuallyOnRail && worldObj.isAirBlock(x, y, z)) {
        	 y -= 1;
        	 isActuallyOnRail = LogisticsUtil.isMagnetRail(worldObj, x, y, z);
         }
         if(isOnRail) {
        	 if(!isActuallyOnRail) {
        		 isOnRail = false;
        	 }
         } else {
        	 if(isActuallyOnRail) {
            	 System.out.println("Snapping to rail");
              	currentRailX = x;
              	currentRailY = y;
              	currentRailZ = z;
				this.prevPosX = this.posX;
 		        this.prevPosY = this.posY;
 		        this.prevPosZ = this.posZ;
 		       
 				setPosition(currentRailX, currentRailY, currentRailZ);
 				if(!worldObj.isRemote) {
 		            MinecraftServer minecraftserver = MinecraftServer.getServer();
 		            WorldServer worldserver = minecraftserver.worldServerForDimension(this.dimension);
	 				worldserver.resetUpdateEntityTick();
 				}
              	ibrProgress = 0;
             	isOnRail = true;
              	ibr = TaamMain.blockMagnetRail.getInBlockRoutes(worldObj, x, y, z).get(0);
        	 } else {

              	isOnRail = false;
        	 }
         }
	}
	
	private boolean findNextRoute() {
		ForgeDirection dir = ibr.leaveTo;
		List<InBlockRoute> nextRoutes = TaamMain.blockMagnetRail.getInBlockRoutes(worldObj, this.currentRailX + dir.offsetX, this.currentRailY + dir.offsetX, this.currentRailZ + dir.offsetX);
		
		Iterator<InBlockRoute> iter = nextRoutes.iterator();
		
		while(iter.hasNext()) {
			InBlockRoute route = iter.next();
			if(route.enterFrom.getOpposite() != dir) {
				iter.remove();
			}
		}
		if(nextRoutes.isEmpty()) {
			//Stop at end of track
			ibrProgress = ibr.totalLength;
			return false;
		}
		if(route == null) {
			return false;
		}
		List<WorldCoord> routePlot = route.getPlot();
		if(routePlot != null && routeProgress + 1 < routePlot.size()) {
			System.out.println("Next step in route");
			//TODO: Do plotting separate from route? (station by station)
			WorldCoord current = new WorldCoord(worldObj, currentRailX, currentRailY, currentRailZ);
			WorldCoord next = routePlot.get(routeProgress + 1);
			for(InBlockRoute nextIbr : nextRoutes) {
				if(current.isDirectionalOffset(nextIbr.leaveTo, next)) {
					ibr = nextIbr;
					ibrProgress -= ibr.totalLength;
					currentRailX += dir.offsetX;
					currentRailY += dir.offsetY;
					currentRailZ += dir.offsetZ;
					routeProgress++;
					return true;
				}
			}
			
		}
		return false;
	}
	
	@Override
	public void onEntityUpdate() {
		if(worldObj.isRemote) {
			
		} else {
			this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

           snapToRail();
            
            if(isOnRail) {
            	//TODO: Check where we need to go, depending on route
            	
            	if(ibr == null) {
            		List<InBlockRoute> routes = TaamMain.blockMagnetRail.getInBlockRoutes(worldObj, currentRailX, currentRailY, currentRailZ);
            		
            		if(routes.isEmpty()) {
            			return;
            		} else {
            			//TODO: Snap using direction/interpolation?
            			ibr = routes.get(0);
            		}
            	} else {
            		// Stop when reaching the end of a track, until we are ready to continue
            		if(ibrProgress <= ibr.totalLength) {
                		ibrProgress += currentSpeed;
            		}
            		while(ibrProgress > ibr.totalLength) {
            			if(!findNextRoute()) {
            				
            				break;
            			}
            		}
            		// Find the point in the ibr we are at and do a linear interpolation between the closes points on the ibr.
            		float calcOffset = ibrProgress;
            		int coordinateCount = ibr.getCoordinateCount();
            		for(int i = 1; i < coordinateCount; i++) {
            			// Are we past that point yet?
            			if(calcOffset <= ibr.lengths[i-1]) {
            				// We are not past it -> interpolate.
            				
            				// Calculate the single deltas for the next and the last point
            				float dX = Math.abs(ibr.xyzCoordinates[i*3] - ibr.xyzCoordinates[(i-1)*3]);
            				float dY = Math.abs(ibr.xyzCoordinates[i*3+1] - ibr.xyzCoordinates[(i-1)*3+1]);
            				float dZ = Math.abs(ibr.xyzCoordinates[i*3+2] - ibr.xyzCoordinates[(i-1)*3+2]);
            				// Calculate the percentage of the whole distance that we have completed.
            				float percentage = (calcOffset / ibr.lengths[i-1]);
            				// Set our postion to the interpolated value
            				float posX = currentRailX + ibr.xyzCoordinates[(i-1)*3] + dX * percentage;
            				float posY = currentRailY + ibr.xyzCoordinates[(i-1)*3+1] + dY * percentage;
            				float posZ = currentRailZ + ibr.xyzCoordinates[(i-1)*3+2] + dZ * percentage;
            		        this.prevPosX = this.posX;
            		        this.prevPosY = this.posY;
            		        this.prevPosZ = this.posZ;
            				setPosition(posX, posY, posZ);
            				// And don't check the next points anymore
            				break;
            			} else {
            				// We are past it -> move to next point.
            				calcOffset -= ibr.lengths[i-1];
            			}
            		}
            	}
            } else {

				AxisAlignedBB box = boundingBox.expand(0.2D, 0.0D, 0.2D);

				List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(
						this, box);

				if (list != null && !list.isEmpty()) {
					for (int k = 0; k < list.size(); ++k) {
						Entity entity = (Entity) list.get(k);

						if (entity != this.riddenByEntity
								&& entity.canBePushed()) {
							entity.applyEntityCollision(this);
						}
					}
				}
				
            	//TODO: Grounded/Air distinction.
            	this.motionX *= 0.5D;
                this.motionY *= 0.5D;
                this.motionZ *= 0.5D;
            }
            
		}
	}

	@Override
	public PredictedInventory getPredictedInventory() {
		//TODO: Respect inventory for given position in route
		//TODO: allow plausibility check for transport from-to different stations, respecting inbetween transactions
		return getCurrentInventory();
	}

	@Override
	public PredictedInventory getCurrentInventory() {
		return new PredictedInventory(false, inventory);
	}

	@Override
	public boolean hasRouteToStation(int stationID, StationGraph graph, LogisticsManager manager) {
		if(isOnRail) {
			WorldCoord currentPosition = new WorldCoord(worldObj, currentRailX, currentRailY, currentRailZ);
			
			//Respect current IBR (already "skip ahead" to next rail on that route)
			if(ibr != null && ibrProgress > 0) {
				currentPosition = currentPosition.getDirectionalOffset(ibr.leaveTo);
			}
			return null != graph.astar(currentPosition, graph.getTrackForStation(manager.getStation(stationID)));
		} else {
			return false;
		}
	}

	@Override
	public void setRoute(Route route) {
		this.route = route;
		this.routeProgress = 0;
		LogisticsManager manager = LogisticsUtil.getManager(worldObj, coordsManager);
		route.plotRoute(manager.graph, manager, this);
	}

	@Override
	public WorldCoord getCurrentLocation() {
		return new WorldCoord(worldObj, currentRailX, currentRailY, currentRailZ);
	}

}
