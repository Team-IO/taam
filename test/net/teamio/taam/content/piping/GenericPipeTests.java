package net.teamio.taam.content.piping;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.taam.piping.IPipe;
import org.junit.jupiter.api.Assertions;

/**
 * Created by oliver on 2017-07-04.
 */
public class GenericPipeTests {

	/**
	 * Used in other test classes to test a pipe end / pipe implementation with a generic test set.
	 *
	 * @param pipe             A pipe object which is to be tested for some common operations like filling and draining.
	 * @param capacity         Maximum capacity of this pipe object.
	 * @param singularCapacity true if this pipe object can only accept a single fluid at the same time.
	 */
	public static void testPipeEnd(IPipe pipe, int capacity, boolean singularCapacity) {
		// Precondition for this test run
		Assertions.assertNotNull(pipe, "Pipe object null, cannot run test");
		Assertions.assertTrue(capacity >= 0, "Negative capacity not supported by test");

		int amount;

		// Insert LAVA
		FluidStack stack = new FluidStack(FluidRegistry.LAVA, 5);
		amount = pipe.addFluid(stack);

		Assertions.assertEquals(5, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		Assertions.assertEquals(5, amount);


		/*
		Test: Inserting fluidstack does not modify original fluid stack
		+ modifying stack does not change amount in pipe
		 */
		Assertions.assertEquals(5, stack.amount);
		stack.amount = 0;
		amount = pipe.getFluidAmount(stack);
		Assertions.assertEquals(5, amount);
		stack.amount = 5;

		amount = pipe.addFluid(stack);
		Assertions.assertEquals(5, stack.amount);
		Assertions.assertEquals(10, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		Assertions.assertEquals(5, amount);

		amount = pipe.removeFluid(stack);
		Assertions.assertEquals(5, stack.amount);
		Assertions.assertEquals(5, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		Assertions.assertEquals(5, amount);

		// Insert WATER
		amount = pipe.addFluid(new FluidStack(FluidRegistry.WATER, 5));
		if (singularCapacity) {
			Assertions.assertEquals(0, amount);
			Assertions.assertEquals(5, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
			Assertions.assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));
		} else {
			Assertions.assertEquals(5, amount);
			Assertions.assertEquals(5, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
			Assertions.assertEquals(5, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));
		}


		// Drain WATER
		amount = pipe.removeFluid(new FluidStack(FluidRegistry.WATER, 5));
		if (singularCapacity) {
			Assertions.assertEquals(0, amount);
		} else {
			Assertions.assertEquals(5, amount);
		}
		Assertions.assertEquals(5, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		Assertions.assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));

		// Drain LAVA
		amount = pipe.removeFluid(new FluidStack(FluidRegistry.LAVA, 5));
		Assertions.assertEquals(5, amount);
		Assertions.assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		Assertions.assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));
		Assertions.assertNotNull(pipe.getFluids());
		Assertions.assertEquals(0, pipe.getFluids().size());

		// Drain LAVA more
		amount = pipe.removeFluid(new FluidStack(FluidRegistry.LAVA, 5));
		Assertions.assertEquals(0, amount);
		Assertions.assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		Assertions.assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));
		Assertions.assertNotNull(pipe.getFluids());
		Assertions.assertEquals(0, pipe.getFluids().size());

		// Insert WATER
		amount = pipe.addFluid(new FluidStack(FluidRegistry.WATER, 5));
		Assertions.assertEquals(5, amount);

		Assertions.assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		Assertions.assertEquals(5, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));

		// Insert WATER above capacity
		amount = pipe.addFluid(new FluidStack(FluidRegistry.WATER, capacity + 50));
		Assertions.assertEquals(capacity - 5, amount);

		Assertions.assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		Assertions.assertEquals(capacity, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));

		// Drain WATER more than inside
		amount = pipe.removeFluid(new FluidStack(FluidRegistry.WATER, capacity + 50));
		Assertions.assertEquals(capacity, amount);

		Assertions.assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		Assertions.assertEquals(0, pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));
		Assertions.assertNotNull(pipe.getFluids());
		Assertions.assertEquals(0, pipe.getFluids().size());
	}
}
