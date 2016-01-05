package net.teamio.taam.content.conveyors;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.util.TaamUtil;

public class ItemAttachable extends ItemMultiTexture {

	
	public ItemAttachable(Block blockA, Block blockB, String[] names) {
		super(blockA, blockB, names);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player,
			World world, int x, int y, int z, int side, float hitX, float hitY,
			float hitZ, int metadata) {
		ForgeDirection dir = ForgeDirection.getOrientation(side).getOpposite();
		if(TaamUtil.canAttach(world, x, y, z, dir)) {
			int meta;
			switch(dir) {
			default:
			case NORTH:
				meta = 0;
				break;
			case SOUTH:
				meta = 1;
				break;
			case EAST:
				meta = 2;
				break;
			case WEST:
				meta = 3;
				break;
			}
			boolean success = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, (metadata & 3) + (meta << 2));
			if(success) {
				TileEntity te = world.getTileEntity(x, y, z);
				if(te instanceof IRotatable) {
					((IRotatable) te).setFacingDirection(dir);
				}
			}
			return success;
		}
		return false;
	}

}
