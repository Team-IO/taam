package net.teamio.taam.conveyors;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;
import net.teamio.taam.util.TaamUtil;
import codechicken.lib.inventory.InventoryRange;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.vec.Vector3;

public class ConveyorUtil {
	
	private static boolean tryInsert(TileEntity tileEntity, EntityItem ei) {
		ItemStack entityItemStack = ei.getEntityItem();
		if(entityItemStack == null || entityItemStack.getItem() == null) {
			return false;
		}
		
		int previousStackSize = entityItemStack.stackSize;
		int added = 0;
		
		double relativeX = ei.posX - tileEntity.xCoord;
		double relativeY = ei.posY - tileEntity.yCoord;
		double relativeZ = ei.posZ - tileEntity.zCoord;
		
		if(tileEntity instanceof IConveyorAwareTE) {
			IConveyorAwareTE conveyorTE = (IConveyorAwareTE) tileEntity;
			
			int slot = getSlotForRelativeCoordinates(relativeX, relativeZ);

			if(slot >= 0 && slot < 9 && relativeY > 0.3 && relativeY < 1.0) {
				added = conveyorTE.insertItemAt(entityItemStack, slot);
			}
		} else if(tileEntity instanceof IInventory) {
			if(
				relativeX >= 0 && relativeX < 1 &&
				relativeY >= 0.9 && relativeY < 1.2 &&
				relativeZ >= 0 && relativeZ < 1
				) {
				IInventory inventory = (IInventory)tileEntity;
				InventoryRange range = new InventoryRange(inventory, ForgeDirection.UP.ordinal());
				added = previousStackSize - InventoryUtils.insertItem(range, entityItemStack, false);
			}
		}
		if(added == previousStackSize) {
			ei.setDead();
			return true;
		} else if(added > 0) {
			entityItemStack.stackSize = previousStackSize - added;
			ei.setEntityItemStack(entityItemStack);
			return true;
		} else {
			return false;
		}
		
	}
	
	/**
	 * Tries to insert item entities from the world into an entity.
	 * Respects the conveyor system.
	 * 
	 * @param tileEntity
	 * @param world
	 * @param bounds
	 *            Optionally give an AABB Instance to speed up the search &
	 *            extend to unloaded chunks. Else only loaded entities are
	 *            respected. TODO: Implement this.
	 * @param stopAtFirstMatch
	 *            Stop processing items after the first one was added?
	 *            
	 */
	public static boolean tryInsertItemsFromWorld(
			TileEntity tileEntity,
			World world,
			AxisAlignedBB bounds,
			boolean stopAtFirstMatch) {
		if(world.isRemote) {
			return false;
		}
		boolean didAdd = false;
		List<?> entities = world.loadedEntityList;
		
		//if Bounding Box is Supplied, use that.
		if(bounds != null) {
			entities = world.getEntitiesWithinAABB(EntityItem.class, bounds);
		}
		for(int i = 0; i < entities.size(); i++) {
			Entity ent = (Entity)entities.get(i);
			
			if(ent instanceof EntityItem) {
				EntityItem ei = (EntityItem)ent;
				
				didAdd = tryInsert(tileEntity, ei) | didAdd;
				
				if(stopAtFirstMatch && didAdd) {
					break;
				}
			}
		}
		return didAdd;
	}
	
	public static int getNextSlot(int slot, ForgeDirection dir) {
		slot = getNextSlotUnwrapped(slot, dir);
		if(slot < 0) {
			slot += 9;
		} else if(slot > 8) {
			slot -= 9;
		}
		return slot;
	}
	
	public static int getNextSlotUnwrapped(int slot, ForgeDirection dir) {
		if(dir.offsetX != 0) {
			slot += dir.offsetX * 3;
		}
		if(dir.offsetZ != 0) {
			int col = slot % 3;
			col += dir.offsetZ;
			if(col < 0) {
				slot -= 7;
			} else if(col > 2) {
				slot += 7;
			} else {
				slot += dir.offsetZ;
			}
		}
		return slot;
	}
	
