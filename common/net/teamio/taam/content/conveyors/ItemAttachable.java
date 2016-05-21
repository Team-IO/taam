package net.teamio.taam.content.conveyors;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
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
		if (TaamUtil.canAttach(world, pos, dir)) {
			if (dir == EnumFacing.UP || dir == EnumFacing.DOWN) {
				dir = EnumFacing.NORTH;
			}
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
