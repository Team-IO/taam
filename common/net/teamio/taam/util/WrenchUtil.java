package net.teamio.taam.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.teamio.taam.Log;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.common.TileEntityCreativeCache;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;

public class WrenchUtil {

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
	
	public static boolean wrenchBlock(World world, BlockPos pos, EntityPlayer player,
			EnumFacing side, float hitX, float hitY,
			float hitZ) {
		Log.debug("Checking for wrench activity.");
		
		boolean playerHasWrench = WrenchUtil.playerHasWrench(player);
		Log.debug("Player has wrench: " + playerHasWrench);
		
		if(!playerHasWrench) {
			Log.debug("Player has no wrench, skipping.");
			return false;
		}
		
		boolean playerIsSneaking = player.isSneaking();
		Log.debug("Player is sneaking: " + playerIsSneaking);
		
		TileEntity te = world.getTileEntity(pos);
		
		if(playerHasWrench) {
			
			if(playerIsSneaking) {
				if(te instanceof IConveyorApplianceHost) {
					IConveyorApplianceHost conveyor = (IConveyorApplianceHost) te;
					Log.debug("Disassembling IConveyorApplianceHost");
					if(ConveyorUtil.dropAppliance(conveyor, player, world, pos)) {
						Log.debug("Dropping appliance done, removing appliance.");
						conveyor.removeAppliance();
						Log.debug("Dropping appliance done, removing appliance done.");
						return true;
					}
					Log.debug("No appliance dropped, moving on.");
				}
				if(WrenchUtil.isWrenchableEntity(te)) {
					TaamUtil.breakBlockToInventory(player, world, pos);
					return true;
				}
			} else {
				world.getBlockState(pos).getBlock().rotateBlock(world, pos, side);
				return true;
			}
		}
		return false;
	}
	
	public static boolean rotateBlock(TileEntity te) {
		if(te instanceof IRotatable) {
			IRotatable rotatable = (IRotatable) te;
			rotatable.setFacingDirection(rotatable.getNextFacingDirection());
			return true;
		}
		return false;
	}

	private static boolean isWrenchableEntity(TileEntity te) {
		return te instanceof IConveyorAwareTE ||
				te instanceof TileEntityCreativeCache;
	}

}
