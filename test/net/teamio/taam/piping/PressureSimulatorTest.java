package net.teamio.taam.piping;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.minecart.MinecartCollisionEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.blockunit.TestMethod;
import net.teamio.blockunit.TestingHarness;
import net.teamio.taam.Config;
import net.teamio.taam.content.piping.MachinePipe;

/**
 * Created by oliver on 2017-12-13.
 */
public class PressureSimulatorTest {

	@TestMethod
	public void testPressureSimulation(TestingHarness t) {
		PipeNetwork net = new PipeNetwork();

		MachinePipe pipe1 = new MachinePipe();
		MachinePipe pipe2 = new MachinePipe();

		net.graph.addNode(pipe1);
		net.graph.addNode(pipe2);
		net.graph.addEdge(pipe1, pipe2);

		pipe1.addFluid(new FluidStack(FluidRegistry.WATER, Config.pl_pipe_capacity));
		t.assertEquals(pipe1.getPressure(), Config.pl_pipe_capacity);

		PressureSimulator.simulate(net);

		// Content should have equalled out
		t.assertEquals(pipe1.getFluidAmount(new FluidStack(FluidRegistry.WATER,0)), pipe2.getFluidAmount(new FluidStack(FluidRegistry.WATER,0)));
	}
}
