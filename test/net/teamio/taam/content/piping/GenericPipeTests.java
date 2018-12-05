package net.teamio.taam.content.piping;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.taam.piping.IPipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by oliver on 2017-07-04.
 */
public class GenericPipeTests {

	/**
	 * Used in other test classes to test a pipe end / pipe implementation with a generic test set.
	 *
	 * @param pipe
	 * @param capacity
	 */
	public static void testPipeEnd(IPipe pipe, int capacity, boolean singularCapacity) {
		int amount;

		// Insert LAVA
		FluidStack stack = new FluidStack(FluidRegistry.LAVA, 5);
		amount = pipe.addFluid(stack);

		assertEquals(5, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		assertEquals(5, amount);


		/*
		Test: Inserting fluidstack does not modify original fluid stack
		+ modifying stack does not change amount in pipe
		 */
		assertEquals(5, stack.amount);
		stack.amount = 0;
		amount = pipe.getFluidAmount(stack);
		assertEquals(5, amount);
		stack.amount = 5;

		amount = pipe.addFluid(stack);
		assertEquals(5, stack.amount);
		assertEquals(10, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		assertEquals(5, amount);

		amount = pipe.removeFluid(stack);
		assertEquals(5, stack.amount);
		assertEquals(5, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		assertEquals(5, amount);

		// Insert WATER
		amount = pipe.addFluid(new FluidStack(FluidRegistry.WATER, 5));
		if(singularCapacity) {
			assertEquals(0, amount);
			assertEquals(5, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
			assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));
		} else {
			assertEquals(5, amount);
			assertEquals(5, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
			assertEquals(5, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));
		}


		// Drain WATER
		amount = pipe.removeFluid(new FluidStack(FluidRegistry.WATER, 5));
		if(singularCapacity) {
			assertEquals(0, amount);
		} else {
			assertEquals(5, amount);
		}
		assertEquals(5, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));

		// Drain LAVA
		amount = pipe.removeFluid(new FluidStack(FluidRegistry.LAVA, 5));
		assertEquals(5, amount);
		assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));
		assertNotNull(pipe.getFluids());
		assertEquals(0, pipe.getFluids().size());

		// Drain LAVA more
		amount = pipe.removeFluid(new FluidStack(FluidRegistry.LAVA, 5));
		assertEquals(0, amount);
		assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));
		assertNotNull(pipe.getFluids());
		assertEquals(0, pipe.getFluids().size());

		// Insert WATER
		amount = pipe.addFluid(new FluidStack(FluidRegistry.WATER, 5));
		assertEquals(5, amount);

		assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		assertEquals(5, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));

		// Insert WATER above capacity
		amount = pipe.addFluid(new FluidStack(FluidRegistry.WATER, capacity + 50));
		assertEquals(capacity - 5, amount);

		assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		assertEquals(capacity, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));

		// Drain WATER more than inside
		amount = pipe.removeFluid(new FluidStack(FluidRegistry.WATER, capacity + 50));
		assertEquals(capacity, amount);

		assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));
		assertNotNull(pipe.getFluids());
		assertEquals(0, pipe.getFluids().size());
	}
}
