package net.teamio.taam.piping;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.teamio.blockunit.TestMethod;
import net.teamio.blockunit.TestingHarness;
import net.teamio.taam.content.piping.GenericPipeTests;

/**
 * Created by oliver on 2017-07-03.
 */
public class PipeEndFluidHandlerTest {

	private final PipeEndFluidHandler pipeEndFluidHandler;
	private final IFluidHandler fluidHandler;

	private static final int CAPACITY = 10;

	public PipeEndFluidHandlerTest() {
		fluidHandler = new FluidTank(CAPACITY);
		pipeEndFluidHandler = new PipeEndFluidHandler(null, fluidHandler, EnumFacing.UP);
	}

	@TestMethod
	public void basicFunctions(TestingHarness t) {
		t.assertEquals(CAPACITY, pipeEndFluidHandler.getCapacity());
	}

	@TestMethod
	public void getInternalPipes(TestingHarness t) {
		t.assertNull(pipeEndFluidHandler.getInternalPipes());
	}

	@TestMethod
	public void fluidAmount(TestingHarness t) {
		GenericPipeTests.testPipeEnd(t, pipeEndFluidHandler, CAPACITY, true);
	}

}