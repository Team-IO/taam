package net.teamio.taam.content.conveyors;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;
import net.teamio.taam.Config;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRedstoneControlled;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;
import net.teamio.taam.network.TPMachineConfiguration;
import net.teamio.taam.util.TaamUtil;
import net.teamio.taam.util.WorldCoord;
import codechicken.lib.inventory.InventoryRange;
import codechicken.lib.inventory.InventoryUtils;

public class TileEntityConveyorSieve extends BaseTileEntity implements ISidedInventory, IConveyorAwareTE, IRotatable, IWorldInteractable, IRedstoneControlled {

	/*
	 * Content
	 */
	private ItemWrapper[] items;
	
	/*
	 * Conveyor State
	 */
	private byte redstoneMode = IRedstoneControlled.MODE_ACTIVE_ON_LOW;
	private ForgeDirection direction = ForgeDirection.NORTH;
	
	/**
	 * Just for rendering purposes we keep this here.
	 */
	public boolean isShutdown;
	
	public TileEntityConveyorSieve() {
		items = new ItemWrapper[9];
		for(int i = 0; i < items.length; i++) {
			items[i] = new ItemWrapper(null);
		}
	}
	
	@Override
	public byte getSpeedsteps() {
		return Config.pl_sieve_speedsteps;
	}

	@Override
	public void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();
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
	public void updateEntity() {

		/*
		 * Find items laying on the conveyor.
		 */

		boolean needsUpdate = false;
		
		if(ConveyorUtil.tryInsertItemsFromWorld(this, worldObj, null, false)) {
			needsUpdate = true;
		}
		
		boolean redstoneHigh = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		
		boolean newShutdown = TaamUtil.isShutdown(worldObj.rand, redstoneMode, redstoneHigh);
		
		
		if(isShutdown != newShutdown) {
			isShutdown = newShutdown;
			needsUpdate = true;
		}

		// process from movement direction backward to keep slot order inside one conveyor,
		// as we depend on the status of the next slot
		int[] slotOrder = ConveyorUtil.getSlotOrderForDirection(direction);

		/*
		 * Process sieving
		 */
		if(processSieve(slotOrder)) {
			needsUpdate = true;
		}
		
		/*
		 * Move items already on the conveyor
		 */
		
		if(!isShutdown) {
			if(ConveyorUtil.defaultTransition(worldObj, this, slotOrder)) {
				needsUpdate = true;
			}
		}
		
		if(needsUpdate) {
			updateState();
		}
	}
	
	public boolean processSieve(int[] slotOrder) {

		IInventory outputInventory = InventoryUtils.getInventory(worldObj, xCoord, yCoord - 1, zCoord);
		if(outputInventory == null && !TaamUtil.canDropIntoWorld(worldObj, xCoord, yCoord - 1, zCoord)) {
			return false;
		}
		
		for(int index = 0; index < slotOrder.length; index++) {
			
			int slot = slotOrder[index];
			
			ItemWrapper wrapper = getSlot(slot);
			
			if(wrapper.isEmpty()) {
				continue;
			}
			if(wrapper.itemStack.getItem() instanceof ItemBlock) {
				wrapper.unblock();
			} else {
				if(isShutdown || !tryOutput(wrapper, outputInventory)) {
					// No force block. if something is already moving, there is probably a reason...
					// Yes, sieves may miss an item by doing that.
					wrapper.block();
				}
			}
		}
		return true;
	}

	private boolean tryOutput(ItemWrapper wrapper, IInventory outputInventory) {
		if(outputInventory == null) {
			// Output to world
			if(!worldObj.isRemote && wrapper.itemStack != null) {
				EntityItem item = new EntityItem(worldObj, xCoord + 0.5, yCoord - 0.3, zCoord + 0.5, wrapper.itemStack);
		        item.motionX = 0;
		        item.motionY = 0;
		        item.motionZ = 0;
		        worldObj.spawnEntityInWorld(item);
		        wrapper.itemStack = null;
			}
	        return true;
		} else {
			// Output to inventory
			InventoryRange range = new InventoryRange(outputInventory, ForgeDirection.UP.ordinal());
			
			if(wrapper.itemStack == null) {
				return true;
			}
			int unable = InventoryUtils.insertItem(range, wrapper.itemStack, false);
			if(unable > 0) {
				wrapper.itemStack.stackSize = unable;
				return false;
			} else {
				wrapper.itemStack = null;
				return true;
			}
		}
	}
	
