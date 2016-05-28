package net.teamio.taam.conveyors;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.teamio.taam.util.inv.InventoryUtils;

public class OutputChuteBacklog extends OutputChute {
	public ItemStack[] backlog;

	public void writeToNBT(NBTTagCompound tag) {
		if (backlog != null) {
			tag.setTag("backlog", InventoryUtils.writeItemStacksToTagSequential(backlog));
		}
	}

	public void readFromNBT(NBTTagCompound tag) {
		if(tag == null) {
			backlog = null;
			return;
		}
		NBTTagList tagList = tag.getTagList("backlog", NBT.TAG_COMPOUND);
		if (tagList == null) {
			backlog = null;
		} else {
			backlog = new ItemStack[tagList.tagCount()];
			InventoryUtils.readItemStacksFromTagSequential(backlog, tagList);
		}
	}

	@Override
	public boolean output(World world, BlockPos pos) {
		boolean somethingLeft = chuteMechanicsOutput(world, pos, outputInventory, backlog, 0);
		if(!somethingLeft) {
			backlog = null;
		}
		return somethingLeft;
	}
}
