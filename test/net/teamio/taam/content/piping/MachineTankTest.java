package net.teamio.taam.content.piping;

import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.teamio.taam.Config;
import net.teamio.taam.Taam;
import net.teamio.taam.TestUtil;
import net.teamio.taam.piping.IPipe;
import org.junit.runner.RunWith;

/**
 * Created by oliver on 2017-07-03.
 */
@RunWith(VoltzTestRunner.class)
public class MachineTankTest extends AbstractTest {
	@Override
	public void setUpForEntireClass() {
		Config.init(null);
		TestUtil.registerCapabilities();
	}

	public void testFluidHandlerPipeHandlerInterop() {

		GenericPipeTests.testPipeEnd(new MachineTank().getCapability(Taam.CAPABILITY_PIPE, EnumFacing.UP), Config.pl_tank_capacity, true);

		MachineTank tank = new MachineTank();

		GenericPipeTests.testPipeEnd(tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.UP), Config.pl_tank_capacity, true);

		IFluidHandler fluidHandler = tank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP);
		IPipe pipe = tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.UP);
		IPipe pipe2 = tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.DOWN);

		assertNotNull(fluidHandler);
		assertNull(fluidHandler.drain(1, false));

		// Insert in FH
		int amount = fluidHandler.fill(new FluidStack(FluidRegistry.WATER, 5), true);
		assertEquals(5, amount);

		// Check amount from the pipes
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		assertEquals(5, amount);
		amount = pipe2.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		assertEquals(5, amount);

		// Drain from FH, fluid not in tank
		FluidStack drained = fluidHandler.drain(new FluidStack(FluidRegistry.LAVA, 5), true);
		assertNull(drained);

		// Check amount from the pipes
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		assertEquals(5, amount);
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0));
		assertEquals(0, amount);
		amount = pipe2.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		assertEquals(5, amount);
		amount = pipe2.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0));
		assertEquals(0, amount);

		// Overdrain from FH
		drained = fluidHandler.drain(new FluidStack(FluidRegistry.WATER, 20), true);
		assertNotNull(drained);
		assertEquals(5, drained.amount);

		// Check amount from the pipes
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		assertEquals(0, amount);
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0));
		assertEquals(0, amount);
		amount = pipe2.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		assertEquals(0, amount);
		amount = pipe2.getFluidAmount(new FluidStack(FluidRegistry.LAVA, 0));
		assertEquals(0, amount);

		// Insert from pipe
		amount = pipe.addFluid(new FluidStack(FluidRegistry.WATER, 5));
		assertEquals(5, amount);

		// Simulate Drain from FH
		drained = fluidHandler.drain(new FluidStack(FluidRegistry.WATER, 5), false);
		assertNotNull(drained);
		assertEquals(5, drained.amount);
		// Check amount still the same
		amount = pipe2.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		assertEquals(5, amount);


		// Simulate Insert in FH
		amount = fluidHandler.fill(new FluidStack(FluidRegistry.WATER, 5), false);
		assertEquals(5, amount);
		// Check amount still the same
		amount = pipe2.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		assertEquals(5, amount);

		// Simulate overfill in FH
		amount = fluidHandler.fill(new FluidStack(FluidRegistry.WATER, Config.pl_tank_capacity + 50), false);
		assertEquals(Config.pl_tank_capacity - 5, amount);
		// Check amount still the same
		amount = pipe2.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		assertEquals(5, amount);


		// Overfill in FH
		amount = fluidHandler.fill(new FluidStack(FluidRegistry.WATER, Config.pl_tank_capacity + 50), true);
		assertEquals(Config.pl_tank_capacity - 5, amount);
		// Check amount is at maximum
		amount = pipe2.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		assertEquals(Config.pl_tank_capacity, amount);

		// Drain from pipe
		amount = pipe.removeFluid(new FluidStack(FluidRegistry.WATER, Config.pl_tank_capacity));
		assertEquals(Config.pl_tank_capacity, amount);

		GenericPipeTests.testPipeEnd(tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.UP), Config.pl_tank_capacity, true);

	}

	public void testSerializeTankContent() {

		MachineTank tank = new MachineTank();
		IPipe pipe = tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.UP);

		int amount = pipe.addFluid(new FluidStack(FluidRegistry.WATER, 315));
		assertEquals(315, amount);
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		assertEquals(315, amount);

		// Write to NBT
		NBTTagCompound tag = new NBTTagCompound();
		tank.writePropertiesToNBT(tag);

		// Read into same tank
		tank.readPropertiesFromNBT(tag);

		// Check amount
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		assertEquals(315, amount);

		// Read into new tank
		tank = new MachineTank();
		pipe = tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.UP);
		tank.readPropertiesFromNBT(tag);

		// Check amount
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		assertEquals(315, amount);
	}

	public void testCapabilitySides() {
		MachineTank tank = new MachineTank();

		assertNotNull(tank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP));
		assertNotNull(tank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN));
		assertNull(tank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.NORTH));
		assertNull(tank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.SOUTH));
		assertNull(tank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.WEST));
		assertNull(tank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.EAST));

		assertNotNull(tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.UP));
		assertNotNull(tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.DOWN));
		assertNull(tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.NORTH));
		assertNull(tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.SOUTH));
		assertNull(tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.WEST));
		assertNull(tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.EAST));

		assertFalse(tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.UP) == tank.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.DOWN));
	}

}
