package founderio.taam.conveyors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.vec.Vector3;
import founderio.taam.TaamMain;
import founderio.taam.conveyors.api.IConveyorApplianceHost;
import founderio.taam.conveyors.api.IConveyorAwareTE;

public class ConveyorUtil {
	/**
	 * Tries to insert item entities from the world into the conveyor system.
	 * 
	 * @param conveyorTE
	 * @param world
	 * @param bounds
	 *            Optionally give an AABB Instance to speed up the search &
	 *            extend to unloaded chunks. Else only loaded entities are
	 *            respected. TODO: Implement this.
	 * @param stopAtFirstMatch
	 *            Stop processing items after the first one was added?
	 */
	public static void tryInsertItemsFromWorld(
			IConveyorAwareTE conveyorTE,
			World world,
			AxisAlignedBB bounds,
			boolean stopAtFirstMatch) {
		if(world.isRemote) {
			return;
		}
		//TODO: if Bounding Box is Supplied, use that.
		for(Object obj : world.loadedEntityList) {
			Entity ent = (Entity)obj;
			
			if(ent instanceof EntityItem) {
				EntityItem ei = (EntityItem)ent;
				ItemStack entityItemStack = ei.getEntityItem();
				int previousStackSize = entityItemStack.stackSize;
				if(entityItemStack == null || entityItemStack.getItem() == null) {
					continue;
				}
				double relativeX = ent.posX - conveyorTE.posX();
				double relativeY = ent.posY - conveyorTE.posY();
				double relativeZ = ent.posZ - conveyorTE.posZ();
				
				
				int slot = getSlotForRelativeCoordinates(relativeX, relativeZ);

				if(slot >= 0 && slot < 9 && relativeY > 0.3 && relativeY < 1.0) {
					int added = conveyorTE.insertItemAt(entityItemStack, slot);
					if(added == previousStackSize) {
						ent.setDead();
						if(stopAtFirstMatch) {
							break;
						}
					} else if(added > 0) {
						entityItemStack.stackSize = previousStackSize - added;
						ei.setEntityItemStack(entityItemStack);
						if(stopAtFirstMatch) {
							break;
						}
					}
				}
			}
		}
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
	
	
	public static int[] getSlotOrderForDirection(ForgeDirection dir) {
		switch(dir) {
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
		ItemStack stack = factory.getItemStack(type);
		//TODO: Make ItemStack retain certain data? (Tanks... Energy...)
		if(stack != null) {
			if(player != null) {
				tryDropToInventory(player, stack, x, y, z);
			} else {
				InventoryUtils.dropItem(stack, world, location);
			}
		}
		/*
		 * Drop appliance content
		 */
		IConveyorAppliance appliance = applianceHost.getAppliance();
		if(appliance == null) {
			return false;
		}
		for(int i = 0; i < appliance.getSizeInventory(); i++) {
			stack = appliance.getStackInSlot(i);
			if(stack == null) {
				continue;
			}
			if(player != null) {
				tryDropToInventory(player, stack, x, y, z);
			} else {
				InventoryUtils.dropItem(stack, world, location);
			}
		}
		return true;
	}
	
	/**
	 * Returns true if the player is holding a wrench in his hand.
	 * @param player
	 * @return
	 */
	public static boolean playerHasWrench(EntityPlayer player) {
		ItemStack held = player.getHeldItem();
		if(held == null) {
			return false;
		}
		//TODO: Check other wrench types once supported
		return held.getItem() == TaamMain.itemWrench;
	}
	
	public static void tryDropToInventory(EntityPlayer player, ItemStack stack, double x, double y, double z) {
		if(player.capabilities.isCreativeMode) {
			return;
		}
		if(!player.inventory.addItemStackToInventory(stack)) {
			if(!player.worldObj.isRemote) {
				InventoryUtils.dropItem(stack, player.worldObj, new Vector3(x, y, z));
			}
		}
	}
}
