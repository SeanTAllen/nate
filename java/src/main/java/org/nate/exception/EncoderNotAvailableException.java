package org.nate.exception;

@SuppressWarnings("serial")
public class EncoderNotAvailableException extends NateException {

	public EncoderNotAvailableException(String type) {
		super("No encoder registered for: " + type);
	}

}
