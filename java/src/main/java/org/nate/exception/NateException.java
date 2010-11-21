package org.nate.exception;

public class NateException extends RuntimeException {

	public NateException() {
	}

	public NateException(String message) {
		super(message);
	}

	public NateException(Throwable cause) {
		super(cause);
	}

	public NateException(String message, Throwable cause) {
		super(message, cause);
	}

}
