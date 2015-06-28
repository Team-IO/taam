package founderio.taam.content.conveyors;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.IHopper;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;
import codechicken.lib.inventory.InventoryRange;
import codechicken.lib.inventory.InventorySimple;
import codechicken.lib.inventory.InventoryUtils;
import founderio.taam.TaamMain;
import founderio.taam.content.BaseTileEntity;
import founderio.taam.conveyors.ConveyorUtil;
import founderio.taam.conveyors.api.IConveyorAwareTE;
import founderio.taam.conveyors.api.IItemFilter;
import founderio.taam.conveyors.api.IProcessingRecipe;
import founderio.taam.conveyors.api.IRedstoneControlled;
import founderio.taam.conveyors.api.ProcessingRegistry;
import founderio.taam.network.TPMachineConfiguration;
import founderio.taam.util.TaamUtil;
import founderio.taam.util.WorldCoord;

public class TileEntityConveyorProcessor extends BaseTileEntity implements ISidedInventory, IConveyorAwareTE, IHopper, IRedstoneControlled {

	public static final byte Shredder = 0;
	public static final byte Grinder = 1;
	public static final byte Crusher = 2;

	private InventorySimple inventory;
	private byte mode;

	private byte redstoneMode;
	
	private byte progress;
	private int timeout;
	
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

		ConveyorUtil.tryInsertItemsFromWorld(this, worldObj, null, false);
		
		isShutdown = false;
		
		
		boolean redstoneHigh = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		
		// Redstone. Other criteria?
		if(redstoneMode == IRedstoneControlled.MODE_ACTIVE_ON_HIGH && !redstoneHigh) {
			isShutdown = true;
		} else if(redstoneMode == IRedstoneControlled.MODE_ACTIVE_ON_LOW && redstoneHigh) {
			isShutdown = true;
		} else if(redstoneMode > 4 || redstoneMode < 0) {
			isShutdown = worldObj.rand.nextBoolean();
		}
		
		if(isShutdown) {
			return;
		}
		
		boolean decrease = false;
		
		if(mode == Shredder) {
			decrease = processShredder();
		} else {
			decrease = processOther();
		}
		
		if(decrease) {
			decrStackSize(0, 1);
		}
		
	}
	
	private boolean processOther() {
		ItemStack[] outputQueue = holdback;
		boolean decrease = false;
		IInventory outputInventory = InventoryUtils.getInventory(worldObj, xCoord, yCoord - 1, zCoord);
		if(outputInventory == null && !TaamUtil.canDropIntoWorld(worldObj, xCoord, yCoord - 1, zCoord)) {
			return false;
		}
		
		if(outputQueue == null) {
			
			if(isCoolingDown()) {
				timeout--;
				return false;
			}
			
			ItemStack input = getStackInSlot(0);
			
			if(input == null) {
				return false;
			}
			
			IProcessingRecipe recipe = getRecipe(input);
			
			if(recipe != null) {
				decrease = true;
				
				outputQueue = recipe.getOutput(input, worldObj.rand);
				
				timeout += 15;
			}
		}
		
		if(outputQueue == null) {
			return false;
		}
		
		if(outputInventory == null) {
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
			boolean hasOutputLeft = false;
			InventoryRange range = new InventoryRange(outputInventory, ForgeDirection.UP.ordinal());
			
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
		//TODO: Config
		timeout += 1;
		
		return true;
	}

	public byte getMode() {
		return mode;
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setTag("items", InventoryUtils.writeItemStacksToTag(inventory.items));
		if(holdback != null) {
			tag.setTag("holdback", InventoryUtils.writeItemStacksToTag(holdback));
		}
		tag.setByte("mode", mode);
		tag.setByte("redstoneMode", redstoneMode);
		tag.setByte("progress", progress);
		tag.setInteger("timeout", timeout);
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
		redstoneMode = tag.getByte("redstoneMode");
		progress = tag.getByte("progress");
		timeout = tag.getInteger("timeout");
	}

	/*
	 * ISidedInventory implementation
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
		return mode == Shredder || getRecipe(itemStack) != null;
	}

	/*
	 * IConveyorAwareTE
	 */
	
	@Override
	public boolean canSlotMove(int slot) {
		return false;
	}

	@Override
	public int getMovementProgress(int slot) {
		return 0;
	}

	@Override
	public int getMaxMovementProgress() {
		return 1;
	}

	@Override
	public IItemFilter getSlotFilter(int slot) {
		return null;
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
	public ItemStack getItemAt(int slot) {
		return null;
	}

	@Override
	public ForgeDirection getMovementDirection() {
		return ForgeDirection.DOWN;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if(side == ForgeDirection.UP.ordinal()) {
			return new int[] { 0 };
		} else {
			return new int[0];
		}
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		if(side == ForgeDirection.UP.ordinal()) {
			if(isItemValidForSlot(0, stack)) {
				return true;
			}
			return false;
		} else {
			return false;
		}
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
		
		return ProcessingRegistry.getRecipe(machine, input);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return false;
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

}
