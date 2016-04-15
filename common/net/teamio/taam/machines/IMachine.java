package net.teamio.taam.machines;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IMachine extends ICapabilityProvider {
	void writePropertiesToNBT(NBTTagCompound tag, boolean isNetwork);
	void readPropertiesFromNBT(NBTTagCompound tag, boolean isNetwork);

    IBlockState getExtendedState(IBlockState state, World world, BlockPos blockPos);
    BlockState createBlockState(Block block);
	String getModelPath();
    
	void update(World world, BlockPos pos);
	void renderUpdate(World world, BlockPos pos);
	void blockUpdate(World world, BlockPos pos);
	void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity);
	void addSelectionBoxes(List<AxisAlignedBB> list);
	void addOcclusionBoxes(List<AxisAlignedBB> list);
}
