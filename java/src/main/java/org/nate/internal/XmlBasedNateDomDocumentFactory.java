package org.nate.internal;

import static java.util.Collections.singletonList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlBasedNateDomDocumentFactory {
	// Element wrapped around html fragments.
	// Needed because the XML parser cannot cope with XML having multiple root elements.
	static final String FAKEROOT = "fakeroot";

	// Node used as a prototype for creating new empty fake root nodes for HTML fragments.
	private static final Document DOCUMENT_FRAGMENT_PROTOTYPE;
	static {
		DOCUMENT_FRAGMENT_PROTOTYPE = wrapInFakeRootElement(emptyInputStream());
	}

	/**
	 * For use when the input stream has a document type or xml declaration.
	 * Note that we convert it to a fake-root wrapped document so we can treat it in the same way as
	 * a document fragment -- it simplifies things.
	 */
	public NateDomDocument createFromXmlDocument(InputStream input) {
		return createFromW3cElements(singletonList((parseXml(input).getDocumentElement())));
	}

	/**
	 * For use when the input stream does not have a document type declaration, nor an xml declaration.
	 */
	public NateDomDocument createFromXmlDocumentFragment(InputStream input) {
		Document document = wrapInFakeRootElement(input);
		return NateDomDocument.fromFakeRootWrappedFragment(document.getDocumentElement());
	}

	/** 
	 * Copy the supplied nodes into a new NateDomDocument.
	 */
	public NateDomDocument createFromW3cElements(Iterable<Element> elements) {
		Element fakeRoot = (Element) DOCUMENT_FRAGMENT_PROTOTYPE.getDocumentElement().cloneNode(true);
		for (Element element : elements) {
			Node copiedNode = DOCUMENT_FRAGMENT_PROTOTYPE.importNode(element, true);
			fakeRoot.appendChild(copiedNode);
		}
		return NateDomDocument.fromFakeRootWrappedFragment(fakeRoot);
	}

	private static Document parseXml(InputStream inputStream) {
		try {
			return createDocumentParser().parse(inputStream);
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

	// Need to wrap fragments in a fake room element otherwise they will not parse.
	private static Document wrapInFakeRootElement(InputStream source) {
		InputStream startTag = new ByteArrayInputStream(("<" + FAKEROOT + ">").getBytes());
		InputStream endTag = new ByteArrayInputStream(("</" + FAKEROOT + ">").getBytes());
		SequenceInputStream wrappedStream = new SequenceInputStream(startTag, new SequenceInputStream(source, endTag));
		return parseXml(wrappedStream);
	}

	private static InputStream emptyInputStream() {
		return new ByteArrayInputStream(new byte[0]);
	}

}
