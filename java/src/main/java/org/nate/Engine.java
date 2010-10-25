package org.nate;

import org.nate.encoder.html.HtmlEncoder;
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

	public static Engine newWith(String source, Encoder encoder) {
		return new Engine(source, encoder);
	}

	public static Engine newWith(File file) {
		Encoder encoder = encoders.encoderFor(file);
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