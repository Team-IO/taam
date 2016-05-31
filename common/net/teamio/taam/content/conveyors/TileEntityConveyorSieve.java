package net.teamio.taam.content.conveyors;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.CapabilityItemHandler;
import net.teamio.taam.Config;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRedstoneControlled;
import net.teamio.taam.content.IRenderable;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.ConveyorSlotsItemHandler;
import net.teamio.taam.conveyors.api.ConveyorSlotsMoving;
import net.teamio.taam.network.TPMachineConfiguration;
import net.teamio.taam.util.TaamUtil;
import net.teamio.taam.util.WorldCoord;
import net.teamio.taam.util.inv.InventoryRange;
import net.teamio.taam.util.inv.InventoryUtils;

public class TileEntityConveyorSieve extends BaseTileEntity implements IRotatable, IWorldInteractable, IRedstoneControlled, ITickable, IRenderable {

	public static final List<String> parts = Collections.unmodifiableList(Lists.newArrayList("Sieve_Base"));

	/*
	 * Conveyor State
	 */
	private final ConveyorSlotsMoving conveyorSlots;
	private byte redstoneMode = IRedstoneControlled.MODE_ACTIVE_ON_LOW;
	private EnumFacing direction = EnumFacing.NORTH;

	/**
	 * Just for rendering purposes we keep this here.
	 */
	public boolean isShutdown;

	public TileEntityConveyorSieve() {
		conveyorSlots = new ConveyorSlotsMoving() {
			@Override
			public byte getSpeedsteps() {
				return Config.pl_sieve_speedsteps;
			}
			
			@Override
			public EnumFacing getNextSlot(int slot) {
				return direction;
			}
			@Override
			public void onChangeHook() {
				updateState(true, false, false);
			};
		};
		conveyorSlots.rotation = direction;
	}
	
	@Override
	public String getName() {
		return "tile.taam.productionline.sieve.name";
	}

	@Override
	public void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();
	}

	@Override
	public List<String> getVisibleParts() {
		return parts;
	}

	@Override
	public void update() {

		/*
		 * Find items laying on the conveyor.
		 */

		boolean needsUpdate = false;
		boolean needsWorldUpdate = false;

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
				needsWorldUpdate = true;
			}

			/*
			 * Move items already on the conveyor
			 */

			if(ConveyorUtil.defaultTransition(worldObj, pos, conveyorSlots, slotOrder)) {
				needsUpdate = true;
			}
		}

		if(needsUpdate) {
			updateState(needsWorldUpdate, false, false);
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

			ItemWrapper wrapper = conveyorSlots.getSlot(slot);

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
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setInteger("direction", direction.ordinal());
		tag.setTag("items", conveyorSlots.serializeNBT());
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		direction = EnumFacing.getFront(tag.getInteger("direction"));
		if(direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
			direction = EnumFacing.NORTH;
		}
		conveyorSlots.rotation = direction;
		conveyorSlots.deserializeNBT(tag.getTagList("items", NBT.TAG_COMPOUND));
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == Taam.CAPABILITY_CONVEYOR) {
			return true;
		}
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == Taam.CAPABILITY_CONVEYOR) {
			return (T) conveyorSlots;
		}
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) new ConveyorSlotsItemHandler(conveyorSlots, facing);
		}
		return super.getCapability(capability, facing);
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
		if(this.direction == direction) {
			// Only update if necessary
			return;
		}
		this.direction = direction;
		if(direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
			this.direction = EnumFacing.NORTH;
		}
		conveyorSlots.rotation = direction;
		updateState(false, true, true);
		worldObj.notifyBlockOfStateChange(pos, blockType);
		if(blockType != null) {
			blockType.onNeighborChange(worldObj, pos, pos);
		}
	}

	@Override
	public EnumFacing getFacingDirection() {
		return direction;
	}

	/*
	 * IWorldInteractable implementation
	 */

	@Override
	public boolean onBlockActivated(World world, EntityPlayer player, EnumHand hand, boolean hasWrench, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		if(side != EnumFacing.UP) {
			return false;
		}
		ConveyorUtil.defaultPlayerInteraction(player, conveyorSlots, hitX, hitZ);
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
		redstoneMode = mode;
		if(worldObj.isRemote) {
			TPMachineConfiguration config = TPMachineConfiguration.newChangeInteger(new WorldCoord(this), (byte)1, redstoneMode);
			TaamMain.network.sendToServer(config);
		} else {
			markDirty();
		}
	}
}
