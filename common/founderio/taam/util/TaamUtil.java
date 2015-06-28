package founderio.taam.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.vec.Vector3;
import founderio.taam.TaamMain;

/**
 * Generic Utility Methods, used across multiple "themes".
 * @author oliver
 *
 */
public final class TaamUtil {
	private TaamUtil() {
		// Util class
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

	/**
	 * Tries to drop an item into a player inventory or drops it at the specified coordinates.
	 * 
	 * @param player
	 * @param stack
	 * @param x
	 * @param y
	 * @param z
	 */
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

	public static boolean canDropIntoWorld(IBlockAccess world, int x, int y, int z) {
		return world.isAirBlock(x, y, z) || world.getBlock(x, y, z).getMaterial().isLiquid();
	}
	
	
}
