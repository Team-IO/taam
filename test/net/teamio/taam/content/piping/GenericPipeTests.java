package net.teamio.taam.content.piping;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.blockunit.TestingHarness;
import net.teamio.taam.piping.IPipe;

/**
 * Created by oliver on 2017-07-04.
 */
public class GenericPipeTests {

	/**
	 * Used in other test classes to test a pipe end / pipe implementation with a generic test set.
	 *
	 * @param t
	 * @param pipe
	 * @param capacity
	 */
	public static void testPipeEnd(TestingHarness t, IPipe pipe, int capacity, boolean singularCapacity) {
		int amount;

		// Insert LAVA
		amount = pipe.addFluid(new FluidStack(FluidRegistry.LAVA, 5));

		t.assertEquals(5, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		t.assertEquals(5, amount);

		// Insert WATER
		amount = pipe.addFluid(new FluidStack(FluidRegistry.WATER, 5));
		if(singularCapacity) {
			t.assertEquals(0, amount);
			t.assertEquals(5, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
			t.assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));
		} else {
			t.assertEquals(5, amount);
			t.assertEquals(5, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
			t.assertEquals(5, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));
		}


		// Drain WATER
		amount = pipe.removeFluid(new FluidStack(FluidRegistry.WATER, 5));
		if(singularCapacity) {
			t.assertEquals(0, amount);
		} else {
			t.assertEquals(5, amount);
		}
		t.assertEquals(5, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		t.assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));

		// Drain LAVA
		amount = pipe.removeFluid(new FluidStack(FluidRegistry.LAVA, 5));
		t.assertEquals(5, amount);
		t.assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		t.assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));
		t.assertNotNull(pipe.getFluids());
		t.assertEquals(0, pipe.getFluids().size());

		// Drain LAVA more
		amount = pipe.removeFluid(new FluidStack(FluidRegistry.LAVA, 5));
		t.assertEquals(0, amount);
		t.assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		t.assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));
		t.assertNotNull(pipe.getFluids());
		t.assertEquals(0, pipe.getFluids().size());

		// Insert WATER
		amount = pipe.addFluid(new FluidStack(FluidRegistry.WATER, 5));
		t.assertEquals(5, amount);

		t.assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		t.assertEquals(5, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));

		// Insert WATER above capacity
		amount = pipe.addFluid(new FluidStack(FluidRegistry.WATER, capacity + 50));
		t.assertEquals(capacity - 5, amount);

		t.assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		t.assertEquals(capacity, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));

		// Drain WATER more than inside
		amount = pipe.removeFluid(new FluidStack(FluidRegistry.WATER, capacity + 50));
		t.assertEquals(capacity, amount);

		t.assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		t.assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));
		t.assertNotNull(pipe.getFluids());
		t.assertEquals(0, pipe.getFluids().size());
	}
}
