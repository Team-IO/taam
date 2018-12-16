package net.teamio.taam;

import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.teamio.taam.piping.IPipe;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Utility methods for unit test scenarios and a test to verify these actually do what they should do.
 */
@RunWith(VoltzTestRunner.class)
public final class TestUtil extends AbstractTest {

	@SuppressWarnings("unchecked")
	public static <T> Capability<T> getCapability(Class<T> capabilityClass) {
		try {

			Field providersField = CapabilityManager.class.getDeclaredField("providers");
			providersField.setAccessible(true);

			Map<String, Capability<?>> providers = (Map<String, Capability<?>>) providersField.get(CapabilityManager.INSTANCE);

			// we are dealing with an IdentityHashMap - String.intern() is required to guarantee instance equality
			String realName = capabilityClass.getName().intern();

			return (Capability<T>) providers.get(realName);

		} catch (NoSuchFieldException e) {
			throw new IllegalStateException("Failed to get field 'providers' from CapabilityManager");
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to read field 'providers' from CapabilityManager");
		}
	}

	public static void registerCapabilities() {
		if (CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY == null) {
			CapabilityFluidHandler.register();

			TaamMain.registerCapabilities();

			CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY = TestUtil.getCapability(IFluidHandler.class);
			Taam.CAPABILITY_PIPE = TestUtil.getCapability(IPipe.class);
		}
	}

	@Test
	public void testRegisteredCapabilities() {
		registerCapabilities();

		assertNotNull(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
		assertNotNull(Taam.CAPABILITY_PIPE);
	}

}
