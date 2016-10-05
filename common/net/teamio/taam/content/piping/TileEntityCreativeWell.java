package net.teamio.taam.content.piping;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.teamio.taam.Config;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.piping.PipeEnd;
import net.teamio.taam.piping.PipeUtil;
import net.teamio.taam.util.FluidHandlerCreative;
import net.teamio.taam.util.FluidUtils;

public class TileEntityCreativeWell extends BaseTileEntity implements ITickable, IWorldInteractable, IFluidHandler {

	private final PipeEnd[] pipeEnds;
	private final FluidHandlerCreative fluidHandler;

	private static final int capacity = Integer.MAX_VALUE;

	public TileEntityCreativeWell() {
		pipeEnds = new PipeEnd[6];
		for (EnumFacing side : EnumFacing.VALUES) {
			int index = side.ordinal();
			pipeEnds[index] = new PipeEnd(side, capacity, true);
			pipeEnds[index].setPressure(Config.pl_creativewell_pressure);
		}
		fluidHandler = new FluidHandlerCreative();
	}

	@Override
	public String getName() {
		return "tile.taam.machines.creativewell.name";
	}

		/*
	BEGIN BACKPORT for old IFluidHandler
	 */

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return fluidHandler.canDrain(from, fluid);
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return fluidHandler.canFill(from, fluid);
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		return fluidHandler.drain(from, maxDrain, doDrain);
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		return fluidHandler.drain(from, resource, doDrain);
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return fluidHandler.getTankInfo(from);
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		return fluidHandler.fill(from, resource, doFill);
	}

	/*
	END BACKPORT for old IFluidHandler
	 */

	@Override
	public void update() {
		for (EnumFacing side : EnumFacing.VALUES) {
			int index = side.ordinal();
			if(fluidHandler.template != null) {
				pipeEnds[index].addFluid(fluidHandler.template);
			}
			PipeUtil.processPipes(pipeEnds[index], worldObj, pos);
		}

	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		if(fluidHandler.template != null) {
			NBTTagCompound fluidTag = new NBTTagCompound();
			fluidHandler.template.writeToNBT(fluidTag);
			tag.setTag("fluid", fluidTag);
		}
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		NBTTagCompound fluidTag = tag.getCompoundTag("fluid");
		if(fluidTag == null) {
			fluidHandler.template = null;
		} else {
			fluidHandler.template = FluidStack.loadFluidStackFromNBT(fluidTag);
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == Taam.CAPABILITY_PIPE) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == Taam.CAPABILITY_PIPE) {
			int index = facing.ordinal();
			return (T) pipeEnds[index];
		}
		return null;
	}

	/*
	 * IWorldInteractable implementation
	 */

	@Override
	public boolean onBlockActivated(World world, EntityPlayer player, boolean hasWrench, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		ItemStack stack = player.inventory.getStackInSlot(player.inventory.currentItem);
		if(stack == null) {
			fluidHandler.template = null;
		} else {
			fluidHandler.template = FluidUtils.getFluidFromItem(stack);
			if(fluidHandler.template != null) {
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
