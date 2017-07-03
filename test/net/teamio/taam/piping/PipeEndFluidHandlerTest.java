package net.teamio.taam.piping;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.teamio.blockunit.TestMethod;
import net.teamio.blockunit.TestingHarness;

/**
 * Created by oliver on 2017-07-03.
 */
public class PipeEndFluidHandlerTest {

	private PipeEndFluidHandler pipeEndFluidHandler;
	private IFluidHandler fluidHandler;

	private static final int CAPACITY = 10;

	public PipeEndFluidHandlerTest() {
		fluidHandler = new FluidTank(CAPACITY);
		pipeEndFluidHandler = new PipeEndFluidHandler(fluidHandler, EnumFacing.UP, true);
	}

	@TestMethod
	public void basicFunctions(TestingHarness t) throws Exception {
		t.assertEquals(CAPACITY, pipeEndFluidHandler.getCapacity());
	}

	@TestMethod
	public void getInternalPipes(TestingHarness t) throws Exception {
		t.assertNull(pipeEndFluidHandler.getInternalPipes(null, null));
	}

	@TestMethod
	public void fluidAmount(TestingHarness t) throws Exception {
		int amount;

		// Insert LAVA
		amount = pipeEndFluidHandler.addFluid(new FluidStack(FluidRegistry.LAVA, 5));

		t.assertEquals(5, pipeEndFluidHandler.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		t.assertEquals(5, amount);

		// Insert WATER
		amount = pipeEndFluidHandler.addFluid(new FluidStack(FluidRegistry.WATER, 5));
		t.assertEquals(0, amount);

		t.assertEquals(5, pipeEndFluidHandler.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		t.assertEquals(0, pipeEndFluidHandler.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));

		// Drain WATER
		amount = pipeEndFluidHandler.removeFluid(new FluidStack(FluidRegistry.WATER, 5));
		t.assertEquals(0, amount);
		t.assertEquals(5, pipeEndFluidHandler.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		t.assertEquals(0, pipeEndFluidHandler.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));

		// Drain LAVA
		amount = pipeEndFluidHandler.removeFluid(new FluidStack(FluidRegistry.LAVA, 5));
		t.assertEquals(5, amount);
		t.assertEquals(0, pipeEndFluidHandler.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		t.assertEquals(0, pipeEndFluidHandler.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));
		t.assertNotNull(pipeEndFluidHandler.getFluids());
		t.assertEquals(0, pipeEndFluidHandler.getFluids().size());

		// Drain LAVA more
		amount = pipeEndFluidHandler.removeFluid(new FluidStack(FluidRegistry.LAVA, 5));
		t.assertEquals(0, amount);
		t.assertEquals(0, pipeEndFluidHandler.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		t.assertEquals(0, pipeEndFluidHandler.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));
		t.assertNotNull(pipeEndFluidHandler.getFluids());
		t.assertEquals(0, pipeEndFluidHandler.getFluids().size());

		// Insert WATER
		amount = pipeEndFluidHandler.addFluid(new FluidStack(FluidRegistry.WATER, 5));
		t.assertEquals(5, amount);

		t.assertEquals(0, pipeEndFluidHandler.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		t.assertEquals(5, pipeEndFluidHandler.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));

		// Insert WATER above capacity
		amount = pipeEndFluidHandler.addFluid(new FluidStack(FluidRegistry.WATER, CAPACITY + 50));
		t.assertEquals(CAPACITY - 5, amount);

		t.assertEquals(0, pipeEndFluidHandler.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		t.assertEquals(CAPACITY, pipeEndFluidHandler.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));

		// Drain WATER more than inside
		amount = pipeEndFluidHandler.removeFluid(new FluidStack(FluidRegistry.WATER, CAPACITY + 50));
		t.assertEquals(CAPACITY, amount);

		t.assertEquals(0, pipeEndFluidHandler.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0)));
		t.assertEquals(0, pipeEndFluidHandler.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));
		t.assertNotNull(pipeEndFluidHandler.getFluids());
		t.assertEquals(0, pipeEndFluidHandler.getFluids().size());
	}

}