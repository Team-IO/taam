package net.teamio.taam.conveyors.appliances;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockHardenedClay;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;
import net.teamio.taam.Config;
import net.teamio.taam.content.conveyors.ATileEntityAppliance;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;

public class ApplianceSprayer extends ATileEntityAppliance implements IFluidHandler {

	public ApplianceSprayer() {
	}
	
	static final String[] dyes = { "Black", "Red", "Green", "Brown", "Blue",
			"Purple", "Cyan", "LightGray", "Gray", "Pink", "Lime", "Yellow",
			"LightBlue", "Magenta", "Orange", "White" };
	
	private int getItemPaintType(ItemStack is) {
		//TODO: Migrate to tanks once ready
		if(is == null) {
			return -1;
		}
		Item item = is.getItem();
		if (item == null) {
			return -1;
		}
		Block block = Block.getBlockFromItem(item);
		if(block instanceof BlockColored ||
				block instanceof BlockHardenedClay ||
				block instanceof BlockCarpet ||
				block instanceof BlockStainedGlass ||
				block instanceof BlockStainedGlassPane) {
			return is.getItemDamage();
		}
		return -1;
	}
	
	private void setItemPaintType(ItemStack is, int paintType) {
		if(is == null) {
			return;
		}
		Item item = is.getItem();
		if (item == null) {
			return;
		}
		Block block = Block.getBlockFromItem(item);
		if(block instanceof BlockColored ||
				block instanceof BlockHardenedClay ||
				block instanceof BlockCarpet ||
				block instanceof BlockStainedGlass ||
				block instanceof BlockStainedGlassPane) {
			is.setItemDamage(paintType);
		}
	}
	
	@Override
	public boolean processItem(IConveyorApplianceHost conveyor, int slot, ItemWrapper wrapper) {
		//TODO: Review this, and change to fluid usage (will use fluid during progress, can leave half-processed items.)
		if(wrapper.processing > 1) {//Config.pl_appl_sprayer_maxProgress) {
			int paintType = 0;//getAvailablePaintType();
			int itemPaintType = getItemPaintType(wrapper.itemStack);
			if(paintType == itemPaintType) {
				System.out.println("No need to paint");
				//Reset progress, as we cannot spray it
				wrapper.processing = 0;
			}
			int resourceLevel = 0;//checkResource();
			if(resourceLevel >= Config.pl_appl_sprayer_resourceUsage) {
				//consumeResource();
				setItemPaintType(wrapper.itemStack, paintType);
				wrapper.processing = -1;
			} else {
				System.out.println("No resources");
				//Reset progress, as we cannot spray it
				wrapper.processing = 0;
			}
			return true;
		}
		return false;
	}

	public boolean canProcessItem(ItemWrapper wrapper) {
		Item item = wrapper.itemStack.getItem();
		if (item == null) {
			return false;
		}
		Block block = Block.getBlockFromItem(item);
		if(block instanceof BlockColored ||
				block instanceof BlockHardenedClay ||
				block instanceof BlockCarpet ||
				block instanceof BlockStainedGlass ||
				block instanceof BlockStainedGlassPane) {
			return true;
		}
		return false;
	}

	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		List<ItemStack> dyeOres = OreDictionary.getOres("dye");
		Iterator<ItemStack> itr = ((ArrayList<ItemStack>)dyeOres).iterator();
        while (itr.hasNext())
        {
            if(OreDictionary.itemMatches(itr.next(), itemStack, false)) {
            	return true;
            }
        }
        return false;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		// TODO: Check if fluid is a matching type
		return false;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		// TODO Auto-generated method stub
		return null;
	}

}
