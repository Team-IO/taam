package net.teamio.taam.conveyors;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.teamio.taam.util.InventoryUtils;

public class OutputChuteBacklog extends OutputChute {
	public ItemStack[] backlog;

	public void writeToNBT(NBTTagCompound tag) {
		if (backlog != null) {
			tag.setTag("backlog", InventoryUtils.writeItemStacksToTag(backlog, 64, true));
		}
	}

	public void readFromNBT(NBTTagCompound tag) {
		if (tag == null) {
			backlog = null;
			return;
		}
		NBTTagList tagList = tag.getTagList("backlog", NBT.TAG_COMPOUND);
		backlog = new ItemStack[tagList.tagCount()];
		InventoryUtils.readItemStacksFromTag(backlog, tagList, true);
	}

	@Override
	public boolean output(World world, BlockPos pos) {
		boolean somethingLeft = chuteMechanicsOutput(world, pos, outputInventory, backlog);
		if (!somethingLeft) {
			backlog = null;
		}
		return somethingLeft;
	}
}
