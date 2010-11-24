package org.nate.exception;

@SuppressWarnings("serial")
public class UnsupportedEncodingNateException extends NateException {

	public UnsupportedEncodingNateException() {
	}

	public UnsupportedEncodingNateException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedEncodingNateException(String message) {
		super(message);
	}

	public UnsupportedEncodingNateException(Throwable cause) {
		super(cause);
	}

}
