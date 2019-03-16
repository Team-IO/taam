package net.teamio.taam.conveyors.appliances;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.teamio.taam.Config;
import net.teamio.taam.Taam;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.content.conveyors.ATileEntityAppliance;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.IConveyorApplianceHost;
import net.teamio.taam.conveyors.ItemWrapper;
import net.teamio.taam.piping.PipeEndFluidHandler;
import net.teamio.taam.piping.PipeNetwork;
import net.teamio.taam.piping.PipeUtil;
import net.teamio.taam.recipes.IProcessingRecipeFluidBased;
import net.teamio.taam.recipes.ProcessingRegistry;
import net.teamio.taam.rendering.TankRenderInfo;
import net.teamio.taam.util.InventoryUtils;

import javax.annotation.Nonnull;

public class ApplianceSprayer extends ATileEntityAppliance implements IWorldInteractable {

	public static final float b_tankBorder = 1.5f / 16f;
	public static final float b_tankBorderSprayer = b_tankBorder + 4f / 16f;
	public static final float b_basePlate = 2f / 16f;

	public static final AxisAlignedBB bounds_sprayer_tank = new AxisAlignedBB(
			b_tankBorder, b_basePlate, b_tankBorder,
			1 - b_tankBorder, 1 - 4f / 16, 1 - b_tankBorderSprayer
	).expand(TankRenderInfo.shrinkValue, TankRenderInfo.shrinkValue, TankRenderInfo.shrinkValue);

	private final FluidTank tank;
	private final PipeEndFluidHandler pipeEnd;
	private final TankRenderInfo tankRI = new TankRenderInfo(bounds_sprayer_tank);

	private FluidStack lastInputFluid;
	private IProcessingRecipeFluidBased[] matchingRecipes;

	public ApplianceSprayer() {
		tank = new FluidTank(Config.pl_sprayer_capacity) {
			@Override
			protected void onContentsChanged() {
				updateState(true, false, false);
			}
		};
		pipeEnd = new PipeEndFluidHandler(this, tank, direction.getOpposite());
	}

	@Override
	public void onLoad() {
		PipeNetwork.NET.addPipe(pipeEnd);
		super.onLoad();
	}

	@Override
	public void onChunkUnload() {
		PipeNetwork.NET.removePipe(pipeEnd);
		super.onChunkUnload();
	}

	@Nonnull
	@Override
	public String getName() {
		return "tile.taam.productionline_appliance.sprayer.name";
	}

	/**
	 * Checks if there is a recipe for the current input fluid and the provided
	 * item stack.
	 *
	 * @param stack Input item stack to be checked together with the current input fluid.
	 * @return true if there is a recipe available, false if not. Also returns
	 * false if there is no input fluid. Does not check for the amount
	 * of fluid, so {@link #processItem(IConveyorApplianceHost, int, ItemWrapper)} may still fail.
	 */
	private IProcessingRecipeFluidBased getRecipe(ItemStack stack) {
		FluidStack inside = tank.getFluid();

		// If we have no remembered fluid, or a new fluid (empty tank is considered "same"), fetch new fluid
		if (inside != null) {
			if (lastInputFluid == null || !lastInputFluid.isFluidEqual(inside)) {
				lastInputFluid = inside;
			}
		}

		if (lastInputFluid == null) {
			return null;
		}
		if (matchingRecipes == null) {
			matchingRecipes = ProcessingRegistry.getRecipes(ProcessingRegistry.SPRAYER, lastInputFluid);
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

	public FluidTank getTank() {
		return tank;
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		super.readPropertiesFromNBT(tag);

		pipeEnd.setSide(direction.getOpposite());

		NBTTagCompound tagTank = tag.getCompoundTag("tank");
		tank.readFromNBT(tagTank);
	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		super.writePropertiesToNBT(tag);

		NBTTagCompound tagTank = new NBTTagCompound();
		tank.writeToNBT(tagTank);
		tag.setTag("tank", tagTank);
	}

	/*
	 * IConveyorAppliance
	 */

	@Override
	public boolean processItem(IConveyorApplianceHost conveyor, int slot, ItemWrapper wrapper) {
		if (InventoryUtils.isEmpty(wrapper.itemStack)) {
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

		if (recipe == null) {
			wrapper.unblock();
			return true;
		}

		/*
		 * Fetch Output
		 */

		ItemStack result = recipe.getOutput(wrapper.itemStack)[0];
		result.stackSize = wrapper.itemStack.stackSize;

		// Fix for re-coloring to the same color (Output == Input)
		if (result.isItemEqual(wrapper.itemStack)) {
			wrapper.unblock();
			return true;
		}

		/*
		 * Check fluid requirements
		 */

		int requiredAmount = wrapper.itemStack.stackSize * recipe.getInputFluid().amount;

		FluidStack inTank = tank.getFluid();

		if (inTank == null || inTank.amount < requiredAmount) {
			wrapper.block();
			return true;
		}

		/*
		 * Consume fluid
		 */

		inTank.amount -= requiredAmount;
		if (inTank.amount == 0) {
			tank.setFluid(null);
		}

		/*
		 * Replace input stack with output
		 */

		wrapper.setStack(result);
		wrapper.unblock();

		markDirty();
		//TODO: Particles

		return true;
	}

	@Override
	public EnumFacing overrideNextSlot(IConveyorApplianceHost host, int slot, ItemWrapper wrapper,
	                                   EnumFacing beforeOverride) {
		return beforeOverride;
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
	 * IWorldInteractable implementation
	 */

	@Override
	public boolean onBlockActivated(World world, EntityPlayer player, EnumHand hand, boolean hasWrench, EnumFacing side,
	                                float hitX, float hitY, float hitZ) {
		boolean didSomething = PipeUtil.defaultPlayerInteraction(player, getTank());

		if (didSomething) {
			updateState(true, false, false);
		}
		return didSomething;
	}

	@Override
	public boolean onBlockHit(World world, EntityPlayer player, boolean hasWrench) {
		return false;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nonnull EnumFacing facing) {
		if (capability == Taam.CAPABILITY_PIPE) {
			return facing == pipeEnd.getSide();
		}
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return facing == direction.getOpposite();
		}
		if (capability == Taam.CAPABILITY_RENDER_TANK) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nonnull EnumFacing facing) {
		if (capability == Taam.CAPABILITY_PIPE && facing == pipeEnd.getSide()) {
			return (T) pipeEnd;
		}
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing == direction.getOpposite()) {
			return (T) tank;
		}
		if (capability == Taam.CAPABILITY_RENDER_TANK) {
			tankRI.setInfo(tank);
			return (T) tankRI.asArray();
		}
		return super.getCapability(capability, facing);
	}
}
