package net.teamio.taam.util;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.teamio.taam.Taam;
import net.teamio.taam.content.IRedstoneControlled;
import net.teamio.taam.conveyors.IConveyorApplianceHost;
import net.teamio.taam.conveyors.IConveyorSlots;

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

	/**
	 * Updates a block on server-side so that an update packet is sent to the
	 * player.
	 * 
	 * @param world
	 * @param pos
	 */
	public static void updateBlock(World world, BlockPos pos) {
		if (world.isRemote) {
			return;
		}
		IBlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 3);
	}

	/**
	 * Checks if the given block can be dropped into, i.e. there is air or a
	 * liquid.
	 * 
	 * @param world
	 * @param pos
	 * @return true, if the block at the given position is air or a liquid.
	 */
	public static boolean canDropIntoWorld(IBlockAccess world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		return block.isAir(state, world, pos) || state.getMaterial().isLiquid();
	}

	/**
	 * Breaks a block in the world, dropping it as an item.
	 * 
	 * @param world
	 * @param pos
	 */
	public static void breakBlockInWorld(World world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		breakBlockInWorld(world, pos, blockState);
	}

	/**
	 * Breaks a block in the world, dropping it as an item.
	 * 
	 * Convenience method if the blockstate is already available.
	 * 
	 * @param world
	 * @param pos
	 * @param blockState
	 */
	public static void breakBlockInWorld(World world, BlockPos pos, IBlockState blockState) {
		Block block = blockState.getBlock();
		block.dropBlockAsItem(world, pos, blockState, 0);
		world.setBlockToAir(pos);
	}

	/**
	 * Breaks a block in the world and tries to add it to the given player's
	 * inventory. If that fails, it is dropped into the world.
	 * 
	 * @param player
	 * @param world
	 * @param pos
	 */
	public static void breakBlockToInventory(EntityPlayer player, World world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		breakBlockToInventory(player, world, pos, blockState);
	}

	/**
	 * Breaks a block in the world and tries to add it to the given player's
	 * inventory. If that fails, it is dropped into the world.
	 * 
	 * Convenience method if the blockstate is already available.
	 * 
	 * @param player
	 * @param world
	 * @param pos
	 * @param blockState
	 */
	public static void breakBlockToInventory(EntityPlayer player, World world, BlockPos pos, IBlockState blockState) {
		List<ItemStack> toDrop = getDropsFromWorld(world, pos, blockState);
		if (toDrop != null) {
			for (ItemStack stack : toDrop) {
				InventoryUtils.tryDropToInventory(player, stack, pos);
			}
		}
		world.setBlockToAir(pos);
	}

	/**
	 * Returns the block drops on a given block. Assumes 0 fortune level.
	 * 
	 * @param world
	 * @param pos
	 * @param blockState
	 * @return
	 */
	public static List<ItemStack> getDropsFromWorld(World world, BlockPos pos, IBlockState blockState) {
		Block block = blockState.getBlock();
		return block.getDrops(world, pos, blockState, 0);
	}

	/**
	 * Utility method for checking if a machine is allowed to run, given a
	 * redstone mode and the available redstone signal.
	 * 
	 * 
	 * @param rand
	 * @param redstoneMode
	 * @param redstoneHigh
	 * @return
	 * 
	 * @deprecated Will be revised & potentially (re)moved. Currently no
	 *             alternative. TODO: Revise
	 */
	@Deprecated
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
	 * solid side or a TileEntity providing {@link IConveyorSlots} via
	 * capability on that side.
	 *
	 * @param world
	 * @param pos
	 *            The attachable block.
	 * @param dir
	 *            The direction in which to check. Checks the block at the
	 *            offset coordinates.
	 * @return
	 */
	public static boolean canAttach(IBlockAccess world, BlockPos pos, EnumFacing dir) {
		EnumFacing opposide = dir.getOpposite();
		if (world.isSideSolid(pos.offset(dir), opposide, false)) {
			return true;
		}
		TileEntity ent = world.getTileEntity(pos.offset(dir));
		return ent != null && ent.hasCapability(Taam.CAPABILITY_CONVEYOR, opposide);
	}

	/**
	 * Decides whether an appliance can be placed somewhere. Checks availability
	 * of an {@link IConveyorApplianceHost}. TODO: use a capability for that!
	 * 
	 * @param world
	 * @param pos
	 * @param dir
	 * @return
	 */
	public static boolean canAttachAppliance(IBlockAccess world, BlockPos pos, EnumFacing dir) {
		TileEntity ent = world.getTileEntity(pos.offset(dir));
		return ent instanceof IConveyorApplianceHost;
	}

	/**
	 * Checks if actualInput is the same item as inputDefinition (respecting
	 * metadata wildcards defined in the recipe) or, if inputDefinition is null,
	 * if actualInput is registered with the ore dictionary matching
	 * inputOreDictName.
	 *
	 * @param inputDefinition
	 * @param inputOreDictName
	 * @param actualInput
	 * @return
	 */
	public static boolean isInputMatching(ItemStack inputDefinition, String inputOreDictName, ItemStack actualInput) {
		if (actualInput == null) {
			// Only accept null input if both stack and ore dictionary key are
			// null
			return inputDefinition != null && inputOreDictName != null;
		} else {
			if (inputDefinition == null) {
				// Ore dictionary match
				int[] oreIDs = OreDictionary.getOreIDs(actualInput);
				int myID = OreDictionary.getOreID(inputOreDictName);
				return ArrayUtils.contains(oreIDs, myID);
			} else {
				// Item stack match, respecting wildcards
				return inputDefinition.isItemEqual(actualInput)
						|| OreDictionary.itemMatches(inputDefinition, actualInput, false);
			}
		}
	}
}
