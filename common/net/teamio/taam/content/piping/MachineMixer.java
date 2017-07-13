package net.teamio.taam.content.piping;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
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
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.ConveyorSlotsBase;
import net.teamio.taam.conveyors.SlotMatrix;
import net.teamio.taam.machines.IMachine;
import net.teamio.taam.piping.PipeEnd;
import net.teamio.taam.piping.PipeEndRestricted;
import net.teamio.taam.piping.PipeUtil;
import net.teamio.taam.recipes.IProcessingRecipeFluidBased;
import net.teamio.taam.recipes.ProcessingRegistry;
import net.teamio.taam.util.FaceBitmap;
import net.teamio.taam.util.TaamUtil;

import java.io.IOException;
import java.util.List;

public class MachineMixer implements IMachine, IRotatable {

	private EnumFacing direction = EnumFacing.NORTH;

	private PipeEndRestricted pipeEndIn;
	private PipeEnd pipeEndOut;

	private FluidStack backlog;

	private FluidStack lastInputFluid;
	private IProcessingRecipeFluidBased[] matchingRecipes;


	private byte redstoneMode = IRedstoneControlled.MODE_ACTIVE_ON_LOW;
	public boolean isShutdown;
	private int timeout;
	private byte occludedSides;

	public static final AxisAlignedBB bounds = new AxisAlignedBB(0, MachinePipe.baseplateSize, 0, 1, 0.5f, 1);

	public static final AxisAlignedBB boundsPipeX = new AxisAlignedBB(
			0, MachinePipe.fromBorderFlange, MachinePipe.fromBorderFlange, 1,
			1 - MachinePipe.fromBorderFlange, 1 - MachinePipe.fromBorderFlange);
	public static final AxisAlignedBB boundsPipeZ = new AxisAlignedBB(
			MachinePipe.fromBorderFlange, MachinePipe.fromBorderFlange, 0,
			1 - MachinePipe.fromBorderFlange, 1 - MachinePipe.fromBorderFlange, 1);

	private World worldObj;
	private BlockPos pos;

	/**
	 * Conveyor Slot set for input on the sides
	 */
	private ConveyorSlotsBase conveyorSlots = new ConveyorSlotsBase() {

		{
			slotMatrix = new SlotMatrix(true, true, true, false, false, false, true, true, true);
			insertMaxY = 1;
			insertMinY = 0.4;
		}

		@Override
		public ItemStack removeItemAt(int slot, int amount, boolean simulate) {
			return null;
		}

		@Override
		public int insertItemAt(ItemStack item, int slot, boolean simulate) {
			if (isSlotAvailable(slot)) {
				//TODO: Move to cache inventory for processing
				return process(item, simulate);
			}
			return 0;
		}
	};

	@Override
	public void onCreated(World worldObj, BlockPos pos) {
		this.worldObj = worldObj;
		this.pos = pos;
	}

	public MachineMixer() {
		pipeEndOut = new PipeEnd(direction, Config.pl_mixer_capacity_output, false);
		pipeEndIn = new PipeEndRestricted(direction.getOpposite(), Config.pl_mixer_capacity_input, false);

		updateOcclusion();
		resetTimeout();
	}

	private void updateOcclusion() {
		pipeEndOut.occluded = FaceBitmap.isSideBitSet(occludedSides, pipeEndOut.getSide());
		pipeEndIn.occluded = FaceBitmap.isSideBitSet(occludedSides, pipeEndIn.getSide());

		conveyorSlots.rotation = direction;
	}

	private void resetTimeout() {
		timeout = Config.pl_mixer_timeout;
	}

	@Override
	public void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setInteger("direction", direction.ordinal());

		NBTTagCompound tagIn = new NBTTagCompound();
		pipeEndIn.writeToNBT(tagIn);
		tag.setTag("pipeEndIn", tagIn);

		NBTTagCompound tagOut = new NBTTagCompound();
		pipeEndOut.writeToNBT(tagOut);
		tag.setTag("pipeEndOut", tagOut);

