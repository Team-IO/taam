package net.teamio.taam.util;

import java.util.Random;

import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.vec.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.teamio.taam.content.IRedstoneControlled;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;

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
	
	public static void tryDropToInventory(EntityPlayer player, ItemStack stack, BlockPos pos) {
		tryDropToInventory(player, stack, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
	}

	public static boolean canDropIntoWorld(IBlockAccess world, BlockPos pos) {
		return world.isAirBlock(pos) || world.getBlockState(pos).getBlock().getMaterial().isLiquid();
	}

	public static void breakBlockInWorld(World world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		breakBlockInWorld(world, pos, blockState);
	}

	public static void breakBlockInWorld(World world, BlockPos pos, IBlockState blockState) {
		Block block = blockState.getBlock();
		block.dropBlockAsItem(world, pos, blockState, 0);
		world.setBlockToAir(pos);
	}
	
	public static void breakBlockToInventory(EntityPlayer player, World world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		breakBlockToInventory(player, world, pos, blockState);
	}

	public static void breakBlockToInventory(EntityPlayer player, World world, BlockPos pos, IBlockState blockState) {
		ItemStack toDrop = getItemStackFromWorld(world, pos, blockState);
		if(toDrop != null) {
			tryDropToInventory(player, toDrop, pos);
		}
		world.setBlockToAir(pos);
	}
	
	public static ItemStack getItemStackFromWorld(World world, BlockPos pos, IBlockState blockState) {
		Block block = blockState.getBlock();
        Item item = Item.getItemFromBlock(block);
        if (item == null) {
        	return null;
        } else {
        	int damage = block.damageDropped(blockState);
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
	public static boolean canAttach(IBlockAccess world, BlockPos pos, EnumFacing dir) {
		if(world.isSideSolid(pos.offset(dir), dir.getOpposite(), false)) {
			return true;
		}
		TileEntity ent = world.getTileEntity(pos.offset(dir));
		return ent instanceof IConveyorAwareTE;
	}
	
	
}
