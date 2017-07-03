package net.teamio.net.teamio.taam.content.piping;

import net.minecraft.nbt.NBTTagCompound;
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
		IPipe pipe2 = tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.DOWN);

		t.assertNotNull(fluidHandler);
		t.assertNull(fluidHandler.drain(1, false));

		// Insert in FH
		int amount = fluidHandler.fill(new FluidStack(FluidRegistry.WATER, 5), true);
		t.assertEquals(5, amount);

		// Check amount from the pipes
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		t.assertEquals(5, amount);
		amount = pipe2.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		t.assertEquals(5, amount);

		// Drain from FH, fluid not in tank
		FluidStack drained = fluidHandler.drain(new FluidStack(FluidRegistry.LAVA, 5), true);
		t.assertNull(drained);

		// Check amount from the pipes
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		t.assertEquals(5, amount);
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0));
		t.assertEquals(0, amount);
		amount = pipe2.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		t.assertEquals(5, amount);
		amount = pipe2.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0));
		t.assertEquals(0, amount);

		// Overdrain from FH
		drained = fluidHandler.drain(new FluidStack(FluidRegistry.WATER, 20), true);
		t.assertNotNull(drained);
		t.assertEquals(5, drained.amount);

		// Check amount from the pipes
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		t.assertEquals(0, amount);
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0));
		t.assertEquals(0, amount);
		amount = pipe2.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		t.assertEquals(0, amount);
		amount = pipe2.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0));
		t.assertEquals(0, amount);

		// Insert from pipe
		pipe.addFluid(new FluidStack(FluidRegistry.WATER, 5));

		// Drain from FH
		drained = fluidHandler.drain(new FluidStack(FluidRegistry.WATER, 5), false);
		t.assertNotNull(drained);
		t.assertEquals(5, drained.amount);


	}

	@TestMethod
	public void testSerializeTankContent(TestingHarness t) {

		MachineTank tank = new MachineTank();
		IPipe pipe = tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.UP);

		int amount = pipe.addFluid(new FluidStack(FluidRegistry.WATER, 315));
		t.assertEquals(315, amount);
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		t.assertEquals(315, amount);

		// Write to NBT
		NBTTagCompound tag = new NBTTagCompound();
		tank.writePropertiesToNBT(tag);

		// Read into same tank
		tank.readPropertiesFromNBT(tag);

		// Check amount
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		t.assertEquals(315, amount);

		// Read into new tank
		tank = new MachineTank();
		pipe = tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.UP);
		tank.readPropertiesFromNBT(tag);

		// Check amount
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		t.assertEquals(315, amount);
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
