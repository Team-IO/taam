package net.teamio.taam.conveyors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.teamio.taam.Config;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.util.InventoryUtils;
import net.teamio.taam.util.TaamUtil;

import java.util.ArrayList;
import java.util.List;

public class ConveyorUtil {

	public static final double oneThird = 1 / 3.0;

	public static final RotatedDefinition LANES = new RotatedDefinition(
			// Remember, this definition is inverted being left-to-right not top-down order, so NORTH is left!
			1, 1, 1,
			2, 2, 2,
			3, 3, 3
	);
	public static final RotatedDefinition ROWS = new RotatedDefinition(
			// Remember, this definition is inverted being left-to-right not top-down order, so NORTH is left!
			3, 2, 1,
			3, 2, 1,
			3, 2, 1
	);

	private static boolean tryInsert(TileEntity tileEntity, EntityItem ei) {
		ItemStack entityItemStack = ei.getItem();
		if (InventoryUtils.isEmpty(entityItemStack)) {
			// We are tidy. Clean up "empty" item entities
			ei.setDead();
			return false;
		}

		BlockPos pos = tileEntity.getPos();
		double relativeX = ei.posX - pos.getX();
		double relativeY = ei.posY - pos.getY();
		double relativeZ = ei.posZ - pos.getZ();

		IConveyorSlots conveyorTE = TaamUtil.getCapability(Taam.CAPABILITY_CONVEYOR, tileEntity, EnumFacing.UP);
		if (conveyorTE != null) {
			int slot = getSlotForRelativeCoordinates(relativeX, relativeZ);

			if (slot >= 0 && slot < 9 // wrapped slot == outside x / z
					// Then check vertical position
					&& relativeY > conveyorTE.getInsertMinY() && relativeY < conveyorTE.getInsertMaxY()) {

				int previousStackSize = entityItemStack.getCount();

				// Insert into conveyor at the determined slot
				int added = conveyorTE.insertItemAt(entityItemStack, slot, false);

				// Update item entity
				if (added == previousStackSize) {
					ei.setDead();
					return true;
				} else if (added > 0) {
					entityItemStack = InventoryUtils.setCount(entityItemStack, previousStackSize - added);
					ei.setItem(entityItemStack);
					return true;
				}
			}
		} else {
			// Check if the item is directly on top of the block, with some extra room & buffer
			if (relativeX >= 0 && relativeX < 1 && relativeY >= 0.9 && relativeY < 1.2 && relativeZ >= 0 && relativeZ < 1) {
				// Get item handler
				IItemHandler itemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
				if (itemHandler != null) {
					// Insert item into item handler
					ItemStack notInserted = ItemHandlerHelper.insertItemStacked(itemHandler, entityItemStack, false);

					// Update item entity
					if (InventoryUtils.isEmpty(notInserted)) {
						ei.setDead();
						return true;
					}

					// If the instance is still the same, it was not changed by
					// the itemHandler > nothing inserted.
					if (notInserted != entityItemStack) {
						ei.setItem(notInserted);
						return true;
					}
				}
			}
		}
		// Finally, nothing was added if we arrive here
		return false;
	}

	/**
	 * Tries to insert item entities from the world into an entity. Respects the
	 * conveyor system and item handlers.
	 *
	 * @param tileEntity       A tile entity that should expose conveyor or item handler capabilities,
	 *                         otherwise this method is an immense waste of processing power
	 * @param world            The world in which to look for items
	 * @param bounds           Optionally give an AABB Instance to speed up the search &
	 *                         extend to unloaded chunks. Else only loaded entities are
	 *                         respected.
	 * @param stopAtFirstMatch Stop processing items after the first one was added?
	 */
	public static boolean tryInsertItemsFromWorld(TileEntity tileEntity, World world, AxisAlignedBB bounds, boolean stopAtFirstMatch) {
		if (world.isRemote) {
			return false;
		}
		boolean didAdd = false;
		List<?> entities = world.loadedEntityList;

		// if Bounding Box is Supplied, use that.
		if (bounds != null) {
			entities = world.getEntitiesWithinAABB(EntityItem.class, bounds);
		}
		for (Object entity : entities) {
			Entity ent = (Entity) entity;

			if (ent instanceof EntityItem) {
				EntityItem ei = (EntityItem) ent;

				didAdd = tryInsert(tileEntity, ei) | didAdd;

				if (stopAtFirstMatch && didAdd) {
					break;
				}
			}
		}
		return didAdd;
	}