		tag.setBoolean("isShutdown", isShutdown);
		tag.setInteger("timeout", timeout);
		tag.setByte("occludedSides", occludedSides);
	}

	@Override
	public void readPropertiesFromNBT(NBTTagCompound tag) {
		direction = EnumFacing.getFront(tag.getInteger("direction"));
		if (direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
			direction = EnumFacing.NORTH;
		}
		pipeEndOut.setSide(direction);
		pipeEndIn.setSide(direction.getOpposite());

		NBTTagCompound tagIn = tag.getCompoundTag("pipeEndIn");
		if (tagIn != null) {
			pipeEndIn.readFromNBT(tagIn);
		}

		NBTTagCompound tagOut = tag.getCompoundTag("pipeEndOut");
		if (tagOut != null) {
			pipeEndOut.readFromNBT(tagOut);
		}

		isShutdown = tag.getBoolean("isShutdown");
		timeout = tag.getInteger("timeout");
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
	public boolean update(World world, BlockPos pos) {
		// Output backlog of already processed stuff
		if (backlog != null) {
			backlog.amount -= pipeEndOut.addFluid(backlog);
			if (backlog.amount <= 0) {
				backlog = null;
			}
			return true;
		}
		PipeUtil.processPipes(pipeEndIn, world, pos);
		PipeUtil.processPipes(pipeEndOut, world, pos);
		//TODO: only true if actually updated... Awaiting pipe network logic
		return true;
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

	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		if (mask.intersectsWith(bounds)) {
			list.add(bounds);
		}
		if (direction.getAxis() == Axis.X) {
			if (mask.intersectsWith(boundsPipeX)) {
				list.add(boundsPipeX);
			}
		} else {
			if (mask.intersectsWith(boundsPipeZ)) {
				list.add(boundsPipeZ);
			}
		}
	}

	@Override
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		list.add(bounds);
		list.add(MachinePipe.bbBaseplate);
		if (direction.getAxis() == Axis.X) {
			list.add(boundsPipeX);
		} else {
			list.add(boundsPipeZ);
		}
	}

	@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {
		list.add(bounds);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == Taam.CAPABILITY_PIPE) {
			return facing.getAxis() == direction.getAxis();
		}
		if (capability == Taam.CAPABILITY_CONVEYOR) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == Taam.CAPABILITY_PIPE) {
			if (facing == direction) {
				return (T) pipeEndOut;
			} else if (facing == direction.getOpposite()) {
				return (T) pipeEndIn;
			} else {
				return null;
			}
		}
		if (capability == Taam.CAPABILITY_CONVEYOR) {
			return (T) conveyorSlots;
		}
		return null;
	}

	/**
	 * Checks if there is a recipe for the current input fluid and the provided
	 * item stack.
	 *
	 * @param stack
	 * @return true if there is a recipe available, false if not. Also returns
	 *         false if there is no input fluid. Does not check for the amount
	 *         of fluid, so {@link #process(ItemStack, boolean)} may still fail.
	 */
	private IProcessingRecipeFluidBased getRecipe(ItemStack stack) {
		FluidStack inside = pipeEndIn.getFluid();
		if (inside == null) {
			lastInputFluid = null;
			matchingRecipes = null;
			return null;
		}
		if (lastInputFluid == null || !lastInputFluid.isFluidEqual(inside)) {
			lastInputFluid = inside;
			matchingRecipes = ProcessingRegistry.getRecipes(ProcessingRegistry.MIXER, lastInputFluid);
		}
		if (matchingRecipes != null) {
			for (IProcessingRecipeFluidBased recipe : matchingRecipes) {
				if (recipe.inputMatches(stack)) {
					return recipe;
				}
			}
		}
		return null;
	}

	/**
	 * Processes the item by consuming input fluid and generating output fluid
	 * if there is space in the output pipe end.
	 *
	 * @param stack
	 * @return the amount of items consumed.
	 */
	private int process(ItemStack stack, boolean simulate) {

		/*
		 * When there is backlog, we cannot process more
		 */
		if (backlog != null) {
			// Skip processing until next tick.
			return 0;
		}

		/*
		 * Check redstone level
		 */

		//TODO: move process() to the update method & save one stack in an internal inventory
		boolean redstoneHigh = worldObj != null && worldObj.isBlockIndirectlyGettingPowered(pos) > 0;

		isShutdown = TaamUtil.isShutdown(TaamUtil.RANDOM, redstoneMode, redstoneHigh);

		if(isShutdown) {
			if(!simulate) resetTimeout();
			return 0;
		}


		/*
		 * Check Recipe
		 */

		IProcessingRecipeFluidBased recipe = getRecipe(stack);

		if (recipe == null) {
			if(!simulate) resetTimeout();
			return 0;
		}

		/*
		 * Check fluid requirements
		 */

		FluidStack inputFluid = recipe.getInputFluid();
		int amount = pipeEndIn.getFluidAmount(inputFluid);
		if (amount < inputFluid.amount) {
			if(!simulate) resetTimeout();
			return 0;
		}

		/*
		 * Check timeout, only if we actually can process.
		 */

		if(timeout > 0) {
			if(!simulate) timeout--;
			return 0;
		}

		/*
		 * Consume fluid
		 */

		amount = simulate ? pipeEndIn.getFluidAmount(inputFluid) : pipeEndIn.removeFluid(inputFluid);
		if (amount != inputFluid.amount) {
			// Not enough, back into the pipe.
			if(!simulate) {
				int reinserted = pipeEndIn.addFluid(new FluidStack(inputFluid, amount));
				if (reinserted != amount) {
					// This should not happen!
					Log.error(
							"Unexpected discrepance between getFluidAmound and removeFluid could not be resolved. Fluid was potentially lost. Asked for {}, got {}, reinserted {}. This is an issue. Report it with the developers of Taam!",
							inputFluid.amount, amount, reinserted);
				}
			}
			return 0;
		}

		/*
		 * Set Output Backlog
		 */
		FluidStack outputFluid = recipe.getOutputFluid(stack, inputFluid);

		if(!simulate) backlog = outputFluid.copy();

		return recipe.getInput().stackSize;
	}

	/*
	 * IRotatable implementation
	 */

	@Override
	public EnumFacing getFacingDirection() {
		return direction;
	}

	@Override
	public EnumFacing getNextFacingDirection() {
		return direction.rotateY();
	}

	@Override
	public void setFacingDirection(EnumFacing direction) {
		if (direction.getAxis() == Axis.Y) {
			return;
		}
		this.direction = direction;

		pipeEndOut.setSide(direction);
		pipeEndIn.setSide(direction.getOpposite());

		updateOcclusion();

		// TODO: updateState(false, true, true);
	}
}
