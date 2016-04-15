package net.teamio.taam.machines;

import java.util.List;

import mcmultipart.multipart.IOccludingPart;
import mcmultipart.multipart.Multipart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

public class MachineMultipart extends Multipart implements IOccludingPart {
	private final IMachine machine;

	public MachineMultipart(IMachine machine) {
		this.machine = machine;
	}

	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		machine.addCollisionBoxes(mask, list, collidingEntity);
	}

	@Override
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		machine.addSelectionBoxes(list);
	}

	@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {
		machine.addOcclusionBoxes(list);
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state) {
		return machine.getExtendedState(state, getWorld(), getPos());
	}

}
