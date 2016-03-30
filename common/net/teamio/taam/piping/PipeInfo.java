package net.teamio.taam.piping;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidStack;

/**
 * Data class that implements most methods of {@link IPipe} to ease
 * implementation of said interface.
 * 
 * @author Oliver Kahrmann
 *
 */
public class PipeInfo {

	public PipeInfo(int capacity) {
		this.capacity = capacity;
		content = new ArrayList<FluidStack>();
	}

	public final int capacity;

	public int pressure;
	public int suction;
	public int fillLevel;
	public ArrayList<FluidStack> content;

	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("pressure", pressure);
		tag.setInteger("suction", suction);
		NBTTagList list = new NBTTagList();
		for (FluidStack stack : content) {
			NBTTagCompound fluidTag = new NBTTagCompound();
			stack.writeToNBT(fluidTag);
			list.appendTag(fluidTag);
		}
		tag.setTag("content", list);
	}

	public void readFromNBT(NBTTagCompound tag) {
		pressure = tag.getInteger("pressure");
		suction = tag.getInteger("suction");
		NBTTagList list = tag.getTagList("content", NBT.TAG_COMPOUND);
		content.clear();
		if (list != null && list.tagCount() != 0) {
			content.ensureCapacity(list.tagCount());
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound fluidTag = list.getCompoundTagAt(i);
				content.add(FluidStack.loadFluidStackFromNBT(fluidTag));
			}
		}
		content.trimToSize();
		recalculateFillLevel();
	}

	private void recalculateFillLevel() {
		fillLevel = 0;
		for (FluidStack stack : content) {
			fillLevel += stack.amount;
		}
	}

	public int addFluid(FluidStack stack) {
		if (stack == null || stack.amount == 0) {
			return 0;
		}

		int current = fillLevel;
		// TODO: Caching. Later.
		recalculateFillLevel();
		int free = capacity - current;

		if (free < 1) {
			return 0;
		}
		int insert = Math.min(stack.amount, free);

		for (FluidStack contentStack : content) {
			if (contentStack.isFluidEqual(stack)) {
				contentStack.amount += insert;
				return insert;
			}
		}
		content.add(stack.copy());

		return insert;
	}

	public int removeFluid(FluidStack stack) {
		if (stack == null || stack.amount == 0) {
			return 0;
		}

		for (int i = 0; i < content.size(); i++) {
			FluidStack contentStack = content.get(i);
			if (contentStack.isFluidEqual(stack)) {
				int removeAmount = Math.min(contentStack.amount, stack.amount);

				// Remove the fluid
				contentStack.amount -= removeAmount;
				if (contentStack.amount <= 0) {
					content.remove(i);
				}
				recalculateFillLevel();

				// And return the amount
				return removeAmount;
			}
		}
		return 0;
	}

	public int getFluidAmount(FluidStack like) {
		if (like == null) {
			return 0;
		}

		for (int i = 0; i < content.size(); i++) {
			FluidStack contentStack = content.get(i);
			if (contentStack.isFluidEqual(like)) {
				return contentStack.amount;
			}
		}
		return 0;
	}

	public FluidStack[] getFluids() {
		return content.toArray(new FluidStack[content.size()]);
	}
}
