package net.teamio.taam;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Utility methods for unit test scenarios
 */
public final class TestUtil {

	private TestUtil() {
		// Utility Class
	}

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

}
