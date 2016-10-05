package net.teamio.taam.rendering;

import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.teamio.taam.Taam;

/**
 * Render info used by the Capability {@link Taam#CAPABILITY_RENDER_TANK} to
 * provide info on where to render tanks on a model.
 *
 * @author Oliver Kahrmann
 *
 */
public class TankRenderInfo {
	private TankRenderInfo[] asArray;

	public AxisAlignedBB bounds;
    public FluidStack fluid;
    public int capacity;

	/**
	 * Value used to shrink the bounding boxes for rendering tank content, to
	 * avoid z-fighting.
	 */
	public static final float shrinkValue = -0.001f;

	public TankRenderInfo(AxisAlignedBB bounds) {
		this.bounds = bounds;
		capacity = 1;
	}

	public TankRenderInfo(AxisAlignedBB bounds, FluidTankInfo tankInfo) {
		this.bounds = bounds;
		setInfo(tankInfo);
	}

	public TankRenderInfo(AxisAlignedBB bounds, IFluidTank tank) {
		this.bounds = bounds;
		setInfo(tank);
	}

	public void setInfo(IFluidTank tank) {
		capacity = tank.getCapacity();
		fluid = tank.getFluid();
	}

	public void setInfo(FluidTankInfo tankInfo) {
		capacity = tankInfo.capacity;
		fluid = tankInfo.fluid;
	}

	/**
	 * Returns a single element array containing this object. The array is only
	 * created once. Useful to prevent having to create a new array on every use
	 * for entities with just this single tank.
	 *
	 * @return
	 */
	public TankRenderInfo[] asArray() {
		if (asArray == null) {
			asArray = new TankRenderInfo[] { this };
		}
		return asArray;
	}
}
