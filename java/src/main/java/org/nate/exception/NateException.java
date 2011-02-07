package org.nate.exception;

@SuppressWarnings("serial")
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
