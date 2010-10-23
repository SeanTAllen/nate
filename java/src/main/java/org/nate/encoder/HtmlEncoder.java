package org.nate.encoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.nate.Encoder;
import org.nate.TransformResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import se.fishtank.css.selectors.NodeSelector;
import se.fishtank.css.selectors.NodeSelectorException;
import se.fishtank.css.selectors.dom.DOMNodeSelector;

public class HtmlEncoder implements Encoder {

	private static final String TYPE = "HTML";

	public boolean isNullEncoder() {
		return false;
	}

	public String type() {
		return TYPE;
	}

	public Object encode(String source) {
		try {
			// Javadoc for these says nothing about thread safety, and so we recreate every time.
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			return builder.parse(new ByteArrayInputStream(source.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public TransformResult transformWith(Object template, Object data) {
		assertType("data", data, Map.class);
		Set<Map.Entry> entrySet = ((Map) data).entrySet();
		final Document document = (Document) template;
		for (Map.Entry entry : entrySet) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			assertType("key", key, String.class);
			processMapEntry((String) key, value, document);
		}
		return new TransformResult() {
			@Override
			public String toHtml() {
		        try {
					Source source = new DOMSource((Node) document);
					Writer stringWriter = new StringWriter();
					Result result = new StreamResult(stringWriter);
					Transformer xformer = TransformerFactory.newInstance().newTransformer();
					xformer.transform(source, result);
					return stringWriter.toString();
				} catch (TransformerConfigurationException e) {
					throw new RuntimeException(e);
				} catch (TransformerFactoryConfigurationError e) {
					throw new RuntimeException(e);
				} catch (TransformerException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	@SuppressWarnings("unchecked")
	private void processMapEntry(String key, Object value, Document document) {
		if (value == null || value instanceof Map) {
			return;
		}
		NodeSelector selector = new DOMNodeSelector(document);
		try {
			Set<Node> nodes = selector.querySelectorAll(key);
			for (Node node : nodes) {
				node.setTextContent((String) value);
			}
		} catch (NodeSelectorException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private void assertType(String description, Object object, Class expectedClass) {
		String actualClassName = object == null ? null : object.getClass().getName();
		if (object == null || !(expectedClass.isAssignableFrom(object.getClass()))) {
			throw new IllegalArgumentException("Expected " + description + " to be a " + Map.class.getName()
					+ ", but got " + actualClassName);
		}
	}

}