	@Override
	public ForgeDirection getNextSlot(int slot) {
		return direction;
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setInteger("direction", direction.ordinal());
		NBTTagList itemsTag = new NBTTagList();
		for(int i = 0; i < items.length; i++) {
			itemsTag.appendTag(items[i].writeToNBT());
		}
		tag.setTag("items", itemsTag);
//		tag.setByte("redstoneMode", redstoneMode);
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		direction = ForgeDirection.getOrientation(tag.getInteger("direction"));
		if(direction == ForgeDirection.UP || direction == ForgeDirection.DOWN || direction == ForgeDirection.UNKNOWN) {
			direction = ForgeDirection.NORTH;
		}
		NBTTagList itemsTag = tag.getTagList("items", NBT.TAG_COMPOUND);
		if(itemsTag != null) {
			int count = Math.min(itemsTag.func_150303_d(), items.length);
			for(int i = 0; i < count; i++) {
				items[i] = ItemWrapper.readFromNBT(itemsTag.getCompoundTagAt(i));
			}
		}
//		redstoneMode = tag.getByte("redstoneMode");
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
	public int posX() {
		return xCoord;
	}
	
	@Override
	public int posY() {
		return yCoord;
	}
	
	@Override
	public int posZ() {
		return zCoord;
	}

	@Override
	public ForgeDirection getMovementDirection() {
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
	public ForgeDirection getNextFacingDirection() {
		return direction.getRotation(ForgeDirection.UP);
	}

	@Override
	public ForgeDirection getNextMountDirection() {
		return ForgeDirection.DOWN;
	}

	@Override
	public void setFacingDirection(ForgeDirection direction) {
		this.direction = direction;
		if(direction == ForgeDirection.UP || direction == ForgeDirection.DOWN || direction == ForgeDirection.UNKNOWN) {
			this.direction = ForgeDirection.NORTH;
		}
		updateState();
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, blockType);
		if(blockType != null) {
			blockType.onNeighborBlockChange(worldObj, xCoord, yCoord, zCoord, blockType);
		}
	}

	@Override
	public void setMountDirection(ForgeDirection direction) {
		return;
	}

	@Override
	public ForgeDirection getFacingDirection() {
		return direction;
	}

	@Override
	public ForgeDirection getMountDirection() {
		return ForgeDirection.DOWN;
	}
	
	/*
	 * IInventory implementation
	 */
	
	@Override
	public int getSizeInventory() {
		return 9;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return getSlot(slot).itemStack;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		if(itemStack == null) {
			items[slot].itemStack = null;
			updateState();
		} else {
			insertItemAt(itemStack, slot);
		}
	}

	@Override
	public String getInventoryName() {
		return "tile.taam.productionline.sieve.name";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		return true;
	}
	
	/*
	 * ISidedInventory implementation
	 */
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		final ForgeDirection dir = ForgeDirection.getOrientation(side);
		final int slot = ConveyorUtil.getSlot(dir);
		if(slot == -1) {
			return new int[0];
		} else {
			return new int[] { slot };
		}
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
		return ConveyorUtil.insertItemAt(this, itemStack, slot, true) > 0;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemStack, int side) {
		return false;
	}

	/*
	 * IWorldInteractable implementation
	 */
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, boolean playerHasWrench, int side, float hitX, float hitY, float hitZ) {
		if(side != ForgeDirection.UP.ordinal()) {
			return false;
		}
		ConveyorUtil.defaultPlayerInteraction(player, this, hitX, hitZ);
		return true;
	}
	
	@Override
	public boolean onBlockHit(World world, int x, int y, int z,
			EntityPlayer player, boolean hasWrench) {
		return false;
	}
	

	/*
	 * IRedstoneControlled implementation
	 */

	@Override
	public boolean isPulsingSupported() {
		return false;
	}

	@Override
	public byte getRedstoneMode() {
		return redstoneMode;
	}

	@Override
	public void setRedstoneMode(byte mode) {
		this.redstoneMode = mode;
		if(worldObj.isRemote) {
			TPMachineConfiguration config = TPMachineConfiguration.newChangeInteger(new WorldCoord(this), (byte)1, redstoneMode);
			TaamMain.network.sendToServer(config);
		} else {
			this.markDirty();
		}
	}
}
