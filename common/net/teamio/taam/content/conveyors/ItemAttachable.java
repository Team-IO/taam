package net.teamio.taam.content.conveyors;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.util.TaamUtil;

public class ItemAttachable extends ItemMultiTexture {

	public ItemAttachable(Block block, String[] names) {
		super(block, block, names);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
			float hitX, float hitY, float hitZ, IBlockState newState) {
		EnumFacing dir = side.getOpposite();

		// If the player clicked the top or bottom use the player's facing direction
		if (dir.getAxis() == EnumFacing.Axis.Y) {
			dir = player.getHorizontalFacing();
		}
		// Only place the attachable if we actually can attach
		if (TaamUtil.canAttach(world, pos, dir)) {
			boolean success = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
			if (success) {
				TileEntity te = world.getTileEntity(pos);
				if (te instanceof IRotatable) {
					((IRotatable) te).setFacingDirection(dir);
				}
			}
			return success;
		}
		return false;
	}
}
