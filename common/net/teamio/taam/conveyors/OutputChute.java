package net.teamio.taam.conveyors;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.teamio.taam.util.InventoryUtils;
import net.teamio.taam.util.TaamUtil;

public abstract class OutputChute {

	protected IItemHandler outputInventory;
	protected boolean canDrop;

	public void refreshOutputInventory(World world, BlockPos pos) {
		outputInventory = InventoryUtils.getInventory(world, pos, EnumFacing.UP);
		canDrop = TaamUtil.canDropIntoWorld(world, pos);
	}

	public boolean isBlocked() {
		return !canDrop && outputInventory == null;
	}

	/**
	 * Output the chute content.
	 *
	 * @param world
	 * @param pos
	 * @return Returns true if there were items transferred or there are still
	 * items left.
	 */
	public abstract boolean output(World world, BlockPos pos);

	/**
	 * Tries to output into the outputInventory, or drop down into the world
	 *
	 * @param world
	 * @param oututPosition   Position to output to, usually one block below the chute.
	 * @param outputInventory Output inventory. If null, will output to world.
	 * @param backlog         The items to output.
	 * @return true if it was able to output items, or unable but still has
	 * items left.
	 */
	public static boolean chuteMechanicsOutput(World world, BlockPos oututPosition, IItemHandler outputInventory, ItemStack[] backlog) {
		if (backlog == null) {
			return false;
		}

		boolean wasAble = false;
		boolean hasOutputLeft = false;
		if (outputInventory == null) {
			double entX = oututPosition.getX() + 0.5;
			double entY = oututPosition.getY() + 0.7;
			double entZ = oututPosition.getZ() + 0.5;

			// Output to world
			for (int i = 0; i < backlog.length; i++) {
				ItemStack itemStack = backlog[i];
				if (InventoryUtils.isEmpty(itemStack)) {
					continue;
				}
				EntityItem item = new EntityItem(world, entX, entY, entZ, itemStack);
				item.motionX = 0;
				item.motionY = 0;
				item.motionZ = 0;
				world.spawnEntity(item);
				wasAble = true;
				backlog[i] = ItemStack.EMPTY;
			}

			hasOutputLeft = false;
		} else {
			// Output to inventory
			for (int i = 0; i < backlog.length; i++) {
				ItemStack itemStack = backlog[i];
				if (InventoryUtils.isEmpty(itemStack)) {
					continue;
				}
				backlog[i] = ItemHandlerHelper.insertItemStacked(outputInventory, itemStack, false);
				if (backlog[i] == ItemStack.EMPTY) {
					wasAble = true;
				} else {
					hasOutputLeft = true;
					wasAble = backlog[i] != itemStack;
				}
			}
		}
		return wasAble || hasOutputLeft;
	}

}
