package org.nate.html;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
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

public class XmlParserBackedHtml implements Html {
	private static final String FAKEROOT = "fakeroot";

	public static XmlParserBackedHtml fromDocument(String source) {
		return new XmlParserBackedHtml(parseXml(source));
	}

	public static Html fromDocument(InputStream source) {
		return new XmlParserBackedHtml(parseXml(source));
	}

	public static XmlParserBackedHtml fromFragment(String source) {
		return new XmlParserBackedHtml(wrapInFakeRootElement(source));
	}
	
	public static XmlParserBackedHtml fromFragments(List<Html> htmlFragments) {
		List<Node> nodes = new ArrayList<Node>();
		for (Html fragment : htmlFragments) {
			nodes.addAll(((XmlParserBackedHtml) fragment).getRootNodes());
		}
		return new XmlParserBackedHtml(nodes);
	}

	// True if the html/xml is all wrapped in a <fakeroot> element to allow for multiple roots.
	private final boolean hasFakeRoot;
	// This node can be one of: the document root, an element, or a fake root.
	private final Node node;

	private XmlParserBackedHtml(Node node) {
		this.hasFakeRoot = node.getNodeName().equals(FAKEROOT);
		this.node = node;
	}

	private XmlParserBackedHtml(List<Node> nodes) {
		this.hasFakeRoot = true;
		this.node = wrapInFakeRootElement("");
		for (Node newNode : nodes) {
			adopt(newNode);
			this.node.appendChild(newNode);
		}
	}

	@Override
	public List<Html> selectNodes(String selector) {
		return selectNodes(selector, false);
	}
	
	@Override
	public List<Html> selectContentOfNodes(String selector) {
		return selectNodes(selector, true);
	}
	
	public void setTextContent(String value) {
		node.setTextContent(value);
	}

	@Override
	public void replaceWith(List<Html> newFragments) {
		Node parentNode = node.getParentNode();
		for (Html newNode : newFragments) {
			parentNode.insertBefore(((XmlParserBackedHtml)newNode).node, node);
		}
		parentNode.removeChild(node);
	}
	
	public Html cloneFragment() {
		return new XmlParserBackedHtml(node.cloneNode(true));
	}

	public void replaceChildren(Html newFragment) {
		removeChildren();
		List<Node> newNodes = ((XmlParserBackedHtml)newFragment).getChildNodes();
		for (Node newNode : newNodes) {
			adopt(newNode);
			node.appendChild(newNode);
		}
	}

	public String toHtml() {
		return hasFakeRoot ? contentToString() : nodeToString();
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

	private List<Html> selectNodes(String selector, boolean selectContent) {
		try {
			Set<Node> matchingNodes = new DOMNodeSelector(node).querySelectorAll(selector);
			List<Html> result = new ArrayList<Html>(matchingNodes.size());
			for (Node matchingNode : matchingNodes) {
				if (selectContent) {
					result.add(new XmlParserBackedHtml(asList(matchingNode.getChildNodes())));
				} else {
					result.add(new XmlParserBackedHtml(matchingNode));
				}
			}
			return result;
		} catch (NodeSelectorException e) {
			throw new RuntimeException(e);
		}
	}

	private void adopt(Node newNode) {
		node.getOwnerDocument().adoptNode(newNode);
	}

	private void removeChildren() {
		for (Node node : getChildNodes()) {
			node.removeChild(node);
		}
	}
	
	private String nodeToString() {
		Writer stringWriter = new StringWriter();
		Result result = new StreamResult(stringWriter);
		convertNodeToString(node, result);
	    return stringWriter.toString();
	}

	private String contentToString() {
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

	private List<Node> getChildNodes() {
		return asList(node.getChildNodes());
	}

	private Collection<Node> getRootNodes() {
		return hasFakeRoot ? getChildNodes() : singletonList(this.node);
	}

	private static DocumentBuilder createDocumentParser() throws ParserConfigurationException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		builder.setEntityResolver(NULL_ENTITY_RESOLVER);
		return builder;
	}

	private static Element wrapInFakeRootElement(String source) {
		// Wrap source in a fake root to allow it to have multiple roots.
		String wrappedSource = String.format("<%s>%s</%s>", FAKEROOT, source, FAKEROOT);
		return parseXml(wrappedSource).getDocumentElement();
	}

	private static Document parseXml(String source) {
		return parseXml(new ByteArrayInputStream(source.getBytes()));
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

	private static List<Node> asList(NodeList nodeList) {
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
