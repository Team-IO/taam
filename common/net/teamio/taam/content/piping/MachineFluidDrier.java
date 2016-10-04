package net.teamio.taam.content.piping;

import java.io.IOException;
import java.util.List;

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
import net.minecraftforge.fluids.FluidStack;
import net.teamio.taam.Config;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.content.IRedstoneControlled;
import net.teamio.taam.conveyors.OutputChuteBacklog;
import net.teamio.taam.machines.IMachine;
import net.teamio.taam.piping.PipeEndRestricted;
import net.teamio.taam.piping.PipeUtil;
import net.teamio.taam.recipes.IProcessingRecipeFluidBased;
import net.teamio.taam.recipes.ProcessingRegistry;
import net.teamio.taam.util.FaceBitmap;
import net.teamio.taam.util.TaamUtil;

public class MachineFluidDrier implements IMachine {

	private PipeEndRestricted pipeEndIn;

	private OutputChuteBacklog chute = new OutputChuteBacklog();

	private FluidStack lastInputFluid;
	private IProcessingRecipeFluidBased[] matchingRecipes;

	private byte redstoneMode = IRedstoneControlled.MODE_ACTIVE_ON_LOW;
	private int timeout;

	public boolean isShutdown;
	private byte occludedSides;

	private static final float fromBorderOcclusion = 2f/16;
	public static final AxisAlignedBB bbCollision = new AxisAlignedBB(0, 0, 0, 1, 1-3/16f, 1);
	public static final AxisAlignedBB bbCoolusion = new AxisAlignedBB(fromBorderOcclusion, fromBorderOcclusion, fromBorderOcclusion, 1-fromBorderOcclusion, 1-fromBorderOcclusion, 1-fromBorderOcclusion);

	public MachineFluidDrier() {
		pipeEndIn = new PipeEndRestricted(EnumFacing.UP, Config.pl_fluid_drier_capacity, false);
		resetTimeout();
	}

	private void updateOcclusion() {
		pipeEndIn.occluded = FaceBitmap.isSideBitSet(occludedSides, EnumFacing.UP);
	}

	private void resetTimeout() {
		timeout = Config.pl_fluid_drier_timeout;
	}

	@Override
	public void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setBoolean("isShutdown", isShutdown);
		tag.setInteger("timeout", timeout);

		NBTTagCompound tagChute = new NBTTagCompound();
		chute.writeToNBT(tagChute);
		tag.setTag("chute", tagChute);

		NBTTagCompound tagIn = new NBTTagCompound();
		pipeEndIn.writeToNBT(tagIn);
		tag.setTag("pipeEndIn", tagIn);

