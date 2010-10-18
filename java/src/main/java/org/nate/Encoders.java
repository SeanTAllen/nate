package org.nate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Encoders {

	private static final Encoder NULL_ENCODER = new NullEncoder();
	private static final TransformResult NULL_TRANSFORM_RESULT = new NullTransformResult();

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

	public Encoder of(String type) {
		return ofType(type.toUpperCase());
	}

	private Encoder ofType(String type) {
		if (encoders.containsKey(type))
			return encoders.get(type);
		return NULL_ENCODER;
	}

	public Encoder of(File file) {
		return of(filenameExtension(file));
	}

	private String filenameExtension(File file) {
		String filename = file.getName();
		int period = filename.lastIndexOf(".");
		if (period != -1)
			return filename.substring(period+1);
		throw new IllegalStateException("Can't file file extension for '" + file.getName() + "'");
	}

	private static class NullEncoder implements Encoder {
		public String type() { return "null"; }
		public Object encode(String source) { return null; }
		public TransformResult transformWith(Object template, Object data) { return NULL_TRANSFORM_RESULT; }
		public boolean isNullEncoder() { return true; }
	}

	private static class NullTransformResult implements TransformResult {
		public String toHtml() { return ""; }
	}
}