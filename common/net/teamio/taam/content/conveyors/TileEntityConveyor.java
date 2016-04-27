package net.teamio.taam.content.conveyors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.teamio.taam.Config;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRenderable;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorAppliance;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;

public class TileEntityConveyor extends BaseTileEntity implements ISidedInventory, IConveyorAwareTE, IRotatable, IConveyorApplianceHost, IWorldInteractable, ITickable, IRenderable {

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
	
	/**
	 * ThreadLocal storage for the list of visible parts (required due to some concurrency issues, See issue #194)
	 * TODO: central location for one list? Not one per entity type.. Adjust getVisibleParts
	 */
	private static final ThreadLocal<List<String>> visibleParts = new ThreadLocal<List<String>>() {
		@Override
		protected List<String> initialValue() {
			return new ArrayList<String>(14);
		}
	};
	
	/**
	 * Appliance cache. Updated when loading & on block update
	 */
	private List<IConveyorAppliance> applianceCache;


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
	public void blockUpdate() {
		if(worldObj != null) {
			updateApplianceCache();
		}
	}
	
	@Override
	public void renderUpdate() {
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
				worldObj.getTileEntity(pos.offset(EnumFacing.UP)) instanceof IConveyorAwareTE;
	}
	
	/*
	 * Part lists for the inventory rendering
	 */

	public static List<String> parts_invalid = Collections.unmodifiableList(Lists.newArrayList(
			"Support_Alu_smdl_alu",
			"ConveyorRoundEnd_Framing_Alu_crfmdl_alu", "ConveyorRoundBegin_Framing_Alu_crfmdl_alu"
			));
	
	public static List<String> parts_1 = Collections.unmodifiableList(Lists.newArrayList(
			"ConveyorRoundEnd_crmdl", "ConveyorRoundBegin_crmdl",
			"ConveyorRoundEnd_Walz_Wood_cwalzmdl_wood", "ConveyorRoundEnd_Framing_Wood_crfmdl_wood",
			"ConveyorRoundBegin_Walz_Wood_cwalzmdl_wood", "ConveyorRoundBegin_Framing_Wood_crfmdl_wood",
			"Support_Wood_smdl_wood", "ConveyorDirectionMarker_Wood_cdmdl_wood"
			));

	public static List<String> parts_2 = Collections.unmodifiableList(Lists.newArrayList(
			"ConveyorRoundEnd_crmdl", "ConveyorRoundBegin_crmdl",
			"ConveyorRoundEnd_Walz_Alu_cwalzmdl_alu", "ConveyorRoundEnd_Framing_Alu_crfmdl_alu",
			"ConveyorRoundBegin_Walz_Alu_cwalzmdl_alu", "ConveyorRoundBegin_Framing_Alu_crfmdl_alu",
			"Support_Alu_smdl_alu", "ConveyorDirectionMarker_Alu_cdmdl_alu"
			));

	public static List<String> parts_3 = Collections.unmodifiableList(Lists.newArrayList(
			"ConveyorRoundEnd_crmdl", "ConveyorRoundBegin_crmdl",
			"ConveyorRoundEnd_Walz_Alu_cwalzmdl_alu", "ConveyorRoundEnd_Framing_Alu_crfmdl_alu",
			"ConveyorRoundBegin_Walz_Alu_cwalzmdl_alu", "ConveyorRoundBegin_Framing_Alu_crfmdl_alu",
			"Support_Alu_smdl_alu", "ConveyorDirectionMarker_Alu_cdmdl_alu",
			"ConveyorHighThroughput_Framing_Alu_chtpmdl_alu"
			));
	
