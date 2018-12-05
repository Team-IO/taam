package net.teamio.taam.piping;

import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.teamio.taam.content.piping.GenericPipeTests;
import org.junit.runner.RunWith;

/**
 * Created by oliver on 2017-07-03.
 */
@RunWith(VoltzTestRunner.class)
public class PipeEndFluidHandlerTest extends AbstractTest {

	private PipeEndFluidHandler pipeEndFluidHandler;
	private IFluidHandler fluidHandler;

	private static final int CAPACITY = 10;

	@Override
	public void setUpForTest(String name) {
		fluidHandler = new FluidTank(CAPACITY);
		pipeEndFluidHandler = new PipeEndFluidHandler(null, fluidHandler, EnumFacing.UP);
	}

	public void testBasicFunctions() {
		assertEquals(CAPACITY, pipeEndFluidHandler.getCapacity());
	}

	public void testGetInternalPipes() {
		assertNull(pipeEndFluidHandler.getInternalPipes());
	}

	public void testFluidAmount() {
		GenericPipeTests.testPipeEnd(pipeEndFluidHandler, CAPACITY, true);
	}

}
