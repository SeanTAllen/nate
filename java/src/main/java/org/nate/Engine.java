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

import org.nate.encoder.HtmlEncoder;
import org.nate.encoder.HtmlFragmentEncoder;
import org.nate.exception.IONateException;
import org.nate.exception.UnsupportedEncodingNateException;
import org.nate.html.Html;
import org.nate.html.XmlParserBackedHtml;

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
	public static final String CONTENT_SELECTION_FLAG = "content:";

	private static Encoders encoders = new Encoders();
	static {
		encoders.register(new HtmlEncoder());
		encoders.register(new HtmlFragmentEncoder());
	}

	private final Html template;

	public static Encoders encoders() {
		return encoders;
	}
	
	private Engine(InputStream source, Encoder encoder) {
		this.template = encoder.encode(source);
	}
	
	private Engine(Html fragment) {
		this.template = fragment;
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
		Html fragment = template.cloneFragment();
		processMapEntries((Map) data, fragment);
		return new Engine(fragment);
	}

	public Engine select(String selector) {
		Html fragment = template.cloneFragment();
		List<Html> selectedNodes = findHtmlElementsMatchingSelector(selector.trim(), fragment);
		return new Engine(XmlParserBackedHtml.fromFragments(selectedNodes));
	}

	private List<Html> findHtmlElementsMatchingSelector(String selector, Html fragment) {
		if (selector.startsWith(CONTENT_SELECTION_FLAG)) {
			return fragment.selectContentOfNodes(selector.substring(CONTENT_SELECTION_FLAG.length()));
		}
		return fragment.selectNodes(selector);
	}

	@SuppressWarnings("unchecked")
	private void processMapEntries(Map map, Html fragment) {
		Set<Map.Entry> entrySet = map.entrySet();
		for (Map.Entry entry : entrySet) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			assertType("key", key, String.class);
			applySelector(((String) key).trim(), value, fragment);
		}
	}

	private void applySelector(String selector, Object value, Html fragment) {
		if (value == null) {
			return;
		}
		if (fragment.hasAttribute(selector)) {
			applySelectorAsAttributeSelector(selector, value, fragment);
		} else {
			applySelectorAsCssSelector(selector, value, fragment);
		}
	}

	private void applySelectorAsAttributeSelector(String attributeName, Object value, Html fragment) {
		fragment.setAttribute(attributeName, value);
	}

	private void applySelectorAsCssSelector(String selector, Object value, Html fragment) {
		if (CONTENT_ATTRIBUTE.equals(selector)) {
			injectValueIntoFragment(value, fragment);
		} else {
			for (Html matchingNode : fragment.selectNodes(selector)) {
				injectValueIntoFragment(value, matchingNode);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void injectValueIntoFragment(Object value, Html fragment) {
		if (value instanceof Iterable) {
			injectValuesIntoFragment((Iterable) value, fragment);
		} else if (value instanceof Map) {
			processMapEntries((Map) value, fragment);
		} else if (value instanceof Engine) {
			injectEngine((Engine)value, fragment);
		} else {
			fragment.setTextContent(value.toString());
		}
	}

	private void injectEngine(Engine value, Html fragment) {
		fragment.replaceChildren(value.template.cloneFragment());
	}

	@SuppressWarnings("unchecked")
	private void injectValuesIntoFragment(Iterable values, Html fragment) {
		List<Html> newFragments = new ArrayList<Html>();
		for (Object value : values) {
			Html newFragment = fragment.cloneFragment();
			injectValueIntoFragment(value, newFragment);
			newFragments.add(newFragment);
		}
		fragment.replaceWith(newFragments);

	}

	public String render() {
		return template.toHtml();
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