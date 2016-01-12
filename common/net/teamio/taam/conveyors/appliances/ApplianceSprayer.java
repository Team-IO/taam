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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.oredict.OreDictionary;
import net.teamio.taam.Config;
import net.teamio.taam.TaamMain;
import net.teamio.taam.conveyors.ApplianceInventory;
import net.teamio.taam.conveyors.IConveyorAppliance;
import net.teamio.taam.conveyors.IConveyorApplianceFactory;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;

public class ApplianceSprayer extends ApplianceInventory {

	public ApplianceSprayer() {
		super(1, 1);
	}
	
	public static class Factory implements IConveyorApplianceFactory {

		@Override
		public IConveyorAppliance setUpApplianceInventory(String type, IConveyorApplianceHost conveyor) {
			IConveyorAppliance ainv = new ApplianceSprayer();
			return ainv; 
		}
	}
	
	/*
	 * "Static"
	 */

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(TaamMain.itemConveyorAppliance, 1, 0);
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
	
	/*
	 * "Dynamic"
	 */
	
	@Override
	public void processItem(IConveyorApplianceHost conveyor, int slot, ItemWrapper wrapper) {
		//TODO: Review this, and change to fluid usage (will use fluid during progress, can leave half-processed items.)
		if(wrapper.processing > 1) {//Config.pl_appl_sprayer_maxProgress) {
			int paintType = getAvailablePaintType();
			int itemPaintType = getItemPaintType(wrapper.itemStack);
			if(paintType == itemPaintType) {
				System.out.println("No need to paint");
				//Reset progress, as we cannot spray it
				wrapper.processing = 0;
				return;
			}
			if(checkResource() >= Config.pl_appl_sprayer_resourceUsage) {
				consumeResource();
				setItemPaintType(wrapper.itemStack, paintType);
				wrapper.processing = -1;
			} else {
				System.out.println("No resources");
				//Reset progress, as we cannot spray it
				wrapper.processing = 0;
			}
		}
	}
	
	private int getAvailablePaintType() {
		ItemStack is = inventory.getStackInSlot(0);
		if(is == null || is.getItem() == null || is.stackSize <= 0) {
			return -1;
		}
		for(int i = 0; i < dyes.length; i++) {
			List<ItemStack> dyeOres = OreDictionary.getOres("dye" + dyes[i]);
			Iterator<ItemStack> itr = ((ArrayList<ItemStack>)dyeOres).iterator();
            while (itr.hasNext())
            {
                if(OreDictionary.itemMatches(itr.next(), is, false)) {
                	return 15-i;
                }
            }
		}
		return -1;
	}
	
	private void consumeResource() {
		inventory.getStackInSlot(0).stackSize -= Config.pl_appl_sprayer_resourceUsage;
		if(inventory.getStackInSlot(0).stackSize <= 0) {
			inventory.setInventorySlotContents(0, null);
		}
	}
	
	private int checkResource() {
		if(getAvailablePaintType() > -1) {
			return inventory.getStackInSlot(0).stackSize;
		}
		return 0;
	}

	@Override
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

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return true;
	}

	@Override
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

	//TODO: Create global recipe registry for the production line
	
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
	protected int getTankForSide(EnumFacing from) {
		return 0;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[] { 0 };
	}
	
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return true;
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return true;
	}

	@Override
	public IChatComponent getDisplayName() {
		return new ChatComponentTranslation("item.taam.item.conveyor_appliance.sprayer.name");
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}
	
	@Override
	public String getCommandSenderName() {
		return "item.taam.item.conveyor_appliance.sprayer.name";
	}

}
