package net.teamio.taam.content.conveyors;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;

public class ItemProductionLine extends ItemMultiTexture {

	
	public ItemProductionLine(Block blockA, Block blockB, String[] names) {
		super(blockA, blockB, names);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player,
			World world, int x, int y, int z, int side, float hitX, float hitY,
			float hitZ, int metadata) {
		ForgeDirection dir = ForgeDirection.getOrientation(side).getOpposite();
		ForgeDirection placeDir = ForgeDirection.NORTH;
		boolean defaultPlacement = false;
		
		if(dir == ForgeDirection.UP || dir == ForgeDirection.DOWN) {
			System.out.println("Default Placement, UP DOWN");
			defaultPlacement = true;
		} else {
			TileEntity ent = world.getTileEntity(x + dir.offsetX, y, z + dir.offsetZ);
			if(ent instanceof TileEntityConveyor) {
				System.out.println("Conveyor Placement");
				ForgeDirection otherDir = ((TileEntityConveyor) ent).getFacingDirection();
				if(otherDir == dir || otherDir == dir.getOpposite()) {
					System.out.println("Same Direction");
					placeDir = otherDir;
				} else {
					System.out.println("Face Direction");
					placeDir = dir;
				}
			} else if(ent instanceof IConveyorAwareTE) {
				placeDir = dir;
				System.out.println("Face Direction IConveyorAwareTE");
			} else {
				defaultPlacement = true;
				System.out.println("Default Placement, unknown block");
			}
		}
		
		if(defaultPlacement) {
			// We hit top/bottom of a block
			double xDist = player.posX - x;
			double zDist = player.posZ - z;
			if(Math.abs(xDist) > Math.abs(zDist)) {
				if(xDist < 0) {
					placeDir = ForgeDirection.EAST;
				} else {
					placeDir = ForgeDirection.WEST;
				}
			} else {
				if(zDist < 0) {
					placeDir = ForgeDirection.SOUTH;
				} else {
					placeDir = ForgeDirection.NORTH;
				}
			}
			System.out.println("Default: " + placeDir);
		}
		
		boolean canStay;
		if(metadata <= 2) {
			// Conveyor
			canStay = BlockProductionLine.canBlockStay(world, x, y, z, placeDir);
		} else {
			canStay = BlockProductionLine.canBlockStay(world, x, y, z, null);
		}
		
		
		
		if(canStay) {
			boolean success = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
			if(success) {
				TileEntity te = world.getTileEntity(x, y, z);
				if(te instanceof IRotatable) {
					System.out.println("Setting direction " + placeDir);
					((IRotatable) te).setFacingDirection(placeDir);
				}
			}
			return success;
		}
		return false;
	}

}
