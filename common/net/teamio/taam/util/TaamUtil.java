package net.teamio.taam.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.teamio.taam.Config;
import net.teamio.taam.MultipartHandler;
import net.teamio.taam.Taam;
import net.teamio.taam.content.IRedstoneControlled;
import net.teamio.taam.conveyors.IConveyorApplianceHost;
import net.teamio.taam.conveyors.IConveyorSlots;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Generic Utility Methods, used across multiple "themes".
 *
 * @author Oliver Kahrmann
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
	public static void updateBlock(World world, BlockPos pos, boolean reRender) {
		if (world.isRemote) {
			return;
		}
		IBlockState state = world.getBlockState(pos);
		/*
		 * Flag documentation from world.markAndNotifyBlock:
		 * Flag 1 will cause a block update.
		 * Flag 2 will send the change to clients (you almost always want this).
		 * Flag 4 prevents the block from being re-rendered, if this is a client
		 * world.
		 * Flags can be added together.
		 */
		world.notifyBlockUpdate(pos, state, state, reRender ? 3 : 7);
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
	 * <p>
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
	 * <p>
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
	 * @param rand
	 * @param redstoneMode
	 * @param redstoneHigh
	 * @return
	 * @deprecated Will be revised & potentially (re)moved. Currently no
	 * alternative. TODO: Revise
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
	 * @param pos   The attachable block.
	 * @param dir   The direction in which to check. Checks the block at the
	 *              offset coordinates.
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
		}
		if (inputDefinition == null) {
			// Ore dictionary match
			int[] oreIDs = OreDictionary.getOreIDs(actualInput);
			int myID = OreDictionary.getOreID(inputOreDictName);
			return ArrayUtils.contains(oreIDs, myID);
		}
		// Item stack match, respecting wildcards
		return inputDefinition.isItemEqual(actualInput)
				|| OreDictionary.itemMatches(inputDefinition, actualInput, false);
	}

	/**
	 * Compares if the two given items tacks have a common ore dictionary name.
	 *
	 * @param stack1
	 * @param stack2
	 * @return
	 */
	public static boolean isOreDictMatch(ItemStack stack1, ItemStack stack2) {
		int[] ids1 = OreDictionary.getOreIDs(stack1);
		int[] ids2 = OreDictionary.getOreIDs(stack2);

		for (int oreID : ids1) {
			if (ArrayUtils.contains(ids2, oreID)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Compares if the two given itemstacks are from the same mod. (a.k.a.
	 * sharing the same domain)
	 *
	 * @param stack1
	 * @param stack2
	 * @return
	 */
	public static boolean isModMatch(@Nullable ItemStack stack1, @Nullable ItemStack stack2) {
		if (InventoryUtils.isEmpty(stack1)) return InventoryUtils.isEmpty(stack2);
		if (InventoryUtils.isEmpty(stack2)) return false;

		ResourceLocation regName1 = stack1.getItem().getRegistryName();
		ResourceLocation regName2 = stack2.getItem().getRegistryName();
		if (regName1 == null || regName2 == null) return false;
		return regName1.getNamespace().equals(regName2.getNamespace());
	}

	/**
	 * Translates an inventory (IWorldNameable) or returns a custom name, if present.
	 *
	 * @param inventory
	 * @return A translated or custom name of the given IWorldNameable.
	 */
	@SideOnly(Side.CLIENT)
	public static String getTranslatedName(IWorldNameable inventory) {
		if (inventory.hasCustomName()) {
			return inventory.getDisplayName().getFormattedText();
		}
		return I18n.format(inventory.getDisplayName().getFormattedText());
	}

	public static <T> T getCapability(Capability<T> capability, TileEntity tileEntity, EnumFacing side) {
		if (tileEntity == null) {
			return null;
		}
		if (tileEntity.hasCapability(capability, side)) {
			return tileEntity.getCapability(capability, side);
		}
		if (Config.multipart_present) {
			return MultipartHandler.getCapabilityForCenter(capability, tileEntity.getWorld(), tileEntity.getPos(), side);
		}
		return null;
	}
}
