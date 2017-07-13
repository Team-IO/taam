package net.teamio.taam.machines;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.teamio.taam.Log;

import java.util.List;

/**
 * Dummy machine used instead of thousands of null-checks
 *
 * @author Oliver Kahrmann
 *
 */
public class MachineDummy implements IMachine {

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return false;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return null;
	}

	@Override
	public void writePropertiesToNBT(NBTTagCompound tag) {
	}

	@Override
	public void readPropertiesFromNBT(NBTTagCompound tag) {
	}

	@Override
	public void writeUpdatePacket(PacketBuffer buf) {
	}

	@Override
	public void readUpdatePacket(PacketBuffer buf) {
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess worldIn, BlockPos blockPos) {
		return state;
	}

	@Override
	public String getModelPath() {
		return "taam:machine";
	}

	@Override
	public boolean update(World world, BlockPos pos) {
		Log.warn("Machine Tile Entity with Dummy machine at {}. This means, a machine was not saved properly.", pos);
		return false;
	}

	@Override
	public boolean renderUpdate(IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public void blockUpdate(World world, BlockPos pos, byte occlusionField) {
	}

	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
	}

	@Override
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		list.add(new AxisAlignedBB(0, 0, 0, 1, 1, 1));
	}

	@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {
	}

	@Override
	public void onCreated(World worldObj, BlockPos pos) {
		Log.error("Dummy entity created on load. This means machine creation code is flawed. THIS IS AN ERROR! Report to author!", pos);
	}
}
