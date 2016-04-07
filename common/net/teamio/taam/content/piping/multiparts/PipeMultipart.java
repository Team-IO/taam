package net.teamio.taam.content.piping.multiparts;

import java.util.List;

import mcmultipart.multipart.Multipart;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.piping.BlockPipe;
import net.teamio.taam.content.piping.TileEntityPipe;

public class PipeMultipart extends Multipart {
	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		list.add(BlockPipe.bbCenter);
	}
	
	@Override
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		list.add(BlockPipe.bbCenter);
	}
	
	@Override
	public String getModelPath() {
		return "taam:pipe";
	}
}