	public static int getNextSlot(int slot, EnumFacing dir) {
		int nextslot = getNextSlotUnwrapped(slot, dir);
		if (nextslot < 0) {
			nextslot += 9;
		} else if (nextslot > 8) {
			nextslot -= 9;
		}
		return nextslot;
	}

	public static int getNextSlotUnwrapped(int slot, EnumFacing dir) {
		// X-Offset skips whole rows
		int frontOffsetX = dir.getXOffset();
		if (frontOffsetX != 0) {
			slot += frontOffsetX * 3;
		}
		// Z-Offset translates only regular,
		// but certain ones skip to the next row
		int frontOffsetZ = dir.getZOffset();
		if (frontOffsetZ != 0) {
			int col = slot % 3;
			col += frontOffsetZ;
			if (col < 0) {
				slot -= 7;
			} else if (col > 2) {
				slot += 7;
			} else {
				slot += frontOffsetZ;
			}
		}
		int frontOffsetY = dir.getYOffset();
		if (frontOffsetY != 0) {
			// Offset by 9 is the same slot, but marks it as wrapped
			slot += 9 * frontOffsetY;
		}
		return slot;
	}

	public static EnumFacing getHighspeedTransition(int slot, EnumFacing direction) {
		EnumFacing transition = direction;
		switch (direction) {
			default:
				// No transition, direction unchanged
				break;
			case NORTH:
				transition = highSpeedTransition[0][slot];
				break;
			case EAST:
				transition = highSpeedTransition[1][slot];
				break;
			case SOUTH:
				transition = highSpeedTransition[2][slot];
				break;
			case WEST:
				transition = highSpeedTransition[3][slot];
				break;
		}
		return transition;
	}

	public static int getSlot(EnumFacing dir) {
		if (dir == null || dir == EnumFacing.DOWN) {
			// Conveyors are only accessible from top/sides!
			return -1;
		} else if (dir == EnumFacing.UP) {
			// Center
			return 4;
		} else {
			// From that center, we go one off
			return getNextSlot(4, dir);
		}
	}

	public static class RotatedDefinition {
		private int[][] rotated;

		public RotatedDefinition(int one, int two, int three, int four, int five, int six, int seven,
		                         int eight, int nine) {
			int[] unrotated = new int[]{one, two, three, four, five, six, seven, eight, nine};
			calculateRotations(unrotated);
		}

		private void calculateRotations(int[] unrotated) {
			rotated = new int[4][];
			// North
			rotated[2] = unrotated;
			// West
			rotated[1] = rotate(unrotated);
			// South
			rotated[0] = rotate(rotated[1]);
			// East
			rotated[3] = rotate(rotated[0]);
		}

		public int get(int slot, EnumFacing rotation) {
			int horizontalIndex = rotation.getHorizontalIndex();
			if (horizontalIndex < 0) {
				horizontalIndex = 0;
			}
			// Horizontal Index: S-W-N-E
			int[] slots = rotated[horizontalIndex];

			return slots[MathHelper.clamp(slot, 0, 8)];
		}
	}

	/**
	 * Rotate a specification of slot-related data.
	 *
	 * @param source An array with 9 integers, e.g. a slot order array
	 * @return A new array with the 9 integers rotated by 90 degrees counter clockwise (North->West)
	 */
	public static int[] rotate(int[] source) {
		return new int[]{
				source[6], source[3], source[0],
				source[7], source[4], source[1],
				source[8], source[5], source[2]
		};
	}


	public static double getItemPositionX(int slot) {
		int row = slot / 3;
		double x = row + 0.5;
		return x * oneThird;
	}

	public static double getItemPositionX(int slot, double progress, EnumFacing dir) {
		double x = getItemPositionX(slot);
		x += dir.getXOffset() * progress * oneThird;
		return x;
	}

	public static double getItemPositionZ(int slot) {
		double z = slot % 3 + 0.5;
		return z * oneThird;
	}

