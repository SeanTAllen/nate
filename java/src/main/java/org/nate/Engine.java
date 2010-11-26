package org.nate;

import static org.nate.internal.Assertions.assertType;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.nate.encoder.HtmlEncoder;
import org.nate.encoder.HtmlFragmentEncoder;
import org.nate.encoder.NateDocument;
import org.nate.exception.IONateException;
import org.nate.exception.UnsupportedEncodingNateException;
import org.nate.internal.transformer.NateTransformers;

public class Engine {

	/**
	 * Pseudo-selector that matches the currently selected node.
	 * To be used in sub-selects when there is a need to replace the content of the currently selected node
	 * as well as one or more of its attributes.
	 * For example: <br /> 
	 * Given the HTML fragment "&lt;a href='#'&gt;my link&lt;/a&gt;" <br /> 
	 * When { 'a' => { 'href' => 'http://www.example.com', Engine.CONTENT_ATTRIBUTE => 'example.com' } } is injected<br /> 
	 * Then the HTML fragment is &lt;a href="http://www.example.com"&gt;example.com&lt;/a&gt; 
	 */
	public static final String CONTENT_ATTRIBUTE = "*content*";

	/**
	 * Prefix to use with the parameter to {@link Engine#select(String)} to indicate when the content of the selected
	 * nodes is desired instead of the nodes themselves.
	 */
	public static final String CONTENT_SELECTION_FLAG = "##";

	private static Encoders encoders = new Encoders();
	static {
		encoders.register(new HtmlEncoder());
		encoders.register(new HtmlFragmentEncoder());
	}
	
	private final NateDocument document;

	public static Encoders encoders() {
		return encoders;
	}
	
	private Engine(InputStream source, Encoder encoder) {
		this.document = encoder.encode(source);
	}
	
	private Engine(NateDocument newDocument) {
		this.document = newDocument;
	}

	public static Engine newWith(InputStream source, Encoder encoder) {
		return new Engine(source, encoder);
	}

	public static Engine newWith(String source) {
		return newWith(source, encoders.encoderFor("HTMLF"));
	}

	public static Engine newWith(String source, Encoder encoder) {
		try {
			// We are using an xml parser, and so we really need to use UTF-8.
			// TODO: Test this assumption!!!
			return newWith(new ByteArrayInputStream(source.getBytes("UTF-8")), encoder);
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedEncodingNateException(e);
		}
	}

	public static Engine newWith(File file) {
		InputStream inputStream = null;
		try {
			inputStream = new BufferedInputStream(new FileInputStream(file));
			Encoder encoder = encoders.encoderFor(file);
			return new Engine(inputStream, encoder);
		} catch (FileNotFoundException e) {
			throw new IONateException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					throw new IONateException(e);
				}
			}
		}
	}

	public Engine inject(Object data) {
		assertType("data", data, Map.class);
		NateDocument newDocument = document.copy();
		NateTransformers.from(data).transform(newDocument);
		return new Engine(newDocument);
	}

	public Engine select(String selector) {
		selector = selector.trim();
		if (selector.startsWith(CONTENT_SELECTION_FLAG)) {
			return new Engine(document.copyContentOf(selector.substring(CONTENT_SELECTION_FLAG.length())));
		}
		return new Engine(document.copy(selector));
	}


	public NateDocument getDocument() {
		return document;
	}

	public String render() {
		return document.render();
	}
	
	@Override
	public String toString() {
		return render();
	}

}