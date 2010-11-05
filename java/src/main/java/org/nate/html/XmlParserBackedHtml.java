package org.nate.html;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import se.fishtank.css.selectors.NodeSelectorException;
import se.fishtank.css.selectors.dom.DOMNodeSelector;

import static java.util.Collections.*;

public class XmlParserBackedHtml implements Html {

	private static final String FAKEROOT = "fakeroot";

	public static XmlParserBackedHtml fromDocument(String source) {
		return new XmlParserBackedHtml(source);
	}
	
	public static XmlParserBackedHtml fromFragment(String source) {
		Element fakeNode = wrapInFakeRootElement(source);
		assert fakeNode.getNodeName().equals(FAKEROOT) : "Expected fakeroot but got " + fakeNode.getNodeName();
		return new XmlParserBackedHtml(fakeNode);
	}

	private static Element wrapInFakeRootElement(String source) {
		String wrappedSource = String.format("<%s>%s</%s>", FAKEROOT, source, FAKEROOT); 
		XmlParserBackedHtml document = new XmlParserBackedHtml(wrappedSource);
		return ((Document)(document.node)).getDocumentElement();
	}

	// This node is the fakeroot element when this is a fragment, otherwise it is the Document node.
	private final Node node;

	public XmlParserBackedHtml(Node node) {
		this.node = node;
	}

	public XmlParserBackedHtml(List<Html> htmlFragments) {
		this.node = fromFragment("").node;
		for (Html fragment : htmlFragments) {
			// TODO: Nice if we could avoid this casting.
			Node newNode = ((XmlParserBackedHtml)fragment).node;
			adopt(newNode);
			this.node.appendChild(newNode);
		}
	}

	private void adopt(Node newNode) {
		node.getOwnerDocument().adoptNode(newNode);
	}

	private XmlParserBackedHtml(String source) {
		try {
			// Javadoc for these says nothing about thread safety, and so we recreate every time.
			DocumentBuilder builder = createDocumentParser();
			node = builder.parse(new ByteArrayInputStream(source.getBytes()));
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

	private DocumentBuilder createDocumentParser() throws ParserConfigurationException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		builder.setEntityResolver(NULL_ENTITY_RESOLVER);
		return builder;
	}

	public List<Html> selectNodes(String selector) {
		try {
			Set<Node> matchingNodes = new DOMNodeSelector(node).querySelectorAll(selector);
			List<Html> result = new ArrayList<Html>(matchingNodes.size());
			for (Node matchingNode : matchingNodes) {
				result.add(new XmlParserBackedHtml(matchingNode));
			}
			return result;
		} catch (NodeSelectorException e) {
			throw new RuntimeException(e);
		}
	}

	public void setTextContent(String value) {
		node.setTextContent(value);
	}

	public Html getParentNode() {
		return new XmlParserBackedHtml(node.getParentNode());
	}

	public void removeChild(Html child) {
		// TODO: Nice if we could avoid this casting.
		node.removeChild(((XmlParserBackedHtml)child).node);
	}

	public Html cloneFragment(boolean deep) {
		// TODO: Nice if we could avoid this casting.
		return new XmlParserBackedHtml(node.cloneNode(deep));
	}

	public void appendChild(Html newFragment) {
		// TODO: Nice if we could avoid this casting.
		node.appendChild(((XmlParserBackedHtml)newFragment).node);
	}

	public void replaceChildren(Html newFragment) {
		removeChildren();
		// TODO: Nice if we could avoid this casting.
		List<Node> newNodes = ((XmlParserBackedHtml)newFragment).getChildNodes();
		for (Node newNode : newNodes) {
			adopt(newNode);
			node.appendChild(newNode);
		}
	}

	public String toHtml() {
		return hasFakeRoot() ? fakeRootToString() : documentToString();
	}

	private void removeChildren() {
		for (Node node : getChildNodes()) {
			node.removeChild(node);
		}
	}
	
	private boolean hasFakeRoot() {
		return node.getNodeName().equals(FAKEROOT);
	}
	
	private String documentToString() {
		Writer stringWriter = new StringWriter();
		Result result = new StreamResult(stringWriter);
		convertNodeToString(node, result);
	    return stringWriter.toString();
	}

	private String fakeRootToString() {
		Writer stringWriter = new StringWriter();
		Result result = new StreamResult(stringWriter);
		for (Node childNode : getChildNodes()) {
			convertNodeToString(childNode, result);
		}
		return stringWriter.toString();
	}

	private void convertNodeToString(Node node, Result result) {
		try {
			Source source = new DOMSource((Node) node);
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty("method", "html");
			xformer.setOutputProperty("omit-xml-declaration", "yes");
			xformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new RuntimeException(e);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean hasAttribute(String name) {
		NamedNodeMap attributes = node.getAttributes();
		if (attributes == null) {
			return false;
		}
		for(int i = 0; i < attributes.getLength(); i++) {
			if (attributes.item(i).getNodeName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public void setAttribute(String name, Object value) {
		NamedNodeMap attributes = node.getAttributes();
		for(int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			if (attribute.getNodeName().equals(name)) {
				attribute.setTextContent(value.toString());
			}
		}
	}
	private List<Node> getChildNodes() {
		NodeList nodeList = node.getChildNodes();
		if (nodeList == null) {
			return emptyList();
		}
		List<Node> result = new ArrayList<Node>();
		int length = nodeList.getLength();
		for (int i = 0; i < length; i++) {
			result.add(nodeList.item(i));
		}
		return result;
	}

	private static final EntityResolver NULL_ENTITY_RESOLVER = new EntityResolver() {
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			return new InputSource(new StringReader(""));
		}
	};

}