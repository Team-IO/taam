package net.teamio.blockunit;

import net.teamio.taam.piping.PipeEndFluidHandlerTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oliver on 2017-07-03.
 */
public class Framework {

	public static final Logger LOGGER = LogManager.getLogger("net.teamio.blockunit");

	private static List<Class<?>> tests = new ArrayList<Class<?>>(20);

	public static void runTests() {
		registerTests();
		for (Class<?> cls : tests) {
			Object instance;
			try {
				instance = cls.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
				continue;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				continue;
			}

			Method[] methods = cls.getMethods();
			for (Method m : methods) {
				if (m.isAnnotationPresent(TestMethod.class)) {
					try {
						LOGGER.info("Running test method " + m.getName());
						runTest(instance, m);
						LOGGER.info("Test case successful");
					} catch(Exception e) {
						LOGGER.error("Test failed horribly", e);
					}
				}
			}
		}
	}

	public static void runTest(Object instance, Method m) {
		Class<?>[] params = m.getParameterTypes();
		if (params.length == 0) {
			// "Just check for failure or not"
			try {
				m.invoke(instance);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Error accessing test method " + m.getName(), e);
			} catch (InvocationTargetException e) {
				Throwable te = e.getTargetException();
				if (te instanceof  TestAssertionException) {
					LOGGER.error("Test assertion failed", te);
				} else {
					LOGGER.error("Test failed with exception", te);
				}
			}
		} else if (params.length == 1) {
			if (params[0] != TestingHarness.class) {
				// Umm.. did you read the documentation?
				LOGGER.error("Incorrect parameters for test method " + m.getName());
				return;
			}
			// Detailed testing with test harness
			try {
				m.invoke(instance, new TestingHarness());
			} catch (IllegalAccessException e) {
				LOGGER.error("Error accessing test method " + m.getName(), e);
				return;
			} catch (InvocationTargetException e) {
				Throwable te = e.getTargetException();
				if (te instanceof  TestAssertionException) {
					LOGGER.error("Test assertion failed", te);
				} else {
					LOGGER.error("Test failed with exception", te);
				}
			}
		} else {
			// Umm.. did you read the documentation?
			LOGGER.error("Incorrect parameters for test method " + m.getName());
			return;
		}
	}

	public static void clearTests() {
		tests.clear();
	}

	public static void registerTestClass(Class cls) {
		tests.add(cls);
	}

	public static void registerTests() {
		//TODO: This should not be in the Framework class
		registerTestClass(PipeEndFluidHandlerTest.class);
	}
}
