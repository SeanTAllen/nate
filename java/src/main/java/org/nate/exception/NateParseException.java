package org.nate.exception;

public class NateParseException extends NateException {

	public NateParseException() {
	}

	public NateParseException(String message) {
		super(message);
	}

	public NateParseException(Throwable cause) {
		super(cause);
	}

	public NateParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
