package org.nate;

import org.nate.encoder.HtmlEncoder;
import org.nate.util.HtmlFile;

import java.io.File;

public class Engine {

	private static Encoders encoders = new Encoders();
	static {
		encoders.register(new HtmlEncoder());
	}

	private final Object template;
	private final Encoder encoder;

	public static Encoders encoders() {
		return encoders;
	}

	public static Engine create(String source, Encoder encoder) {
		return new Engine(source, encoder);
	}

	public static Engine create(File file) {
		Encoder encoder = encoders.of(file);
		return new Engine(HtmlFile.contentsOf(file), encoder);
	}

	public String inject(Object data) {
		TransformResult transformResult = encoder.transformWith(template, data);
		return transformResult.toHtml();
	}

	protected Engine(String source, Encoder encoder) {
		this.template = encoder.encode(source);
		this.encoder = encoder;
	}
}