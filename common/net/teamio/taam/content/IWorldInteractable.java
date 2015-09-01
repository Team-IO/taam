package net.teamio.taam.content;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IWorldInteractable {
	/**
	 * Called when the containing block is activated (Right Clicked).
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param player
	 * @param side
	 * @param hitX
	 * @param hitY
	 * @param hitZ
	 * @return true when the click was intercepted, no further processing is to be done.
	 */
	boolean onBlockActivated(World world, int x,
			int y, int z, EntityPlayer player,
			int side, float hitX, float hitY,
			float hitZ);
}
