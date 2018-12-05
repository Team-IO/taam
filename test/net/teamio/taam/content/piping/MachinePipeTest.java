package net.teamio.taam.content.piping;

import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.teamio.taam.Config;
import net.teamio.taam.Taam;
import org.junit.runner.RunWith;

/**
 * Created by oliver on 2017-07-03.
 */
@RunWith(VoltzTestRunner.class)
public class MachinePipeTest extends AbstractTest {

	@Override
	public void setUpForEntireClass() {
		Config.init(null);
	}

	public void testPipeImplementation() {

		MachinePipe pipe = new MachinePipe();

		GenericPipeTests.testPipeEnd(pipe, Config.pl_pipe_capacity, false);
	}

	public void testSerializePipeContent() {

		MachinePipe pipe = new MachinePipe();

		int fillAmount = Config.pl_pipe_capacity / 2;

		int amount = pipe.addFluid(new FluidStack(FluidRegistry.WATER, fillAmount));
		assertEquals(fillAmount, amount);
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		assertEquals(fillAmount, amount);

		// Write to NBT
		NBTTagCompound tag = new NBTTagCompound();
		pipe.writePropertiesToNBT(tag);

		// Read into same pipe
		pipe.readPropertiesFromNBT(tag);

		// Check amount
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		assertEquals(fillAmount, amount);

		// Read into new pipe
		pipe = new MachinePipe();
		pipe.readPropertiesFromNBT(tag);

		// Check amount
		amount = pipe.getFluidAmount(new FluidStack(FluidRegistry.WATER, 0));
		assertEquals(fillAmount, amount);
	}

	public void testCapabilitySides() {

		MachinePipe pipe = new MachinePipe();

		assertNotNull(pipe.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.UP));
		assertNotNull(pipe.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.DOWN));
		assertNotNull(pipe.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.NORTH));
		assertNotNull(pipe.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.SOUTH));
		assertNotNull(pipe.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.WEST));
		assertNotNull(pipe.getCapability(Taam.CAPABILITY_PIPE, EnumFacing.EAST));
	}

}
