package net.teamio.taam.content.conveyors;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.teamio.taam.Taam;
import net.teamio.taam.content.IRenderableItem;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.util.TaamUtil;

public class ItemAttachable extends ItemMultiTexture implements IRenderableItem {

	
	public ItemAttachable(Block blockA, Block blockB, String[] names) {
		super(blockA, blockB, names);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
			float hitX, float hitY, float hitZ, IBlockState newState) {
		EnumFacing dir = side.getOpposite();
		if(TaamUtil.canAttach(world, pos, dir)) {
			if(dir == EnumFacing.UP || dir == EnumFacing.DOWN) {
				dir = EnumFacing.NORTH;
			}
			boolean success = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState.withProperty(BlockProductionLineAttachable.FACING, dir));
			if(success) {
				TileEntity te = world.getTileEntity(pos);
				if(te instanceof IRotatable) {
					((IRotatable) te).setFacingDirection(dir);
				}
			}
			return success;
		}
		return false;
	}

	@Override
	public List<String> getVisibleParts(ItemStack stack) {
		int meta = stack.getMetadata();
		Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META variant = Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.values()[meta];
		switch(variant) {
		case itembag:
			return TileEntityConveyorItemBag.parts;
		case trashcan:
			return TileEntityConveyorTrashCan.parts;
		default:
			return TileEntityConveyor.parts_invalid;
		}
	}

}
