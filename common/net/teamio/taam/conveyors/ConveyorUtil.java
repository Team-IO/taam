package net.teamio.taam.conveyors;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.teamio.taam.Config;
import net.teamio.taam.MultipartHandler;
import net.teamio.taam.Taam;

public class ConveyorUtil {

	public static final double oneThird = 1 / 3.0;

	private static boolean tryInsert(TileEntity tileEntity, EntityItem ei) {
		ItemStack entityItemStack = ei.getEntityItem();
		if (entityItemStack == null || entityItemStack.stackSize == 0 || entityItemStack.getItem() == null) {
			// We are tidy. Clean up "empty" item entities
			ei.setDead();
			return false;
		}

		BlockPos pos = tileEntity.getPos();
		double relativeX = ei.posX - pos.getX();
		double relativeY = ei.posY - pos.getY();
		double relativeZ = ei.posZ - pos.getZ();

		IConveyorSlots conveyorTE = tileEntity.getCapability(Taam.CAPABILITY_CONVEYOR, EnumFacing.UP);
		if (conveyorTE != null) {
			int slot = getSlotForRelativeCoordinates(relativeX, relativeZ);

			if (slot >= 0 && slot < 9 // wrapped slot == outside x / z
			// Then check vertical position
					&& relativeY > conveyorTE.getInsertMinY() && relativeY < conveyorTE.getInsertMaxY()) {

				int previousStackSize = entityItemStack.stackSize;

				// Insert into conveyor at the determined slot
				int added = conveyorTE.insertItemAt(entityItemStack, slot, false);

				// Update item entity
				if (added == previousStackSize) {
					ei.setDead();
					return true;
				} else if (added > 0) {
					entityItemStack.stackSize = previousStackSize - added;
					ei.setEntityItemStack(entityItemStack);
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
					if (notInserted == null) {
						ei.setDead();
						return true;
					}

					// If the instance is still the same, it was not changed by
					// the itemHandler > nothing inserted.
					if (notInserted != entityItemStack) {
						ei.setEntityItemStack(notInserted);
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
	 * conveyor system.
	 *
	 * @param tileEntity
	 * @param world
	 * @param bounds
	 *            Optionally give an AABB Instance to speed up the search &
	 *            extend to unloaded chunks. Else only loaded entities are
	 *            respected.
	 * @param stopAtFirstMatch
	 *            Stop processing items after the first one was added?
	 *
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
		for (int i = 0; i < entities.size(); i++) {
			Entity ent = (Entity) entities.get(i);

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
		slot = getNextSlotUnwrapped(slot, dir);
		if (slot < 0) {
			slot += 9;
		} else if (slot > 8) {
			slot -= 9;
		}
		return slot;
	}

	public static int getNextSlotUnwrapped(int slot, EnumFacing dir) {
		// X-Offset skips whole rows
		int frontOffsetX = dir.getFrontOffsetX();
		if (frontOffsetX != 0) {
			slot += frontOffsetX * 3;
		}
		// Z-Offset translates only regular,
		// but certain ones skip to the next row
		int frontOffsetZ = dir.getFrontOffsetZ();
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
		int frontOffsetY = dir.getFrontOffsetY();
		if(frontOffsetY != 0) {
			// Offset by 9 is the same slot, but marks it as wrapped
			slot += 9 * frontOffsetY;
		}
		return slot;
	}

	public static EnumFacing getHighspeedTransition(int slot, EnumFacing direction) {
		EnumFacing transition = direction;
		switch (direction) {
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
		default:
			transition = direction;
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
			int[] unrotated = new int[] { one, two, three, four, five, six, seven, eight, nine };
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
			// Horizontal Index: S-W-N-E
			int[] slots = rotated[rotation.getHorizontalIndex()];

			slot = MathHelper.clamp_int(slot, 0, 8);

			return slots[slot];
		}
	}

	/**
	 * Rotate a specification of slot-related data.
	 * @param source
	 * @return
	 */
	public static int[] rotate(int[] source) {
		return new int[] {
				source[6], source[3], source[0],
				source[7], source[4], source[1],
				source[8], source[5], source[2]
		};
	}
	
	public static RotatedDefinition LANES = new RotatedDefinition(
			// Remember, this definition is inverted being left-to-right not top-down order, so NORTH is left!
			1, 1, 1,
			2, 2, 2,
			3, 3, 3
			);
	public static RotatedDefinition ROWS = new RotatedDefinition(
			// Remember, this definition is inverted being left-to-right not top-down order, so NORTH is left!
			3, 2, 1,
			3, 2, 1,
			3, 2, 1
			);
	

	public static double getItemPositionX(int slot) {
		double x = Math.floor(slot / 3) + 0.5;
		return x * oneThird;
	}

	public static double getItemPositionX(int slot, double progress, EnumFacing dir) {
		double x = getItemPositionX(slot);
		x += dir.getFrontOffsetX() * progress * oneThird;
		return x;
	}

	public static double getItemPositionZ(int slot) {
		double z = slot % 3 + 0.5;
		return z * oneThird;
	}

	public static double getItemPositionZ(int slot, double progress, EnumFacing dir) {
		double z = getItemPositionZ(slot);
		z += dir.getFrontOffsetZ() * progress * oneThird;
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
		slotOrders[0] = new int[] {
				//North -Z to +Z (Processes line by line)
				//West -X to +X (Processes each line in "parallel")
				0, 1, 2,
				3, 4, 5,
				6, 7, 8
		};
		slotOrders[1] = new int[] {
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
		highSpeedTransition[0] = new EnumFacing[] {
				EnumFacing.EAST, EnumFacing.EAST, EnumFacing.EAST,
				EnumFacing.NORTH, EnumFacing.NORTH, EnumFacing.NORTH,
				EnumFacing.WEST, EnumFacing.WEST, EnumFacing.WEST
		};
		highSpeedTransition[1] = new EnumFacing[] {
				EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.NORTH,
				EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.NORTH,
				EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.NORTH
		};
		highSpeedTransition[2] = new EnumFacing[] {
				EnumFacing.EAST, EnumFacing.EAST, EnumFacing.EAST,
				EnumFacing.SOUTH, EnumFacing.SOUTH, EnumFacing.SOUTH,
				EnumFacing.WEST, EnumFacing.WEST, EnumFacing.WEST
		};
		highSpeedTransition[3] = new EnumFacing[] {
				EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.NORTH,
				EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.NORTH,
				EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.NORTH
		};
	}

	/**
	 * Returns the ideal order to process slots on a conveyor. (Always from
	 * front to back, but lanes are not in a guaranteed order!)
	 *
	 * @param dir
	 * @return
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
	 * @param slot
	 *            The slot to be dropped.
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

			if (slotObject.itemStack != null) {
				EntityItem item = new EntityItem(world, posX, posY, posZ, slotObject.itemStack);
				if (withVelocity) {
					float speed = (Byte.MAX_VALUE - speedsteps) * 0.0019f;
					item.motionX = direction.getFrontOffsetX() * speed;
					item.motionY = direction.getFrontOffsetY() * speed;
					item.motionZ = direction.getFrontOffsetZ() * speed;
				} else {
					item.motionX = 0;
					item.motionY = 0;
					item.motionZ = 0;
				}
				world.spawnEntityInWorld(item);
			}
		}

		slotObject.itemStack = null;
	}

	public static boolean transferSlot(IConveyorSlots tileEntity, int slot, IConveyorSlots nextBlock, int nextSlot) {
		ItemWrapper slotObject = tileEntity.getSlot(slot);

		int transferred = nextBlock.insertItemAt(slotObject.itemStack.copy(), nextSlot, false);
		if (transferred > 0) {
			slotObject.itemStack.stackSize -= transferred;
			if (slotObject.itemStack.stackSize <= 0) {
				// Stack moved completely
				slotObject.itemStack = null;
			}
			// Something moved
			return true;
		}
		// Nothing moved
		return false;
	}

	public static boolean transferSlot(IConveyorSlots tileEntity, int slot, int nextSlot) {

		ItemWrapper slotObject = tileEntity.getSlot(slot);
		ItemWrapper nextSlotObject = tileEntity.getSlot(nextSlot);
		if (nextSlotObject.itemStack == null) {
			nextSlotObject.itemStack = slotObject.itemStack;

			slotObject.itemStack = null;

			// Something moved
			return true;
		}
		// Nothing moved
		return false;
	}

	public static int insertItemAt(IConveyorSlots tileEntity, ItemStack item, int slot, boolean simulate) {
		ItemWrapper slotObject = tileEntity.getSlot(slot);
		if (slotObject.itemStack == null) {
			if (!simulate) {
				slotObject.itemStack = item.copy();
				slotObject.unblock();
				slotObject.resetMovement();
			}
			return item.stackSize;
		} else if (slotObject.itemStack.isItemEqual(item)) {
			int availableSpace = slotObject.itemStack.getMaxStackSize() - slotObject.itemStack.stackSize;
			if (availableSpace > 0) {
				availableSpace = Math.min(availableSpace, item.stackSize);
				if (!simulate) {
					slotObject.itemStack.stackSize += availableSpace;
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
		if (tileEntity instanceof IConveyorSlots) {
			return (IConveyorSlots) tileEntity;
		}
		IConveyorSlots candidate = tileEntity.getCapability(Taam.CAPABILITY_CONVEYOR, side);
		if (candidate == null && Config.multipart_present) {
			candidate = MultipartHandler.getCapabilityForCenter(Taam.CAPABILITY_CONVEYOR, tileEntity.getWorld(), tileEntity.getPos(), side);
		}
		return candidate;
	}

	/**
	 * Runs the default transition logic for the items on a conveyor entity.
	 *
	 * Respects the supplied slot order, processes items if tileEntity
	 * instanceof {@link IConveyorApplianceHost}.
	 *
	 * @param world
	 * @param pos
	 * @param tileEntity
	 * @param slotOrder
	 *            The order used when working through the slots.
	 * @return true if the state of any item changed (TIleEntity should be
	 *         marked dirty).
	 */
	public static boolean defaultTransition(World world, BlockPos pos, IConveyorSlots tileEntity, int[] slotOrder) {
		/*
		 * Fetch info on appliances
		 */
		List<IConveyorAppliance> appliances = null;
		IConveyorApplianceHost applianceHost = null;
		if (tileEntity instanceof IConveyorApplianceHost) {
			applianceHost = (IConveyorApplianceHost) tileEntity;
			appliances = applianceHost.getAppliances();
		}

		/**
		 * Tracks if the tileEntity state needs to be updated
		 */
		boolean needsUpdate = false;
		/**
		 * Tracks if we need a world update (send to client)
		 */
		boolean needsWorldUpdate = false;
		/*
		 * Process each slot individually, using the predefined slot order
		 */
		for (int index = 0; index < slotOrder.length; index++) {

			int slot = slotOrder[index];

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
			if (appliances != null) {

				// Let each appliance process the item
				for (IConveyorAppliance appliance : appliances) {
					if (appliance.processItem(applianceHost, slot, wrapper)) {
						needsWorldUpdate = true;
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

			boolean slotWrapped = false;
			boolean nextSlotFree = false;
			boolean nextSlotMovable = false;
			int nextSlotProgress = 0;
			boolean wrappedIsSameDirection = true;

			IConveyorSlots nextBlock = null;

			/*
			 * Get next slot
			 */

			EnumFacing nextSlotDir = tileEntity.getNextSlot(slot);
			int nextSlot = getNextSlotUnwrapped(slot, nextSlotDir);

			/*
			 * Check if we need to wrap & stept to next block
			 */

			if (nextSlot < 0) {
				nextSlot += 9;
				slotWrapped = true;
			} else if (nextSlot > 8) {
				nextSlot -= 9;
				slotWrapped = true;
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
					wrappedIsSameDirection = nextBlock.getNextSlot(nextSlot) == nextSlotDir;
					nextSlotMovable = nextBlock.canSlotMove(nextSlot) && wrappedIsSameDirection;
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
				nextSlotFree = nextWrapper.itemStack == null;
				nextSlotMovable = !nextWrapper.isBlocked();
				nextSlotProgress = nextWrapper.movementProgress;
				wrappedIsSameDirection = tileEntity.getNextSlot(nextSlot) == nextSlotDir;
			}

			// Check transition to next slot
			if (nextSlotFree || nextSlotMovable) {
				if (wrapper.movementProgress == speedsteps && nextSlotFree) {
					if (slotWrapped && (nextBlock == null || !nextBlock.isSlotAvailable(nextSlot))) {
						// No next block, drop it.
						dropItem(world, pos, tileEntity, slot, true);
						needsUpdate = true;
					} else {
						boolean somethingTransferred;
						if (slotWrapped) {
							somethingTransferred = transferSlot(tileEntity, slot, nextBlock, nextSlot);
						} else {
							somethingTransferred = transferSlot(tileEntity, slot, nextSlot);
						}
						if (!somethingTransferred || wrapper.itemStack != null) {
							// Nothing did transfer, or something is left (itemStack not null)
							nextSlotFree = false;
							nextSlotMovable = false;
						}
						needsUpdate = somethingTransferred;
					}
				}
			}
			/*
			 * If we can progress (next slot is empty or far enough away), step
			 * forward
			 */
			if (nextSlotFree
					|| nextSlotMovable && wrappedIsSameDirection && wrapper.movementProgress < nextSlotProgress) {
				wrapper.movementProgress++;
				if (wrapper.movementProgress > speedsteps) {
					wrapper.movementProgress = 0;
				}
				if(world.isRemote) {
					wrapper.setStuck(false);
				}
			} else {
				if(world.isRemote) {
					wrapper.setStuck(true);
				}
			}
		}
		// TODO: needsUpdate -> markDirty ??
		return needsWorldUpdate;
	}

	public static void defaultPlayerInteraction(EntityPlayer player, IConveyorSlots tileEntity, float hitX, float hitZ) {
		int clickedSlot = getSlotForRelativeCoordinates(hitX, hitZ);
		int playerSlot = player.inventory.currentItem;
		ItemStack playerStack = player.inventory.getCurrentItem();
		if (playerStack == null) {
			// Take from Conveyor
			ItemStack removed = tileEntity.removeItemAt(clickedSlot, player.inventory.getInventoryStackLimit(), false);
			player.inventory.setInventorySlotContents(playerSlot, removed);
		} else {
			// Put on conveyor
			int inserted = tileEntity.insertItemAt(playerStack, clickedSlot, false);
			if (inserted == playerStack.stackSize) {
				player.inventory.setInventorySlotContents(playerSlot, null);
			} else {
				playerStack.stackSize -= inserted;
				player.inventory.setInventorySlotContents(playerSlot, playerStack);
			}
		}
	}

	public static List<IConveyorAppliance> getTouchingAppliances(IConveyorApplianceHost tileEntityConveyor, IBlockAccess world, BlockPos pos) {
		List<IConveyorAppliance> appliances = new ArrayList<IConveyorAppliance>();

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

	public static RedirectorSide getRedirectorSide(EnumFacing direction, EnumFacing hitSide,
			float hitX, float hitY, float hitZ, boolean topOnly) {
		EnumFacing sideToConsider = hitSide;
	
		if (hitSide == EnumFacing.UP) {
			if (direction.getAxis() == Axis.Z) {
				// We look in Z direction, need to check X
				sideToConsider = hitX > 0.5 ? EnumFacing.EAST : EnumFacing.WEST;
			} else {
				// We look in X direction, need to check Z
				sideToConsider = hitZ > 0.5 ? EnumFacing.SOUTH : EnumFacing.NORTH;
			}
		} else if (topOnly) {
			return RedirectorSide.None;
		}
	
		if (sideToConsider == direction.rotateY()) {
			return topOnly ? RedirectorSide.None : RedirectorSide.Right;
		} else if (sideToConsider == direction.rotateYCCW()) {
			return topOnly ? RedirectorSide.None : RedirectorSide.Left;
		} else {
			return RedirectorSide.None;
		}
	}
}
