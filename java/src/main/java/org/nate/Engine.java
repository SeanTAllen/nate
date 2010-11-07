package org.nate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nate.encoder.HtmlEncoder;
import org.nate.encoder.HtmlFragmentEncoder;
import org.nate.html.Html;
import org.nate.html.XmlParserBackedHtml;
import org.nate.util.HtmlFile;

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
	
	private Engine(String source, Encoder encoder) {
		this.template = encoder.encode(source);
	}
	
	private Engine(Html fragment) {
		this.template = fragment;
	}

	public static Engine newWith(String source) {
		return new Engine(source, encoders.encoderFor("HTMLF"));
	}

	public static Engine newWith(String source, Encoder encoder) {
		return new Engine(source, encoder);
	}

	public static Engine newWith(File file) {
		Encoder encoder = encoders.encoderFor(file);
		return new Engine(HtmlFile.contentsOf(file), encoder);
	}

	@SuppressWarnings("unchecked")
	public Engine inject(Object data) {
		assertType("data", data, Map.class);
		Html fragment = template.cloneFragment();
		processMapEntries((Map) data, fragment);
		return new Engine(fragment);
	}

	public Engine select(String selector) {
		List<Html> selectedNodes = findHtmlElementsMatchingSelector(selector.trim());
		return new Engine(XmlParserBackedHtml.fromFragments(selectedNodes));
	}

	private List<Html> findHtmlElementsMatchingSelector(String selector) {
		if (selector.startsWith(CONTENT_SELECTION_FLAG)) {
			return clone(template.selectContentOfNodes(selector.substring(CONTENT_SELECTION_FLAG.length())));
		}
		return clone(template.selectNodes(selector));
	}

	private List<Html> clone(List<Html> nodes) {
		List<Html> clones = new ArrayList<Html>(nodes.size());
		for (Html node : nodes) {
			clones.add(node.cloneFragment());
		}
		return clones;
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
		Html parentNode = fragment.getParentNode();
		parentNode.removeChild(fragment);
		for (Object value : values) {
			Html newFragment = fragment.cloneFragment();
			injectValueIntoFragment(value, newFragment);
			parentNode.appendChild(newFragment);
		}
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