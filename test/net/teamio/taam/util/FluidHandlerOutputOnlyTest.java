package net.teamio.taam.util;

import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.junit.runner.RunWith;

import javax.annotation.Nullable;

/**
 * Created by oliver on 2018-06-17.
 */
@RunWith(VoltzTestRunner.class)
public class FluidHandlerOutputOnlyTest extends AbstractTest {

	HandlerDouble hd;
	FluidHandlerOutputOnly handlerOutputOnly;

	@Override
	public void setUpForTest(String name) {
		hd = new HandlerDouble();
		handlerOutputOnly = new FluidHandlerOutputOnly();
		handlerOutputOnly.origin = hd;
	}

	public void testGetTankProperties() {
		IFluidTankProperties[] tankProperties = handlerOutputOnly.getTankProperties();

		assertEquals(1, tankProperties.length);
	}

	public void testFill() {
		handlerOutputOnly.fill(new FluidStack(FluidRegistry.LAVA, 300), true);

		assertEquals(300, hd.amountRequested);
		assertTrue(hd.doFlag);


		handlerOutputOnly.fill(new FluidStack(FluidRegistry.LAVA, 200), false);

		assertEquals(200, hd.amountRequested);
		assertFalse(hd.doFlag);
	}

	public void testDrain_resource() {
		handlerOutputOnly.drain(new FluidStack(FluidRegistry.LAVA, 300), true);

		assertEquals(0, hd.amountRequested);
		assertFalse(hd.doFlag);


		handlerOutputOnly.drain(new FluidStack(FluidRegistry.LAVA, 200), false);

		assertEquals(0, hd.amountRequested);
		assertFalse(hd.doFlag);
	}

	public void testDrain_maxDrain() {
		handlerOutputOnly.drain(300, true);

		assertEquals(0, hd.amountRequested);
		assertFalse(hd.doFlag);


		handlerOutputOnly.drain(200, false);

		assertEquals(0, hd.amountRequested);
		assertFalse(hd.doFlag);
	}

	public static class HandlerDouble implements IFluidHandler {
		public int amountRequested;
		public boolean doFlag;

		@Override
		public IFluidTankProperties[] getTankProperties() {
			// Return a non-empty tank properties array, but the actual content is irrelevant
			return new IFluidTankProperties[]{null};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) {
			amountRequested = resource.amount;
			doFlag = doFill;
			return resource.amount;
		}

		@Nullable
		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
			amountRequested = resource.amount;
			doFlag = doDrain;
			return new FluidStack(FluidRegistry.LAVA, resource.amount);
		}

		@Nullable
		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {
			amountRequested = maxDrain;
			doFlag = doDrain;
			return new FluidStack(FluidRegistry.LAVA, maxDrain);
		}
	}
}
