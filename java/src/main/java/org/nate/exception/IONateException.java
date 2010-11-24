package org.nate.exception;

@SuppressWarnings("serial")
public class IONateException extends NateException {

	public IONateException() {
	}

	public IONateException(String message, Throwable cause) {
		super(message, cause);
	}

	public IONateException(String message) {
		super(message);
	}

	public IONateException(Throwable cause) {
		super(cause);
	}

}
