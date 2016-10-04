package net.teamio.taam.content;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public interface IWorldInteractable {
	/**
	 * Called when the containing block is activated (Right Clicked).
	 *
	 * @param world
	 * @param player
	 * @param hand
	 * @param hasWrench
	 * @param side
	 * @param hitX
	 * @param hitY
	 * @param hitZ
	 * @param x
	 * @param y
	 * @param z
	 * @return true when the click was intercepted, no further processing is to
	 *         be done.
	 */
	boolean onBlockActivated(World world, EntityPlayer player, EnumHand hand, boolean hasWrench, EnumFacing side, float hitX,
			float hitY, float hitZ);

	/**
	 * Called when the containing block is hit (Left Clicked).
	 *
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param player
	 * @param hasWrench
	 * @return true when the click was intercepted, no further processing is to
	 *         be done. (Currently unused)
	 */
	boolean onBlockHit(World world, EntityPlayer player, boolean hasWrench);
}
