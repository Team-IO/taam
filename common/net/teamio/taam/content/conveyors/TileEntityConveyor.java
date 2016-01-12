package net.teamio.taam.content.conveyors;

import java.util.ArrayList;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.OBJModel.OBJState;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.teamio.taam.Config;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.conveyors.ApplianceRegistry;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.IConveyorAppliance;
import net.teamio.taam.conveyors.IConveyorApplianceFactory;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;

public class TileEntityConveyor extends BaseTileEntity implements ISidedInventory, IFluidHandler, IConveyorAwareTE, IRotatable, IConveyorApplianceHost, IWorldInteractable {

	/*
	 * Content
	 */
	private ItemWrapper[] items;
	
	/*
	 * Conveyor State
	 */
	private EnumFacing direction = EnumFacing.NORTH;
	private int speedLevel = 0;
	
	public boolean isEnd = false;
	public boolean isBegin = false;
	public boolean renderEnd = false;
	public boolean renderBegin = false;
	public boolean renderRight = false;
	public boolean renderLeft = false;
	public boolean renderAbove = false;
	
	public final OBJState modelState = new OBJState(new ArrayList<String>(), true);//TODO: Default to list of all visible parts
	
	/*
	 * Appliance state
	 */
	private String applianceType;
	private IConveyorAppliance appliance;


	public TileEntityConveyor() {
		items = new ItemWrapper[9];
		for(int i = 0; i < items.length; i++) {
			items[i] = new ItemWrapper(null);
		}
	}
	
	public TileEntityConveyor(int speedLevel) {
		this();
		this.speedLevel = speedLevel;
	}
	
	@Override
	public byte getSpeedsteps() {
		return Config.pl_conveyor_speedsteps[speedLevel];
	}

	public int getSpeedLevel() {
		return speedLevel;
	}
	
	@Override
	public void updateRenderingInfo() {
		if(worldObj != null) {
			// Check in front
			TileEntity te = worldObj.getTileEntity(pos.offset(direction));
			if(te instanceof TileEntityConveyor) {
				TileEntityConveyor next = (TileEntityConveyor)te;
				renderEnd = next.speedLevel != speedLevel;
				renderEnd = renderEnd || next.getFacingDirection() != direction;
				isEnd = renderEnd;
			} else {
				isEnd = true;
				renderEnd = te instanceof IConveyorAwareTE;
			}
			
			// Check behind
			EnumFacing inverse = direction.getOpposite();
			te = worldObj.getTileEntity(pos.offset(inverse));
			if(te instanceof TileEntityConveyor) {
				TileEntityConveyor next = (TileEntityConveyor)te;
				renderBegin = next.speedLevel != speedLevel;
				renderBegin = renderBegin || next.getFacingDirection() != direction;
				isBegin = renderBegin;
			} else {
				isBegin = true;
				renderBegin = te instanceof IConveyorAwareTE;
			}
			
			// Check right
			inverse = direction.rotateAround(Axis.Y);
			te = worldObj.getTileEntity(pos.offset(inverse));
			
			if(te instanceof TileEntityConveyor) {
				TileEntityConveyor next = (TileEntityConveyor)te;
				EnumFacing nextFacing = next.getFacingDirection();
				renderRight = nextFacing != direction && nextFacing != direction.getOpposite();
			} else {
				renderRight = te instanceof IConveyorAwareTE;
			}
			
			// Check left
			inverse = direction.getOpposite().rotateAround(Axis.Y);
			te = worldObj.getTileEntity(pos.offset(inverse));
			
			if(te instanceof TileEntityConveyor) {
				TileEntityConveyor next = (TileEntityConveyor)te;
				EnumFacing nextFacing = next.getFacingDirection();
				renderLeft = nextFacing != direction && nextFacing != direction.getOpposite();
			} else {
				renderLeft = te instanceof IConveyorAwareTE;
			}
			
			// Check above
			renderAbove = worldObj.isSideSolid(pos.offset(EnumFacing.UP), EnumFacing.DOWN) ||
					worldObj.getTileEntity(pos.offset(EnumFacing.DOWN)) instanceof IConveyorAwareTE;
		}
		
		Map<String, Boolean> visibility = modelState.getVisibilityMap();
		visibility.put("conveyorEnd", !isEnd);
		visibility.put("conveyorEndCurved", isEnd);
		
		visibility.put("conveyorBegin", !isBegin);
		visibility.put("conveyorBeginCurved", isBegin);
		
		visibility.put("conveyorEndCap", isEnd);
		visibility.put("conveyorBeginCap", isEnd);
		
		visibility.put("conveyorLeftCap", renderLeft);
		visibility.put("conveyorRightCap", renderRight);
		visibility.put("conveyorSupportAbove", renderAbove);
	}

	/**
	 * Get all items. Danger! Returns the array directly!
	 * @return
	 */
	public ItemWrapper[] getItems() {
		return items;
	}

	/**
	 * Drops all contained items, exactly where they are rendered now.
	 */
	public void dropItems() {
		for (int index = 0; index < items.length; index++) {
			ConveyorUtil.dropItem(worldObj, this, index, false);
		}
	}
	