		tag.setByte("occludedSides", occludedSides);
	}

	@Override
	public void readPropertiesFromNBT(NBTTagCompound tag) {
		isShutdown = tag.getBoolean("isShutdown");
		timeout = tag.getInteger("timeout");

		chute.readFromNBT(tag.getCompoundTag("chute"));

		NBTTagCompound tagIn = tag.getCompoundTag("pipeEndIn");
		if (tagIn != null) {
			pipeEndIn.readFromNBT(tagIn);
		}

		occludedSides = tag.getByte("occludedSides");
		updateOcclusion();
	}

	@Override
	public void writeUpdatePacket(PacketBuffer buf) {
		NBTTagCompound tag = new NBTTagCompound();
		writePropertiesToNBT(tag);
		buf.writeNBTTagCompoundToBuffer(tag);
	}

	@Override
	public void readUpdatePacket(PacketBuffer buf) {
		try {
			NBTTagCompound tag = buf.readNBTTagCompoundFromBuffer();
			readPropertiesFromNBT(tag);
		} catch (IOException e) {
			Log.error(getClass().getSimpleName()
					+ " has trouble reading tag from update packet. THIS IS AN ERROR, please report.", e);
		}
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos blockPos) {
		return state;
	}

	@Override
	public String getModelPath() {
		return "taam:machine";
	}

	@Override
	public void update(World world, BlockPos pos) {
		PipeUtil.processPipes(pipeEndIn, world, pos);

		if(world.isRemote) {
			return;
		}

		if(process(world, pos)) {
			//TODO: updateState(false, false, false);
		}
	}

	@Override
	public boolean renderUpdate(IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public void blockUpdate(World world, BlockPos pos, byte occlusionField) {
		occludedSides = occlusionField;
		updateOcclusion();
	}

	private boolean process(World world, BlockPos pos) {
		BlockPos down = pos.down();

		/*
		 * Check redstone level
		 */

		boolean redstoneHigh = world.isBlockIndirectlyGettingPowered(pos) > 0;

		isShutdown = TaamUtil.isShutdown(world.rand, redstoneMode, redstoneHigh);

		if(isShutdown) {
			resetTimeout();
			return true;
		}


		/*
		 * Check blocked & fetch output inventory
		 */
		chute.refreshOutputInventory(world, down);
		if(!chute.isOperable()) {
			resetTimeout();
			return false;
		}

		/*
		 * Output Backlog
		 */

		// Output the backlog. Returns true if there were items transferred or there are still items left.
		if(chute.output(world, down)) {
			resetTimeout();
			return true;
		}

		/*
		 * Check Recipe
		 */

		IProcessingRecipeFluidBased recipe = getRecipe();

		if(recipe == null) {
			resetTimeout();
			return false;
		}

		/*
		 * Check fluid requirements
		 */

		int requiredAmount = recipe.getInputFluid().amount;

		FluidStack inTank = pipeEndIn.getFluid();

		if(inTank == null || inTank.amount < requiredAmount) {
			resetTimeout();
			return false;
		}

		/*
		 * Check timeout, only if we actually can process.
		 */

		if(timeout > 0) {
			timeout--;
			return true;
		}

		/*
		 * Consume fluid
		 */

		int consumed = pipeEndIn.removeFluid(recipe.getInputFluid());
		if(consumed != requiredAmount) {
			// This should not happen.
			Log.error("Detected inconsistency in {}. Expected fluid amount to be consumed: {} Actually consumed: {}. Fluid might have been duplicated or lost.",
					getClass().getName(), requiredAmount, consumed);
		}

		/*
		 * Set Output Backlog
		 */

		chute.backlog = recipe.getOutput(null);
		resetTimeout();
		return true;
	}

	/**
	 * Checks if there is a recipe for the current input fluid & returns it.
	 *
	 * @param stack
	 */
	private IProcessingRecipeFluidBased getRecipe() {
		FluidStack inside = pipeEndIn.getFluid();
		if(inside == null) {
			lastInputFluid = null;
			matchingRecipes = null;
			return null;
		}
		if(lastInputFluid == null || !lastInputFluid.isFluidEqual(inside)) {
			lastInputFluid = inside;
			matchingRecipes = ProcessingRegistry.getRecipes(ProcessingRegistry.FLUIDDRIER, lastInputFluid);
		}
		if(matchingRecipes != null) {
			for(IProcessingRecipeFluidBased recipe : matchingRecipes) {
				if(recipe.inputFluidMatches(inside)) {
					return recipe;
				}
			}
		}
		return null;
	}

	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		if (mask.intersectsWith(bbCollision)) {
			list.add(bbCollision);
		}
	}

	@Override
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		list.add(bbCollision);
		list.add(MachinePipe.bbFaces[EnumFacing.UP.ordinal()]);
		list.add(MachinePipe.bbFlanges[EnumFacing.UP.ordinal()]);
	}

	@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {
		list.add(bbCoolusion);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == Taam.CAPABILITY_PIPE) {
			return facing == EnumFacing.UP;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == Taam.CAPABILITY_PIPE && facing == EnumFacing.UP) {
			return (T) pipeEndIn;
		}
		return null;
	}
}
