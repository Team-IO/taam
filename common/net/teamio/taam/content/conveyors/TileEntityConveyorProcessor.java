package net.teamio.taam.content.conveyors;

import java.util.List;

import codechicken.lib.inventory.InventoryRange;
import codechicken.lib.inventory.InventorySimple;
import codechicken.lib.inventory.InventoryUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.IHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.teamio.taam.Config;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRedstoneControlled;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;
import net.teamio.taam.conveyors.api.IProcessingRecipe;
import net.teamio.taam.conveyors.api.ProcessingRegistry;
import net.teamio.taam.network.TPMachineConfiguration;
import net.teamio.taam.util.TaamUtil;
import net.teamio.taam.util.WorldCoord;

public class TileEntityConveyorProcessor extends BaseTileEntity implements ISidedInventory, IConveyorAwareTE, IHopper, IRedstoneControlled, IWorldInteractable, IRotatable {

	public static final byte Shredder = 0;
	public static final byte Grinder = 1;
	public static final byte Crusher = 2;

	private InventorySimple inventory;
	private byte mode;

	private byte redstoneMode = IRedstoneControlled.MODE_ACTIVE_ON_LOW;
	private EnumFacing direction = EnumFacing.NORTH;
	
	private byte progress;
	private int timeout;
	
	/**
	 * Cached recipe, that will not change during processing of one stack
	 */
	private IProcessingRecipe recipe;
	
	/**
	 * Just for rendering purposes we keep this here.
	 */
	public boolean isShutdown;

	public TileEntityConveyorProcessor() {
		this(Shredder);
	}
	
	public TileEntityConveyorProcessor(byte mode) {
		inventory = new InventorySimple(1, getInventoryName());
		this.mode = mode;
	}
	
	public boolean isCoolingDown() {
		return timeout > 0;
	}
	
	private ItemStack[] holdback;
	
	@Override
	public void updateEntity() {
		if(worldObj.isRemote) {
			return;
		}

		if(!isCoolingDown()) {
			ItemStack stack = inventory.getStackInSlot(0);
			if(stack == null || stack.stackSize < stack.getMaxStackSize()) {
				ConveyorUtil.tryInsertItemsFromWorld(this, worldObj, null, false);
			}
		}
		
		boolean redstoneHigh = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		
		boolean newShutdown = TaamUtil.isShutdown(worldObj.rand, redstoneMode, redstoneHigh);
		
		boolean needsUpdate = false;
		
		if(isShutdown != newShutdown) {
			isShutdown = newShutdown;
			needsUpdate = true;
		}
		
		if(!isShutdown) {
			boolean decrease = false;
			
			if(mode == Shredder) {
				decrease = processShredder();
			} else {
				decrease = processOther();
			}
			
			if(decrease) {
				decrStackSize(0, 1);
				needsUpdate = false; // decrStackSize already updates
			}
			
			if(worldObj.rand.nextFloat() < Config.pl_processor_hurt_chance) {
				hurtEntities();
			}
		}
		
		if(needsUpdate) {
			updateState();
		}
		
	}