	@Override
	public void update() {

		/*
		 * Find items laying on the conveyor.
		 */
		boolean needsUpdate = false;
		
		if(ConveyorUtil.tryInsertItemsFromWorld(this, worldObj, null, false)) {
			needsUpdate = true;
		}

		/*
		 * Move items already on the conveyor
		 */
		
		// process from movement direction backward to keep slot order inside one conveyor,
		// as we depend on the status of the next slot
		int[] slotOrder = ConveyorUtil.getSlotOrderForDirection(direction);
		if(ConveyorUtil.defaultTransition(worldObj, this, slotOrder)) {
			needsUpdate = true;
		}
		if(needsUpdate) {
			updateState();
		}
	}

	@Override
	public EnumFacing getNextSlot(int slot) {
		if(speedLevel >= 2) {
			return ConveyorUtil.getHighspeedTransition(slot, direction);
		} else {
			return direction;
		}
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setInteger("direction", direction.ordinal());
		tag.setInteger("speedLevel", speedLevel);
		if(applianceType != null) {
			tag.setString("applianceType", applianceType);
		}
		if(appliance != null) {
			NBTTagCompound applianceData = new NBTTagCompound();
			appliance.writeToNBT(applianceData);
			tag.setTag("appliance", applianceData);
		}
		NBTTagList itemsTag = new NBTTagList();
		for(int i = 0; i < items.length; i++) {
			itemsTag.appendTag(items[i].writeToNBT());
		}
		tag.setTag("items", itemsTag);
		
		int flags = 0;
		if(isEnd) {
			flags += 1;
		}
		if(isBegin) {
			flags += 2;
		}
		if(renderEnd) {
			flags += 4;
		}
		if(renderBegin) {
			flags += 8;
		}
		if(renderRight) {
			flags += 16;
		}
		if(renderLeft) {
			flags += 32;
		}
		if(renderAbove) {
			flags += 64;
		}
		tag.setInteger("flags", flags);
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		direction = EnumFacing.getFront(tag.getInteger("direction"));
		if(direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
			direction = EnumFacing.NORTH;
		}
		speedLevel = tag.getInteger("speedLevel");
		NBTTagList itemsTag = tag.getTagList("items", NBT.TAG_COMPOUND);
		if(itemsTag != null) {
			int count = Math.min(itemsTag.tagCount(), items.length);
			for(int i = 0; i < count; i++) {
				items[i] = ItemWrapper.readFromNBT(itemsTag.getCompoundTagAt(i));
			}
		}
		String newApplianceType = tag.getString("applianceType");
		setAppliance(newApplianceType);
		if(appliance != null) {
			NBTTagCompound applianceData = tag.getCompoundTag("appliance");
			if(applianceData != null) {
				appliance.readFromNBT(applianceData);
			}
		}
		int flags = tag.getInteger("flags");
		isEnd = (flags & 1) != 0;
		isBegin = (flags & 2) != 0;
		renderEnd = (flags & 4) != 0;
		renderBegin = (flags & 8) != 0;
		renderRight = (flags & 16) != 0;
		renderLeft = (flags & 32) != 0;
		renderAbove = (flags & 64) != 0;
	}
	
	/*
	 * IConveyorAwareTE implementation
	 */

	@Override
	public boolean shouldRenderItemsDefault() {
		return true;
	}
	
	@Override
	public int insertItemAt(ItemStack item, int slot) {
		int count = ConveyorUtil.insertItemAt(this, item, slot, false);
		if(count > 0) {
			updateState();
		}
		return count;
	}
	
	@Override
	public boolean canSlotMove(int slot) {
		ItemWrapper slotObject = items[slot];
		return !slotObject.isBlocked();
	};
	
	@Override
	public boolean isSlotAvailable(int slot) {
		return true;
	}
	
	@Override
	public ItemWrapper getSlot(int slot) {
		return items[slot];
	};

	@Override
	public EnumFacing getMovementDirection() {
		return direction;
	}

	@Override
	public int getMovementProgress(int slot) {
		ItemWrapper slotObject = items[slot];
		return slotObject.movementProgress;
	}

	@Override
	public double getInsertMaxY() {
		return 0.9;
	}

	@Override
	public double getInsertMinY() {
		return 0.3;
	}
	
	/*
	 * IRotatable implementation
	 */
	
	@Override
	public EnumFacing getNextFacingDirection() {
		return direction.rotateAround(Axis.Y);
	}

	@Override
	public void setFacingDirection(EnumFacing direction) {
		this.direction = direction;
		if(direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
			this.direction = EnumFacing.NORTH;
		}
		updateRenderingInfo();
		updateState();
		worldObj.notifyNeighborsOfStateChange(pos, blockType);
		if(blockType != null) {
			//TODO: Update this block -> drop if it can't stay anymore
			//blockType.onNeighborBlockChange(worldObj, pos, blockType);
		}
	}

	@Override
	public EnumFacing getFacingDirection() {
		return direction;
	}

	/*
	 * IConveyorApplianceHost implementation
	 */
	
