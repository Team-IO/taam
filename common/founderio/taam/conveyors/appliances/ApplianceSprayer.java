package founderio.taam.conveyors.appliances;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockHardenedClay;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import founderio.taam.Config;
import founderio.taam.blocks.TileEntityConveyor;
import founderio.taam.conveyors.ApplianceInventory;
import founderio.taam.conveyors.IConveyorAppliance;
import founderio.taam.conveyors.ItemWrapper;

public class ApplianceSprayer implements IConveyorAppliance {

	@Override
	public int getProgressBegin() {
		return 40;
	}

	@Override
	public int getProgressEnd() {
		return 60;
	}

	static final String[] dyes = { "Black", "Red", "Green", "Brown", "Blue",
			"Purple", "Cyan", "LightGray", "Gray", "Pink", "Lime", "Yellow",
			"LightBlue", "Magenta", "Orange", "White" };
	
	@Override
	public void processItem(TileEntityConveyor conveyor, ApplianceInventory inventory, ItemWrapper wrapper) {
		//TODO: Review this, and change to fluid usage (will use fluid during progress, can leave half-processed items.)
		if(wrapper.progress > Config.pl_appl_sprayer_maxProgress) {
			int paintType = getPaintType(inventory);
			int itemPaintType = getItemPaintType(wrapper.itemStack);
			if(paintType == itemPaintType) {
				//Reset progress, as we cannot spray it
				wrapper.progress = 0;
				return;
			}
			if(checkResource(inventory) >= Config.pl_appl_sprayer_resourceUsage) {
				consumeResource(inventory);
				setItemPaintType(wrapper.itemStack, paintType);
			}
		}
		wrapper.progress = -1;
	}

	@Override
	public ApplianceInventory setUpApplianceInventory(TileEntityConveyor conveyor) {
		ApplianceInventory ainv = new ApplianceInventory();
		//TODO: Migrate to tanks once ready
		ainv.inventory = new InventoryBasic("appliance_sprayer", true, 1);
		return ainv; 
	}
	
	private int getItemPaintType(ItemStack is) {
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
	
	private int getPaintType(ApplianceInventory inventory) {
		ItemStack is = inventory.inventory.getStackInSlot(0);
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
	
	private void consumeResource(ApplianceInventory inventory) {
		inventory.inventory.getStackInSlot(0).stackSize -= Config.pl_appl_sprayer_resourceUsage;
		if(inventory.inventory.getStackInSlot(0).stackSize <= 0) {
			inventory.inventory.setInventorySlotContents(0, null);
		}
	}
	
	private int checkResource(ApplianceInventory inventory) {
		if(getPaintType(inventory) > -1) {
			return inventory.inventory.getStackInSlot(0).stackSize;
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
	public boolean isApplianceSetupCompatible(
			TileEntityConveyor conveyorSource,
			ApplianceInventory inventorySource,
			TileEntityConveyor conveyorTarget,
			ApplianceInventory inventoryTarget) {
		// TODO Auto-generated method stub
		return true;
	}

}
