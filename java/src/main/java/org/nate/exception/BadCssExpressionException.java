package org.nate.exception;

public class BadCssExpressionException extends NateException {

	public BadCssExpressionException() {
	}

	public BadCssExpressionException(String message) {
		super(message);
	}

	public BadCssExpressionException(Throwable cause) {
		super(cause);
	}

	public BadCssExpressionException(String message, Throwable cause) {
		super(message, cause);
	}

}
