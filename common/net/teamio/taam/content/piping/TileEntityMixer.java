package net.teamio.taam.content.piping;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.taam.Log;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorAwareTE;
import net.teamio.taam.piping.IPipe;
import net.teamio.taam.piping.IPipeTE;
import net.teamio.taam.piping.PipeEnd;
import net.teamio.taam.piping.PipeEndRestricted;
import net.teamio.taam.piping.PipeUtil;
import net.teamio.taam.recipes.IProcessingRecipeFluidBased;
import net.teamio.taam.recipes.ProcessingRegistry;

public class TileEntityMixer extends BaseTileEntity implements IRotatable, IConveyorAwareTE, ITickable, IPipeTE {

	private EnumFacing direction = EnumFacing.NORTH;
	
	private PipeEndRestricted pipeEndIn;
	private PipeEnd pipeEndOut;
	
	private static final int capacity = 50;
	
	public TileEntityMixer() {
		pipeEndOut = new PipeEnd(direction, capacity, false);
		pipeEndIn = new PipeEndRestricted(direction.getOpposite(), capacity, false);
	}
	
	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setInteger("direction", direction.ordinal());
		
		NBTTagCompound tagIn = new NBTTagCompound();
		pipeEndIn.writeToNBT(tagIn);
		tag.setTag("pipeEndIn", tagIn);

		NBTTagCompound tagOut = new NBTTagCompound();
		pipeEndOut.writeToNBT(tagOut);
		tag.setTag("pipeEndOut", tagOut);
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
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

	private FluidStack lastInputFluid;
	private IProcessingRecipeFluidBased[] matchingRecipes;
	
	@Override
	public void update() {
		// Output backlog of already processed stuff
		if(backlog != null) {
			backlog.amount -= pipeEndOut.addFluid(backlog);
			if(backlog.amount <= 0) {
				backlog = null;
			}
		}
		PipeUtil.processPipes(pipeEndIn, worldObj, pos);
		PipeUtil.processPipes(pipeEndOut, worldObj, pos);
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
		if(lastInputFluid == null) {
			lastInputFluid = inside;
			matchingRecipes = ProcessingRegistry.getRecipes(ProcessingRegistry.MIXER, lastInputFluid);
			for(IProcessingRecipeFluidBased recipe : matchingRecipes) {
				if(recipe.inputMatches(stack)) {
					return recipe;
				}
			}
		}
		return null;
	}
	
	private FluidStack backlog;
	
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
		FluidStack outputFluid = recipe.getOutputFluid(stack, inputFluid, worldObj.rand);
		
		/*
		 * Add to backlog
		 */
		
		backlog = outputFluid.copy();
		Log.debug("Mixer Produced {}", backlog);
		
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
		
		blockUpdate();
		updateState();
		worldObj.notifyNeighborsOfStateChange(pos, blockType);
	}
	
	/*
	 * IPipeTE implementation
	 */
	
	@Override
	public IPipe[] getPipesForSide(EnumFacing side) {
		if (side == direction) {
			return pipeEndOut.asPipeArray();
		} else if (side == direction.getOpposite()) {
			return pipeEndIn.asPipeArray();
		} else {
			return null;
		}
	}

	/*
	 * IConveyorAwareTE implementation
	 */
	
	@Override
	public boolean canSlotMove(int slot) {
		return false;
	}

	@Override
	public boolean isSlotAvailable(int slot) {
		switch(direction) {
		default:
		case SOUTH:
		case NORTH:
			return slot == 6 || slot == 7 || slot == 8
			|| slot == 0 || slot == 1 || slot == 2;
		case EAST:
		case WEST:
			return slot == 2 || slot == 5 || slot == 8
			|| slot == 0 || slot == 3 || slot == 6;
		}
	}

	@Override
	public int getMovementProgress(int slot) {
		return 0;
	}

	@Override
	public byte getSpeedsteps() {
		return 0;
	}

	@Override
	public int insertItemAt(ItemStack item, int slot) {
		if(isSlotAvailable(slot)) {
			return process(item);
		}
		return 0;
	}

	@Override
	public EnumFacing getMovementDirection() {
		return EnumFacing.DOWN;
	}

	@Override
	public ItemWrapper getSlot(int slot) {
		return ItemWrapper.EMPTY;
	}

	@Override
	public EnumFacing getNextSlot(int slot) {
		return EnumFacing.DOWN;
	}

	@Override
	public boolean shouldRenderItemsDefault() {
		return false;
	}

	@Override
	public double getInsertMaxY() {
		return 1;
	}

	@Override
	public double getInsertMinY() {
		return .4f;
	}

}