	public static double getItemPositionZ(int slot, double progress, EnumFacing dir) {
		double z = getItemPositionZ(slot);
		z += dir.getZOffset() * progress * oneThird;
		return z;
	}

	public static int getSlotForRelativeCoordinates(double x, double z) {
		if (x > 1 || x < 0 || z > 1 || z < 0) {
			return -1;
		}
		int row = (int) Math.floor(x * 3f);
		int col = (int) Math.floor(z * 3f);
		return row * 3 + col;
	}

	private static final int[][] slotOrders;
	private static final EnumFacing[][] highSpeedTransition;

	static {
		slotOrders = new int[2][];
		slotOrders[0] = new int[]{
				//North -Z to +Z (Processes line by line)
				//West -X to +X (Processes each line in "parallel")
				0, 1, 2,
				3, 4, 5,
				6, 7, 8
		};
		slotOrders[1] = new int[]{
				//South +Z to -Z (Processes line by line)
				//East +X to -X (Processes each line in "parallel")
				6, 7, 8,
				3, 4, 5,
				0, 1, 2
		};

		/*
		 * Mind Map:
		 *        NORTH
		 *      0	3	6
		 * WEST 1	4	7  EAST
		 *      2	5	8
		 *        SOUTH
		 */
		highSpeedTransition = new EnumFacing[4][];
		highSpeedTransition[0] = new EnumFacing[]{
				EnumFacing.EAST, EnumFacing.EAST, EnumFacing.EAST,
				EnumFacing.NORTH, EnumFacing.NORTH, EnumFacing.NORTH,
				EnumFacing.WEST, EnumFacing.WEST, EnumFacing.WEST
		};
		highSpeedTransition[1] = new EnumFacing[]{
				EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.NORTH,
				EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.NORTH,
				EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.NORTH
		};
		highSpeedTransition[2] = new EnumFacing[]{
				EnumFacing.EAST, EnumFacing.EAST, EnumFacing.EAST,
				EnumFacing.SOUTH, EnumFacing.SOUTH, EnumFacing.SOUTH,
				EnumFacing.WEST, EnumFacing.WEST, EnumFacing.WEST
		};
		highSpeedTransition[3] = new EnumFacing[]{
				EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.NORTH,
				EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.NORTH,
				EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.NORTH
		};
	}

	/**
	 * Returns the ideal order to process slots on a conveyor. (Always from
	 * front to back, but lanes are not in a guaranteed order!)
	 *
	 * @param dir Direction of the conveyor movement
	 * @return An array containing slot indices
	 */
	public static int[] getSlotOrderForDirection(EnumFacing dir) {
		switch (dir) {
			default:
			case NORTH:
			case WEST:
				return slotOrders[0];
			case SOUTH:
			case EAST:
				return slotOrders[1];
		}
	}


	public static void dropItems(World world, BlockPos pos, IConveyorSlots slots, boolean withVelocity) {
		for (int index = 0; index < 9; index++) {
			ConveyorUtil.dropItem(world, pos, slots, index, withVelocity);
		}
	}

	/**
	 * Drops the item in the passed slot, exactly where it is rendered now.
	 *
	 * @param slot The slot to be dropped.
	 */
	public static void dropItem(World world, BlockPos pos, IConveyorSlots slots, int slot, boolean withVelocity) {
		ItemWrapper slotObject = slots.getSlot(slot);

		if (!world.isRemote) {
			float speedsteps = slots.getSpeedsteps();
			EnumFacing direction = slots.getNextSlot(slot);
			float progress = slotObject.movementProgress / speedsteps;

			double posX = pos.getX() + getItemPositionX(slot, progress, direction);
			double posY = pos.getY() + 0.5f;
			double posZ = pos.getZ() + getItemPositionZ(slot, progress, direction);

			if (!InventoryUtils.isEmpty(slotObject.itemStack)) {
				EntityItem item = new EntityItem(world, posX, posY, posZ, slotObject.itemStack);
				if (withVelocity) {
					float speed = (Byte.MAX_VALUE - speedsteps) * 0.0019f;
					item.motionX = direction.getXOffset() * speed;
					item.motionY = direction.getYOffset() * speed;
					item.motionZ = direction.getZOffset() * speed;
				} else {
					item.motionX = 0;
					item.motionY = 0;
					item.motionZ = 0;
				}
				world.spawnEntity(item);
			}
		}

		slotObject.itemStack = ItemStack.EMPTY;
	}

