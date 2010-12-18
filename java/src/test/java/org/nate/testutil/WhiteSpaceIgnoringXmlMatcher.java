package org.nate.testutil;

import static org.nate.internal.util.StreamUtils.wrapInPseudoRootElement;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.Diff;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.nate.exception.IONateException;
import org.nate.exception.NateParseException;
import org.nate.exception.UnsupportedEncodingNateException;
import org.nate.internal.dom.W3cUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Designed to work with jsoup which adds newlines and extra white space.
 */
public class WhiteSpaceIgnoringXmlMatcher extends TypeSafeMatcher<String> {
	
	private final Document expected;

	public WhiteSpaceIgnoringXmlMatcher(String expected) {
		this.expected = stripWhiteSpace(parse(expected));
	}

	/**
	 * Matches when the only differences are empty text nodes, or leading/trailing white space around text in text nodes
	 */
	public static Matcher<String> matchesXmlIgnoringWhiteSpace(String expected) {
		return new WhiteSpaceIgnoringXmlMatcher(expected);
	}
	
	@Override
	protected boolean matchesSafely(String actual) {
		diff = new Diff(expected, stripWhiteSpace(parse(actual)));
		return diff.identical();
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("Expected to match: " + expected + ". Restult: " + diff);
	}

	private static Document parse(String string) {
		return parseXml(wrapInPseudoRootElement(new ByteArrayInputStream(string.getBytes())));
	}
	
	private static Document parseXml(InputStream inputStream) {
		try {
			return createDocumentParser().parse(inputStream);
		} catch (UnsupportedEncodingNateException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new NateParseException(e);
		} catch (SAXException e) {
			throw new NateParseException(e);
		} catch (IOException e) {
			throw new IONateException(e);
		}
	}

	private static <T extends Node> T stripWhiteSpace(T parent) {
		List<Node> nodes = W3cUtils.asNodeList(parent.getChildNodes());
		for (Node node : nodes) {
			if (node.getNodeType() == Node.TEXT_NODE) {
				trimTextNode(node);
			} else {
				stripWhiteSpace(node);
			}
		}
		return parent;
	}

	private static void trimTextNode(Node node) {
		String trimmedValue = node.getNodeValue().trim();
		if (trimmedValue.length() == 0) {
			node.getParentNode().removeChild(node);
		} else {
			node.setTextContent(trimmedValue);
		}
	}

	private static DocumentBuilder createDocumentParser() throws ParserConfigurationException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		builder.setEntityResolver(NULL_ENTITY_RESOLVER);
		return builder;
	}

	private static final EntityResolver NULL_ENTITY_RESOLVER = new EntityResolver() {
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			return new InputSource(new StringReader(""));
		}
	};
	private Diff diff;

}
