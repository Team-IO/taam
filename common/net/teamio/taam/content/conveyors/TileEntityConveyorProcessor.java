package net.teamio.taam.content.conveyors;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.IHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
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
import net.teamio.taam.conveyors.api.IConveyorSlots;
import net.teamio.taam.network.TPMachineConfiguration;
import net.teamio.taam.recipes.IProcessingRecipe;
import net.teamio.taam.recipes.ProcessingRegistry;
import net.teamio.taam.util.ProcessingUtil;
import net.teamio.taam.util.TaamUtil;
import net.teamio.taam.util.WorldCoord;
import net.teamio.taam.util.inv.InventorySimple;
import net.teamio.taam.util.inv.InventoryUtils;

public class TileEntityConveyorProcessor extends BaseTileEntity implements ISidedInventory, IConveyorSlots, IHopper, IRedstoneControlled, IWorldInteractable, IRotatable, ITickable, IRenderable {

	public static final byte Shredder = 0;
	public static final byte Grinder = 1;
	public static final byte Crusher = 2;

	private InventorySimple inventory;
	private byte mode;
	
	private ItemStack[] backlog;

	private byte redstoneMode = IRedstoneControlled.MODE_ACTIVE_ON_LOW;
	private EnumFacing direction = EnumFacing.NORTH;
	
	private int timeout;
	