	public static boolean transferSlot(IConveyorSlots tileEntity, int slot, IConveyorSlots nextBlock, int nextSlot) {
		ItemWrapper slotObject = tileEntity.getSlot(slot);

		int transferred = nextBlock.insertItemAt(slotObject.itemStack.copy(), nextSlot, false);
		if (transferred > 0) {
			slotObject.itemStack = InventoryUtils.adjustCount(slotObject.itemStack, -transferred);
			// Something moved
			return true;
		}
		// Nothing moved
		return false;
	}

	public static boolean transferSlot(IConveyorSlots tileEntity, int slot, int nextSlot) {

		ItemWrapper slotObject = tileEntity.getSlot(slot);
		ItemWrapper nextSlotObject = tileEntity.getSlot(nextSlot);
		if (nextSlotObject.isEmpty()) {
			nextSlotObject.itemStack = slotObject.itemStack;

			slotObject.itemStack = ItemStack.EMPTY;

			// Something moved
			return true;
		}
		// Nothing moved
		return false;
	}

	public static int insertItemAt(IConveyorSlots tileEntity, ItemStack item, int slot, boolean simulate) {
		ItemWrapper slotObject = tileEntity.getSlot(slot);
		if (InventoryUtils.isEmpty(slotObject.itemStack)) {
			if (!simulate) {
				slotObject.itemStack = item.copy();
				slotObject.unblock();
				slotObject.resetMovement();
			}
			return item.getCount();
		} else if (InventoryUtils.canStack(slotObject.itemStack, item)) {
			int availableSpace = slotObject.itemStack.getMaxStackSize() - slotObject.itemStack.getCount();
			if (availableSpace > 0) {
				availableSpace = Math.min(availableSpace, item.getCount());
				if (!simulate) {
					slotObject.itemStack = InventoryUtils.adjustCount(slotObject.itemStack, availableSpace);
				}
				return availableSpace;
			}
			return 0;
		} else {
			return 0;
		}
	}

	public static IConveyorSlots getSlots(TileEntity tileEntity, EnumFacing side) {
		if (tileEntity == null) {
			return null;
		}
		IConveyorSlots slots = TaamUtil.getCapability(Taam.CAPABILITY_CONVEYOR, tileEntity, side);
		if (slots != null) {
			return slots;
		}
		if (tileEntity instanceof IConveyorSlots) {
			return (IConveyorSlots) tileEntity;
		}
		return null;
	}

