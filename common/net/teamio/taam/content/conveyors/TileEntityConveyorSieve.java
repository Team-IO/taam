package net.teamio.taam.content.conveyors;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.teamio.taam.Config;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRedstoneControlled;
import net.teamio.taam.content.IRenderable;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;
import net.teamio.taam.network.TPMachineConfiguration;
import net.teamio.taam.util.TaamUtil;
import net.teamio.taam.util.WorldCoord;
import net.teamio.taam.util.inv.InventoryRange;
import net.teamio.taam.util.inv.InventoryUtils;

public class TileEntityConveyorSieve extends BaseTileEntity implements ISidedInventory, IConveyorAwareTE, IRotatable, IWorldInteractable, IRedstoneControlled, ITickable, IRenderable {

	public static List<String> parts = Collections.unmodifiableList(Lists.newArrayList("Support_Alu_smdl_alu", "SieveChute_cscmdl", "Sieve_csvmdl"));
	/*
	 * Content
	 */
	private ItemWrapper[] items;
	
	/*
	 * Conveyor State
	 */
	private byte redstoneMode = IRedstoneControlled.MODE_ACTIVE_ON_LOW;
	private EnumFacing direction = EnumFacing.NORTH;
	
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
	
	@Override
	public List<String> getVisibleParts() {
		return parts;
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
		
		boolean redstoneHigh = worldObj.isBlockIndirectlyGettingPowered(pos) > 0;
		
		boolean newShutdown = TaamUtil.isShutdown(worldObj.rand, redstoneMode, redstoneHigh);
		
		
		if(isShutdown != newShutdown) {
			isShutdown = newShutdown;
			needsUpdate = true;
		}

		if(!isShutdown) {
			
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
		
			if(ConveyorUtil.defaultTransition(worldObj, this, slotOrder)) {
				needsUpdate = true;
			}
		}
		
		if(needsUpdate) {
			updateState(false, false, false);
		}
	}
	
	public boolean processSieve(int[] slotOrder) {
		
		BlockPos down = pos.down();
		
		// If we are blocked below, act as a conveyor.
		IInventory outputInventory = InventoryUtils.getInventory(worldObj, down);
		if(outputInventory == null && !TaamUtil.canDropIntoWorld(worldObj, down)) {
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
				EntityItem item = new EntityItem(worldObj, pos.getX() + 0.5, pos.getY() - 0.3, pos.getZ() + 0.5, wrapper.itemStack);
		        item.motionX = 0;
		        item.motionY = 0;
		        item.motionZ = 0;
		        worldObj.spawnEntityInWorld(item);
		        wrapper.itemStack = null;
			}
	        return true;
		} else {
			// Output to inventory
			InventoryRange range = new InventoryRange(outputInventory, EnumFacing.UP.ordinal());
			
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
	public EnumFacing getNextSlot(int slot) {
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
		direction = EnumFacing.getFront(tag.getInteger("direction"));
		if(direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
			direction = EnumFacing.NORTH;
		}
		NBTTagList itemsTag = tag.getTagList("items", NBT.TAG_COMPOUND);
		if(itemsTag != null) {
			int count = Math.min(itemsTag.tagCount(), items.length);
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
			updateState(true, false, false);
		}
		return count;
	}
	
	@Override
	public ItemStack removeItemAt(int slot) {
		ItemWrapper candidate = items[slot];
		ItemStack removed = candidate.itemStack;
		if(removed != null) {
			candidate.itemStack = null;
			updateState(true, false, false);
		}
		return removed;
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
		return direction.rotateY();
	}

	@Override
	public void setFacingDirection(EnumFacing direction) {
		this.direction = direction;
		if(direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
			this.direction = EnumFacing.NORTH;
		}
		updateState(false, true, true);
		worldObj.notifyBlockOfStateChange(pos, blockType);
		if(blockType != null) {
			blockType.onNeighborBlockChange(worldObj, pos, worldObj.getBlockState(pos), blockType);
		}
	}

	@Override
	public EnumFacing getFacingDirection() {
		return direction;
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
	public ItemStack removeStackFromSlot(int slot) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		if(itemStack == null) {
			items[slot].itemStack = null;
			updateState(true, false, false);
		} else {
			insertItemAt(itemStack, slot);
		}
	}

	@Override
	public String getName() {
		return "tile.productionline.sieve.name";
	}
	
	@Override
	public IChatComponent getDisplayName() {
		return new ChatComponentTranslation(getName());
	}
	
	@Override
	public boolean hasCustomName() {
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
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
	}
	
	/*
	 * ISidedInventory implementation
	 */
	
	public int[] getSlotsForFace(EnumFacing side) {
		final int slot = ConveyorUtil.getSlot(side);
		if(slot == -1) {
			return new int[0];
		} else {
			return new int[] { slot };
		}
	};
	
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return ConveyorUtil.insertItemAt(this, itemStackIn, index, true) > 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return false;
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
