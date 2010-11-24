package org.nate;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nate.encoder.HtmlEncoder;
import org.nate.encoder.HtmlFragmentEncoder;
import org.nate.encoder.NateDocument;
import org.nate.encoder.NateElement;
import org.nate.encoder.NateNode;
import org.nate.exception.IONateException;
import org.nate.exception.UnsupportedEncodingNateException;

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
	
	private static final Pattern ATTRIBUTE_SELECTOR_PATTERN = Pattern.compile("^@@(.+)$"); 

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

	@SuppressWarnings("unchecked")
	public Engine inject(Object data) {
		assertType("data", data, Map.class);
		NateDocument newDocument = document.copy();
		processMapEntries((Map) data, newDocument);
		return new Engine(newDocument);
	}

	public Engine select(String selector) {
		selector = selector.trim();
		if (selector.startsWith(CONTENT_SELECTION_FLAG)) {
			return new Engine(document.copyContentOf(selector.substring(CONTENT_SELECTION_FLAG.length())));
		}
		return new Engine(document.copy(selector));
	}

	@SuppressWarnings("unchecked")
	private void processMapEntries(Map map, NateNode node) {
		Set<Map.Entry> entrySet = map.entrySet();
		for (Map.Entry entry : entrySet) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			assertType("key", key, String.class);
			applySelector(((String) key).trim(), value, node);
		}
	}

	private void applySelector(String selector, Object value, NateNode node) {
		if (value == null) {
			return;
		}
		Matcher attributeSelectorMatcher = ATTRIBUTE_SELECTOR_PATTERN.matcher(selector);
		if (attributeSelectorMatcher.matches()) {
			applySelectorAsAttributeSelector(attributeSelectorMatcher.group(1), value, node);
		} else {
			applySelectorAsCssSelector(selector, value, node);
		}
	}

	private void applySelectorAsAttributeSelector(String attributeName, Object value, NateNode node) {
		node.setAttribute(attributeName, value.toString());
	}

	private void applySelectorAsCssSelector(String selector, Object value,  NateNode node) {
		if (CONTENT_ATTRIBUTE.equals(selector)) {
			injectValueIntoNode(value, node);
		} else {
			for (NateElement matchingNode : node.find(selector)) {
				injectValueIntoNode(value, matchingNode);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void injectValueIntoNode(Object value, NateNode node) {
		if (value instanceof Iterable) {
			injectValuesIntoNode((Iterable) value, node);
		} else if (value instanceof Map) {
			processMapEntries((Map) value, node);
		} else if (value instanceof Engine) {
			injectEngine((Engine)value, node);
		} else {
			node.setTextContent(value.toString());
		}
	}

	private void injectEngine(Engine value, NateNode node) {
		node.replaceChildren(value.document);
	}

	@SuppressWarnings("unchecked")
	private void injectValuesIntoNode(Iterable values, NateNode node) {
		List<NateNode> newNodes = new ArrayList<NateNode>();
		for (Object value : values) {
			NateNode newNode = node.copy();
			injectValueIntoNode(value, newNode);
			newNodes.add(newNode);
		}
		node.replaceWith(newNodes);

	}

	public String render() {
		return document.render();
	}
	
	@Override
	public String toString() {
		return render();
	}

	@SuppressWarnings("unchecked")
	private void assertType(String description, Object object, Class expectedClass) {
		String actualClassName = object == null ? null : object.getClass().getName();
		if (object == null || !(expectedClass.isAssignableFrom(object.getClass()))) {
			throw new IllegalArgumentException("Expected " + description + " to be a " + expectedClass + ", but got "
					+ actualClassName + ", with value: " + object);
		}
	}

}