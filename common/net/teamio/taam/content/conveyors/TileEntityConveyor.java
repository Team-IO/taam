package net.teamio.taam.content.conveyors;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.teamio.taam.Config;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRenderable;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.conveyors.ConveyorSlotsItemHandler;
import net.teamio.taam.conveyors.ConveyorSlotsMoving;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.IConveyorAppliance;
import net.teamio.taam.conveyors.IConveyorApplianceHost;
import net.teamio.taam.conveyors.IConveyorSlots;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.RedirectorSide;
import net.teamio.taam.util.InventoryUtils;
import net.teamio.taam.util.TaamUtil;
import net.teamio.taam.util.WrenchUtil;

public class TileEntityConveyor extends BaseTileEntity implements IRotatable, IConveyorApplianceHost, IWorldInteractable, ITickable, IRenderable {

	/*
	 * Conveyor State
	 */
	private final ConveyorSlotsMoving conveyorSlots;
	private EnumFacing direction = EnumFacing.NORTH;
	private int speedLevel = 0;

	private boolean redirectorLeft = false;
	private boolean redirectorRight = false;

	public boolean isEnd = false;
	public boolean isBegin = false;
	@SideOnly(Side.CLIENT)
	public boolean renderEnd = false;
	@SideOnly(Side.CLIENT)
	public boolean renderBegin = false;
	@SideOnly(Side.CLIENT)
	public boolean renderRight = false;
	@SideOnly(Side.CLIENT)
	public boolean renderLeft = false;
	@SideOnly(Side.CLIENT)
	public boolean renderAbove = false;

	/**
	 * Appliance cache. Updated when loading & on block update
	 */
	private List<IConveyorAppliance> applianceCache;

	public TileEntityConveyor() {
		this(0);
	}

