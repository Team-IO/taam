package net.teamio.taam.conveyors.appliances;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.teamio.taam.content.conveyors.ATileEntityAppliance;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;
import net.teamio.taam.piping.IPipe;
import net.teamio.taam.piping.IPipeTE;
import net.teamio.taam.piping.PipeEndFluidHandler;
import net.teamio.taam.piping.PipeUtil;
import net.teamio.taam.recipes.IProcessingRecipeFluidBased;
import net.teamio.taam.recipes.ProcessingRegistry;

public class ApplianceSprayer extends ATileEntityAppliance implements IFluidHandler, IPipeTE, ITickable {

	private static final int capacity = 2000;
	
	public ApplianceSprayer() {
		tank = new FluidTank(capacity);
		pipeEnd = new PipeEndFluidHandler(this, direction.getOpposite(), false);
	}
	
	private FluidTank tank;
	private PipeEndFluidHandler pipeEnd;

	private FluidStack lastInputFluid;
	private IProcessingRecipeFluidBased[] matchingRecipes;
	
	/**
	 * Checks if there is a recipe for the current input fluid and the provided
	 * item stack.
	 * 
	 * @param stack
	 * @return true if there is a recipe available, false if not. Also returns
	 *         false if there is no input fluid. Does not check for the amount
	 *         of fluid, so {@link #processItem(IConveyorApplianceHost, int, ItemWrapper)} may still fail.
	 */
	private IProcessingRecipeFluidBased getRecipe(ItemStack stack) {
		FluidStack inside = tank.getFluid();
		
		// If we have no remembered fluid, or a new fluid (empty tank is considered "same"), fetch new fluid
		if(inside != null) {
			if(lastInputFluid == null || !lastInputFluid.isFluidEqual(inside)) {
				lastInputFluid = inside;
			}
		}
		
		if(lastInputFluid == null) {
			return null;
		}
		if(matchingRecipes == null) {
			matchingRecipes = ProcessingRegistry.getRecipes(ProcessingRegistry.SPRAYER, lastInputFluid);
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
	
	public FluidTank getTank() {
		return tank;
	}
	
	@Override
	public void update() {
		PipeUtil.processPipes(pipeEnd, worldObj, pos);
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		super.readPropertiesFromNBT(tag);

		pipeEnd.setSide(direction.getOpposite());

		NBTTagCompound tagTank = tag.getCompoundTag("tank");
		if (tagTank != null) {
			tank.readFromNBT(tagTank);
		}
	}

	protected void writePropertiesToNBT(NBTTagCompound tag) {
		super.writePropertiesToNBT(tag);

		NBTTagCompound tagTank = new NBTTagCompound();
		tank.writeToNBT(tagTank);
		tag.setTag("tank", tagTank);
	};

	@Override
	public boolean processItem(IConveyorApplianceHost conveyor, int slot, ItemWrapper wrapper) {
		if (wrapper.itemStack == null) {
			return false;
		}

		boolean isProcessableSlot = slot == 4
				|| slot == ConveyorUtil.getSlot(direction)
				|| slot == ConveyorUtil.getSlot(direction.getOpposite());

		// We can only process what is directly in front of us
		if (!isProcessableSlot) {
			return false;
		}

		/*
		 * Fetch Recipe
		 */
		
		IProcessingRecipeFluidBased recipe = getRecipe(wrapper.itemStack);
		
		if(recipe == null) {
			wrapper.unblock();
			return true;
		}
		
		/*
		 * Fetch Output
		 */
		
		ItemStack result = recipe.getOutput(wrapper.itemStack, worldObj.rand)[0];
		result.stackSize = wrapper.itemStack.stackSize;
		
		// Fix for re-coloring to the same color (Output == Input)
		if(result.isItemEqual(wrapper.itemStack)) {
			wrapper.unblock();
			return true;
		}
		
		/*
		 * Check fluid requirements
		 */
		
		int requiredAmount = wrapper.itemStack.stackSize * recipe.getInputFluid().amount;
		
		FluidStack inTank = tank.getFluid();
		
		if(inTank == null || inTank.amount < requiredAmount) {
			wrapper.block();
			return true;
		}
		
		/*
		 * Consume fluid
		 */
		
		inTank.amount -= requiredAmount;
		if(inTank.amount == 0) {
			tank.setFluid(null);
		}
		
		/*
		 * Replace input stack with output
		 */
		
		wrapper.itemStack = result;
		wrapper.unblock();
		
		updateState(false, false, false);
		//TODO: Particles
		
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see net.teamio.taam.content.conveyors.ATileEntityAppliance#setFacingDirection(net.minecraft.util.EnumFacing)
	 * 
	 * Overridden because of the pipeEnd.
	 */
	@Override
	public void setFacingDirection(EnumFacing direction) {
		super.setFacingDirection(direction);

		pipeEnd.setSide(direction.getOpposite());
	}

	/*
	 * IPipeTE
	 */

	@Override
	public IPipe[] getPipesForSide(EnumFacing side) {
		if (side == direction.getOpposite()) {
			return pipeEnd.asPipeArray();
		} else {
			return null;
		}
	}

	/*
	 * IFluidHandler implementation
	 */

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		if (from != direction.getOpposite()) {
			return 0;
		}
		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		if (from != direction.getOpposite()) {
			return null;
		}
		if (tank.getFluid() == null || resource == null) {
			return null;
		}
		if (!tank.getFluid().isFluidEqual(resource)) {
			return null;
		}
		FluidStack drained = tank.drain(resource.amount, doDrain);
		if (tank.getFluidAmount() == 0) {
			tank.setFluid(null);
		}
		return drained;
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		if (from != direction.getOpposite()) {
			return null;
		}
		FluidStack drained = tank.drain(maxDrain, doDrain);
		if (tank.getFluidAmount() == 0) {
			tank.setFluid(null);
		}
		return drained;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		if (from != direction.getOpposite()) {
			return false;
		}
		if (fluid == null) {
			return false;
		}
		if (tank.getFluid() == null) {
			// TODO: Check recipes?
			return true;
		} else {
			return tank.getFluid().getFluid() == fluid;
		}
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		if (from != direction.getOpposite()) {
			return false;
		}
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return new FluidTankInfo[] { tank.getInfo() };
	}
}
