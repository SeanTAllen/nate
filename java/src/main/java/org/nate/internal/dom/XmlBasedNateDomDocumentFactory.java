package org.nate.internal.dom;

import static java.util.Collections.singletonList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.nate.encoder.NateDocument;
import org.nate.exception.IONateException;
import org.nate.exception.NateParseException;
import org.nate.exception.UnsupportedEncodingNateException;
import org.nate.internal.util.StreamUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlBasedNateDomDocumentFactory {

	// Node used as a prototype for creating new empty pseudo root nodes for HTML fragments.
	private static final Document DOCUMENT_FRAGMENT_PROTOTYPE;
	static {
		DOCUMENT_FRAGMENT_PROTOTYPE = parseXml(StreamUtils.wrapInPseudoRootElement(emptyInputStream()));
	}

	/**
	 * For use when the input stream has a document type or xml declaration.
	 * Note that we convert it to a pseudo-root wrapped document so we can treat it in the same way as
	 * a document fragment -- it simplifies things.
	 */
	public NateDocument createFromXmlDocument(InputStream input) {
		return createFromW3cNodes(singletonList(parseXml(input).getDocumentElement()));
	}

	/**
	 * For use when the input stream does not have a document type declaration, nor an xml declaration.
	 */
	public PseudoWrappingElementBasedNateDocument createFromXmlDocumentFragment(InputStream input) {
		Document document = parseXml(StreamUtils.wrapInPseudoRootElement(input));
		return PseudoWrappingElementBasedNateDocument.fromPseudoRootWrappedFragment(document.getDocumentElement());
	}

	/** 
	 * Copy the supplied nodes into a new NateDomDocument.
	 */
	public NateDocument createFromW3cNodes(Iterable<? extends Node> nodes) {
		Element pseudoRoot = (Element) DOCUMENT_FRAGMENT_PROTOTYPE.getDocumentElement().cloneNode(true);
		for (Node element : nodes) {
			Node copiedNode = DOCUMENT_FRAGMENT_PROTOTYPE.importNode(element, true);
			pseudoRoot.appendChild(copiedNode);
		}
		return PseudoWrappingElementBasedNateDocument.fromPseudoRootWrappedFragment(pseudoRoot);
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

	private static InputStream emptyInputStream() {
		return new ByteArrayInputStream(new byte[0]);
	}

}
