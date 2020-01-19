package net.teamio.taam.integration.mcmultipart;

import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.IMultipart;
import mcmultipart.api.slot.EnumCenterSlot;
import mcmultipart.api.slot.IPartSlot;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamMain;
import net.teamio.taam.machines.IMachine;
import net.teamio.taam.machines.MachineBlock;
import net.teamio.taam.machines.MachineTileEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MachineMultipart implements IMultipart {

	@Override
	public Block getBlock() {
		return TaamMain.blockMachine;
	}

	@Override
	public IPartSlot getSlotForPlacement(World world, BlockPos blockPos, IBlockState iBlockState, EnumFacing enumFacing, float v, float v1, float v2, EntityLivingBase entityLivingBase) {
		return EnumCenterSlot.CENTER;
	}

	@Override
	public IPartSlot getSlotFromWorld(IBlockAccess iBlockAccess, BlockPos blockPos, IBlockState iBlockState) {
		return EnumCenterSlot.CENTER;
	}

	@Override
	public List<AxisAlignedBB> getOcclusionBoxes(IPartInfo part) {
		IMachine machine;

		TileEntity te = part.getTile().getTileEntity();

		if (te == null) {
			// We don't have a block yet, create a temporary machine instance
			Taam.MACHINE_META meta = part.getState().getValue(MachineBlock.VARIANT);
			Log.debug("Creating a temporary instance of {}", meta.unlocalizedName());
			machine = meta.createMachine(null);
		} else if (te instanceof MachineTileEntity) {
			machine = ((MachineTileEntity) te).machine;
			if (machine == null) {
				Log.error("MachineMultipart at {} does not have a machine instance", part.getPartPos().toString());

				Taam.MACHINE_META meta = part.getState().getValue(MachineBlock.VARIANT);
				Log.debug("Creating a temporary instance of {}", meta.unlocalizedName());
				machine = meta.createMachine(null);
			}
		} else {
			Log.error("MachineMultipart at %s did not wrap a MachineTileEntity (got {})", part.getPartPos().toString(), te);
			return Collections.emptyList();
		}
		ArrayList<AxisAlignedBB> list = new ArrayList<>();
		if (machine != null) {
			machine.addOcclusionBoxes(list);
		}
		return list;
	}
}
