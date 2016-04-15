package net.teamio.taam.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.teamio.taam.Log;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.BaseBlock;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.common.TileEntityCreativeCache;
import net.teamio.taam.content.common.TileEntitySensor;
import net.teamio.taam.conveyors.api.IConveyorAppliance;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;
import net.teamio.taam.machines.MachineTileEntity;

public class WrenchUtil {

	/**
	 * Returns true if the player is holding a wrench in his hand.
	 * 
	 * @param player
	 * @return
	 */
	public static boolean playerHasWrench(EntityPlayer player) {
		ItemStack held = player.getHeldItem();
		if (held == null) {
			return false;
		}
		// TODO: Check other wrench types once supported
		return held.getItem() == TaamMain.itemWrench;
	}
	
	public static boolean playerHasDebugTool(EntityPlayer player) {
		ItemStack held = player.getHeldItem();
		if (held == null) {
			return false;
		}
		// TODO: Check other tool types once supported
		return held.getItem() == TaamMain.itemDebugTool;
	}

	public static boolean wrenchBlock(World world, BlockPos pos, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {

		boolean playerHasWrench = WrenchUtil.playerHasWrench(player);

		if (!playerHasWrench) {
			return false;
		}

		boolean playerIsSneaking = player.isSneaking();
		Log.debug("Wrenching block. Player is sneaking: {}", playerIsSneaking);

		TileEntity te = world.getTileEntity(pos);

		IBlockState blockState = world.getBlockState(pos);
		if (playerIsSneaking) {
			if (WrenchUtil.isWrenchableBlock(blockState) || WrenchUtil.isWrenchableEntity(te)) {
				TaamUtil.breakBlockToInventory(player, world, pos, blockState);
				return true;
			}
		} else {
			blockState.getBlock().rotateBlock(world, pos, side);
			return true;
		}
		return false;
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
		return te instanceof IConveyorAwareTE ||
				te instanceof MachineTileEntity ||
				te instanceof TileEntityCreativeCache ||
				te instanceof TileEntitySensor ||
				te instanceof IConveyorAppliance ||
				te instanceof IConveyorApplianceHost;
	}

	private static boolean isWrenchableBlock(IBlockState blockState) {
		Block block = blockState.getBlock();
		return block == TaamMain.blockSupportBeam || block instanceof BaseBlock;
	}

}