	private void hurtEntities() {
		@SuppressWarnings("unchecked")
		List<EntityLivingBase> entitites = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1));
		for(EntityLivingBase living : entitites) {
			hurtEntity(living);
		}
	}
	
	private void hurtEntity(EntityLivingBase living) {
		DamageSource ds = TaamMain.ds_processed;
		switch(mode) {
		case Shredder:
			ds = TaamMain.ds_shredded;
			break;
		case Grinder:
			ds = TaamMain.ds_ground;
			break;
		case Crusher:
			ds = TaamMain.ds_crushed;
			break;
		}
		living.attackEntityFrom(ds, 5);
	}
	
	private boolean processOther() {
		IInventory outputInventory = InventoryUtils.getInventory(worldObj, xCoord, yCoord - 1, zCoord);
		if(outputInventory == null && !TaamUtil.canDropIntoWorld(worldObj, xCoord, yCoord - 1, zCoord)) {
			return false;
		}

		ItemStack[] outputQueue = holdback;
		boolean decrease = false;
		
		// If output finished, continue processing.
		// else, the holdback queue will be processed below.
		
		if(outputQueue == null) {
			
			if(isCoolingDown()) {
				timeout--;
				return false;
			}
			
			ItemStack input = getStackInSlot(0);
			
			if(input == null) {
				recipe = null;
				return false;
			}
			
			if(recipe == null) {
				recipe = getRecipe(input);
			}
			
			if(recipe != null) {
				decrease = true;
				
				outputQueue = recipe.getOutput(input, worldObj.rand);
				
				if(mode == Grinder) {
					timeout += Config.pl_processor_grinder_timeout;
				} else {
					timeout += Config.pl_processor_crusher_timeout;
				}
			}
		}
		
		// No output, we can skip.
		
		if(outputQueue == null) {
			return false;
		}
		
		if(outputInventory == null) {
			// Output to world
			for(ItemStack itemStack : outputQueue) {
				if(itemStack == null) {
					continue;
				}
				EntityItem item = new EntityItem(worldObj, xCoord + 0.5, yCoord - 0.3, zCoord + 0.5, itemStack);
		        item.motionX = 0;
		        item.motionY = 0;
		        item.motionZ = 0;
		        worldObj.spawnEntityInWorld(item);
			}

			holdback = null;
		} else {
			// Output to inventory
			boolean hasOutputLeft = false;
			InventoryRange range = new InventoryRange(outputInventory, EnumFacing.UP.ordinal());
			
			for(int i = 0; i < outputQueue.length; i++) {
				ItemStack itemStack = outputQueue[i];
				if(itemStack == null) {
					continue;
				}
				int unable = InventoryUtils.insertItem(range, itemStack, false);
				if(unable > 0) {
					itemStack.stackSize = unable;
					hasOutputLeft = true;
				} else {
					outputQueue[i] = null;
				}
			}
			if(hasOutputLeft) {
				holdback = outputQueue;
			} else {
				holdback = null;
			}
		}
		
		return decrease;
	}
	
	private boolean processShredder() {

		if(isCoolingDown()) {
			timeout--;
			return false;
		}
		
		ItemStack input = getStackInSlot(0);
		
		if(input == null) {
			return false;
		}
		timeout += Config.pl_processor_shredder_timeout;
		
		return true;
	}

	public byte getMode() {
		return mode;
	}

	private IProcessingRecipe getRecipe(ItemStack input) {
		int machine;
		switch(mode) {
		case Crusher:
			machine = ProcessingRegistry.CRUSHER;
			break;
		case Grinder:
			machine = ProcessingRegistry.GRINDER;
			break;
		default:
			return null;
		}
		
		IProcessingRecipe recipe = ProcessingRegistry.getRecipe(machine, input);
		return recipe;
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setTag("items", InventoryUtils.writeItemStacksToTag(inventory.items));
		if(holdback != null) {
			tag.setTag("holdback", InventoryUtils.writeItemStacksToTag(holdback));
		}
		tag.setByte("mode", mode);
//		tag.setByte("redstoneMode", redstoneMode);
		tag.setByte("progress", progress);
		tag.setInteger("timeout", timeout);
		tag.setBoolean("isShutdown", isShutdown);
		tag.setInteger("direction", direction.ordinal());
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		inventory.items = new ItemStack[inventory.getSizeInventory()];
		InventoryUtils.readItemStacksFromTag(inventory.items, tag.getTagList("items", NBT.TAG_COMPOUND));
		
		NBTTagList holdbackList = tag.getTagList("holdback", NBT.TAG_COMPOUND);
		if(holdbackList == null) {
			holdback = null;
		} else {
			holdback = new ItemStack[holdbackList.func_150303_d()];
			InventoryUtils.readItemStacksFromTag(holdback, holdbackList);
		}

		mode = tag.getByte("mode");
//		redstoneMode = tag.getByte("redstoneMode");
		progress = tag.getByte("progress");
		timeout = tag.getInteger("timeout");
		isShutdown = tag.getBoolean("isShutdown");
		direction = EnumFacing.getOrientation(tag.getInteger("direction"));
		if(direction == EnumFacing.UP || direction == EnumFacing.DOWN || direction == EnumFacing.UNKNOWN) {
			direction = EnumFacing.NORTH;
		}
	}

	private void checkRecipe() {
		if(inventory.getStackInSlot(0) == null) {
			recipe = null;
		}
	}
	
	/*
	 * IInventory implementation
	 */
	
	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack retVal = inventory.decrStackSize(slot, amount);
		checkRecipe();
		updateState();
		return retVal;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
		recipe = null;
		updateState();
	}

	@Override
	public String getInventoryName() {
		switch(mode) {
		case Shredder:
			return "tile.taam.productionline.shredder.name";
		case Grinder:
			return "tile.taam.productionline.grinder.name";
		case Crusher:
			return "tile.taam.productionline.crusher.name";
		default:
			return "tile.taam.productionline.invalid.name";
		}
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory() {
		// Nothig to do
	}

	@Override
	public void closeInventory() {
		// Nothig to do
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		return true;
		// Removed this check for performance reasons... Clog will happen at processor, not at conveyor.
//		return mode == Shredder || getRecipe(itemStack) != null;
	}

	/*
	 * IConveyorAwareTE
	 */

	@Override
	public boolean shouldRenderItemsDefault() {
		return false;
	}
	
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
		return 1;
	}

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
	public int insertItemAt(ItemStack stack, int slot) {
		if(!isItemValidForSlot(0, stack)) {
			return 0;
		}
		// insertItem returns item count unable to insert.
		int inserted = stack.stackSize - InventoryUtils.insertItem(inventory, stack, false);
		updateState();
		return inserted;
	}

	@Override
	public ItemWrapper getSlot(int slot) {
		return ItemWrapper.EMPTY;
	}

	@Override
	public EnumFacing getMovementDirection() {
		return EnumFacing.DOWN;
	}

	@Override
	public double getInsertMaxY() {
		return 0.9;
	}

	@Override
	public double getInsertMinY() {
		return 0.3;
	}

	public EnumFacing getNextSlot(int slot) {
		return EnumFacing.UNKNOWN;
	}
	
	/*
	 * ISidedInventory implementation
	 */

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if(side == EnumFacing.UP.ordinal()) {
			return new int[] { 0 };
		} else {
			return new int[0];
		}
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		if(side == EnumFacing.UP.ordinal()) {
			if(isItemValidForSlot(0, stack)) {
				return true;
			}
			return false;
		} else {
			return false;
		}
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return side == EnumFacing.UP.ordinal();
	}
	
	/*
	 * IHopper implementation
	 */
	
	@Override
	public double getXPos() {
		return this.xCoord;
	}

	@Override
	public double getYPos() {
		return this.yCoord;
	}

	@Override
	public double getZPos() {
		return this.zCoord;
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

	/*
	 * IWorldInteractable implementation
	 */
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, boolean playerHasWrench, int side, float hitX, float hitY, float hitZ) {
		if(side != EnumFacing.UP.ordinal()) {
			return false;
		}
		if(!isShutdown) {
			hurtEntity(player);
			return true;
		}
		int clickedSlot = 0;
		int playerSlot = player.inventory.currentItem;
		ItemStack playerStack = player.inventory.getCurrentItem();
		if(playerStack == null) {
			// Take from Processor
			ItemStack taken = getStackInSlot(0);
			if(taken != null) {
				player.inventory.setInventorySlotContents(playerSlot, taken);
				setInventorySlotContents(clickedSlot, null);
			}
		} else {
			if(mode != Shredder) {
				// Put into processor
				int inserted = insertItemAt(playerStack, clickedSlot);
				if(inserted == playerStack.stackSize) {
					player.inventory.setInventorySlotContents(playerSlot, null);
				} else {
					playerStack.stackSize -= inserted;
					player.inventory.setInventorySlotContents(playerSlot, playerStack);
				}
			}
		}
		return true;
	}
	
	@Override
	public boolean onBlockHit(World world, int x, int y, int z,
			EntityPlayer player, boolean hasWrench) {
//		if(side != EnumFacing.UP.ordinal()) {
//			return false;
//		}
		if(hasWrench) {
			ItemStack taken = getStackInSlot(0);
			if(taken != null) {
				TaamUtil.tryDropToInventory(player, taken, .5, .5, .5);
				setInventorySlotContents(0, null);
			}
		} else if(!isShutdown) {
			hurtEntity(player);
		}
		return true;
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
		return direction.getRotation(EnumFacing.UP);
	}

	@Override
	public void setFacingDirection(EnumFacing direction) {
		this.direction = direction;
		updateState();
	}
	
}
