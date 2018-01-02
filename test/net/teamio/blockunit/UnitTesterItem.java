package net.teamio.blockunit;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 * Created by oliver on 2017-07-03.
 */
public class UnitTesterItem extends Item {

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		boolean onClient = worldIn.isRemote;

		if(!onClient) {
			//TODO: decision between serverside and clientside tests?
			Framework.runTests();
		}

		return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);

	}
}
