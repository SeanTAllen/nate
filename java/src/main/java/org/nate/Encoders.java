package org.nate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Encoders {

	private Map<String, Encoder> encoders = new HashMap<String, Encoder>();

	public void register(Encoder encoder) {
		String type = encoder.type().toUpperCase();
		register(type, encoder);
	}

	private void register(String type, Encoder encoder) {
		if (encoders.containsKey(type))
			throw new IllegalArgumentException("Encoder for type '" + encoder.type() + "' is already registered.");
		encoders.put(type, encoder);
	}

	public Encoder encoderFor(String type) {
		return encoderForType(type.toUpperCase());
	}

	private Encoder encoderForType(String type) {
		if (encoders.containsKey(type)) {
			return encoders.get(type);
		}
		return null;
	}

	public Encoder encoderFor(File file) {
		return encoderFor(filenameExtension(file));
	}

	private String filenameExtension(File file) {
		String filename = file.getName();
		int period = filename.lastIndexOf(".");
		if (period != -1)
			return filename.substring(period+1);
		throw new IllegalStateException("Can't file file extension for '" + file.getName() + "'");
	}
	
}