	@Override
	public boolean canAcceptAppliance(String type) {
		// Only "regular" conveyors can accept appliances
		return speedLevel == 1;
	}
	
	@Override
	public boolean initAppliance(String name) {
		if(hasAppliance()) {
			return false;
		}
		boolean result = setAppliance(name);
		updateState();
		updateContainingBlockInfo();
		return result;
	}
	
	private boolean setAppliance(String type) {
		if(type == null) {
			appliance = null;
			applianceType = null;
			return true;
		}
		if(!canAcceptAppliance(type)) {
			return false;
		}
		if(type.equals(applianceType)) {
			return false;
		}
		IConveyorApplianceFactory factory = ApplianceRegistry.getFactory(type);
		if(factory == null) {
			appliance = null;
			applianceType = null;
		} else {
			appliance = factory.setUpApplianceInventory(type, this);
			applianceType = type;
		}
		return true;
	}
	
	@Override
	public boolean hasAppliance() {
		return appliance != null;
	}

	@Override
	public boolean hasApplianceWithType(String type) {
		return hasAppliance() && applianceType.equals(type);
	}

	@Override
	public String getApplianceType() {
		return applianceType;
	}

	@Override
	public IConveyorAppliance getAppliance() {
		return appliance;
	}

	@Override
	public boolean removeAppliance() {
		boolean hadAppliance = hasAppliance();
		appliance = null;
		applianceType = null;
		updateState();
		return hadAppliance;
	}

	/*
	 * IInventory implementation
	 */
	
	@Override
	public int getSizeInventory() {
		if(appliance == null) {
			return 9;
		} else {
			return appliance.getSizeInventory();
		}
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if(appliance == null) {
			return getSlot(slot).itemStack;
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
			if(itemStack == null) {
				items[slot].itemStack = null;
				updateState();
			} else {
				insertItemAt(itemStack, slot);
			}
		} else {
			appliance.setInventorySlotContents(slot, itemStack);
		}
	}

	@Override
	public IChatComponent getDisplayName() {
		if(appliance == null) {
			return new ChatComponentTranslation("tile.taam.productionline.conveyor.name");
		} else {
			return appliance.getDisplayName();
		}
	}

	@Override
	public String getCommandSenderName() {
		return "tile.taam.productionline.conveyor.name";
	}

	@Override
	public boolean hasCustomName() {
		if(appliance == null) {
			return false;
		} else {
			return appliance.hasCustomName();
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
			return true;
		} else {
			return appliance.isUseableByPlayer(player);
		}
	}

	@Override
	public void openInventory(EntityPlayer player) {
		if(appliance != null) {
			appliance.openInventory(player);
		}
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		if(appliance != null) {
			appliance.closeInventory(player);
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		if(appliance == null) {
			return true;
		} else {
			return appliance.isItemValidForSlot(slot, itemStack);
		}
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
		if(appliance == null) {
		} else {
			appliance.clear();
		}
	}
	
	/*
	 * ISidedInventory implementation
	 */
	
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if(appliance == null) {
			final int slot = ConveyorUtil.getSlot(side);
			if(slot == -1) {
				return new int[0];
			} else {
				return new int[] { slot };
			}
		} else {
			return appliance.getSlotsForFace(side);
		}
	}
	
	@Override
	public boolean canInsertItem(int slot, ItemStack itemStack, EnumFacing side) {
		if(appliance == null) {
			return ConveyorUtil.insertItemAt(this, itemStack, slot, true) > 0;
		} else {
			return appliance.canInsertItem(slot, itemStack, side);
		}
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemStack, EnumFacing side) {
		if(appliance == null) {
			return false;
		} else {
			return appliance.canExtractItem(slot, itemStack, side);
		}
	}

	/*
	 * IFluidHandler implementation
	 */
	
	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		if(appliance == null) {
			return 0;
		} else {
			return appliance.fill(from, resource, doFill);
		}
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource,
			boolean doDrain) {
		if(appliance == null) {
			return null;
		} else {
			return appliance.drain(from, resource, doDrain);
		}
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		if(appliance == null) {
			return null;
		} else {
			return appliance.drain(from, maxDrain, doDrain);
		}
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		if(appliance == null) {
			return false;
		} else {
			return appliance.canFill(from, fluid);
		}
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		if(appliance == null) {
			return false;
		} else {
			return appliance.canDrain(from, fluid);
		}
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		if(appliance == null) {
			return new FluidTankInfo[0];
		} else {
			return appliance.getTankInfo(from);
		}
	}

	/*
	 * IWorldInteractable implementation
	 */
	@Override
	public boolean onBlockActivated(World world, EntityPlayer player, boolean hasWrench, EnumFacing side, float hitX,
			float hitY, float hitZ) {
		if(side != EnumFacing.UP) {
			return false;
		}
		ConveyorUtil.defaultPlayerInteraction(player, this, hitX, hitZ);
		return true;
	}
	
	@Override
	public boolean onBlockHit(World world, EntityPlayer player, boolean hasWrench) {
		return false;
	}
}
