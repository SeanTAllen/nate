package org.nate;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nate.html.Html;

public class Encoders {

	private static final Encoder NULL_ENCODER = new NullEncoder();

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
		return NULL_ENCODER;
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

	private static class NullEncoder implements Encoder {
		public String type() { return "null"; }
		public Html encode(String source) { return new NullHtml(source); }
		public boolean isNullEncoder() { return true; }
	}
	
	private static class NullHtml implements Html {

		private final String source;

		public NullHtml(String source) {
			this.source = source;
		}

		@Override
		public Html cloneFragment() {
			return new NullHtml(source);
		}

		@Override
		public Html getParentNode() {
			return this;
		}

		@Override
		public boolean hasAttribute(String name) {
			return false;
		}

		@Override
		public void replaceWith(List<Html> newFragments) {
		}

		@Override
		public List<Html> selectNodes(String selector) {
			return Collections.emptyList();
		}
		
		@Override
		public List<Html> selectContentOfNodes(String selector) {
			return Collections.emptyList();
		}

		@Override
		public void setAttribute(String name, Object value) {
		}

		@Override
		public void setTextContent(String value) {
		}

		@Override
		public String toHtml() {
			return source;
		}

		@Override
		public void replaceChildren(Html template) {
		}
		
	}
}