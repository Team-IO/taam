package net.teamio.net.teamio.taam.content.piping;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.teamio.blockunit.TestMethod;
import net.teamio.blockunit.TestingHarness;
import net.teamio.taam.Taam;
import net.teamio.taam.content.piping.MachineTank;
import net.teamio.taam.piping.IPipe;

/**
 * Created by oliver on 2017-07-03.
 */
public class MachineTankTest {

	@TestMethod
	public void testFluidHandlerPipeHandlerInterop(TestingHarness t) {

		MachineTank tank = new MachineTank();

		IFluidHandler fluidHandler = tank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP);
		IPipe pipe = tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.UP);

		t.assertNotNull(fluidHandler);
		t.assertNull(fluidHandler.drain(1, false));

		int amount = fluidHandler.fill(new FluidStack(FluidRegistry.WATER, 5), true);
		t.assertEquals(5, amount);

		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		t.assertEquals(5, amount);

		FluidStack drained = fluidHandler.drain(new FluidStack(FluidRegistry.LAVA, 5), true);
		t.assertNull(drained);

		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		t.assertEquals(5, amount);
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0));
		t.assertEquals(0, amount);

		drained = fluidHandler.drain(new FluidStack(FluidRegistry.WATER, 20), true);
		t.assertNotNull(drained);
		t.assertEquals(5, drained.amount);

		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		t.assertEquals(0, amount);
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0));
		t.assertEquals(0, amount);

		pipe.addFluid(new FluidStack(FluidRegistry.WATER, 5));

		drained = fluidHandler.drain(new FluidStack(FluidRegistry.WATER, 5), false);
		t.assertNotNull(drained);
		t.assertEquals(5, drained.amount);

	}

	@TestMethod
	public void testCapabilitySides(TestingHarness t) {
		MachineTank tank = new MachineTank();

		t.assertNotNull(tank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP));
		t.assertNotNull(tank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN));
		t.assertNull(tank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.NORTH));
		t.assertNull(tank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.SOUTH));
		t.assertNull(tank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.WEST));
		t.assertNull(tank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.EAST));

		t.assertNotNull(tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.UP));
		t.assertNotNull(tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.DOWN));
		t.assertNull(tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.NORTH));
		t.assertNull(tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.SOUTH));
		t.assertNull(tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.WEST));
		t.assertNull(tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.EAST));

		t.assertFalse(tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.UP) == tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.DOWN));
	}

}
