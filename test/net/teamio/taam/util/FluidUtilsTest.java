package net.teamio.taam.util;

import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.teamio.taam.TestUtil;
import org.junit.runner.RunWith;

import javax.annotation.Nullable;

/**
 * Created by oliver on 2018-06-18.
 */
@RunWith(VoltzTestRunner.class)
public class FluidUtilsTest extends AbstractTest {

	@Override
	public void setUpForEntireClass() {
		TestUtil.registerCapabilities();
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
				if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
					return (T) new IFluidHandler() {

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
