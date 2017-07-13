package net.teamio.taam.content.piping;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.blockunit.TestMethod;
import net.teamio.blockunit.TestingHarness;
import net.teamio.taam.Config;
import net.teamio.taam.Taam;

/**
 * Created by oliver on 2017-07-03.
 */
public class MachinePipeTest {

	@TestMethod
	public void testPipeImplementation(TestingHarness t) {

		MachinePipe pipe = new MachinePipe();

		GenericPipeTests.testPipeEnd(t, pipe, Config.pl_pipe_capacity, false);
	}

	@TestMethod
	public void testSerializePipeContent(TestingHarness t) {

		MachinePipe pipe = new MachinePipe();

		int fillAmount = Config.pl_pipe_capacity / 2;

		int amount = pipe.addFluid(new FluidStack(FluidRegistry.WATER, fillAmount));
		t.assertEquals(fillAmount, amount);
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		t.assertEquals(fillAmount, amount);

		// Write to NBT
		NBTTagCompound tag = new NBTTagCompound();
		pipe.writePropertiesToNBT(tag);

		// Read into same pipe
		pipe.readPropertiesFromNBT(tag);

		// Check amount
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		t.assertEquals(fillAmount, amount);

		// Read into new pipe
		pipe = new MachinePipe();
		pipe.readPropertiesFromNBT(tag);

		// Check amount
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		t.assertEquals(fillAmount, amount);
	}

	@TestMethod
	public void testCapabilitySides(TestingHarness t) {

		MachinePipe pipe = new MachinePipe();

		t.assertNotNull(pipe.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.UP));
		t.assertNotNull(pipe.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.DOWN));
		t.assertNotNull(pipe.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.NORTH));
		t.assertNotNull(pipe.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.SOUTH));
		t.assertNotNull(pipe.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.WEST));
		t.assertNotNull(pipe.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.EAST));
	}

}
