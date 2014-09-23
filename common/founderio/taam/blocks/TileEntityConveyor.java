package founderio.taam.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import codechicken.lib.inventory.InventoryUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import founderio.taam.conveyors.ApplianceRegistry;
import founderio.taam.conveyors.ConveyorUtil;
import founderio.taam.conveyors.IConveyorAppliance;
import founderio.taam.conveyors.IConveyorApplianceFactory;
import founderio.taam.conveyors.IConveyorAwareTE;
import founderio.taam.conveyors.IRotatable;
import founderio.taam.conveyors.ItemWrapper;

public class TileEntityConveyor extends BaseTileEntity implements ISidedInventory, IFluidHandler, IConveyorAwareTE, IRotatable {
	
	private ArrayList<ItemWrapper> items;
	
	private ForgeDirection direction = ForgeDirection.NORTH;
	
	private boolean isEnd = false;
	private boolean isBegin = false;
	
	//TODO: encapsulate
	public String applianceType;
	public IConveyorAppliance appliance;
	
	public boolean isBegin() {
		return isBegin;
	}
	
	public boolean isEnd() {
		return isEnd;
	}
	
	@Override
	public void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();
		if(worldObj != null) {
			//TODO: Refine this (can we update that in a more reliable manner?)
			TileEntity te = worldObj.getTileEntity(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
			isEnd = !(te instanceof TileEntityConveyor) || ((TileEntityConveyor)te).getFacingDirection() != direction;
			ForgeDirection inverse = direction.getOpposite();
			te = worldObj.getTileEntity(xCoord + inverse.offsetX, yCoord + inverse.offsetY, zCoord + inverse.offsetZ);
			isBegin = !(te instanceof TileEntityConveyor) || ((TileEntityConveyor)te).getFacingDirection() != direction;
		}
	}
	
	
	//TODO: Migrate to IRotatable version..
	public void setDirection(ForgeDirection direction) {
		this.direction = direction;
		updateState();
	}

	@Override
	public ForgeDirection getFacingDirection() {
		return direction;
	}

	@Override
	public ForgeDirection getMountDirection() {
		return ForgeDirection.DOWN;
	}

	public List<ItemWrapper> getItems() {
		return items;
	}

	public TileEntityConveyor() {
		items = new ArrayList<ItemWrapper>();
	}

	@Override
	public int addItemAt(ItemStack item, double x, double y, double z) {
		x -= xCoord;
		y -= yCoord;
		z -= zCoord;
		if(y < 0.4 || y > 1) {
			return 0;
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
			return 0;
		}
		// check with security buffer in mind
		if(progress < -0.01 || progress > 1.01 || offset < 0.2 || offset > 0.8) {
			return 0;
		}
		items.add(new ItemWrapper(InventoryUtils.copyStack(item, item.stackSize), (int)(progress * 100), (int)(offset * 100)));
		updateState();
		return item.stackSize;
	}

	@Override
	public int addItemAt(ItemWrapper item, double x, double y, double z) {
		x -= xCoord;
		y -= yCoord;
		z -= zCoord;
		if(y < 0.4 || y > 1) {
			return 0;
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
			return 0;
		}
		// check with security buffer in mind
		if(progress < -0.01 || progress > 1.01 || offset < 0.2 || offset > 0.8) {
			return 0;
		}
		ItemWrapper clone = item.copy();
		clone.offset = (int)(offset * 100);
		clone.progress = (int)(progress * 100);
		items.add(clone);
		updateState();
		return item.getStackSize();
	}
	
	public static final int maxProgress = 130;
	
