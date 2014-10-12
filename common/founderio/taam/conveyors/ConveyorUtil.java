package founderio.taam.conveyors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class ConveyorUtil {
	/**
	 * Tries to insert item entities from the world into the conveyor system.
	 * 
	 * @param conveyorTE
	 * @param world
	 * @param bounds
	 *            Optionally give an AABB Instance to speed up the search &
	 *            extend to unloaded chunks. Else only loaded entities are
	 *            respected. TODO: Implement this.
	 * @param stopAtFirstMatch
	 *            Stop processing items after the first one was added?
	 */
	public static void tryInsertItemsFromWorld(
			IConveyorAwareTE conveyorTE,
			World world,
			AxisAlignedBB bounds,
			boolean stopAtFirstMatch) {
		if(world.isRemote) {
			return;
		}
		//TODO: if Bounding Box is Supplied, use that.
		for(Object obj : world.loadedEntityList) {
			Entity ent = (Entity)obj;
			
			if(ent instanceof EntityItem) {
				EntityItem ei = (EntityItem)ent;
				ItemStack entityItemStack = ei.getEntityItem();
				int previousStackSize = entityItemStack.stackSize;
				if(entityItemStack == null || entityItemStack.getItem() == null) {
					continue;
				}
				int added = conveyorTE.addItemAt(entityItemStack, ent.posX, ent.posY, ent.posZ);
				if(added == previousStackSize) {
					ent.setDead();
					if(stopAtFirstMatch) {
						break;
					}
				} else if(added > 0) {
					entityItemStack.stackSize = previousStackSize - added;
					ei.setEntityItemStack(entityItemStack);
					if(stopAtFirstMatch) {
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Tries to insert items into the conveyor passed. Sets the stack size accordingly.
	 * @param conveyorTE
	 * @param itemWrapper
	 * @param absX
	 * @param absY
	 * @param absZ
	 * @return true if items were inserted.
	 */
	public static boolean tryInsertItems(IConveyorAwareTE conveyorTE, ItemWrapper itemWrapper,
			double absX, double absY, double absZ) {
		int previousStackSize = itemWrapper.getStackSize();
		if(previousStackSize == 0) {
			// Just remove it.
			return true;
		}
		int added = conveyorTE.addItemAt(itemWrapper, absX, absY, absZ);
		if(added > 0) {
			// Fully or Partially inserted
			itemWrapper.setStackSize(previousStackSize - added);
			return true;
		} else {
			return false;
		}
	}
}