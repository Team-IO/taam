package net.teamio.taam.content.piping;

import java.io.IOException;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.SlotMatrix;
import net.teamio.taam.conveyors.api.ConveyorSlotsStatic;
import net.teamio.taam.machines.IMachine;
import net.teamio.taam.piping.PipeEnd;
import net.teamio.taam.piping.PipeEndRestricted;
import net.teamio.taam.piping.PipeUtil;
import net.teamio.taam.recipes.IProcessingRecipeFluidBased;
import net.teamio.taam.recipes.ProcessingRegistry;

public class MachineMixer implements IMachine, IRotatable {

	private EnumFacing direction = EnumFacing.NORTH;
	
	private PipeEndRestricted pipeEndIn;
	private PipeEnd pipeEndOut;
	
	private FluidStack backlog;

	private FluidStack lastInputFluid;
	private IProcessingRecipeFluidBased[] matchingRecipes;
	
	private static final int capacity = 2000;
	
	public static final AxisAlignedBB bounds = new AxisAlignedBB(0, MachinePipe.baseplateSize, 0, 1, 0.5f, 1);
	
	public static final AxisAlignedBB boundsPipeX = new AxisAlignedBB(
			0, MachinePipe.fromBorderFlange, MachinePipe.fromBorderFlange, 1,
			1 - MachinePipe.fromBorderFlange, 1 - MachinePipe.fromBorderFlange);
	public static final AxisAlignedBB boundsPipeZ = new AxisAlignedBB(
			MachinePipe.fromBorderFlange, MachinePipe.fromBorderFlange, 0,
			1 - MachinePipe.fromBorderFlange, 1 - MachinePipe.fromBorderFlange, 1);
	
	/**
	 * Conveyor Slot set for input on the sides
	 */
	private ConveyorSlotsStatic conveyorSlots = new ConveyorSlotsStatic() {
		
		{
			slotMatrix = new SlotMatrix(
					true, true, true,
					false, false, false,
					true, true, true);
			insertMaxY = 1;
			insertMinY = 0.4;
		}
		
		@Override
		public ItemStack removeItemAt(int slot) {
			return null;
		}
		
		@Override
		public int insertItemAt(ItemStack item, int slot) {
			if(isSlotAvailable(slot)) {
				return process(item);
			}
			return 0;
		}
	};
	
	public MachineMixer() {
		pipeEndOut = new PipeEnd(direction, capacity, false);
		pipeEndIn = new PipeEndRestricted(direction.getOpposite(), capacity, false);
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
	}

	@Override
	public void readPropertiesFromNBT(NBTTagCompound tag) {
		direction = EnumFacing.getFront(tag.getInteger("direction"));
		if(direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
			direction = EnumFacing.NORTH;
		}
		pipeEndOut.setSide(direction);
		pipeEndIn.setSide(direction.getOpposite());
		
		NBTTagCompound tagIn = tag.getCompoundTag("pipeEndIn");
		if(tagIn != null) {
			pipeEndIn.readFromNBT(tagIn);
		}

		NBTTagCompound tagOut = tag.getCompoundTag("pipeEndOut");
		if(tagOut != null) {
			pipeEndOut.readFromNBT(tagOut);
		}
	}

	public void writeUpdatePacket(PacketBuffer buf) {
		NBTTagCompound tag = new NBTTagCompound();
		writePropertiesToNBT(tag);
		buf.writeNBTTagCompoundToBuffer(tag);
	}

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
	public IBlockState getExtendedState(IBlockState state, World world, BlockPos blockPos) {
		renderUpdate(world, blockPos);
		return state;
	}

	@Override
	public String getModelPath() {
		return "taam:machine";
	}

	@Override
	public void update(World world, BlockPos pos) {
		// Output backlog of already processed stuff
		if(backlog != null) {
			backlog.amount -= pipeEndOut.addFluid(backlog);
			if(backlog.amount <= 0) {
				backlog = null;
			}
		}
		PipeUtil.processPipes(pipeEndIn, world, pos);
		PipeUtil.processPipes(pipeEndOut, world, pos);
	}

	@Override
	public boolean renderUpdate(World world, BlockPos pos) {
		return false;
	}

	@Override
	public void blockUpdate(World world, BlockPos pos) {
	}

	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		if (mask.intersectsWith(bounds)) {
			list.add(bounds);
		}
	}

	@Override
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		list.add(bounds);
		list.add(MachinePipe.bbBaseplate);
		if(direction.getAxis() == Axis.X) {
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
		if(capability == Taam.CAPABILITY_PIPE) {
			return facing.getAxis() == direction.getAxis();
		}
		if(capability == Taam.CAPABILITY_CONVEYOR) {
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
		if(capability == Taam.CAPABILITY_CONVEYOR) {
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
	 *         of fluid, so {@link #process(ItemStack)} may still fail.
	 */
	private IProcessingRecipeFluidBased getRecipe(ItemStack stack) {
		FluidStack inside = pipeEndIn.getFluid();
		if(inside == null) {
			lastInputFluid = null;
			matchingRecipes = null;
			return null;
		}
		if(lastInputFluid == null || !lastInputFluid.isFluidEqual(inside)) {
			lastInputFluid = inside;
			matchingRecipes = ProcessingRegistry.getRecipes(ProcessingRegistry.MIXER, lastInputFluid);
		}
		if(matchingRecipes != null) {
			for(IProcessingRecipeFluidBased recipe : matchingRecipes) {
				if(recipe.inputMatches(stack)) {
					return recipe;
				}
			}
		}
		return null;
	}
	
	/**
	 * Processes the item by consuming input fluid and generating output fluid if there is space in the output pipe end.
	 * @param stack
	 * @return the amount of items consumed.
	 */
	private int process(ItemStack stack) {
		// When there is backlog, we cannot process more
		if(backlog != null) {
			// Skip processing until next tick.
			return 0;
		}
		
		// Actual processing
		
		IProcessingRecipeFluidBased recipe = getRecipe(stack);
		
		if(recipe == null) {
			return 0;
		}
		
		//TODO: Speed limit, cooldown, ...
		
		/*
		 * Get input fluid
		 */
		
		FluidStack inputFluid = recipe.getInputFluid();
		int amount = pipeEndIn.getFluidAmount(inputFluid);
		if(amount < inputFluid.amount) {
			return 0;
		}
		amount = pipeEndIn.removeFluid(inputFluid);
		if(amount != inputFluid.amount) {
			// Not enough, back into the pipe.
			int reinserted = pipeEndIn.addFluid(new FluidStack(inputFluid, amount));
			if (reinserted != amount) {
				// This should not happen!
				Log.error(
						"Unexpected discrepance between getFluidAmound and removeFluid could not be resolved. Fluid was potentially lost. Asked for {}, got {}, reinserted {}. This is an issue. Report it with the developers of Taam!",
						inputFluid.amount, amount, reinserted);
			}
			return 0;
		}
		
		/*
		 * Get output fluid
		 */
		FluidStack outputFluid = recipe.getOutputFluid(stack, inputFluid);
		
		/*
		 * Add to backlog
		 */
		
		backlog = outputFluid.copy();
		
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
		if(direction.getAxis() == Axis.Y) {
			return;
		}
		this.direction = direction;
		
		pipeEndOut.setSide(direction);
		pipeEndIn.setSide(direction.getOpposite());
		
		//TODO: updateState(false, true, true);
	}
	

}
