package org.nate;

import static java.util.Collections.emptyList;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nate.encoder.NateDocument;
import org.nate.encoder.NateElement;
import org.nate.encoder.NateNode;
import org.nate.exception.IONateException;

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
		public boolean isNullEncoder() { return true; }
		public NateDocument encode(InputStream source) {return new NullNateDocument(source);}
	}
	
	private static class NullNateDocument implements NateDocument {

		private final String result;
	
		public NullNateDocument(InputStream source) {
			this.result = generateResult(source);
		}
		
		private String generateResult(InputStream source) {
			try {
				StringWriter result = new StringWriter();
				BufferedReader reader = new BufferedReader(new InputStreamReader(source));
				for (String line = reader.readLine(); line != null; line = reader.readLine()) {
					result.append(line);
				}
				return result.toString();
			} catch (IOException e) {
				throw new IONateException(e);
			}
		}

		@Override
		public NateDocument copy() {
			return this;
		}

		@Override
		public NateDocument copy(String selector) {
			return this;
		}

		@Override
		public NateDocument copyContentOf(String selector) {
			return this;
		}

		@Override
		public String render() {
			return result;
		}

		@Override
		public List<NateElement> find(String selector) {
			return emptyList();
		}

		@Override
		public void replaceChildren(NateDocument newChildren) {
		}

		@Override
		public void replaceWith(List<NateNode> newNodes) {
		}

		@Override
		public void setAttribute(String name, String value) {
		}

		@Override
		public void setTextContent(String text) {
		}	
	}
	
}