package net.teamio.taam.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.teamio.taam.Log;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.common.TileEntityCreativeCache;
import net.teamio.taam.content.common.TileEntitySensor;
import net.teamio.taam.content.conveyors.TileEntityConveyor;
import net.teamio.taam.conveyors.IConveyorAppliance;
import net.teamio.taam.conveyors.IConveyorApplianceHost;
import net.teamio.taam.conveyors.IConveyorSlots;
import net.teamio.taam.machines.MachineTileEntity;

import javax.annotation.Nonnull;

public class WrenchUtil {


	/**
	 * Returns true if the player is holding a wrench in any hand.
	 *
	 * @param player The player to check for
	 * @return true if the player holds a wrench item
	 */
	public static boolean playerHoldsWrench(EntityPlayer player) {
		return playerHoldsWrench(player, EnumHand.MAIN_HAND) || playerHoldsWrench(player, EnumHand.OFF_HAND);
	}

	/**
	 * Returns true if the player is holding a wrench in the given hand.
	 *
	 * @param player The player to check for
	 * @return true if the given hand holds a wrench item
	 */
	public static boolean playerHoldsWrench(EntityPlayer player, EnumHand hand) {
		ItemStack held = player.getHeldItem(hand);
		if (InventoryUtils.isEmpty(held)) return false;
		//TODO: Check other wrench types once supported
		return held.getItem() == TaamMain.itemWrench;
	}

	/**
	 * Returns true if the player is holding a debug item in any hand.
	 *
	 * @param player The player to check for
	 * @return true if the player holds a debug item item
	 */
	public static boolean playerHoldsDebugTool(EntityPlayer player) {
		return playerHoldsDebugTool(player, EnumHand.MAIN_HAND) || playerHoldsDebugTool(player, EnumHand.OFF_HAND);
	}

	/**
	 * Returns true if the player is holding a debug item in the given hand.
	 *
	 * @param player The player to check for
	 * @return true if the given hand holds a debug item item
	 */
	public static boolean playerHoldsDebugTool(EntityPlayer player, EnumHand hand) {
		ItemStack held = player.getHeldItem(hand);
		if (InventoryUtils.isEmpty(held)) return false;
		return held.getItem() == TaamMain.itemDebugTool;
	}

	@Nonnull
	public static EnumActionResult wrenchBlock(World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		Log.debug("Checking for wrench activity.");

		boolean playerHasWrenchInMainhand = WrenchUtil.playerHoldsWrench(player, EnumHand.MAIN_HAND);
		boolean playerHasWrench = playerHasWrenchInMainhand || (player.isSneaking() && WrenchUtil.playerHoldsWrench(player, EnumHand.OFF_HAND));
		Log.debug("Player has wrench: " + playerHasWrench);

		if (!playerHasWrench) {
			Log.debug("Player has no wrench, skipping.");
			return EnumActionResult.PASS;
		}

		boolean playerIsSneaking = player.isSneaking() && playerHasWrenchInMainhand;
		Log.debug("Wrenching block. Player is sneaking: {}", playerIsSneaking);

		TileEntity te = world.getTileEntity(pos);

		IBlockState blockState = world.getBlockState(pos);
		if (playerIsSneaking) {
			if (WrenchUtil.isWrenchableBlock(blockState) || WrenchUtil.isWrenchableEntity(te)) {
				TaamUtil.breakBlockToInventory(player, world, pos, blockState);
				return EnumActionResult.SUCCESS;
			}
			return EnumActionResult.FAIL;
		}
		blockState.getBlock().rotateBlock(world, pos, side);
		return EnumActionResult.SUCCESS;
	}

	public static boolean rotateBlock(TileEntity te) {
		if (te instanceof IRotatable) {
			IRotatable rotatable = (IRotatable) te;
			rotatable.setFacingDirection(rotatable.getNextFacingDirection());
			return true;
		}
		return false;
	}

	private static boolean isWrenchableEntity(TileEntity te) {
		boolean is = te instanceof IConveyorSlots ||
				te instanceof MachineTileEntity ||
				te instanceof TileEntityCreativeCache ||
				te instanceof TileEntitySensor ||
				te instanceof IConveyorAppliance ||
				te instanceof IConveyorApplianceHost;
		// Conveyors handled separately, as they have stuff that disassembles separately
		return is && !(te instanceof TileEntityConveyor);
	}

	private static boolean isWrenchableBlock(IBlockState blockState) {
		Block block = blockState.getBlock();
		return block == TaamMain.blockSupportBeam;
	}

}
