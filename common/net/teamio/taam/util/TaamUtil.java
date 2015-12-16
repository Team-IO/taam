package net.teamio.taam.util;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.teamio.taam.content.IRedstoneControlled;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.vec.Vector3;

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

	public static void breakBlockInWorld(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		breakBlockInWorld(world, x, y, z, block);
	}

	public static void breakBlockInWorld(World world, int x, int y, int z, Block block) {
		int meta = world.getBlockMetadata(x, y, z);
		block.dropBlockAsItem(world, x, y, z, meta, 0);
		world.setBlockToAir(x, y, z);
	}
	
	public static void breakBlockToInventory(EntityPlayer player, World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		breakBlockToInventory(player, world, x, y, z, block);
	}

	public static void breakBlockToInventory(EntityPlayer player, World world, int x, int y, int z, Block block) {
		ItemStack toDrop = getItemStackFromWorld(world, x, y, z, block);
		if(toDrop != null) {
			tryDropToInventory(player, toDrop, x, y, z);
		}
		world.setBlockToAir(x, y, z);
	}
	
	public static ItemStack getItemStackFromWorld(World world, int x, int y, int z, Block block) {
		int metadata = world.getBlockMetadata(x, y, z);
        Item item = Item.getItemFromBlock(block);
        if (item == null) {
        	return null;
        } else {
        	int damage = block.damageDropped(metadata);
        	return new ItemStack(block, 1, damage);
        }
	}

	public static boolean isShutdown(Random rand, int redstoneMode, boolean redstoneHigh) {
		boolean newShutdown = false;
		// Redstone. Other criteria?
		if(redstoneMode == IRedstoneControlled.MODE_ACTIVE_ON_HIGH && !redstoneHigh) {
			newShutdown = true;
		} else if(redstoneMode == IRedstoneControlled.MODE_ACTIVE_ON_LOW && redstoneHigh) {
			newShutdown = true;
		} else if(redstoneMode > 4 || redstoneMode < 0) {
			newShutdown = rand.nextBoolean();
		}
		return newShutdown;
	}

	/**
	 * Decides whether an attachable block can be placed somewhere.
	 * Checks for a solid side or a TileEntity implementing {@link IConveyorAwareTE}.
	 * @param world
	 * @param x The attachable block.
	 * @param y The attachable block.
	 * @param z The attachable block.
	 * @param dir The direction in which to check. Checks the block at the offset coordinates.
	 * @return
	 */
	public static boolean canAttach(IBlockAccess world, int x, int y, int z, ForgeDirection dir) {
		if(world.isSideSolid(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir.getOpposite(), false)) {
			return true;
		}
		TileEntity ent = world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
		return ent instanceof IConveyorAwareTE;
	}
	
	
}
