package net.teamio.taam.conveyors;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.teamio.taam.Taam;
import net.teamio.taam.conveyors.api.IConveyorAppliance;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;
import net.teamio.taam.conveyors.api.IConveyorSlots;
import net.teamio.taam.util.inv.InventoryRange;
import net.teamio.taam.util.inv.InventoryUtils;

public class ConveyorUtil {
	
	private static boolean tryInsert(TileEntity tileEntity, EntityItem ei) {
		ItemStack entityItemStack = ei.getEntityItem();
		if(entityItemStack == null || entityItemStack.getItem() == null) {
			return false;
		}
		
		int previousStackSize = entityItemStack.stackSize;
		int added = 0;
		
		BlockPos pos = tileEntity.getPos();
		double relativeX = ei.posX - pos.getX();
		double relativeY = ei.posY - pos.getY();
		double relativeZ = ei.posZ - pos.getZ();
		
		if(tileEntity instanceof IConveyorSlots) {
			IConveyorSlots conveyorTE = (IConveyorSlots) tileEntity;
			
			int slot = getSlotForRelativeCoordinates(relativeX, relativeZ);

			if(slot >= 0 && slot < 9 && relativeY > conveyorTE.getInsertMinY() && relativeY < conveyorTE.getInsertMaxY()) {
				added = conveyorTE.insertItemAt(entityItemStack, slot);
			}
		} else if(tileEntity instanceof IInventory) {
			if(
				relativeX >= 0 && relativeX < 1 &&
				relativeY >= 0.9 && relativeY < 1.2 &&
				relativeZ >= 0 && relativeZ < 1
				) {
				IInventory inventory = (IInventory)tileEntity;
				InventoryRange range = new InventoryRange(inventory, EnumFacing.UP.ordinal());
				added = previousStackSize - InventoryUtils.insertItem(range, entityItemStack, false);
			}
		}
		if(added == previousStackSize) {
			ei.setDead();
			return true;
		} else if(added > 0) {
			entityItemStack.stackSize = previousStackSize - added;
			ei.setEntityItemStack(entityItemStack);
			return true;
		} else {
			return false;
		}
		
	}
	
