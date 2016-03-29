package net.teamio.taam.piping;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidStack;

public class PipeInfo {
	
	public PipeInfo(int capacity) {
		this.capacity = capacity;
		content = new FluidStack[0];
	}
	
	public final int capacity;
	
	public int pressure;
	public int suction;
	public int fillLevel;
	public FluidStack[] content;

	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("pressure", pressure);
		tag.setInteger("suction", suction);
		NBTTagList list = new NBTTagList();
		for(FluidStack stack : content) {
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
		if(list == null || list.tagCount() == 0) {
			content = new FluidStack[0];
		} else {
			content = new FluidStack[list.tagCount()];
			for(int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound fluidTag = list.getCompoundTagAt(i);
				content[i] = FluidStack.loadFluidStackFromNBT(fluidTag);
			}
		}
		recalculateFillLevel();
	}
	
	private void recalculateFillLevel() {
		fillLevel = 0;
		for(FluidStack stack : content) {
			fillLevel += stack.amount;
		}
	}
	
	public int addFluid(FluidStack stack) {
		int current = fillLevel;
		//TODO: Caching. Later.
		recalculateFillLevel();
		int free = capacity - current;
		//System.out.println("Add: " + stack.amount + " Current: " + current + " free: " + free);
		if(free < 1) {
			return 0;
		}
		int insert = Math.min(stack.amount, free);
		
		for(FluidStack contentStack : content) {
			if(contentStack.isFluidEqual(stack)) {
				contentStack.amount += insert;
				return insert;
			}
		}
		
		FluidStack[] newContent = new FluidStack[content.length + 1];
		System.arraycopy(content, 0, newContent, 0, content.length);
		newContent[content.length] = stack.copy();
		content = newContent;
		
		return insert;
	}
}
