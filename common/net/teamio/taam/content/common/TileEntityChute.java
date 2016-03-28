package net.teamio.taam.content.common;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Lists;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRenderable;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;
import net.teamio.taam.util.TaamUtil;
import net.teamio.taam.util.inv.InventoryRange;
import net.teamio.taam.util.inv.InventoryUtils;

public class TileEntityChute extends BaseTileEntity implements IInventory, ISidedInventory, IFluidHandler, IConveyorAwareTE, IRotatable, ITickable, IRenderable {

	public boolean isConveyorVersion = false;
	private EnumFacing direction = EnumFacing.NORTH;
	
	public static List<String> parts_conveyor_version = Collections.unmodifiableList(Lists.newArrayList("Support_Alu_smdl_alu", "Chute_cchmdl"));
	
	public TileEntityChute(boolean isConveyorVersion) {
		this.isConveyorVersion = isConveyorVersion;
	}
	
	public TileEntityChute() {
		this(false);
	}
	@Override
	public void validate() {
		// TODO Auto-generated method stub
		super.validate();
	}
	
	@Override
	public void update() {
		// Skip item insertion if there is a solid block / other chute above us
		if(isConveyorVersion || !worldObj.isSideSolid(pos.up(), EnumFacing.DOWN, false)) {
			ConveyorUtil.tryInsertItemsFromWorld(this, worldObj, null, false);
		}
	}
	
	@Override
	public List<String> getVisibleParts() {
		if(isConveyorVersion) {
			return parts_conveyor_version;
		} else {
			return null;
		}
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setBoolean("isConveyorVersion", isConveyorVersion);
		if(isConveyorVersion) {
			tag.setInteger("direction", direction.ordinal());
		}
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		isConveyorVersion = tag.getBoolean("isConveyorVersion");
		if(isConveyorVersion) {
			direction = EnumFacing.getFront(tag.getInteger("direction"));
			if(!ArrayUtils.contains(EnumFacing.HORIZONTALS, direction)) {
				direction = EnumFacing.NORTH;
			}
		}
	}
	
	private TileEntity getTarget() {
		return worldObj.getTileEntity(pos.down());
	}
	
	private InventoryRange getTargetRange() {
		IInventory inventory = getTargetInventory();
		if(inventory == null) {
			return null;
		} else {
			return new InventoryRange(inventory, EnumFacing.UP.ordinal());
		}
	}
	
	private IInventory getTargetInventory() {
		return InventoryUtils.getInventory(worldObj, pos.down());
	}
	
	private IFluidHandler getTargetFluidHandler() {
		TileEntity target = getTarget();
		if(target instanceof IFluidHandler) {
			return (IFluidHandler) target;
		} else {
			return null;
		}
	}
	
	private boolean canDrop() {
		return TaamUtil.canDropIntoWorld(worldObj, pos.down());
	}

	/*
	 * IInventory implementation
	 */
	
	@Override
	public int getSizeInventory() {
		InventoryRange target = getTargetRange();
		if(target == null) {
			if(canDrop()) {
				return 1;
			} else {
				return 0;
			}
		} else {
			return target.slots.length;
		}
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		/*InventoryRange target = getTargetRange();
		if(target == null) {
			return null;
		} else {
			return InventoryUtils.getExtractableStack(target, target.slots[slot]);
		}*/
		return null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		InventoryRange target = getTargetRange();
		if(target == null) {
			if(!worldObj.isRemote && canDrop()) {
				EntityItem item = new EntityItem(worldObj, pos.getX() + 0.5, pos.getY() - 0.3, pos.getZ() + 0.5, stack);
		        item.motionX = 0;
		        item.motionY = 0;
		        item.motionZ = 0;
		        worldObj.spawnEntityInWorld(item);
			}
		} else {
			target.inv.setInventorySlotContents(target.slots[slot], stack);
		}
	}

	@Override
	public IChatComponent getDisplayName() {
		IInventory target = getTargetInventory();
		if(target == null) {
			return new ChatComponentTranslation("tile.taam.chute.name");
		} else {
			return target.getDisplayName();
		}
	}

	@Override
	public boolean hasCustomName() {
		IInventory target = getTargetInventory();
		if(target == null) {
			return false;
		} else {
			return target.hasCustomName();
		}
	}