	@Override
	public List<String> getVisibleParts() {
		List<String> visibleParts = TileEntityConveyor.visibleParts.get();
		
		// Visible parts list is re-used to reduce object creation
		visibleParts.clear();
		
		boolean isWood = speedLevel == 0;
		boolean isHighSpeed = speedLevel > 1;
		
		if(isEnd) {
			visibleParts.add("ConveyorRoundEnd_crmdl");
			visibleParts.add(isWood ? "ConveyorRoundEnd_Walz_Wood_cwalzmdl_wood" : "ConveyorRoundEnd_Walz_Alu_cwalzmdl_alu");
			visibleParts.add(isWood ? "ConveyorRoundEnd_Framing_Wood_crfmdl_wood" : "ConveyorRoundEnd_Framing_Alu_crfmdl_alu");
		} else {
			visibleParts.add("ConveyorStraightEnd_csmdl");
			visibleParts.add(isWood ? "ConveyorStraightEnd_Walz_Wood_cwalzmdl_wood" : "ConveyorStraightEnd_Walz_Alu_cwalzmdl_alu");
			visibleParts.add(isWood ? "ConveyorStraightEnd_Framing_Wood_csfmdl_wood" : "ConveyorStraightEnd_Framing_Alu_csfmdl_alu");
		}
		if(isBegin) {
			visibleParts.add("ConveyorRoundBegin_crmdl");
			visibleParts.add(isWood ? "ConveyorRoundBegin_Walz_Wood_cwalzmdl_wood" : "ConveyorRoundBegin_Walz_Alu_cwalzmdl_alu");
			visibleParts.add(isWood ? "ConveyorRoundBegin_Framing_Wood_crfmdl_wood" : "ConveyorRoundBegin_Framing_Alu_crfmdl_alu");
		} else {
			visibleParts.add("ConveyorStraightBegin_csmdl");
			visibleParts.add(isWood ? "ConveyorStraightBegin_Walz_Wood_cwalzmdl_wood" : "ConveyorStraightBegin_Walz_Alu_cwalzmdl_alu");
			visibleParts.add(isWood ? "ConveyorStraightBegin_Framing_Wood_csfmdl_wood" : "ConveyorStraightBegin_Framing_Alu_csfmdl_alu");
		}
		visibleParts.add(isWood ? "Support_Wood_smdl_wood" : "Support_Alu_smdl_alu");
		visibleParts.add(isWood ? "ConveyorDirectionMarker_Wood_cdmdl_wood" : "ConveyorDirectionMarker_Alu_cdmdl_alu");
		if(renderAbove) {
			visibleParts.add(isWood ? "ConveyorSupportAbove_Wood_samdl_wood" : "ConveyorSupportAbove_Alu_samdl_alu");
		}
		if(renderBegin) {
			visibleParts.add(isWood ? "ConveyorRoundBeginCap_Wood_cecmdl_wood" : "ConveyorRoundBeginCap_Alu_cecmdl_alu");
		}
		if(renderEnd) {
			visibleParts.add(isWood ? "ConveyorRoundEndCap_Wood_cecmdl_wood" : "ConveyorRoundEndCap_Alu_cecmdl_alu");
		}
		if(renderLeft) {
			visibleParts.add(isWood ? "ConveyorLeftCap_Wood_cecmdl_wood" : "ConveyorLeftCap_Alu_cecmdl_alu");
		}
		if(renderRight) {
			visibleParts.add(isWood ? "ConveyorRightCap_Wood_cecmdl_wood" : "ConveyorRightCap_Alu_cecmdl_alu");
		}
		if(isHighSpeed) {
			visibleParts.add("ConveyorHighThroughput_Framing_Alu_chtpmdl_alu");
		}
		
		return visibleParts;
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

		// Call this method to initialize the appliance cache if needed.
		getAppliances();
		
		/*
		 * Find items laying on the conveyor.
		 */
		
		if(ConveyorUtil.tryInsertItemsFromWorld(this, worldObj, null, false)) {
			updateState(false, false, false);
		}

		/*
		 * Move items already on the conveyor
		 */
		
		// process from movement direction backward to keep slot order inside one conveyor,
		// as we depend on the status of the next slot
		int[] slotOrder = ConveyorUtil.getSlotOrderForDirection(direction);
		if(ConveyorUtil.defaultTransition(worldObj, this, slotOrder)) {
			updateState(true, false, false);
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
		return direction.rotateAround(Axis.Y);
	}

	@Override
	public void setFacingDirection(EnumFacing direction) {
		this.direction = direction;
		if(direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
			this.direction = EnumFacing.NORTH;
		}
		updateState(false, true, true);
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
	public List<IConveyorAppliance> getAppliances() {
		if(applianceCache == null) {
			updateApplianceCache();
		}
		return applianceCache;
	}
	
	public void updateApplianceCache() {
		if(speedLevel == 1) {
			applianceCache = ConveyorUtil.getTouchingAppliances(this, worldObj, pos);
		} else {
			applianceCache = null;
		}
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
			removeItemAt(slot);
		} else {
			insertItemAt(itemStack, slot);
		}
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(getName());
	}

	@Override
	public String getName() {
		return "tile.productionline.conveyor.name";
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
		// Nothing to do.
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		// Nothing to do.
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		return true;
	}


	@Override
	public int getField(int id) {
		// Whatever..
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// Whatever..
	}

	@Override
	public int getFieldCount() {
		// Whatever..
		return 0;
	}

	@Override
	public void clear() {
		// Nothing to do.
	}
	
	/*
	 * ISidedInventory implementation
	 */
	
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		final int slot = ConveyorUtil.getSlot(side);
		if(slot == -1) {
			return new int[0];
		} else {
			return new int[] { slot };
		}
	}
	
	@Override
	public boolean canInsertItem(int slot, ItemStack itemStack, EnumFacing side) {
		return ConveyorUtil.insertItemAt(this, itemStack, slot, true) > 0;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemStack, EnumFacing side) {
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
}
