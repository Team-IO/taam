package net.teamio.taam.content.piping;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.teamio.taam.Config;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.piping.PipeEnd;
import net.teamio.taam.piping.PipeNetwork;
import net.teamio.taam.util.FluidHandlerCreative;
import net.teamio.taam.util.FluidUtils;

import javax.annotation.Nonnull;

public class TileEntityCreativeWell extends BaseTileEntity implements ITickable, IWorldInteractable {

	private final PipeEnd[] pipeEnds;
	private final FluidHandlerCreative fluidHandler;

	private static final int capacity = Integer.MAX_VALUE;

	public TileEntityCreativeWell() {
		pipeEnds = new PipeEnd[6];
		for (EnumFacing side : EnumFacing.VALUES) {
			int index = side.ordinal();
			pipeEnds[index] = new PipeEnd(this, side, capacity);
		}
		fluidHandler = new FluidHandlerCreative();
	}

	@Nonnull
	@Override
	public String getName() {
		return "tile.taam.machines.creativewell.name";
	}

	@Override
	public void update() {
		for (EnumFacing side : EnumFacing.VALUES) {
			int index = side.ordinal();
			if (fluidHandler.template != null) {
				pipeEnds[index].addFluid(fluidHandler.template);
			}
		}
	}

	@Override
	public void onLoad() {
		for (EnumFacing side : EnumFacing.VALUES) {
			int index = side.ordinal();
			PipeNetwork.NET.addPipe(pipeEnds[index]);
		}
	}

	@Override
	public void onChunkUnload() {
		for (EnumFacing side : EnumFacing.VALUES) {
			int index = side.ordinal();
			PipeNetwork.NET.removePipe(pipeEnds[index]);
		}
	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		if (fluidHandler.template != null) {
			NBTTagCompound fluidTag = new NBTTagCompound();
			fluidHandler.template.writeToNBT(fluidTag);
			tag.setTag("fluid", fluidTag);
		}
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		NBTTagCompound fluidTag = tag.getCompoundTag("fluid");
		fluidHandler.template = FluidStack.loadFluidStackFromNBT(fluidTag);
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nonnull EnumFacing facing) {
		if (capability == Taam.CAPABILITY_PIPE) {
			return true;
		}
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nonnull EnumFacing facing) {
		if (capability == Taam.CAPABILITY_PIPE) {
			int index = facing.ordinal();
			return (T) pipeEnds[index];
		}
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return (T) fluidHandler;
		}
		return super.getCapability(capability, facing);
	}

	/*
	 * IWorldInteractable implementation
	 */

	@Override
	public boolean onBlockActivated(World world, EntityPlayer player, EnumHand hand, boolean hasWrench, EnumFacing side,
	                                float hitX, float hitY, float hitZ) {
		ItemStack stack = player.inventory.getStackInSlot(player.inventory.currentItem);
		if (stack == null) {
			fluidHandler.template = null;
		} else {
			fluidHandler.template = FluidUtils.getFluidFromItem(stack);
			if (fluidHandler.template != null) {
				fluidHandler.template.amount = capacity;
				Log.debug("Set creative well fluid to " + fluidHandler.template);
			}
		}
		for (EnumFacing peSide : EnumFacing.VALUES) {
			int index = peSide.ordinal();
			pipeEnds[index].info.content.clear();
			pipeEnds[index].info.recalculateFillLevel();
		}
		updateState(true, false, true);
		return true;
	}

	@Override
	public boolean onBlockHit(World world, EntityPlayer player, boolean hasWrench) {
		return false;
	}
}