	@Override
	public String getName() {
		return "tile.taam.chute.name";
	}

	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// Nope
	}

	@Override
	public int getInventoryStackLimit() {
		IInventory target = getTargetInventory();
		if(target == null) {
			return 64;
		} else {
			return target.getInventoryStackLimit();
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		IInventory target = getTargetInventory();
		if(target == null) {
			return false;
		} else {
			return target.isUseableByPlayer(player);
		}
	}
	
	@Override
	public void openInventory(EntityPlayer player) {
		IInventory target = getTargetInventory();
		if(target != null) {
			target.openInventory(player);
		}
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		IInventory target = getTargetInventory();
		if(target != null) {
			target.closeInventory(player);
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		InventoryRange target = getTargetRange();
		if(target == null) {
			return canDrop();
		} else {
			return target.canInsertItem(target.slots[slot], stack);
		}
	}

	/*
	 * ISidedInventory implementation
	 */
	
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if(side != EnumFacing.UP) {
			return new int[0];
		}
		InventoryRange target = getTargetRange();
		if(target == null) {
			if(canDrop()) {
				return new int[] {0};
			} else {
				return new int[0];
			}
		} else {
			// We reorder the slots from 0 onwards, then convert back later.
			int[] slots = new int[target.slots.length];
			for(int i = 0; i < slots.length; i++) {
				slots[i] = i;
			}
			return slots;
		}
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack,
			EnumFacing direction) {
		if(direction != EnumFacing.UP) {
			return false;
		}
		InventoryRange target = getTargetRange();
		if(target == null) {
			return canDrop();
		} else {
			return target.canInsertItem(target.slots[slot], stack);
		}
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack,
			EnumFacing direction) {
		return false;
	}

	/*
	 * IFluidHandler implementation
	 */
	
	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		if(from != EnumFacing.UP) {
			return 0;
		}
		IFluidHandler target = getTargetFluidHandler();
		if(target != null ) {
			return target.fill(from, resource, doFill);
		} else {
			return 0;
		}
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		if(from != EnumFacing.UP) {
			return false;
		}
		IFluidHandler target = getTargetFluidHandler();
		if(target != null ) {
			return target.canFill(from, fluid);
		} else {
			return false;
		}
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		if(from != EnumFacing.UP) {
			return new FluidTankInfo[0];
		}
		IFluidHandler target = getTargetFluidHandler();
		if(target != null ) {
			return target.getTankInfo(from);
		} else {
			return new FluidTankInfo[0];
		}
	}



	/*
	 * IConveyorAwareTE implementation
	 */
	
	@Override
	public boolean canSlotMove(int slot) {
		return false;
	}

	@Override
	public boolean isSlotAvailable(int slot) {
		return true;
	}

	@Override
	public int getMovementProgress(int slot) {
		return 0;
	}

	@Override
	public byte getSpeedsteps() {
		return 0;
	}

	@Override
	public ItemWrapper getSlot(int slot) {
		return ItemWrapper.EMPTY;
	}

	@Override
	public int insertItemAt(ItemStack stack, int slot) {
		InventoryRange target = getTargetRange();
		if(target == null) {
			if(!worldObj.isRemote && canDrop()) {
				EntityItem item = new EntityItem(worldObj, pos.getX() + 0.5, pos.getY() - 0.3, pos.getZ() + 0.5, stack);
		        item.motionX = 0;
		        item.motionY = 0;
		        item.motionZ = 0;
		        worldObj.spawnEntityInWorld(item);
		        return stack.stackSize;
			}
			return 0;
		} else {
			return stack.stackSize - InventoryUtils.insertItem(target, stack, false);
		}
	}

	@Override
	public EnumFacing getMovementDirection() {
		return EnumFacing.DOWN;
	}

	public EnumFacing getNextSlot(int slot) {
		return null;
	}

	@Override
	public boolean shouldRenderItemsDefault() {
		return false;
	}

	@Override
	public double getInsertMaxY() {
		if(isConveyorVersion) {
			return 0.9;
		} else {
			return 1.3;
		}
	}

	@Override
	public double getInsertMinY() {
		if(isConveyorVersion) {
			return 0.3;
		} else {
			return 0.9;
		}
	}
	/*
	 * IRotatable Implementation
	 */
	
	@Override
	public EnumFacing getFacingDirection() {
		return direction;
	}

	@Override
	public EnumFacing getNextFacingDirection() {
		return direction.rotateY();
	}

	@Override
	public void setFacingDirection(EnumFacing direction) {
		if(isConveyorVersion) {
			this.direction = direction;
			if(!ArrayUtils.contains(EnumFacing.HORIZONTALS, direction)) {
				this.direction = EnumFacing.NORTH;
			}
			updateState();
			blockUpdate();
		}
	}

}
