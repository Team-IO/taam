package net.teamio.blockunit;

/**
 * Created by oliver on 2017-07-03.
 */
public class TestAssertionException extends RuntimeException {
	public TestAssertionException(String message) {
		super(message);
	}

	public TestAssertionException(String format, Object... params) {
		super(String.format(format, (Object[]) params));
	}
}
