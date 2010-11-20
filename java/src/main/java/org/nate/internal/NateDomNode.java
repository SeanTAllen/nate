package org.nate.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import se.fishtank.css.selectors.NodeSelectorException;
import se.fishtank.css.selectors.dom.DOMNodeSelector;

public abstract class NateDomNode {

	// The top node is the top of the tree... either a Document or an element.
	private final Node topNode;
	
	// The root nodes may be the single topNode, or a number of nodes wrapped in a fake topNode to represent a fragment.
	private final List<? extends Node> rootNodes;

	NateDomNode(Node node, List<? extends Node> rootNodes) {
		this.topNode = node;
		this.rootNodes = rootNodes;
	}

	/**
	 * Find references to nodes matching the selector such that modifications to these references will modify
	 * this NateDomNode.
	 */
	public List<NateDomElement> find(String selector) {
		try {
			Set<Node> matchingNodes = new DOMNodeSelector(topNode).querySelectorAll(selector);
			List<NateDomElement> result = new ArrayList<NateDomElement>(matchingNodes.size());
			for (Node matchingNode : matchingNodes) {
				result.add(new NateDomElement((Element) matchingNode));
			}
			return result;
		} catch (NodeSelectorException e) {
			throw new RuntimeException(e);
		}
	}

	public abstract String render();
	
	@Override
	public String toString() {
		return render();
	}

	public List<? extends Node> getRootNodes() {
		return rootNodes;
	}

//	private List<Node> getChildNodes() {
//		return asNodeList(topNode.getChildNodes());
//	}

//	@Override
//	public List<Html> selectNodes(String selector) {
//		return selectNodes(selector, false);
//	}
//	
//	@Override
//	public List<Html> selectContentOfNodes(String selector) {
//		return selectNodes(selector, true);
//	}
//	
//	public void setTextContent(String value) {
//		topNode.setTextContent(value);
//	}
//
//	@Override
//	public void replaceWith(List<Html> newFragments) {
//		Node parentNode = topNode.getParentNode();
//		for (Html newNode : newFragments) {
//			parentNode.insertBefore(((NateDomDocument)newNode).node, topNode);
//		}
//		parentNode.removeChild(topNode);
//	}
//	
//	public Html cloneFragment() {
//		return new NateDomDocument(topNode.cloneNode(true));
//	}
//
//	public void replaceChildren(Html newFragment) {
//		removeChildren();
//		List<Node> newNodes = ((NateDomDocument)newFragment).getChildNodes();
//		for (Node newNode : newNodes) {
//			adopt(newNode);
//			topNode.appendChild(newNode);
//		}
//	}
//
//
//	public boolean hasAttribute(String name) {
//		NamedNodeMap attributes = topNode.getAttributes();
//		if (attributes == null) {
//			return false;
//		}
//		for(int i = 0; i < attributes.getLength(); i++) {
//			if (attributes.item(i).getNodeName().equals(name)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public void setAttribute(String name, Object value) {
//		NamedNodeMap attributes = topNode.getAttributes();
//		for(int i = 0; i < attributes.getLength(); i++) {
//			Node attribute = attributes.item(i);
//			if (attribute.getNodeName().equals(name)) {
//				attribute.setTextContent(value.toString());
//			}
//		}
//	}
//
//	private List<Html> selectNodes(String selector, boolean selectContent) {
//		try {
//			Set<Node> matchingNodes = new DOMNodeSelector(topNode).querySelectorAll(selector);
//			List<Html> result = new ArrayList<Html>(matchingNodes.size());
//			for (Node matchingNode : matchingNodes) {
//				if (selectContent) {
//					result.add(new NateDomDocument(asList(matchingNode.getChildNodes())));
//				} else {
//					result.add(new NateDomDocument(matchingNode));
//				}
//			}
//			return result;
//		} catch (NodeSelectorException e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	private void adopt(Node newNode) {
//		topNode.getOwnerDocument().adoptNode(newNode);
//	}
//
//	private void removeChildren() {
//		for (Node topNode : getChildNodes()) {
//			topNode.removeChild(topNode);
//		}
//	}
//	

//
//	private String contentToString() {
//		Writer stringWriter = new StringWriter();
//		Result result = new StreamResult(stringWriter);
//		for (Node childNode : getChildNodes()) {
//			convertNodeToString(childNode, result);
//		}
//		return stringWriter.toString();
//	}
//

//
//	private List<Node> getChildNodes() {
//		return asList(topNode.getChildNodes());
//	}
//
//	private Collection<Node> getRootNodes() {
//		return hasFakeRoot ? getChildNodes() : singletonList(this.node);
//	}
//
//	private static DocumentBuilder createDocumentParser() throws ParserConfigurationException {
//		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder builder = domFactory.newDocumentBuilder();
//		builder.setEntityResolver(NULL_ENTITY_RESOLVER);
//		return builder;
//	}
//
//	private static Node wrapInFakeRootElement(InputStream source) {
//		InputStream startTag = new ByteArrayInputStream(("<" + FAKEROOT + ">").getBytes());
//		InputStream endTag = new ByteArrayInputStream(("</" + FAKEROOT + ">").getBytes());
//		SequenceInputStream wrappedStream = new SequenceInputStream(startTag, new SequenceInputStream(source, endTag));
//		return parseXml(wrappedStream).getDocumentElement();
//	}
//
//	private static Document parseXml(InputStream inputStream) {
//		try {
//			return createDocumentParser().parse(inputStream);
//		} catch (UnsupportedEncodingException e) {
//			throw new RuntimeException(e);
//		} catch (ParserConfigurationException e) {
//			throw new RuntimeException(e);
//		} catch (SAXException e) {
//			throw new RuntimeException(e);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	private static List<Node> asList(NodeList nodeList) {
//		if (nodeList == null) {
//			return emptyList();
//		}
//		List<Node> result = new ArrayList<Node>();
//		int length = nodeList.getLength();
//		for (int i = 0; i < length; i++) {
//			result.add(nodeList.item(i));
//		}
//		return result;
//	}
//
//	private static InputStream emptyInputStream() {
//		return new ByteArrayInputStream(new byte[0]) ;
//	}
//	private static final EntityResolver NULL_ENTITY_RESOLVER = new EntityResolver() {
//		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
//			return new InputSource(new StringReader(""));
//		}
//	};
}