	private boolean checkSpace(int progress, int offset, int except) {
		for(int i = except + 1; i < items.size(); i++) {
			ItemWrapper item = items.get(i);
//			System.out.println(Math.abs(item.progress - progress)/100f);
			int distP = Math.abs(item.progress - progress);
			int distO = Math.abs(item.offset - offset);
			if(distP * distP + distO * distO < 40*40) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void updateEntity() {

		/*
		 * Find items laying on the conveyor.
		 */

		ConveyorUtil.tryInsertItemsFromWorld(this, worldObj, null, true);
		
		Collections.sort(items);
		
		/*
		 * Move items already on the conveyor
		 */
		
		boolean changed = false;
		for(int idx = items.size() - 1; idx >= 0; idx--) {
		
			ItemWrapper wrapper = items.get(idx);
			
			if(wrapper == null || wrapper.itemStack == null) {
				items.remove(idx);
				//TODO: Fix that. Please.
//				System.out.println("Removing NULL Wrapper/ItemStack from Conveyor. " + wrapper);
				continue;
			}
			
			if(checkSpace(wrapper.progress+1, wrapper.offset, idx)) {
				wrapper.progress += 1;
			}
			if(appliance != null) {
				int min = appliance.getProgressBegin();
				int max = appliance.getProgressEnd();
				if(wrapper.processing > -1) {
					if(wrapper.progress >= min && wrapper.progress < max) {
						System.out.println("Process");
						wrapper.processing++;
						appliance.processItem(this, wrapper);
					}
				}
			}
			if(wrapper.progress > maxProgress) {
				wrapper.progress = maxProgress;//Just to keep the item where it is when the next conveyor is blocked.

				ForgeDirection dirRotated = direction.getRotation(ForgeDirection.UP);
				
				//TODO: Extract position calculation
				
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
				// Absolute Position of the Item
				float absX = xCoord + direction.offsetX * progress + dirRotated.offsetX * offset;
				float absY = yCoord + 0.4f;
				float absZ = zCoord + direction.offsetZ * progress + dirRotated.offsetZ * offset;
				
				// Next block, potentially a conveyor-aware block.
				int nextBlockX = xCoord + direction.offsetX;
				int nextBlockY = yCoord + direction.offsetY;
				int nextBlockZ = zCoord + direction.offsetZ;
				
				TileEntity te = worldObj.getTileEntity(nextBlockX, nextBlockY, nextBlockZ);
				
				//TODO: Items Stacking up does not work correctly (works only inside one conveyor)
				
				// Next conveyor aware block
				if(te instanceof IConveyorAwareTE) {
					IConveyorAwareTE conveyor = (IConveyorAwareTE) worldObj.getTileEntity(nextBlockX, nextBlockY, nextBlockZ);
					
					boolean resetProcessing = true;
					//TODO: conveyor instance host
					if(appliance != null && conveyor instanceof TileEntityConveyor) {
						IConveyorAppliance applianceOther = ((TileEntityConveyor)conveyor).appliance;
						if(applianceOther != null &&
								appliance.isApplianceSetupCompatible((TileEntityConveyor)conveyor, applianceOther)
							) {
							resetProcessing = false;
						}
					}
					if(resetProcessing) {
						//TODO: handle processing tracking different
					}
					// If the item was added (fully, no backlog), remove it from this entity
					if(ConveyorUtil.tryInsertItems(conveyor, wrapper, absX, absY, absZ)) {
						if(wrapper.getStackSize() == 0) {
							items.remove(idx);
						}
					}
					changed = true;
				// Drop it
				} else if(!worldObj.isRemote) {
					if(wrapper.itemStack != null) {
						EntityItem item = new EntityItem(worldObj, absX, absY, absZ, wrapper.itemStack);
						item.setVelocity(direction.offsetX * 0.05, direction.offsetY * 0.05, direction.offsetZ * 0.05);
						worldObj.spawnEntityInWorld(item);
					}
					items.remove(idx);
					changed = true;
				}
			}
		}
		
		// Content changed, send Network update.
		if(changed && !worldObj.isRemote) {
			//TODO: Do not update the items all the time. (Update differently)
			updateState();
		}
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setInteger("direction", direction.ordinal());
		if(applianceType != null) {
			tag.setString("applianceType", applianceType);
		}
		if(appliance != null) {
			NBTTagCompound applianceData = new NBTTagCompound();
			appliance.writeToNBT(applianceData);
			tag.setTag("appliance", applianceData);
		}
		if(!items.isEmpty()) {
			NBTTagList itemsTag = new NBTTagList();
			for(int i = 0; i < items.size(); i++) {
				itemsTag.appendTag(items.get(i).writeToNBT());
			}
			tag.setTag("items", itemsTag);
		}
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		direction = ForgeDirection.getOrientation(tag.getInteger("direction"));
		NBTTagList itemsTag = tag.getTagList("items", NBT.TAG_COMPOUND);
		if(itemsTag != null) {
			int count = itemsTag.func_150303_d();
			items.clear();
			items.ensureCapacity(count);
			for(int i = 0; i < count; i++) {
				items.add(ItemWrapper.readFromNBT(itemsTag.getCompoundTagAt(i)));
			}
			items.trimToSize();
		}
		String newApplianceType = tag.getString("applianceType");
		if(newApplianceType == null) {
			appliance = null;
			applianceType = null;
		} else if(!newApplianceType.equals(applianceType)) {
			initAppliance(newApplianceType);
		}
		if(appliance != null) {
			NBTTagCompound applianceData = tag.getCompoundTag("appliance");
			if(applianceData != null) {
				appliance.readFromNBT(applianceData);
			}
		}
	}
	
	public void initAppliance(String name) {
		IConveyorApplianceFactory factory = ApplianceRegistry.getFactory(name);
		if(factory == null) {
			applianceType = null;
		} else {
			appliance = factory.setUpApplianceInventory(name, this);
			applianceType = name;
		}
	}

	@Override
	public int getSizeInventory() {
		if(appliance == null) {
			return 1;
		} else {
			return appliance.getSizeInventory();
		}
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if(appliance == null) {
			return null;
		} else {
			return appliance.getStackInSlot(slot);
		}
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if(appliance == null) {
			return null;
		} else {
			return appliance.decrStackSize(slot, amount);
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if(appliance == null) {
			return null;
		} else {
			return appliance.getStackInSlotOnClosing(slot);
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		if(appliance == null) {
			//TODO: Check if the area is free, else drop it (?) (since we cannot abort...)
			items.add(new ItemWrapper(itemStack, 50, 50));
			updateState();
		} else {
			appliance.setInventorySlotContents(slot, itemStack);
		}
	}

	@Override
	public String getInventoryName() {
		if(appliance == null) {
			return "Conveyor Belt";
		} else {
			return appliance.getInventoryName();
		}
	}

	@Override
	public boolean hasCustomInventoryName() {
		if(appliance == null) {
			return true;
		} else {
			return appliance.hasCustomInventoryName();
		}
	}

	@Override
	public int getInventoryStackLimit() {
		if(appliance == null) {
			return 64;
		} else {
			return appliance.getInventoryStackLimit();
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if(appliance == null) {
			//TODO: check if that area is free
			return true;
		} else {
			return appliance.isUseableByPlayer(player);
		}
	}

	@Override
	public void openInventory() {
		if(appliance != null) {
			appliance.openInventory();
		}
	}

	@Override
	public void closeInventory() {
		if(appliance != null) {
			appliance.closeInventory();
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		if(appliance == null) {
			//TODO: check if that area is free
			return true;
		} else {
			return appliance.isItemValidForSlot(slot, itemStack);
		}
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if(appliance == null) {
			return 0;
		} else {
			return appliance.fill(from, resource, doFill);
		}
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
		if(appliance == null) {
			return null;
		} else {
			return appliance.drain(from, resource, doDrain);
		}
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if(appliance == null) {
			return null;
		} else {
			return appliance.drain(from, maxDrain, doDrain);
		}
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		if(appliance == null) {
			return false;
		} else {
			return appliance.canFill(from, fluid);
		}
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		if(appliance == null) {
			return false;
		} else {
			return appliance.canDrain(from, fluid);
		}
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		if(appliance == null) {
			return new FluidTankInfo[0];
		} else {
			return appliance.getTankInfo(from);
		}
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if(appliance == null) {
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			if(dir == ForgeDirection.UP) {
				return new int[] { 0 };
			} else {
				return new int[0];
			}
		} else {
			return appliance.getAccessibleSlotsFromSide(side);
		}
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
		if(appliance == null) {
			return checkSpace(50, 50, -1);
		} else {
			return appliance.canInsertItem(slot, itemStack, side);
		}
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemStack, int side) {
		if(appliance == null) {
			return false;
		} else {
			return appliance.canExtractItem(slot, itemStack, side);
		}
	}

}
