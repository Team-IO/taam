package net.teamio.taam.machines;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.teamio.taam.util.FaceBitmap;

import java.util.List;

public interface IMachine extends ICapabilityProvider {
	void writePropertiesToNBT(NBTTagCompound tag);
	void readPropertiesFromNBT(NBTTagCompound tag);

	void writeUpdatePacket(PacketBuffer buf);
	void readUpdatePacket(PacketBuffer buf);

	IBlockState getExtendedState(IBlockState state, IBlockAccess worldIn, BlockPos blockPos);
	String getModelPath();

	/**
	 * Called in {@link ITickable#update()}
	 *
	 * @param world
	 * @param pos
	 * @return true if state changed & the TileEntity/Multipart needs to be marked as dirty.
	 */
	boolean update(World world, BlockPos pos);

	/**
	 * Called when a neighbor block changes, or for multiparts, a part in the
	 * same/neighboring block changes. If true is returned, the block is marked
	 * for a render update.
	 *
	 * @param world
	 * @param pos
	 * @return true to mark the containing block for a render update.
	 */
	boolean renderUpdate(IBlockAccess world, BlockPos pos);

	/**
	 * Called when a neighbor block changes, or for multiparts, a part in the
	 * same/neighboring block changes.
	 *
	 * @param world
	 * @param pos
	 * @param occlusionField
	 *            A {@link FaceBitmap}-compatible value describing which sides
	 *            are occluded for pipes/cables. Probably 0 for non-multiparts.
	 */
	void blockUpdate(World world, BlockPos pos, byte occlusionField);
	void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity);
	void addSelectionBoxes(List<AxisAlignedBB> list);
	void addOcclusionBoxes(List<AxisAlignedBB> list);

	void onCreated(World worldObj, BlockPos pos);
}
