package org.nate.internal;

public final class Assertions {

	private Assertions() {
	}

	@SuppressWarnings("unchecked")
	public static void assertType(String description, Object object, Class expectedClass) {
		String actualClassName = object == null ? null : object.getClass().getName();
		if (object == null || !(expectedClass.isAssignableFrom(object.getClass()))) {
			throw new IllegalArgumentException("Expected " + description + " to be a " + expectedClass + ", but got "
					+ actualClassName + ", with value: " + object);
		}
	}
}