	/**
	 * Runs the default transition logic for the items on a conveyor entity.
	 * <p>
	 * Respects the supplied slot order, processes items if tileEntity
	 * instanceof {@link IConveyorApplianceHost}.
	 *
	 * @param world      The world we are working in
	 * @param pos        World position of the machine containing the processed slots
	 * @param tileEntity The conveyor slots reference that is to be updated
	 * @param slotOrder  The order used when working through the slots.
	 * @return true if the state of any item changed (TIleEntity should be
	 * marked dirty).
	 */
	public static boolean defaultTransition(World world, BlockPos pos, IConveyorSlots tileEntity, IConveyorApplianceHost applianceHost, int[] slotOrder) {
		/*
		 * Fetch info on appliances
		 */
		List<IConveyorAppliance> appliances = null;
		if (applianceHost != null) {
			appliances = applianceHost.getAppliances();
		}

		// Tracks if we changed the content of the machine, thus marking the machine dirty
		boolean contentUpdated = false;

		/*
		 * Process each slot individually, using the predefined slot order
		 */
		for (int slot : slotOrder) {

			ItemWrapper wrapper = tileEntity.getSlot(slot);

			if (wrapper.isEmpty()) {
				continue;
			}

			// Unblock Wrapper to prevent them from staying blocked if we remove
			// an appliance
			wrapper.unblock();

			/*
			 * Let the appliances process the current slot.
			 */
			if (appliances != null && appliances.size() > 0) {

				// Let each appliance process the item
				for (IConveyorAppliance appliance : appliances) {
					if (appliance.processItem(applianceHost, slot, wrapper)) {
						contentUpdated = true;
					}
				}
			}

			if (wrapper.isBlocked()) {
				continue;
			}

			/*
			 * Move the contents to the next slot
			 */

			byte speedsteps = tileEntity.getSpeedsteps();

			int nextSlotProgress = 0;

			IConveyorSlots nextBlock = null;

			/*
			 * Get next slot
			 */

			EnumFacing nextSlotDir = tileEntity.getNextSlot(slot);
			int nextSlot = getNextSlotUnwrapped(slot, nextSlotDir);
			boolean nextSlotFree;
			boolean nextSlotMovable;

			/*
			 * Check if we need to wrap & stept to next block
			 */

			boolean slotWrapped;
			if (nextSlot < 0) {
				nextSlot += 9;
				slotWrapped = true;
			} else if (nextSlot > 8) {
				nextSlot -= 9;
				slotWrapped = true;
			} else {
				slotWrapped = false;
			}

			// Check the condition of the next slot
			if (slotWrapped) {
				// Next block, potentially a conveyor-aware block.
				BlockPos nextBlockPos = pos.offset(nextSlotDir);

				TileEntity te = world.getTileEntity(nextBlockPos);

				nextBlock = getSlots(te, nextSlotDir.getOpposite());

				if (nextBlock == null) {
					// Drop it
					nextSlotFree = true;
					nextSlotMovable = true;
				} else {
					// Move it to next block
					ItemWrapper nextWrapper = nextBlock.getSlot(nextSlot);
					nextSlotFree = nextWrapper.isEmpty();
					boolean wrappedIsSameDirection = nextBlock.getNextSlot(nextSlot) == nextSlotDir;
					nextSlotMovable = nextBlock.canSlotMove(nextSlot) && (wrappedIsSameDirection || nextSlotDir.getAxis() == Axis.Y);
					nextSlotProgress = nextWrapper.movementProgress;
					byte nextSpeedSteps = nextBlock.getSpeedsteps();
					if (nextSpeedSteps != speedsteps) {
						if (nextSpeedSteps == 0) {
							nextSlotProgress = 0;
						} else {
							nextSlotProgress = Math.round(nextSlotProgress / (float) nextSpeedSteps * speedsteps);
						}
					}
				}
			} else {
				ItemWrapper nextWrapper = tileEntity.getSlot(nextSlot);
				nextSlotFree = nextWrapper.isEmpty();
				nextSlotMovable = !nextWrapper.isBlocked();
				nextSlotProgress = nextWrapper.movementProgress;
			}

			// Check transition to next slot
			if (nextSlotFree || nextSlotMovable) {
				if (wrapper.movementProgress == speedsteps && nextSlotFree) {
					if (slotWrapped && (nextBlock == null || !nextBlock.isSlotAvailable(nextSlot))) {
						// No next block, drop it.
						dropItem(world, pos, tileEntity, slot, true);
						contentUpdated = true;
					} else {
						boolean somethingTransferred;
						if (slotWrapped) {
							somethingTransferred = transferSlot(tileEntity, slot, nextBlock, nextSlot);
						} else {
							somethingTransferred = transferSlot(tileEntity, slot, nextSlot);
						}
						if (somethingTransferred) {
							contentUpdated = true;
						} else if (!wrapper.isEmpty()) {
							// Nothing did transfer, or something is left (itemStack not null)
							nextSlotFree = false;
							nextSlotMovable = false;
						}
					}
				}
			}
			/*
			 * If we can progress (next slot is empty or far enough away), step forward
			 */
			if (nextSlotFree || nextSlotMovable && wrapper.movementProgress < nextSlotProgress) {
				wrapper.movementProgress++;
				if (wrapper.movementProgress > speedsteps) {
					wrapper.movementProgress = 0;
				}
				if (world.isRemote) {
					wrapper.setStuck(false);
				}
			} else {
				if (world.isRemote) {
					wrapper.setStuck(true);
				}
			}
		}
		return contentUpdated;
	}

