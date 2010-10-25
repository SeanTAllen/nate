package org.nate.encoder.html;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashSet;
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

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import se.fishtank.css.selectors.NodeSelectorException;
import se.fishtank.css.selectors.dom.DOMNodeSelector;

public class HtmlFragment {

	private final Node node;

	public HtmlFragment(String source) {
		String wrappedSource = wrapSourceToSupportMultipleFragments(source);
		try {
			// Javadoc for these says nothing about thread safety, and so we recreate every time.
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			node = builder.parse(new ByteArrayInputStream(wrappedSource.getBytes()));
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

	public HtmlFragment(Node node) {
		this.node = node;
	}

	private String wrapSourceToSupportMultipleFragments(String source) {
		// TODO See if there is a better way of doing this.
		// It allows the input to not be a single full xml document, but instead be multiple fragments
		// It also allows a transformation to transform a single fragment into multiple fragments.
		return "<fakeroot>" + source + "</fakeroot>";
	}

	public Set<HtmlFragment> selectNodes(String selector) {
		try {
			Set<Node> matchingNodes = new DOMNodeSelector(node).querySelectorAll(selector);
			Set<HtmlFragment> result = new HashSet<HtmlFragment>(matchingNodes.size());
			for (Node matchingNode : matchingNodes) {
				result.add(new HtmlFragment(matchingNode));
			}
			return result;
		} catch (NodeSelectorException e) {
			throw new RuntimeException(e);
		}
	}

	public void setTextContent(String value) {
		node.setTextContent(value);
	}

	public HtmlFragment getParentNode() {
		return new HtmlFragment(node.getParentNode());
	}

	public void removeChild(HtmlFragment child) {
		node.removeChild(child.node);
	}

	public HtmlFragment cloneFragment(boolean deep) {
		return new HtmlFragment(node.cloneNode(deep));
	}

	public void appendChild(HtmlFragment newNode) {
		node.appendChild(newNode.node);
	}

	public String toHtml() {
		Writer stringWriter = new StringWriter();
		Result result = new StreamResult(stringWriter);
		// Need to unwap from the fakeroot node that was added in HtmlEncoder.encode(). Must be a better way!!!
		NodeList childNodes = node.getChildNodes().item(0).getChildNodes();
		int length = childNodes.getLength();
		for (int i = 0; i < length; i++) {
			convertNodeToString(childNodes.item(i), result);
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
}
