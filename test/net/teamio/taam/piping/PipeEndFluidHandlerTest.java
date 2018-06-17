package net.teamio.taam.piping;

import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.teamio.taam.content.piping.GenericPipeTests;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by oliver on 2017-07-03.
 */
@RunWith(VoltzTestRunner.class)
public class PipeEndFluidHandlerTest {

	private final PipeEndFluidHandler pipeEndFluidHandler;
	private final IFluidHandler fluidHandler;

	private static final int CAPACITY = 10;

	public PipeEndFluidHandlerTest() {
		fluidHandler = new FluidTank(CAPACITY);
		pipeEndFluidHandler = new PipeEndFluidHandler(null, fluidHandler, EnumFacing.UP);
	}

	public void basicFunctions() {
		assertEquals(CAPACITY, pipeEndFluidHandler.getCapacity());
	}

	public void getInternalPipes() {
		assertNull(pipeEndFluidHandler.getInternalPipes());
	}

	public void fluidAmount() {
		GenericPipeTests.testPipeEnd(pipeEndFluidHandler, CAPACITY, true);
	}

}
