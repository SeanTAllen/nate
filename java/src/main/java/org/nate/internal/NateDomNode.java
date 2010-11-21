package org.nate.internal;

import static org.nate.internal.W3cUtils.convertNodeToString;
import static org.nate.internal.W3cUtils.convertToNateDomElements;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.nate.exception.BadCssExpressionException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import se.fishtank.css.selectors.NodeSelectorException;
import se.fishtank.css.selectors.dom.DOMNodeSelector;

public abstract class NateDomNode {

	// The top element is the top of the tree... either a fake wrapping element or an internal element found by a selector.
	private final Element topElement;

	NateDomNode(Element topElement) {
		this.topElement = topElement;
	}

	/**
	 * Find references to nodes matching the selector such that modifications to these references will modify this
	 * NateDomNode.
	 */
	public List<NateDomElement> find(String selector) {
		return convertToNateDomElements(findMatchingW3cElements(selector));
	}

	@SuppressWarnings("unchecked")
	Collection<Element> findMatchingW3cElements(String selector) {
		try {
			return (Collection) new DOMNodeSelector(topElement).querySelectorAll(selector);
		} catch (NodeSelectorException e) {
			throw new BadCssExpressionException("Invalid CSS Expression: " + selector, e);
		}
	}

	public String render() {
		Writer stringWriter = new StringWriter();
		Result result = new StreamResult(stringWriter);
		for (Node node : getRootNodes()) {
			convertNodeToString(node, result);
		}
		return stringWriter.toString();
	}

	@Override
	public String toString() {
		return render();
	}

	public abstract List<Node> getRootNodes();

	// private List<Node> getChildNodes() {
	// return asNodeList(topElement.getChildNodes());
	// }

	// @Override
	// public List<Html> selectNodes(String selector) {
	// return selectNodes(selector, false);
	// }
	//	
	// @Override
	// public List<Html> selectContentOfNodes(String selector) {
	// return selectNodes(selector, true);
	// }
	//	
	// public void setTextContent(String value) {
	// topElement.setTextContent(value);
	// }
	//
	// @Override
	// public void replaceWith(List<Html> newFragments) {
	// Node parentNode = topElement.getParentNode();
	// for (Html newNode : newFragments) {
	// parentNode.insertBefore(((NateDomDocument)newNode).node, topElement);
	// }
	// parentNode.removeChild(topElement);
	// }
	//	
	// public Html cloneFragment() {
	// return new NateDomDocument(topElement.cloneNode(true));
	// }
	//
	// public void replaceChildren(Html newFragment) {
	// removeChildren();
	// List<Node> newNodes = ((NateDomDocument)newFragment).getChildNodes();
	// for (Node newNode : newNodes) {
	// adopt(newNode);
	// topElement.appendChild(newNode);
	// }
	// }
	//
	//
	// public boolean hasAttribute(String name) {
	// NamedNodeMap attributes = topElement.getAttributes();
	// if (attributes == null) {
	// return false;
	// }
	// for(int i = 0; i < attributes.getLength(); i++) {
	// if (attributes.item(i).getNodeName().equals(name)) {
	// return true;
	// }
	// }
	// return false;
	// }
	//
	// public void setAttribute(String name, Object value) {
	// NamedNodeMap attributes = topElement.getAttributes();
	// for(int i = 0; i < attributes.getLength(); i++) {
	// Node attribute = attributes.item(i);
	// if (attribute.getNodeName().equals(name)) {
	// attribute.setTextContent(value.toString());
	// }
	// }
	// }
	//
	// private List<Html> selectNodes(String selector, boolean selectContent) {
	// try {
	// Set<Node> matchingNodes = new DOMNodeSelector(topElement).querySelectorAll(selector);
	// List<Html> result = new ArrayList<Html>(matchingNodes.size());
	// for (Node matchingNode : matchingNodes) {
	// if (selectContent) {
	// result.add(new NateDomDocument(asList(matchingNode.getChildNodes())));
	// } else {
	// result.add(new NateDomDocument(matchingNode));
	// }
	// }
	// return result;
	// } catch (NodeSelectorException e) {
	// throw new RuntimeException(e);
	// }
	// }
	//
	// private void adopt(Node newNode) {
	// topElement.getOwnerDocument().adoptNode(newNode);
	// }
	//
	// private void removeChildren() {
	// for (Node topElement : getChildNodes()) {
	// topElement.removeChild(topElement);
	// }
	// }
	//	

	//
	// private String contentToString() {
	// Writer stringWriter = new StringWriter();
	// Result result = new StreamResult(stringWriter);
	// for (Node childNode : getChildNodes()) {
	// convertNodeToString(childNode, result);
	// }
	// return stringWriter.toString();
	// }
	//

	//
	// private List<Node> getChildNodes() {
	// return asList(topElement.getChildNodes());
	// }
	//
	// private Collection<Node> getRootNodes() {
	// return hasFakeRoot ? getChildNodes() : singletonList(this.node);
	// }
	//
	// private static DocumentBuilder createDocumentParser() throws ParserConfigurationException {
	// DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	// DocumentBuilder builder = domFactory.newDocumentBuilder();
	// builder.setEntityResolver(NULL_ENTITY_RESOLVER);
	// return builder;
	// }
	//
	// private static Node wrapInFakeRootElement(InputStream source) {
	// InputStream startTag = new ByteArrayInputStream(("<" + FAKEROOT + ">").getBytes());
	// InputStream endTag = new ByteArrayInputStream(("</" + FAKEROOT + ">").getBytes());
	// SequenceInputStream wrappedStream = new SequenceInputStream(startTag, new SequenceInputStream(source, endTag));
	// return parseXml(wrappedStream).getDocumentElement();
	// }
	//
	// private static Document parseXml(InputStream inputStream) {
	// try {
	// return createDocumentParser().parse(inputStream);
	// } catch (UnsupportedEncodingException e) {
	// throw new RuntimeException(e);
	// } catch (ParserConfigurationException e) {
	// throw new RuntimeException(e);
	// } catch (SAXException e) {
	// throw new RuntimeException(e);
	// } catch (IOException e) {
	// throw new RuntimeException(e);
	// }
	// }
	//
	// private static List<Node> asList(NodeList nodeList) {
	// if (nodeList == null) {
	// return emptyList();
	// }
	// List<Node> result = new ArrayList<Node>();
	// int length = nodeList.getLength();
	// for (int i = 0; i < length; i++) {
	// result.add(nodeList.item(i));
	// }
	// return result;
	// }
	//
	// private static InputStream emptyInputStream() {
	// return new ByteArrayInputStream(new byte[0]) ;
	// }
	// private static final EntityResolver NULL_ENTITY_RESOLVER = new EntityResolver() {
	// public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
	// return new InputSource(new StringReader(""));
	// }
	// };
}