	public static void defaultPlayerInteraction(EntityPlayer player, IConveyorSlots tileEntity, float hitX, float hitZ) {
		int clickedSlot = getSlotForRelativeCoordinates(hitX, hitZ);
		int playerSlot = player.inventory.currentItem;
		ItemStack playerStack = player.inventory.getCurrentItem();
		if (InventoryUtils.isEmpty(playerStack)) {
			// Take from Conveyor
			ItemStack removed = tileEntity.removeItemAt(clickedSlot, player.inventory.getInventoryStackLimit(), false);
			player.inventory.setInventorySlotContents(playerSlot, InventoryUtils.guardAgainstNull(removed));
		} else if (!isBlacklistedForConveyor(playerStack)) {
			// Put on conveyor
			int inserted = tileEntity.insertItemAt(playerStack, clickedSlot, false);
			playerStack = InventoryUtils.adjustCount(playerStack, -inserted);
			player.inventory.setInventorySlotContents(playerSlot, playerStack);
		}
	}

	/**
	 * Checks the configured blacklist of items that cannot be put on a conveyor by right clicking.
	 *
	 * @param stack The item stack a player wants to put on a conveyor
	 * @return true, if the item is blacklisted, or the specific meta/NBT is blacklisted.
	 */
	public static boolean isBlacklistedForConveyor(ItemStack stack) {
		if (InventoryUtils.isEmpty(stack)) return false;

		// Check for name-only (taam:wrench)
		ResourceLocation name = Item.REGISTRY.getNameForObject(stack.getItem());
		Log.debug("Checking for item {}", name);
		if (name == null) {
			return false;
		}
		if (Config.pl_conveyor_rightclick_blacklist.contains(name.toString())) {
			return true;
		}

		// Early bail to avoid using a string builder
		if (!stack.getHasSubtypes() && !stack.hasTagCompound()) return false;

		// Check for sub-type (taam:productionline@2)
		StringBuilder subType = new StringBuilder(name.toString());
		if (stack.getHasSubtypes()) {
			subType.append('@').append(stack.getItemDamage());
			Log.debug("Checking for item {}", subType);
			if (Config.pl_conveyor_rightclick_blacklist.contains(subType.toString())) {
				return true;
			}
		}

		// Check for tag compound (potion@0#{Potion:"minecraft:healing"})
		if (stack.hasTagCompound()) {
			subType.append('#').append(stack.getTagCompound());
			Log.debug("Checking for item {}", subType);
			if (Config.pl_conveyor_rightclick_blacklist.contains(subType.toString())) {
				return true;
			}
		}

		return false;
	}

	public static List<IConveyorAppliance> getTouchingAppliances(IBlockAccess world, BlockPos pos) {
		List<IConveyorAppliance> appliances = new ArrayList<>();

		for (EnumFacing direction : EnumFacing.VALUES) {
			TileEntity te = world.getTileEntity(pos.offset(direction));
			if (te instanceof IConveyorAppliance) {
				IConveyorAppliance appliance = (IConveyorAppliance) te;
				if (appliance.getFacingDirection() == direction.getOpposite()) {
					appliances.add(appliance);
				}
			}
		}

		return appliances;
	}

	public static RedirectorSide getRedirectorSide(EnumFacing dir, EnumFacing hitSide, float hitX, float hitZ, boolean topOnly) {
		EnumFacing sideToConsider = hitSide;

		if (hitSide == EnumFacing.UP) {
			if (dir.getAxis() == Axis.Z) {
				// We look in Z direction, need to check X
				sideToConsider = hitX > 0.5 ? EnumFacing.EAST : EnumFacing.WEST;
			} else {
				// We look in X direction, need to check Z
				sideToConsider = hitZ > 0.5 ? EnumFacing.SOUTH : EnumFacing.NORTH;
			}
		} else if (topOnly) {
			return RedirectorSide.None;
		}

		if (sideToConsider == dir.rotateY()) {
			return topOnly ? RedirectorSide.None : RedirectorSide.Right;
		} else if (sideToConsider == dir.rotateYCCW()) {
			return topOnly ? RedirectorSide.None : RedirectorSide.Left;
		} else {
			return RedirectorSide.None;
		}
	}
}
