package net.teamio.taam.content.piping.multiparts;

import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemPartPipe extends ItemMultiPart {

	@Override
	public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3 hit, ItemStack stack,
			EntityPlayer player) {
		return new PipeMultipart();
	}

}
