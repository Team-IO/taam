package founderio.taam.conveyors;

import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.vec.Vector3;
import founderio.taam.TaamMain;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

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
				int added = conveyorTE.addItemAt(entityItemStack, ent.posX, ent.posY, ent.posZ);
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
	
	/**
	 * Tries to insert items into the conveyor passed. Sets the stack size accordingly.
	 * @param conveyorTE
	 * @param itemWrapper
	 * @param absX
	 * @param absY
	 * @param absZ
	 * @return true if items were inserted.
	 */
	public static boolean tryInsertItems(IConveyorAwareTE conveyorTE, ItemWrapper itemWrapper,
			double absX, double absY, double absZ) {
		int previousStackSize = itemWrapper.getStackSize();
		if(previousStackSize == 0) {
			// Just remove it.
			return true;
		}
		int added = conveyorTE.addItemAt(itemWrapper, absX, absY, absZ);
		if(added > 0) {
			// Fully or Partially inserted
			itemWrapper.setStackSize(previousStackSize - added);
			return true;
		} else {
			return false;
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
	//TODO: Make it drop directly to player inventory if possible
	public static boolean dropAppliance(IConveyorApplianceHost applianceHost, World world, int x, int y, int z) {
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
			InventoryUtils.dropItem(stack, world, location);
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
			if(stack != null) {
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
}
