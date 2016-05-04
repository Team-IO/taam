package net.teamio.taam.content.piping;

import java.io.IOException;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.taam.Config;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.content.IRedstoneControlled;
import net.teamio.taam.machines.IMachine;
import net.teamio.taam.piping.PipeEndRestricted;
import net.teamio.taam.piping.PipeUtil;
import net.teamio.taam.recipes.IProcessingRecipeFluidBased;
import net.teamio.taam.recipes.ProcessingRegistry;
import net.teamio.taam.util.ProcessingUtil;
import net.teamio.taam.util.TaamUtil;
import net.teamio.taam.util.inv.InventoryUtils;

public class MachineFluidDrier implements IMachine {

	private PipeEndRestricted pipeEndIn;

	private FluidStack lastInputFluid;
	private IProcessingRecipeFluidBased[] matchingRecipes;

	private ItemStack[] backlog;

	private byte redstoneMode = IRedstoneControlled.MODE_ACTIVE_ON_LOW;
	private static final int capacity = 1000;
	private int timeout;
	
	public boolean isShutdown;
	
	public MachineFluidDrier() {
		pipeEndIn = new PipeEndRestricted(EnumFacing.UP, capacity, false);
		resetTimeout();
	}
	
	@Override
	public void writePropertiesToNBT(NBTTagCompound tag) {
		tag.setBoolean("isShutdown", isShutdown);
		tag.setInteger("timeout", timeout);

		if (backlog != null) {
			tag.setTag("holdback", InventoryUtils.writeItemStacksToTagSequential(backlog));
		}

		NBTTagCompound tagIn = new NBTTagCompound();
		pipeEndIn.writeToNBT(tagIn);
		tag.setTag("pipeEndIn", tagIn);
	}

	@Override
	public void readPropertiesFromNBT(NBTTagCompound tag) {
		isShutdown = tag.getBoolean("isShutdown");
		timeout = tag.getInteger("timeout");

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
		PipeUtil.processPipes(pipeEndIn, world, pos);

		if(world.isRemote) {
			return;
		}
		
		if(process(world, pos)) {
			//TODO: updateState(false, false, false);
		}
	}

	@Override
	public boolean renderUpdate(World world, BlockPos pos) {
		return false;
	}

	@Override
	public void blockUpdate(World world, BlockPos pos) {
	}
	

	
	private boolean process(World world, BlockPos pos) {
		BlockPos down = pos.down();
		
		/*
		 * Check blocked & fetch output inventory
		 */
		
		IInventory outputInventory = InventoryUtils.getInventory(world, down);
		if(outputInventory == null && !TaamUtil.canDropIntoWorld(world, down)) {
			resetTimeout();
			return false;
		}
		
		/*
		 * Check redstone level
		 */
		
		boolean redstoneHigh = world.isBlockIndirectlyGettingPowered(pos) > 0;
		
		boolean newShutdown = TaamUtil.isShutdown(world.rand, redstoneMode, redstoneHigh);
		
		if(isShutdown != newShutdown) {
			isShutdown = newShutdown;
			resetTimeout();
			return true;
		}
		
		/*
		 * Output Backlog
		 */
		
		// Output the backlog. Returns true if there were items transferred or there are still items left.
		if(ProcessingUtil.chuteMechanicsOutput(world, down, outputInventory, backlog, 0)) {
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
		
		backlog = recipe.getOutput(null);
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

	private static final float fromBorder = 2f/16;
	private static final float fromBorderOcclusion = 2f/16;
	public static final AxisAlignedBB bbCollision = new AxisAlignedBB(0, 0, 0, 1, 1-fromBorder, 1);
	public static final AxisAlignedBB bbCoolusion = new AxisAlignedBB(fromBorderOcclusion, fromBorderOcclusion, fromBorderOcclusion, 1-fromBorderOcclusion, 1-fromBorderOcclusion, 1-fromBorderOcclusion);

	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		if (mask.intersectsWith(bbCollision)) {
			list.add(bbCollision);
		}
	}

	@Override
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		list.add(bbCollision);
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
		if (capability == Taam.CAPABILITY_PIPE) {
			if (facing == EnumFacing.UP) {
				return (T) pipeEndIn;
			} else {
				return null;
			}
		}
		return null;
	}
}
