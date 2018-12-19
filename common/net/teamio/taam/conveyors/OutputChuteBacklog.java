package net.teamio.taam.conveyors;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.teamio.taam.util.InventoryUtils;

/**
 * Implementation of the output chute logic that has an internal backlog of items.
 * This is useful for processing machines, as they can directly put their backlog in here.
 * <p>
 * The backlog array is automatically set to null once output is complete.
 * <p>
 * Use writeToNBT/readFromNBT to store the backlog. Only the 'backlog' tag is used,
 * so you can re-utilize the same tag compound used for the rest of a machine.
 *
 * @author Oliver Kahrmann
 */
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
