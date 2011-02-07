package org.nate.internal.dom4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.nate.exception.IONateException;
import org.nate.internal.util.StreamUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Dom4jBackedNateDocumentFactory {
	private static final String NATE_FRAGMENT_WRAPPER = "natewrapper";
	private static final String BEGIN_NATE_FRAGMENT_WRAPPER = "<" + NATE_FRAGMENT_WRAPPER + ">";
	private static final String END_NATE_FRAGMENT_WRAPPER = "</" + NATE_FRAGMENT_WRAPPER + ">";

	private static final EntityResolver NULL_ENTITY_RESOLVER = new EntityResolver() {
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			return new InputSource(new StringReader(""));
		}
	};

	public Dom4jBackedNateDocument createFromXmlDocument(String source) {
		return new Dom4jBackedNateDocument(parseXml(source));
	}

	public Dom4jBackedNateDocument createFromXmlDocument(InputStream source) {
		return new Dom4jBackedNateDocument(parseXml(source));
	}

	public Dom4jBackedDocumentFragment createFromXmlDocumentFragment(InputStream source) {
		return new Dom4jBackedDocumentFragment(parseFragment(source));
	}

	public Dom4jBackedDocumentFragment createFromXmlDocumentFragment(String source) {
		return new Dom4jBackedDocumentFragment(parseFragment(source));
	}

	@SuppressWarnings("unchecked")
	private List<Node> parseFragment(InputStream input) {
		Element pseudoRootNode =
			parseXml(StreamUtils.wrapInPseudoRootElement(input, NATE_FRAGMENT_WRAPPER))
				.getRootElement();
		return pseudoRootNode.content();
	}

	@SuppressWarnings("unchecked")
	private static List<Node> parseFragment(String input) {
		Element pseudoRootNode =
			parseXml(wrapInPseudoRootElement(input)).getRootElement();
		return pseudoRootNode.content();
	}

	private static Document parseXml(String input) {
		try {
			return saxReader().read(new StringReader(input));
		} catch (DocumentException e) {
			throw new IONateException(e);
		}
	}
	private static Document parseXml(InputStream input) {
		try {
			return saxReader().read(input);
		} catch (DocumentException e) {
			throw new IONateException(e);
		}
	}

	private static SAXReader saxReader() {
		SAXReader saxReader = new SAXReader();
		saxReader.setEntityResolver(NULL_ENTITY_RESOLVER);
		return saxReader;
	}

	private static String wrapInPseudoRootElement(String source) {
		return BEGIN_NATE_FRAGMENT_WRAPPER + source + END_NATE_FRAGMENT_WRAPPER;
	}

}
