package founderio.taam.blocks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import founderio.taam.conveyors.IConveyorAwareTE;
import founderio.taam.conveyors.ItemWrapper;

public class TileEntityConveyor extends BaseTileEntity implements IInventory, IConveyorAwareTE {
	
	private List<ItemWrapper> items;
	
	private ForgeDirection direction = ForgeDirection.NORTH;
	
	public ForgeDirection getDirection() {
		return direction;
	}

	public void setDirection(ForgeDirection direction) {
		this.direction = direction;
		updateState();
	}
	
	

	public List<ItemWrapper> getItems() {
		return items;
	}

	public TileEntityConveyor() {
		items = new ArrayList<ItemWrapper>();
	}

	@Override
	public boolean addItemAt(ItemStack item, double x, double y, double z) {
		x -= xCoord;
		y -= yCoord;
		z -= zCoord;
		if(y < 0.4 || y > 1) {
			return false;
		}
		double progress;
		double offset;
		if(direction.offsetX < 0) {
			progress = 1f-x;
			offset = z;
		} else if(direction.offsetX > 0) {
			progress = x;
			offset = z;
		} else if(direction.offsetZ < 0) {
			progress = 1f-z;
			offset = x;
		} else if(direction.offsetZ > 0) {
			progress = z;
			offset = x;
		} else {
			return false;
		}
		// check with security buffer in mind
		if(progress < -0.01 || progress > 1.01 || offset < 0.2 || offset > 0.8) {
			return false;
		}
		items.add(new ItemWrapper(item, (int)(progress * 100), (int)(offset * 100)));
		return true;
	}

	@Override
	public boolean addItemAt(ItemWrapper item, double x, double y, double z) {
		x -= xCoord;
		y -= yCoord;
		z -= zCoord;
		if(y < 0.4 || y > 1) {
			return false;
		}
		double progress;
		double offset;
		if(direction.offsetX < 0) {
			progress = 1f-x;
			offset = z;
		} else if(direction.offsetX > 0) {
			progress = x;
			offset = z;
		} else if(direction.offsetZ < 0) {
			progress = 1f-z;
			offset = x;
		} else if(direction.offsetZ > 0) {
			progress = z;
			offset = x;
		} else {
			return false;
		}
		// check with security buffer in mind
		if(progress < -0.01 || progress > 1.01 || offset < 0.2 || offset > 0.8) {
			return false;
		}
		item.offset = (int)(offset * 100);
		item.progress = (int)(progress * 100);
		items.add(item);
		return true;
	}
	
	public static final int maxProgress = 130;
	
	@Override
	public void updateEntity() {
		
		AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(xCoord, yCoord + 0.5, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);

		for(Object obj : worldObj.loadedEntityList) {
			Entity ent = (Entity)obj;
			
			if(ent instanceof EntityItem && ent.boundingBox.intersectsWith(bb)) {
				if(addItemAt(((EntityItem)ent).getEntityItem(), ent.posX, ent.posY, ent.posZ)) {
					ent.setDead();
					break;
				}
			}
		}
		
		Iterator<ItemWrapper> iter = items.iterator();
		
		while(iter.hasNext()) {
			ItemWrapper wrapper = iter.next();
			wrapper.progress += 1;
			if(wrapper.progress > maxProgress) {
				wrapper.progress = maxProgress;//Just to be sure... (maybe needed later, so commented out.)
				if(isBlockConveyor(worldObj, xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ)) {
					//TODO: delegate this with coordinates to the other "conveyor"
					TileEntityConveyor conveyor = (TileEntityConveyor) worldObj.getTileEntity(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
					
					ForgeDirection dirRotated = direction.getRotation(ForgeDirection.UP);

					System.out.println("Trying to remove");
					
					float progress = wrapper.progress / 100f;
					if(direction.offsetX < 0 || direction.offsetZ < 0) {
						progress = 1-progress;
						progress *= -1;// cope for the fact that direction offset is negative
					}
					float offset = wrapper.offset / 100f;
					if(dirRotated.offsetX < 0 || dirRotated.offsetZ < 0) {
						offset = 1-offset;
						offset *= -1;// cope for the fact that direction offset is negative
					}
					
					if(conveyor.addItemAt(wrapper,
							xCoord + direction.offsetX * progress + dirRotated.offsetX * offset,
							yCoord + 0.4f,
							zCoord + direction.offsetZ * progress + dirRotated.offsetZ * offset)) {
						System.out.println("Removing.");
						iter.remove();
					}
				} else if(!worldObj.isRemote) {
					
					EntityItem item = new EntityItem(worldObj, xCoord + 0.5f + direction.offsetX * 0.5f, yCoord + 0.4, zCoord + 0.5f + direction.offsetZ * 0.5f, wrapper.itemStack);//new EntityItem(worldObj);
					worldObj.spawnEntityInWorld(item);
					iter.remove();
				}
			}
		}
	}
	
	public static boolean isBlockConveyor(IBlockAccess world, int x, int y, int z) {
		//TODO: create interface that allows other blocks to mimic the same receiving method.

		Block nextBlock = world.getBlock(x, y, z);
		if(nextBlock instanceof BlockProductionLine) {
			if(world.getTileEntity(x, y, z) instanceof TileEntityConveyor) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setInteger("direction", direction.ordinal());
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		direction = ForgeDirection.getOrientation(tag.getInteger("direction"));
	}

	@Override
	public int getSizeInventory() {
		// One more slot than already used so we can always accept items? Maybe not....
		return items.size() + 1;
	}

	@Override
	public ItemStack getStackInSlot(int p_70301_1_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getInventoryName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void openInventory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		// TODO Auto-generated method stub
		return false;
	}

}
