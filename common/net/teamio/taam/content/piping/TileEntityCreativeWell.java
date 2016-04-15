package net.teamio.taam.content.piping;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.teamio.taam.Log;
import net.teamio.taam.content.BaseTileEntity;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.piping.PipeEnd;
import net.teamio.taam.piping.PipeUtil;

public class TileEntityCreativeWell extends BaseTileEntity implements IFluidHandler, ITickable, IWorldInteractable {

	private final PipeEnd[] pipeEnds;

	private FluidStack fluid;
	
	private static final int capacity = Integer.MAX_VALUE; 
	
	public TileEntityCreativeWell() {
		pipeEnds = new PipeEnd[6];
		for (EnumFacing side : EnumFacing.VALUES) {
			int index = side.ordinal();
			pipeEnds[index] = new PipeEnd(side, capacity, true);
			pipeEnds[index].setPressure(20);
		}
	}

	@Override
	public void update() {
		for (EnumFacing side : EnumFacing.VALUES) {
			int index = side.ordinal();
			if(fluid != null) {
				pipeEnds[index].addFluid(fluid);
			}
			PipeUtil.processPipes(pipeEnds[index], worldObj, pos);
		}

	}

	@Override
	protected void writePropertiesToNBT(NBTTagCompound tag) {
		if(fluid != null) {
			NBTTagCompound fluidTag = new NBTTagCompound();
			fluid.writeToNBT(fluidTag);
			tag.setTag("fluid", fluidTag);
		}
	}

	@Override
	protected void readPropertiesFromNBT(NBTTagCompound tag) {
		NBTTagCompound fluidTag = tag.getCompoundTag("fluid");
		if(fluidTag == null) {
			fluid = null;
		} else {
			fluid = FluidStack.loadFluidStackFromNBT(fluidTag);
		}
	}
	
	/*
	 * IPipeTE implementation
	 */
	
//	@Override
//	public IPipe[] getPipesForSide(EnumFacing side) {
//		int index = side.ordinal();
//		return new IPipe[] { pipeEnds[index] };
//	}

	/*
	 * IFluidHandler implementation
	 */
	
	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		if(fluid != null && resource != null && resource.isFluidEqual(fluid)) {
			return resource;
		} else {
			return null;
		}
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		if(fluid == null) {
			return null;
		} else {
			return new FluidStack(fluid, maxDrain);
		}
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return this.fluid != null && this.fluid.getFluid() == fluid;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return new FluidTankInfo[] { new FluidTankInfo(fluid, capacity) };
	}
	
	/*
	 * IWorldInteractable implementation
	 */
	
	@Override
	public boolean onBlockActivated(World world, EntityPlayer player, boolean hasWrench, EnumFacing side, float hitX,
			float hitY, float hitZ) {
		ItemStack stack = player.inventory.getStackInSlot(player.inventory.currentItem);
		if(stack == null) {
			fluid = null;
		} else {
			fluid = FluidContainerRegistry.getFluidForFilledItem(stack);
			fluid.amount = capacity;
			Log.debug("Set creative well fluid to " + fluid);
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
