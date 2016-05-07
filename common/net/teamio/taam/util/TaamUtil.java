package net.teamio.taam.util;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.teamio.taam.content.IRedstoneControlled;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;
import net.teamio.taam.conveyors.api.IConveyorSlots;
import net.teamio.taam.util.inv.InventoryUtils;

/**
 * Generic Utility Methods, used across multiple "themes".
 * 
 * @author Oliver Kahrmann
 *
 */
public final class TaamUtil {
	private TaamUtil() {
		// Util class
	}

	/**
	 * Global random instance for things that can't access the world's random..
	 */
	public static final Random RANDOM = new Random();

	public static boolean canDropIntoWorld(IBlockAccess world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		return block.isAir(world, pos) || block.getMaterial().isLiquid();
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
		List<ItemStack> toDrop = getDropsFromWorld(world, pos, blockState);
		if (toDrop != null) {
			for (ItemStack stack : toDrop) {
				InventoryUtils.tryDropToInventory(player, stack, pos);
			}
		}
		world.setBlockToAir(pos);
	}

	public static List<ItemStack> getDropsFromWorld(World world, BlockPos pos, IBlockState blockState) {
		Block block = blockState.getBlock();
		return block.getDrops(world, pos, blockState, 0);
	}

	public static boolean isShutdown(Random rand, int redstoneMode, boolean redstoneHigh) {
		boolean newShutdown = false;
		// Redstone. Other criteria?
		if (redstoneMode == IRedstoneControlled.MODE_ACTIVE_ON_HIGH && !redstoneHigh) {
			newShutdown = true;
		} else if (redstoneMode == IRedstoneControlled.MODE_ACTIVE_ON_LOW && redstoneHigh) {
			newShutdown = true;
		} else if (redstoneMode > 4 || redstoneMode < 0) {
			newShutdown = rand.nextBoolean();
		}
		return newShutdown;
	}

	/**
	 * Decides whether an attachable block can be placed somewhere. Checks for a
	 * solid side or a TileEntity implementing {@link IConveyorSlots}.
	 * 
	 * @param world
	 * @param x
	 *            The attachable block.
	 * @param y
	 *            The attachable block.
	 * @param z
	 *            The attachable block.
	 * @param dir
	 *            The direction in which to check. Checks the block at the
	 *            offset coordinates.
	 * @return
	 */
	public static boolean canAttach(IBlockAccess world, BlockPos pos, EnumFacing dir) {
		if (world.isSideSolid(pos.offset(dir), dir.getOpposite(), false)) {
			return true;
		}
		TileEntity ent = world.getTileEntity(pos.offset(dir));
		return ent instanceof IConveyorSlots;
	}

	public static boolean canAttachAppliance(IBlockAccess world, BlockPos pos, EnumFacing dir) {
		TileEntity ent = world.getTileEntity(pos.offset(dir));
		return ent instanceof IConveyorApplianceHost;
	}

	/**
	 * Checks if actualInput is the same item as inputDefinition (respecting
	 * OreDictionary) or, if inputDefinition is null, if actualInput is
	 * registered with the ore dictionary matching inoutOreDictName.
	 * 
	 * @param inputDefinition
	 * @param inputOreDictName
	 * @param actualInput
	 * @return
	 */
	public static boolean isInputMatching(ItemStack inputDefinition, String inputOreDictName, ItemStack actualInput) {
		if (actualInput == null) {
			return inputDefinition != null && inputOreDictName != null;
		} else {
			if (inputDefinition == null) {
				int[] oreIDs = OreDictionary.getOreIDs(actualInput);
				int myID = OreDictionary.getOreID(inputOreDictName);
				return ArrayUtils.contains(oreIDs, myID);
			} else {
				return inputDefinition.isItemEqual(actualInput)
						|| OreDictionary.itemMatches(inputDefinition, actualInput, false);
			}
		}
	}
}