	public static int getSlot(ForgeDirection dir) {
		if(dir == ForgeDirection.DOWN || dir == ForgeDirection.UNKNOWN) {
			// Conveyors are only accessible from top/sides!
			return -1;
		} else if(dir == ForgeDirection.UP) {
			// Center
			return 4;
		} else {
			// From that center, we go one off
			return getNextSlot(4, dir);
		}
	}

	public static final double oneThird = 1/3.0;
	
	public static double getItemPositionX(int slot) {
		double x = Math.floor(slot / 3) + 0.5;
		return x * oneThird;
	}
	
	public static double getItemPositionX(int slot, double progress, ForgeDirection dir) {
		double x = getItemPositionX(slot);
		x += dir.offsetX * progress * oneThird;
		return x;
	}

	public static double getItemPositionZ(int slot) {
		double z = (slot % 3) + 0.5;
		return z * oneThird;
	}
	
	public static double getItemPositionZ(int slot, double progress, ForgeDirection dir) {
		double z = getItemPositionZ(slot);
		z += dir.offsetZ * progress * oneThird;
		return z;
	}
	
	public static int getSlotForRelativeCoordinates(double x, double z) {
		if(x > 1 || x < 0 || z > 1 || z < 0) {
			return -1;
		}
		int row = (int)Math.floor(x * 3f);
		int col = (int)Math.floor(z * 3f);
		return row * 3 + col;
	}
	
	private static final int[][] slotOrders;
	
	static {
		slotOrders = new int[2][];
		slotOrders[0] = new int[] {
				//North -Z to +Z (Processes line by line)
				//West -X to +X (Processes each line in "parallel")
				0, 1, 2,
				3, 4, 5,
				6, 7, 8
		};
		slotOrders[1] = new int[] {
				//South +Z to -Z (Processes line by line)
				//East +X to -X (Processes each line in "parallel")
				6, 7, 8,
				3, 4, 5,
				0, 1, 2
		};
	}
	
	
	/**
	 * Returns the ideal order to process slots on a conveyor. (Always from
	 * front to back, but lanes are not in a guaranteed order!)
	 * 
	 * @param dir
	 * @return
	 */
	public static int[] getSlotOrderForDirection(ForgeDirection dir) {
		switch (dir) {
		default:
		case NORTH:
			return slotOrders[0];
		case SOUTH:
			return slotOrders[1];
		case WEST:
			return slotOrders[0];
		case EAST:
			return slotOrders[1];
		}
	}
	
	
	/**
	 * Drops the installed appliance and its content, if available.
	 * @param applianceHost
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return true if an appliance was there and did drop.
	 */
	public static boolean dropAppliance(IConveyorApplianceHost applianceHost, EntityPlayer player, World world, int x, int y, int z) {
		String type = applianceHost.getApplianceType();
		if(type == null) {
			return false;
		}
		IConveyorApplianceFactory factory = ApplianceRegistry.getFactory(type);
		if(factory == null) {
			return false;
		}
		Vector3 location = new Vector3(x, y, z);
		/*
		 * Drop appliance
		 */
		IConveyorAppliance appliance = applianceHost.getAppliance();
		if(appliance == null) {
			return false;
		}
		ItemStack stack = appliance.getItemStack();
		//TODO: Make ItemStack retain certain data? (Tanks... Energy...)
		if(stack != null) {
			if(player != null) {
				TaamUtil.tryDropToInventory(player, stack, x, y, z);
			} else {
				InventoryUtils.dropItem(stack, world, location);
			}
		}
		/*
		 * Drop appliance content
		 */
		for(int i = 0; i < appliance.getSizeInventory(); i++) {
			stack = appliance.getStackInSlot(i);
			if(stack == null) {
				continue;
			}
			if(player != null) {
				TaamUtil.tryDropToInventory(player, stack, x, y, z);
			} else {
				InventoryUtils.dropItem(stack, world, location);
			}
		}
		return true;
	}
}
