package net.teamio.taam.util;

import com.builtbroken.mc.testing.junit.AbstractTest;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.teamio.taam.TestUtil;

import javax.annotation.Nullable;

/**
 * Created by oliver on 2018-06-18.
 */
public class FluidUtilsTest extends AbstractTest {

	@Override
	public void setUpForEntireClass() {
		CapabilityFluidHandler.register();
		CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY = TestUtil.getCapability(IFluidHandler.class);
	}

	public void testGetFluidHandler_capability() {
		ICapabilityProvider provider = new ICapabilityProvider() {
			@Override
			public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
				return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
			}

			@Override
			@SuppressWarnings("unchecked")
			public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
				if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
					return (T)new IFluidHandler() {

						@Override
						public IFluidTankProperties[] getTankProperties() {
							return new IFluidTankProperties[0];
						}

						@Override
						public int fill(FluidStack resource, boolean doFill) {
							return 0;
						}

						@Nullable
						@Override
						public FluidStack drain(FluidStack resource, boolean doDrain) {
							return null;
						}

						@Nullable
						@Override
						public FluidStack drain(int maxDrain, boolean doDrain) {
							return null;
						}
					};
				}
				return null;
			}
		};

		IFluidHandler handler = FluidUtils.getFluidHandler(provider, EnumFacing.UP);

		assertNotNull(handler);
	}

	public void testGetFluidHandler_noCapability() {
		ICapabilityProvider provider = new ICapabilityProvider() {
			@Override
			public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
				return false;
			}

			@Override
			public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
				return null;
			}
		};

		IFluidHandler handler = FluidUtils.getFluidHandler(provider, EnumFacing.UP);

		assertNull(handler);
	}

	public void testGetFluidHandler_null() {
		ICapabilityProvider provider = null;

		IFluidHandler handler = FluidUtils.getFluidHandler(provider, EnumFacing.UP);

		assertNull(handler);
	}

	public void testGetFluidHandler_legacyFluidHandler() {
		@SuppressWarnings("deprecation")
		class FakeHandler implements net.minecraftforge.fluids.IFluidHandler, ICapabilityProvider {

			/*
			 * ICapabilityProvider
			 */

			@Override
			public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
				return false;
			}

			@Override
			public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
				return null;
			}

			/*
			 * net.minecraftforge.fluids.IFluidHandler
			 */

			@Override
			public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
				return 0;
			}

			@Override
			public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
				return null;
			}

			@Override
			public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
				return null;
			}

			@Override
			public boolean canFill(EnumFacing from, Fluid fluid) {
				return false;
			}

			@Override
			public boolean canDrain(EnumFacing from, Fluid fluid) {
				return false;
			}

			@Override
			public FluidTankInfo[] getTankInfo(EnumFacing from) {
				return new FluidTankInfo[0];
			}


		}

		ICapabilityProvider provider = new FakeHandler();

		IFluidHandler handler = FluidUtils.getFluidHandler(provider, EnumFacing.UP);

		assertNotNull(handler);
	}

	public void testGetFluidHandler_fallbackFluidHandler() {
		class FakeHandler implements IFluidHandler, ICapabilityProvider {

			/*
			 * ICapabilityProvider
			 */

			@Override
			public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
				return false;
			}

			@Override
			public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
				return null;
			}

			/*
			 * IFluidHandler
			 */

			@Override
			public IFluidTankProperties[] getTankProperties() {
				return new IFluidTankProperties[0];
			}

			@Override
			public int fill(FluidStack resource, boolean doFill) {
				return 0;
			}

			@Nullable
			@Override
			public FluidStack drain(FluidStack resource, boolean doDrain) {
				return null;
			}

			@Nullable
			@Override
			public FluidStack drain(int maxDrain, boolean doDrain) {
				return null;
			}
		}

		ICapabilityProvider provider = new FakeHandler();

		IFluidHandler handler = FluidUtils.getFluidHandler(provider, EnumFacing.UP);

		assertNotNull(handler);
	}
}