	public static List<String> parts_shredder = Collections.unmodifiableList(Lists.newArrayList("Support_Alu_smdl_alu", "ProcessorChute_chutemdl", "Processor_Walzes", "ProcessorMarker_Shredder_pmmdl_shr", "BumpsShredder"));
	public static List<String> parts_grinder = Collections.unmodifiableList(Lists.newArrayList("Support_Alu_smdl_alu", "ProcessorChute_chutemdl", "Processor_Walzes", "ProcessorMarker_Grinder_pmmdl_gri", "BumpsGrinder"));
	public static List<String> parts_crusher = Collections.unmodifiableList(Lists.newArrayList("Support_Alu_smdl_alu", "ProcessorChute_chutemdl", "Processor_Walzes", "ProcessorMarker_Crusher_pmmdl_cru", "BumpsCrusher"));
	
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
		inventory = new InventorySimple(1, getName());
		this.mode = mode;
		if(mode == Grinder) {
			timeout = Config.pl_processor_grinder_timeout;
		} else {
			timeout = Config.pl_processor_crusher_timeout;
		}
	}
	
	@Override
	public List<String> getVisibleParts() {
		if(mode == Shredder) {
			return parts_shredder;
		} else if(mode == Grinder) {
			return parts_grinder;
		} else if(mode == Crusher) {
			return parts_crusher;
		} else {
			return null;
		}
	}
	
	public boolean isCoolingDown() {
		return timeout > 0;
	}
	
	
	@Override
	public void update() {
		if(worldObj.isRemote) {
			return;
		}

		if(!isCoolingDown()) {
			ItemStack stack = inventory.getStackInSlot(0);
			if(stack == null || stack.stackSize < stack.getMaxStackSize()) {
				ConveyorUtil.tryInsertItemsFromWorld(this, worldObj, null, false);
			}
		}
		
		boolean redstoneHigh = worldObj.isBlockIndirectlyGettingPowered(pos) > 0;
		
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
			updateState(true, false, false);
		}
		
	}

	private void hurtEntities() {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		List<EntityLivingBase> entitites = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.fromBounds(x, y, z, x + 1, y + 1, z + 1));
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
		BlockPos down = pos.down();
		
		IInventory outputInventory = InventoryUtils.getInventory(worldObj, down);
		if(outputInventory == null && !TaamUtil.canDropIntoWorld(worldObj, down)) {
			return false;
		}

		// Output the backlog. Returns true if there were items transferred or there are still items left.
		if(!ProcessingUtil.chuteMechanicsOutput(worldObj, down, outputInventory, backlog, 0)) {
			backlog = null;
		} else {
			return false;
		}

		// If output finished, continue processing.
		if(backlog == null) {
			
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
				backlog = recipe.getOutput(input);
				
				if(mode == Grinder) {
					timeout += Config.pl_processor_grinder_timeout;
				} else {
					timeout += Config.pl_processor_crusher_timeout;
				}
				// Consume input
				return true;
			}
		}
		
		return false;
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
		if (backlog != null) {
			tag.setTag("holdback", InventoryUtils.writeItemStacksToTagSequential(backlog));
		}
		tag.setByte("mode", mode);
		// tag.setByte("redstoneMode", redstoneMode);
		tag.setInteger("timeout", timeout);
		tag.setBoolean("isShutdown", isShutdown);
		tag.setInteger("direction", direction.ordinal());
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		inventory.items = new ItemStack[inventory.getSizeInventory()];
		InventoryUtils.readItemStacksFromTag(inventory.items, tag.getTagList("items", NBT.TAG_COMPOUND));

		NBTTagList holdbackList = tag.getTagList("holdback", NBT.TAG_COMPOUND);
		if (holdbackList == null) {
			backlog = null;
		} else {
			backlog = new ItemStack[holdbackList.tagCount()];
			InventoryUtils.readItemStacksFromTagSequential(backlog, holdbackList);
		}

		mode = tag.getByte("mode");
		// redstoneMode = tag.getByte("redstoneMode");
		timeout = tag.getInteger("timeout");
		isShutdown = tag.getBoolean("isShutdown");
		direction = EnumFacing.getFront(tag.getInteger("direction"));
		if (direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
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
		updateState(true, false, false);
		return retVal;
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {
		return inventory.removeStackFromSlot(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
		recipe = null;
		updateState(true, false, false);
	}

	@Override
	public String getName() {
		switch(mode) {
		case Shredder:
			return "tile.productionline.shredder.name";
		case Grinder:
			return "tile.productionline.grinder.name";
		case Crusher:
			return "tile.productionline.crusher.name";
		default:
			return "tile.productionline.invalid.name";
		}
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
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		// Nothig to do
	}

	@Override
	public void closeInventory(EntityPlayer player) {
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
	public int insertItemAt(ItemStack stack, int slot) {
		if(!isItemValidForSlot(0, stack)) {
			return 0;
		}
		// insertItem returns item count unable to insert.
		int inserted = stack.stackSize - InventoryUtils.insertItem(inventory, stack, false);
		updateState(true, false, false);
		return inserted;
	}
	
	@Override
	public ItemStack removeItemAt(int slot) {
		ItemStack content = getStackInSlot(slot);
		setInventorySlotContents(slot, null);
		return content;
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
		return EnumFacing.DOWN;
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

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if(side == EnumFacing.UP) {
			return new int[] { 0 };
		} else {
			return new int[0];
		}
	}
	
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		if(direction == EnumFacing.UP) {
			if(isItemValidForSlot(0, itemStackIn)) {
				return true;
			}
			return false;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return direction == EnumFacing.UP;
	}
	
	/*
	 * IHopper implementation
	 */
	
	@Override
	public double getXPos() {
		return pos.getX();
	}

	@Override
	public double getYPos() {
		return pos.getY();
	}

	@Override
	public double getZPos() {
		return pos.getZ();
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
	public boolean onBlockActivated(World world, EntityPlayer player, boolean hasWrench, EnumFacing side, float hitX,
			float hitY, float hitZ) {
		if(side != EnumFacing.UP) {
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
	public boolean onBlockHit(World world, EntityPlayer player, boolean hasWrench) {
//		if(side != EnumFacing.UP.ordinal()) {
//			return false;
//		}
		if(hasWrench) {
			ItemStack taken = getStackInSlot(0);
			if(taken != null) {
				net.teamio.taam.util.inv.InventoryUtils.tryDropToInventory(player, taken, .5, .5, .5);
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
		return direction.rotateY();
	}

	@Override
	public void setFacingDirection(EnumFacing direction) {
		this.direction = direction;
		updateState(false, true, false);
	}
	
}