	public TileEntityConveyor(int speedLevel) {
		this.speedLevel = speedLevel;
		conveyorSlots = new ConveyorSlotsMoving() {
			@Override
			public byte getSpeedsteps() {
				return Config.pl_conveyor_speedsteps[TileEntityConveyor.this.speedLevel];
			}
			
			@Override
			public EnumFacing getNextSlot(int slot) {
				return TileEntityConveyor.this.getNextSlot(slot);
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
		return "tile.taam.productionline.conveyor.name";
	}

	public int getSpeedLevel() {
		return speedLevel;
	}

	public boolean isRedirectorLeft() {
		return redirectorLeft;
	}

	public void setRedirectorLeft(boolean redirectorLeft) {
		this.redirectorLeft = redirectorLeft;
		updateState(true, true, true);
	}

	public boolean isRedirectorRight() {
		return redirectorRight;
	}

	public void setRedirectorRight(boolean redirectorRight) {
		this.redirectorRight = redirectorRight;
		updateState(true, true, true);
	}

	private EnumFacing getNextSlot(int slot) {
		if (speedLevel >= 2) {
			return ConveyorUtil.getHighspeedTransition(slot, direction);
		} else {
			/*
			 * Handle indicators
			 */
			if (redirectorRight) {
				EnumFacing right = direction.rotateY();
				if (slot == ConveyorUtil.getSlot(right)) {
					return right;
				}
			}
			if (redirectorLeft) {
				EnumFacing left = direction.rotateYCCW();
				if (slot == ConveyorUtil.getSlot(left)) {
					return left;
				}
			}

			/*
			 * Assume default direction
			 */
			EnumFacing nextSlot = direction;

			/*
			 * Let the appliances override the direction, if required
			 */
			List<IConveyorAppliance> appliances = getAppliances();
			if (appliances != null) {
				ItemWrapper wrapper = conveyorSlots.getSlot(slot);
				// Let each appliance have the chance to override the next slot
				for (IConveyorAppliance appliance : appliances) {
					nextSlot = appliance.overrideNextSlot(this, slot, wrapper, nextSlot);
				}
			}

			return nextSlot;
		}
	}

	@Override
	public void blockUpdate() {
		if (worldObj != null) {
			updateApplianceCache();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderUpdate() {
		// Check in front
		TileEntity te = worldObj.getTileEntity(pos.offset(direction));

		if (te instanceof TileEntityConveyor) {
			TileEntityConveyor next = (TileEntityConveyor) te;
			renderEnd = next.speedLevel != speedLevel;
			renderEnd = renderEnd || next.getFacingDirection() != direction;
			isEnd = renderEnd;
		} else {
			isEnd = true;
			renderEnd = ConveyorUtil.getSlots(te, direction.getOpposite()) != null;
		}

		// Check behind
		EnumFacing inverse = direction.getOpposite();
		te = worldObj.getTileEntity(pos.offset(inverse));
		if (te instanceof TileEntityConveyor) {
			TileEntityConveyor next = (TileEntityConveyor) te;
			renderBegin = next.speedLevel != speedLevel;
			renderBegin = renderBegin || next.getFacingDirection() != direction;
			isBegin = renderBegin;
		} else {
			isBegin = true;
			renderBegin = ConveyorUtil.getSlots(te, direction) != null;
		}

		// Check right
		if (redirectorRight) {
			renderRight = true;
		} else {
			inverse = direction.rotateY();
			te = worldObj.getTileEntity(pos.offset(inverse));

			if (te instanceof TileEntityConveyor) {
				TileEntityConveyor next = (TileEntityConveyor) te;
				EnumFacing nextFacing = next.getFacingDirection();
				renderRight = nextFacing.getAxis() != direction.getAxis()
						|| (next.redirectorRight && nextFacing.rotateYCCW() == inverse)
						|| (next.redirectorLeft && nextFacing.rotateY() == inverse);
			} else {
				renderRight = ConveyorUtil.getSlots(te, inverse.getOpposite()) != null;
			}
		}

		// Check left
		if (redirectorLeft) {
			renderLeft = true;
		} else {
			inverse = direction.rotateYCCW();
			te = worldObj.getTileEntity(pos.offset(inverse));

			if (te instanceof TileEntityConveyor) {
				TileEntityConveyor next = (TileEntityConveyor) te;
				EnumFacing nextFacing = next.getFacingDirection();
				renderLeft = nextFacing.getAxis() != direction.getAxis()
						|| (next.redirectorRight && nextFacing.rotateYCCW() == inverse)
						|| (next.redirectorLeft && nextFacing.rotateY() == inverse);
			} else {
				renderLeft = ConveyorUtil.getSlots(te, inverse.getOpposite()) != null;
			}
		}

		// Check above
		// Render supports if above face is solid or there is a conveyor machine
		// there.
		renderAbove = worldObj.isSideSolid(pos.offset(EnumFacing.UP), EnumFacing.DOWN)
				|| ConveyorUtil.getSlots(worldObj.getTileEntity(pos.offset(EnumFacing.UP)), EnumFacing.DOWN) != null;
		
		updateApplianceCache();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<String> getVisibleParts() {
		List<String> visibleParts = BaseTileEntity.visibleParts.get();

		// Visible parts list is re-used to reduce object creation
		visibleParts.clear();

		visibleParts.add(speedLevel + "_Conveyor_base");
		visibleParts.add(speedLevel + (isEnd ? "_Conveyor_RoundEnd" : "_Conveyor_StraightEnd"));
		visibleParts.add(speedLevel + (isBegin ? "_Conveyor_RoundBegin" : "_Conveyor_StraightBegin"));
		if (renderAbove) visibleParts.add(speedLevel + "_Conveyor_T_Above");
		if (renderBegin) visibleParts.add(speedLevel + "_Conveyor_T_Begin");
		if (renderEnd) visibleParts.add(speedLevel + "_Conveyor_T_End");
		if (renderLeft) visibleParts.add(speedLevel + "_Conveyor_T_Left");
		if (renderRight) visibleParts.add(speedLevel + "_Conveyor_T_Right");
		if (redirectorLeft) visibleParts.add(speedLevel + "_Conveyor_R_Left");
		if (redirectorRight) visibleParts.add(speedLevel + "_Conveyor_R_Right");

		return visibleParts;
	}

	@Override
	public void update() {

		// Call this method to initialize the appliance cache if needed.
		getAppliances();

		/*
		 * Find items laying on the conveyor.
		 */

		if (ConveyorUtil.tryInsertItemsFromWorld(this, worldObj, null, false)) {
			updateState(false, false, false);
		}

		/*
		 * Move items already on the conveyor
		 */

		// process from movement direction backward to keep slot order inside
		// one conveyor,
		// as we depend on the status of the next slot
		int[] slotOrder = ConveyorUtil.getSlotOrderForDirection(direction);
		if (ConveyorUtil.defaultTransition(worldObj, pos, conveyorSlots, slotOrder)) {
			updateState(false, false, false);
		}
	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setInteger("direction", direction.ordinal());
		tag.setInteger("speedLevel", speedLevel);
		tag.setBoolean("redirectorLeft", redirectorLeft);
		tag.setBoolean("redirectorRight", redirectorRight);
		tag.setTag("items", conveyorSlots.serializeNBT());
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		direction = EnumFacing.getFront(tag.getInteger("direction"));
		if (direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
			direction = EnumFacing.NORTH;
		}
		conveyorSlots.rotation = direction;
		speedLevel = tag.getInteger("speedLevel");
		redirectorLeft = tag.getBoolean("redirectorLeft");
		redirectorRight = tag.getBoolean("redirectorRight");
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
		return direction.rotateAround(Axis.Y);
	}

	@Override
	public void setFacingDirection(EnumFacing direction) {
		if (this.direction == direction) {
			// Only update if necessary
			return;
		}
		this.direction = direction;
		if (direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
			this.direction = EnumFacing.NORTH;
		}
		conveyorSlots.rotation = direction;
		updateState(false, true, true);
		worldObj.notifyBlockOfStateChange(pos, blockType);
		if (blockType != null) {
			blockType.onNeighborChange(worldObj, pos, pos);
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
		if (applianceCache == null) {
			updateApplianceCache();
		}
		return applianceCache;
	}

	public void updateApplianceCache() {
		if (speedLevel == 1) {
			applianceCache = ConveyorUtil.getTouchingAppliances(this, worldObj, pos);
		} else {
			applianceCache = null;
		}
	}
	
	@Override
	public IConveyorSlots getSlots() {
		return conveyorSlots;
	}

	/*
	 * IWorldInteractable implementation
	 */
	@Override
	public boolean onBlockActivated(World world, EntityPlayer player, EnumHand hand, boolean hasWrench, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		ItemStack held = player.getHeldItem(hand);
		if (held != null && held.getItem() == TaamMain.itemPart
				&& held.getMetadata() == Taam.ITEM_PART_META.redirector.ordinal()) {
			RedirectorSide redirectorSide = ConveyorUtil.getRedirectorSide(direction, side, hitX, hitY, hitZ, false);
			Log.debug("Tried placing redirector on side: {}", redirectorSide);
			if (redirectorSide == RedirectorSide.Left) {
				if (!redirectorLeft) {
					setRedirectorLeft(true);
					if(!player.capabilities.isCreativeMode) {
						held.stackSize--;
					}
					return true;
				}
				return false;
			} else if (redirectorSide == RedirectorSide.Right) {
				if (!redirectorRight) {
					setRedirectorRight(true);
					if(!player.capabilities.isCreativeMode) {
						held.stackSize--;
					}
					return true;
				}
				return false;
			} else {
				return false;
			}
		}

		//TODO: Cleanup, move to base block with the rest of the hand logic
		
		boolean playerHasWrenchInMainhand = WrenchUtil.playerHasWrenchInHand(player, EnumHand.MAIN_HAND);
		boolean playerHasWrench = playerHasWrenchInMainhand || (player.isSneaking() && WrenchUtil.playerHasWrenchInHand(player, EnumHand.OFF_HAND));
		if (playerHasWrench) {
			boolean playerIsSneaking = player.isSneaking() && playerHasWrenchInMainhand;
			if (playerIsSneaking) {
				RedirectorSide redirectorSide = ConveyorUtil.getRedirectorSide(direction, side, hitX, hitY, hitZ, false);
				Log.debug("Tried disassembling redirector on side: {}", redirectorSide);
				if (redirectorSide == RedirectorSide.Left) {
					if (redirectorLeft) {
						setRedirectorLeft(false);
						InventoryUtils.tryDropToInventory(player,
								new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.redirector.ordinal()), pos);
						return true;
					}
				} else if (redirectorSide == RedirectorSide.Right) {
					if (redirectorRight) {
						setRedirectorRight(false);
						InventoryUtils.tryDropToInventory(player,
								new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.redirector.ordinal()), pos);
						return true;
					}
				}
				Log.debug("Disassembling conveyor.");
				TaamUtil.breakBlockToInventory(player, world, pos, world.getBlockState(pos));
				if (redirectorLeft) {
					InventoryUtils.tryDropToInventory(player, new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.redirector.ordinal()), pos);
				}
				if (redirectorRight) {
					InventoryUtils.tryDropToInventory(player, new ItemStack(TaamMain.itemPart, 1, Taam.ITEM_PART_META.redirector.ordinal()), pos);
				}
				return true;
			}
		}
		if (side != EnumFacing.UP) {
			return false;
		}
		ConveyorUtil.defaultPlayerInteraction(player, conveyorSlots, hitX, hitZ);
		return true;
	}

	@Override
	public boolean onBlockHit(World world, EntityPlayer player, boolean hasWrench) {
		return false;
	}
}
