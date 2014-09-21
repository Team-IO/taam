package founderio.taam.blocks;

import codechicken.lib.inventory.InventorySimple;
import codechicken.lib.inventory.InventoryUtils;
import founderio.taam.conveyors.IConveyorAwareTE;
import founderio.taam.conveyors.ItemWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;


public class TileEntityConveyorHopper extends BaseTileEntity implements IConveyorAwareTE {

	private InventorySimple inventory;
	
	public TileEntityConveyorHopper() {
		inventory = new InventorySimple(5, "Conveyor Hopper");
	}
	
	@Override
	public void updateEntity() {
		/*
		 * Find items laying on the conveyor.
		 */

		if(!worldObj.isRemote) {
			for(Object obj : worldObj.loadedEntityList) {
				Entity ent = (Entity)obj;
				
				if(ent instanceof EntityItem) {
					if(addItemAt(((EntityItem)ent).getEntityItem(), ent.posX, ent.posY, ent.posZ)) {
						ent.setDead();
						break;
					}
				}
			}
		}
		
		
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		//TODO: write intenvory
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		//TODO: read intenvory
	}

	@Override
	public boolean addItemAt(ItemStack item, double x, double y, double z) {
		x -= xCoord;
		y -= yCoord;
		z -= zCoord;
		if(y < 0.4 || y > 1) {
			return false;
		}
		if(x > 1.1 || x < -0.1 || z > 1.1 || z < -0.1) {
			return false;
		}
//		InventoryUtils.
//		inventory.isItemValidForSlot(p_94041_1_, p_94041_2_)
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addItemAt(ItemWrapper item, double x, double y, double z) {
		return addItemAt(item.itemStack, x, y, z);
	}

}