	/**
	 * Tries to insert item entities from the world into an entity.
	 * Respects the conveyor system.
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
	public static boolean tryInsertItemsFromWorld(
			TileEntity tileEntity,
			World world,
			AxisAlignedBB bounds,
			boolean stopAtFirstMatch) {
		if(world.isRemote) {
			return false;
		}
		boolean didAdd = false;
		List<?> entities = world.loadedEntityList;
		
		//if Bounding Box is Supplied, use that.
		if(bounds != null) {
			entities = world.getEntitiesWithinAABB(EntityItem.class, bounds);
		}
		for(int i = 0; i < entities.size(); i++) {
			Entity ent = (Entity)entities.get(i);
			
			if(ent instanceof EntityItem) {
				EntityItem ei = (EntityItem)ent;
				
				didAdd = tryInsert(tileEntity, ei) | didAdd;
				
				if(stopAtFirstMatch && didAdd) {
					break;
				}
			}
		}
		return didAdd;
	}
	
	public static int getNextSlot(int slot, EnumFacing dir) {
		slot = getNextSlotUnwrapped(slot, dir);
		if(slot < 0) {
			slot += 9;
		} else if(slot > 8) {
			slot -= 9;
		}
		return slot;
	}

	public static int getNextSlotUnwrapped(int slot, EnumFacing dir) {
		// X-Offset skips whole rows
		int frontOffsetX = dir.getFrontOffsetX();
		if(frontOffsetX != 0) {
			slot += frontOffsetX * 3;
		}
		// Z-Offset translates only regular,
		// but certain ones skip to the next row
		int frontOffsetZ = dir.getFrontOffsetZ();
		if(frontOffsetZ != 0) {
			int col = slot % 3;
			col += frontOffsetZ;
			if(col < 0) {
				slot -= 7;
			} else if(col > 2) {
				slot += 7;
			} else {
				slot += frontOffsetZ;
			}
		}
		return slot;
	}
	
	public static EnumFacing getHighspeedTransition(int slot,
			EnumFacing direction) {
		EnumFacing transition = direction;
		switch(direction) {
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
		if(dir == null || dir == EnumFacing.DOWN) {
			// Conveyors are only accessible from top/sides!
			return -1;
		} else if(dir == EnumFacing.UP) {
			// Center
			return 4;
		} else {
			// From that center, we go one off
			return getNextSlot(4, dir);
		}
	}

	public static final double oneThird = 1/3.0;
	
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
		double z = (slot % 3) + 0.5;
		return z * oneThird;
	}
	
	public static double getItemPositionZ(int slot, double progress, EnumFacing dir) {
		double z = getItemPositionZ(slot);
		z += dir.getFrontOffsetZ() * progress * oneThird;
		return z;
	}
	
	public static int getSlotForRelativeCoordinates(double x, double z) {
		if(x > 1 || x < 0 || z > 1 || z < 0) {
			return -1;
		}
		int row = (int)Math.floor(x * 3f);
		int col = (int)Math.floor(z * 3f);
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
	
	/**
	 * Drops the item in the passed slot, exactly where it is rendered now.
	 * @param slot The slot to be dropped.
	 */
	public static void dropItem(World world, BlockPos pos, IConveyorSlots tileEntity, int slot, boolean withVelocity) {
		ItemWrapper slotObject = tileEntity.getSlot(slot);
		// System.out.println("Dropping slot " + slot + " >>" + slotObject.itemStack);
		
		if(!world.isRemote) {
			float speedsteps = tileEntity.getSpeedsteps();
			EnumFacing direction = tileEntity.getMovementDirection();
			float progress = slotObject.movementProgress / speedsteps;
			
			double posX = pos.getX() + getItemPositionX(slot, progress, direction);
			double posY = pos.getY() + 0.5f;
			double posZ = pos.getZ() + getItemPositionZ(slot, progress, direction);
			
			if(slotObject.itemStack != null) {
				EntityItem item = new EntityItem(world, posX, posY, posZ, slotObject.itemStack);
				if(withVelocity) {
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
		// System.out.println("Transfer external " + slot + " to " + nextSlot);
		
		ItemWrapper slotObject = tileEntity.getSlot(slot);
		
		int transferred = nextBlock.insertItemAt(slotObject.itemStack.copy(), nextSlot);
		if(transferred > 0) {
			slotObject.itemStack.stackSize -= transferred;
			if(slotObject.itemStack.stackSize <= 0) {
				slotObject.itemStack = null;
	
				// Stack moved completely
				return true;
			}
		}
		// Stack not moved at all, or has backlog
		return false;
	}

	public static boolean transferSlot(IConveyorSlots tileEntity, int slot, int nextSlot) {
		// System.out.println("Transfer internal " + slot + " to " + nextSlot);
		
		ItemWrapper slotObject = tileEntity.getSlot(slot);
		ItemWrapper nextSlotObject = tileEntity.getSlot(nextSlot);
		if(nextSlotObject.itemStack == null) {
			nextSlotObject.itemStack = slotObject.itemStack;
			
			slotObject.itemStack = null;
	
			// Stack moved completely
			return true;
		}
		return false;
	}

	public static int insertItemAt(IConveyorSlots tileEntity, ItemStack item, int slot, boolean simulate) {
		ItemWrapper slotObject = tileEntity.getSlot(slot);
		if(slotObject.itemStack == null) {
			if(!simulate) {
				slotObject.itemStack = item.copy();
				slotObject.unblock();
				slotObject.resetMovement();
			}
			return item.stackSize;
		} else if(slotObject.itemStack.isItemEqual(item)) {
			int availableSpace = slotObject.itemStack.getMaxStackSize() - slotObject.itemStack.stackSize;
			if(availableSpace > 0) {
				availableSpace = Math.min(availableSpace, item.stackSize);
				if(!simulate) {
					slotObject.itemStack.stackSize += availableSpace;
				}
				return availableSpace;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	public static IConveyorSlots getSlots(TileEntity tileEntity, EnumFacing side) {
		if(tileEntity == null) {
			return null;
		}
		if (tileEntity instanceof IConveyorSlots) {
			return (IConveyorSlots) tileEntity;
		} else {
			return tileEntity.getCapability(Taam.CAPABILITY_CONVEYOR, side);
		}
	}
	
	/**
	 * Runs the default transition logic for the items on a conveyor entity.
	 * 
	 * Respects the supplied slot order, processes items if tileEntity instanceof {@link IConveyorApplianceHost}.
	 * @param world
	 * @param pos
	 * @param tileEntity
	 * @param slotOrder The order used when working through the slots.
	 * @return true if the state of any item changed (TIleEntity should be marked dirty). 
	 */
	public static boolean defaultTransition(World world, BlockPos pos, IConveyorSlots tileEntity, int[] slotOrder) {
		/*
		 * Fetch info on appliances
		 */
		List<IConveyorAppliance> appliances = null;
		IConveyorApplianceHost applianceHost = null;
		if(tileEntity instanceof IConveyorApplianceHost) {
			applianceHost = (IConveyorApplianceHost)tileEntity;
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
		for(int index = 0; index < slotOrder.length; index++) {
		
			int slot = slotOrder[index];
			
			ItemWrapper wrapper = tileEntity.getSlot(slot);
			
			if(wrapper.isEmpty()) {
				continue;
			}
			

			// Unblock Wrapper to prevent them from staying blocked if we remove an appliance
			wrapper.unblock();
			
			/*
			 * Let the appliances process the current slot.
			 */
			if(appliances != null) {
				
				// Let each appliance process the item
				for(IConveyorAppliance appliance : appliances) {
					if(appliance.processItem(applianceHost, slot, wrapper)) {
						needsWorldUpdate = true;
					}
				}
			}
			
			if(wrapper.isBlocked()) {
				continue;
			}
			
			/*
			 * Move the contents to the next slot
			 */
			
			EnumFacing direction = tileEntity.getMovementDirection();
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
			
			if(nextSlot < 0) {
				nextSlot += 9;
				slotWrapped = true;
			} else if(nextSlot > 8) {
				nextSlot -= 9;
				slotWrapped = true;
			}
			
			// Check the condition of the next slot
			if(slotWrapped) {
				// Next block, potentially a conveyor-aware block.
				BlockPos nextBlockPos = pos.offset(direction);
				
				TileEntity te = world.getTileEntity(nextBlockPos);
				
				nextBlock = getSlots(te, direction.getOpposite());
				
				if(nextBlock == null) {
					// Drop it
					nextSlotFree = true;
					nextSlotMovable = true;
				} else {
					// Move it to next block
					nextSlotFree = nextBlock.getSlot(nextSlot).isEmpty();
					wrappedIsSameDirection = nextBlock.getMovementDirection() == direction;
					nextSlotMovable = nextBlock.canSlotMove(nextSlot) && wrappedIsSameDirection;
					nextSlotProgress = nextBlock.getMovementProgress(nextSlot);
					byte nextSpeedSteps = nextBlock.getSpeedsteps();
					if(nextSpeedSteps != speedsteps) {
						if(nextSpeedSteps == 0) {
							nextSlotProgress = 0;
						} else {
							nextSlotProgress = Math.round((nextSlotProgress / (float)nextSpeedSteps) * speedsteps);
						}
					}
					
				}
			} else {
				ItemWrapper nextWrapper = tileEntity.getSlot(nextSlot);
				nextSlotFree = nextWrapper.itemStack == null;
				nextSlotMovable = !nextWrapper.isBlocked();
				nextSlotProgress = nextWrapper.movementProgress;
			}
			
			// Check transition to next slot
			if(nextSlotFree || nextSlotMovable) {
				if(wrapper.movementProgress == speedsteps && nextSlotFree) {
					if(slotWrapped && (nextBlock == null || !nextBlock.isSlotAvailable(nextSlot))) {
						// No next block, drop it.
						dropItem(world, pos, tileEntity, slot, true);
					} else {
						boolean completeTransfer;
						if(slotWrapped) {
							completeTransfer = transferSlot(tileEntity, slot, nextBlock, nextSlot);
						} else {
							completeTransfer = transferSlot(tileEntity, slot, nextSlot);
						}
						if(!completeTransfer) {
							// We still have some items pending here..
							nextSlotFree = false;
							nextSlotMovable = false;
						}
					}
					needsUpdate = true;
				}
			}
			/*
			 * If we can progress (next slot is empty or far enough away), step forward
			 */
			if(nextSlotFree || (nextSlotMovable && wrappedIsSameDirection && wrapper.movementProgress < nextSlotProgress)) {
				wrapper.movementProgress++;
				if(wrapper.movementProgress > speedsteps) {
					wrapper.movementProgress = 0;
				}
			}
		}
		//TODO: needsUpdate -> markDirty ??
		return needsWorldUpdate;
	}

	public static void defaultPlayerInteraction(EntityPlayer player, IConveyorSlots tileEntity, float hitX, float hitZ) {
		int clickedSlot = getSlotForRelativeCoordinates(hitX, hitZ);
		int playerSlot = player.inventory.currentItem;
		ItemStack playerStack = player.inventory.getCurrentItem();
		if(playerStack == null) {
			// Take from Conveyor
			ItemStack removed = tileEntity.removeItemAt(clickedSlot);
			player.inventory.setInventorySlotContents(playerSlot, removed);
		} else {
			// Put on conveyor
			int inserted = tileEntity.insertItemAt(playerStack, clickedSlot);
			if(inserted == playerStack.stackSize) {
				player.inventory.setInventorySlotContents(playerSlot, null);
			} else {
				playerStack.stackSize -= inserted;
				player.inventory.setInventorySlotContents(playerSlot, playerStack);
			}
		}
	}

	public static List<IConveyorAppliance> getTouchingAppliances(IConveyorApplianceHost tileEntityConveyor, IBlockAccess world, BlockPos pos) {
		List<IConveyorAppliance> appliances = new ArrayList<IConveyorAppliance>();
		
		for(EnumFacing direction : EnumFacing.VALUES) {
			TileEntity te = world.getTileEntity(pos.offset(direction));
			if(te instanceof IConveyorAppliance) {
				IConveyorAppliance appliance = (IConveyorAppliance)te;
				if(appliance.getFacingDirection() == direction.getOpposite()) {
					appliances.add(appliance);
				}
			}
		}
		
		return appliances;
	}
}
