package net.teamio.taam.piping;

import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.taam.Config;
import net.teamio.taam.content.piping.MachinePipe;
import org.junit.runner.RunWith;

/**
 * Created by oliver on 2017-12-13.
 */
@RunWith(VoltzTestRunner.class)
public class PressureSimulatorTest extends AbstractTest {

	public void testPressureSimulation() {
		PipeNetwork net = new PipeNetwork();

		MachinePipe pipe1 = new MachinePipe();
		MachinePipe pipe2 = new MachinePipe();

		net.graph.addNode(pipe1);
		net.graph.addNode(pipe2);
		net.graph.addEdge(pipe1, pipe2);

		pipe1.addFluid(new FluidStack(FluidRegistry.WATER, Config.pl_pipe_capacity));
		assertEquals(pipe1.getPressure(), Config.pl_pipe_capacity);

		PressureSimulator.simulate(net);

		// Content should have equalled out
		// TODO: write a more predictable test here
		//assertEquals(pipe1.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)), pipe2.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0)));
	}

}
