package net.teamio.taam.content.piping;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.taam.Config;
import net.teamio.taam.Log;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IRedstoneControlled;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.piping.IPipe;
import net.teamio.taam.piping.IPipeTE;
import net.teamio.taam.piping.PipeEndRestricted;
import net.teamio.taam.piping.PipeUtil;
import net.teamio.taam.recipes.IProcessingRecipeFluidBased;
import net.teamio.taam.recipes.ProcessingRegistry;
import net.teamio.taam.util.ProcessingUtil;
import net.teamio.taam.util.TaamUtil;
import net.teamio.taam.util.inv.InventoryUtils;

public class TileEntityFluidDrier extends BaseTileEntity implements IRotatable, ITickable, IPipeTE {

	private EnumFacing direction = EnumFacing.NORTH;
	
	private PipeEndRestricted pipeEndIn;
	
	private FluidStack lastInputFluid;
	private IProcessingRecipeFluidBased[] matchingRecipes;

	private ItemStack[] backlog;

	private byte redstoneMode = IRedstoneControlled.MODE_ACTIVE_ON_LOW;
	private static final int capacity = 1000;
	private int timeout;
	
	/**
	 * Just for rendering purposes we keep this here.
	 */
	public boolean isShutdown;
	
	public TileEntityFluidDrier() {
		pipeEndIn = new PipeEndRestricted(EnumFacing.UP, capacity, false);
		resetTimeout();
	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setBoolean("isShutdown", isShutdown);
		tag.setInteger("timeout", timeout);

		tag.setInteger("direction", direction.ordinal());

		if (backlog != null) {
			tag.setTag("holdback", InventoryUtils.writeItemStacksToTagSequential(backlog));
		}

		NBTTagCompound tagIn = new NBTTagCompound();
		pipeEndIn.writeToNBT(tagIn);
		tag.setTag("pipeEndIn", tagIn);
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		isShutdown = tag.getBoolean("isShutdown");
		timeout = tag.getInteger("timeout");

		direction = EnumFacing.getFront(tag.getInteger("direction"));
		if (direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
			direction = EnumFacing.NORTH;
		}

		NBTTagList holdbackList = tag.getTagList("holdback", NBT.TAG_COMPOUND);
		if (holdbackList == null) {
			backlog = null;
		} else {
			backlog = new ItemStack[holdbackList.tagCount()];
			InventoryUtils.readItemStacksFromTagSequential(backlog, holdbackList);
		}

		NBTTagCompound tagIn = tag.getCompoundTag("pipeEndIn");
		if (tagIn != null) {
			pipeEndIn.readFromNBT(tagIn);
		}
	}
	
	@Override
	public void update() {
		PipeUtil.processPipes(pipeEndIn, worldObj, pos);

		if(worldObj.isRemote) {
			return;
		}
		
		if(process()) {
			updateState(false, false, false);
		}
		
	}
	
	private boolean process() {
		BlockPos down = pos.down();
		
		/*
		 * Check blocked & fetch output inventory
		 */
		
		IInventory outputInventory = InventoryUtils.getInventory(worldObj, down);
		if(outputInventory == null && !TaamUtil.canDropIntoWorld(worldObj, down)) {
			resetTimeout();
			return false;
		}
		
		/*
		 * Check redstone level
		 */
		
		boolean redstoneHigh = worldObj.isBlockIndirectlyGettingPowered(pos) > 0;
		
		boolean newShutdown = TaamUtil.isShutdown(worldObj.rand, redstoneMode, redstoneHigh);
		
		if(isShutdown != newShutdown) {
			isShutdown = newShutdown;
			resetTimeout();
			return true;
		}
		
		/*
		 * Output Backlog
		 */
		
		// Output the backlog. Returns true if there were items transferred or there are still items left.
		if(ProcessingUtil.chuteMechanicsOutput(worldObj, down, outputInventory, backlog, 0)) {
			resetTimeout();
			return true;
		}

		backlog = null;
		
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
		
		backlog = recipe.getOutput(null, worldObj.rand);
		resetTimeout();
		return true;
	}
	
	private void resetTimeout() {
		timeout = Config.pl_processor_fluid_drier_timeout;
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
		
		updateState(false, true, true);
	}
	
	/*
	 * IPipeTE implementation
	 */
	
	@Override
	public IPipe[] getPipesForSide(EnumFacing side) {
		if (side == EnumFacing.UP) {
			return pipeEndIn.asPipeArray();
		} else {
			return null;
		}
	}
}
