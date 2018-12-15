package net.teamio.taam.content.conveyors;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.teamio.taam.Config;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRedstoneControlled;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.conveyors.ConveyorSlotsInventory;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.IConveyorSlots;
import net.teamio.taam.conveyors.OutputChuteBacklog;
import net.teamio.taam.network.TPMachineConfiguration;
import net.teamio.taam.recipes.IProcessingRecipe;
import net.teamio.taam.recipes.ProcessingRegistry;
import net.teamio.taam.util.InventoryUtils;
import net.teamio.taam.util.TaamUtil;
import net.teamio.taam.util.WorldCoord;

import java.util.List;

public class TileEntityConveyorProcessor extends BaseTileEntity implements IRedstoneControlled, IWorldInteractable, IRotatable, ITickable {

	public static final byte Shredder = 0;
	public static final byte Grinder = 1;
	public static final byte Crusher = 2;

	private final ItemStackHandler itemHandler;
	private final IConveyorSlots conveyorSlots;
	private ItemStack cachedInput;
	private byte mode;

	private final OutputChuteBacklog chute = new OutputChuteBacklog();

	private byte redstoneMode = IRedstoneControlled.MODE_ACTIVE_ON_LOW;
	private EnumFacing direction = EnumFacing.NORTH;

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
		itemHandler = new ItemStackHandler(1);
		conveyorSlots = new ConveyorSlotsInventory(itemHandler) {
			@Override
			public void onChangeHook() {
				updateState(true, false, false);
			}
		};
		this.mode = mode;
		if (mode == Grinder) {
			timeout = Config.pl_processor_grinder_timeout;
		} else {
			timeout = Config.pl_processor_crusher_timeout;
		}
	}

	@Override
	public String getName() {
		switch (mode) {
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

	public boolean isCoolingDown() {
		return timeout > 0;
	}

	@Override
	public void update() {
		if (worldObj.isRemote) {
			return;
		}

		if (!isCoolingDown()) {
			ItemStack stack = itemHandler.getStackInSlot(0);
			if (stack == null || stack.stackSize < stack.getMaxStackSize()) {
				ConveyorUtil.tryInsertItemsFromWorld(this, worldObj, null, false);
			}
		}

		boolean redstoneHigh = worldObj.isBlockIndirectlyGettingPowered(pos) > 0;

		boolean newShutdown = TaamUtil.isShutdown(worldObj.rand, redstoneMode, redstoneHigh);

		boolean needsUpdate = false;

		if (isShutdown != newShutdown) {
			isShutdown = newShutdown;
			needsUpdate = true;
		}

		if (!isShutdown) {

			if (mode == Shredder) {
				if (processShredder()) {
					itemHandler.setStackInSlot(0, null);
					needsUpdate = true;
				}
			} else {
				ProcessResult processResult = processOther();
				if (processResult == ProcessResult.Processed) {
					itemHandler.extractItem(0, 1, false);
					needsUpdate = true;
				} else if (processResult == ProcessResult.Output) {
					needsUpdate = true;
				}
			}

			if (Config.pl_processor_hurt && worldObj.rand.nextFloat() < Config.pl_processor_hurt_chance) {
				hurtEntities();
			}
		}

		if (needsUpdate) {
			updateState(true, false, false);
		}

	}

	private void hurtEntities() {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		List<EntityLivingBase> entitites = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1));
		for (EntityLivingBase living : entitites) {
			hurtEntity(living);
		}
	}

	private void hurtEntity(EntityLivingBase living) {
		DamageSource ds;
		switch (mode) {
			default:
				ds = TaamMain.ds_processed;
				break;
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

	public enum ProcessResult {
		NoOperation,
		Output,
		Processed
	}

	private ProcessResult processOther() {
		BlockPos down = pos.down();

		/*
		 * Check blocked & fetch output inventory
		 */
		chute.refreshOutputInventory(worldObj, down);
		if (chute.isBlocked()) {
			return ProcessResult.NoOperation;
		}

		/*
		 * Output Backlog
		 */
		// Output the backlog. Returns true if there were items transferred or there are still items left.
		if (chute.output(worldObj, down)) {
			return ProcessResult.Output;
		}

		// If output finished, continue processing.
		if (isCoolingDown()) {
			timeout--;
			return ProcessResult.NoOperation;
		}

		ItemStack input = itemHandler.getStackInSlot(0);

		if (input == null) {
			recipe = null;
			return ProcessResult.NoOperation;
		}

		if (recipe == null || !input.isItemEqual(cachedInput)) {
			recipe = getRecipe(input);
			cachedInput = input;
		}

		if (recipe != null) {
			chute.backlog = recipe.getOutput(input);

			if (mode == Grinder) {
				timeout += Config.pl_processor_grinder_timeout;
			} else {
				timeout += Config.pl_processor_crusher_timeout;
			}
			// Consume input
			return ProcessResult.Processed;
		}

		return ProcessResult.NoOperation;
	}

	private boolean processShredder() {

		if (isCoolingDown()) {
			timeout--;
			return false;
		}

		ItemStack input = itemHandler.getStackInSlot(0);

		if (input == null) {
			return false;
		}
		timeout += Config.pl_processor_shredder_timeout;

		return true;
	}

	public byte getMode() {
		return mode;
	}

	@SideOnly(Side.CLIENT)
	public ItemStack getRenderStack() {
		return itemHandler.getStackInSlot(0);
	}

	private IProcessingRecipe getRecipe(ItemStack input) {
		int machine;
		switch (mode) {
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
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setTag("items", itemHandler.serializeNBT());

		NBTTagCompound tagChute = new NBTTagCompound();
		chute.writeToNBT(tagChute);
		tag.setTag("chute", tagChute);

		tag.setByte("mode", mode);
		// tag.setByte("redstoneMode", redstoneMode);
		tag.setInteger("timeout", timeout);
		tag.setBoolean("isShutdown", isShutdown);
		tag.setInteger("direction", direction.ordinal());
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		NBTTagCompound itemTag = tag.getCompoundTag("items");
		if (itemTag != null) {
			itemHandler.deserializeNBT(itemTag);
		}

		chute.readFromNBT(tag.getCompoundTag("chute"));

		mode = tag.getByte("mode");
		// redstoneMode = tag.getByte("redstoneMode");
		timeout = tag.getInteger("timeout");
		isShutdown = tag.getBoolean("isShutdown");
		direction = EnumFacing.getFront(tag.getInteger("direction"));
		if (direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
			direction = EnumFacing.NORTH;
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == Taam.CAPABILITY_CONVEYOR) {
			return true;
		}
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return facing.getAxis() == EnumFacing.Axis.Y;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == Taam.CAPABILITY_CONVEYOR) {
			return (T) conveyorSlots;
		}
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == EnumFacing.UP) {
				return (T) itemHandler;
			} else {
				// This is to prevent hoppers from doing weird things... (#244)
				return (T) EmptyHandler.INSTANCE;
			}
		}
		return super.getCapability(capability, facing);
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
		if (worldObj.isRemote) {
			TPMachineConfiguration config = TPMachineConfiguration.newChangeInteger(new WorldCoord(this), (byte) 1, redstoneMode);
			TaamMain.network.sendToServer(config);
		} else {
			markDirty();
		}
	}

	/*
	 * IWorldInteractable implementation
	 */

	@Override
	public boolean onBlockActivated(World world, EntityPlayer player, EnumHand hand, boolean hasWrench, EnumFacing side,
	                                float hitX, float hitY, float hitZ) {
		if (side != EnumFacing.UP) {
			return false;
		}
		if (!isShutdown) {
			hurtEntity(player);
			return true;
		}
		int playerSlot = player.inventory.currentItem;
		ItemStack playerStack = player.inventory.getCurrentItem();
		if (playerStack == null) {
			// Take from Processor
			ItemStack taken = itemHandler.extractItem(0, player.inventory.getInventoryStackLimit(), false);
			if (taken != null) {
				player.inventory.setInventorySlotContents(playerSlot, taken);
			}
		} else {
			if (mode != Shredder) {
				// Put into processor
				ItemStack notInserted = itemHandler.insertItem(0, playerStack, false);
				if (notInserted != playerStack) {
					player.inventory.setInventorySlotContents(playerSlot, notInserted);
				}
			}
		}
		return true;
	}

	@Override
	public boolean onBlockHit(World world, EntityPlayer player, boolean hasWrench) {
		if (hasWrench) {
			ItemStack taken = itemHandler.getStackInSlot(0);
			if (taken != null) {
				InventoryUtils.tryDropToInventory(player, taken, .5, .5, .5);
				itemHandler.setStackInSlot(0, null);
			}
		} else if (!isShutdown) {
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
		if (this.direction != direction) {
			// Only update if necessary
			this.direction = direction;
			updateState(false, true, false);
		}
	}

}
