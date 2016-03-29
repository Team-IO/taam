package net.teamio.taam.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.teamio.taam.Log;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.common.TileEntityCreativeCache;
import net.teamio.taam.content.common.TileEntitySensor;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;

public class WrenchUtil {

	/**
	 * Returns true if the player is holding a wrench in his hand.
	 * @param player
	 * @return
	 */
	public static boolean playerHasWrenchInMainhand(EntityPlayer player) {
		ItemStack held = player.getHeldItemMainhand();
		if(held == null) {
			return false;
		}
			//TODO: Check other wrench types once supported
		return held.getItem() == TaamMain.itemWrench;
	}
	public static boolean playerHasHasWrenchInOffhand(EntityPlayer player){
		ItemStack held = player.getHeldItemOffhand();
		if(held == null) {
			return false;
		}
			//TODO: Check other wrench types once supported
		return held.getItem() == TaamMain.itemWrench;
	}
	
	public static EnumActionResult wrenchBlock(World world, BlockPos pos, EntityPlayer player,
			EnumFacing side, float hitX, float hitY,
			float hitZ) {
		Log.debug("Checking for wrench activity.");
		
		boolean playerHasWrench = WrenchUtil.playerHasWrenchInMainhand(player);
		Log.debug("Player has wrench: " + playerHasWrench);
		
		if(!playerHasWrench) {
			Log.debug("Player has no wrench, skipping.");
			return EnumActionResult.PASS;
		}
		
		boolean playerIsSneaking = player.isSneaking();
		Log.debug("Player is sneaking: " + playerIsSneaking);
		
		TileEntity te = world.getTileEntity(pos);
		
		if(playerHasWrench) {
			
			if(playerIsSneaking) {
				if(WrenchUtil.isWrenchableEntity(te)) {
					TaamUtil.breakBlockToInventory(player, world, pos);
					return EnumActionResult.SUCCESS;
				}
			} else {
				world.getBlockState(pos).getBlock().rotateBlock(world, pos, side);
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.FAIL;
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
				te instanceof TileEntityCreativeCache ||
				te instanceof TileEntitySensor;
	}

}
