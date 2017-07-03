package net.teamio.blockunit;

/**
 * Created by oliver on 2017-07-03.
 */
public class TestingHarness {
	public void assertEquals(int a, int b) {
		if (a != b) {
			throw new TestAssertionException("AssertEquals: %d is not equal to %d", a, b);
		}
	}

	public void assertEquals(boolean a, boolean b) {
		if (a != b) {
			throw new TestAssertionException("AssertEquals: %s is not equal to %s", a, b);
		}
	}

	public void assertNull(Object o) {
		if (o != null) {
			throw new TestAssertionException("AssertNull: passed object is not null");
		}
	}

	public void assertNotNull(Object o) {
		if (o == null) {
			throw new TestAssertionException("AssertNotNull: passed object is null");
		}
	}

	public void assertTrue(boolean b) {
		if (!b) {
			throw new TestAssertionException("AssertTrue: passed value is false");
		}
	}

	public void assertFalse(boolean b) {
		if (!b) {
			throw new TestAssertionException("AssertFalse: passed value is true");
		}
	}
}
