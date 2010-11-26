package org.nate.internal.dom;

import static org.nate.internal.dom.W3cUtils.asNodeList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.nate.encoder.NateDocument;
import org.nate.encoder.NateNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class NateDomDocument extends NateDomNode implements NateDocument {

	// TODO: May want to inject this...
	private final XmlBasedNateDomDocumentFactory domDocumentFactory = new XmlBasedNateDomDocumentFactory();
	private final Element wrappingElement;

	private NateDomDocument(Element wrappingElement) {
		super(wrappingElement);
		this.wrappingElement = wrappingElement;
	}

	static NateDomDocument fromFakeRootWrappedFragment(Element wrappingElement) {
		return new NateDomDocument(wrappingElement);
	}

	public NateDomDocument copy() {
		return new NateDomDocument((Element) wrappingElement.cloneNode(true));
	}

	/**
	 * Create copies such that modifications will not modify this NateDomDocument.
	 */
	public NateDocument copy(String selector) {
		return domDocumentFactory.createFromW3cNodes(findMatchingW3cElements(selector));
	}

	public NateDocument copyContentOf(String selector) {
		Collection<Element> elements = findMatchingW3cElements(selector);
		List<Node> content = new ArrayList<Node>();
		for (Element element : elements) {
			content.addAll(asNodeList(element.getChildNodes()));
		}
		return domDocumentFactory.createFromW3cNodes(content);
	}
		
	@Override
	public List<Node> getRootNodes() {
		return asNodeList(wrappingElement.getChildNodes());
	}

	@Override
	public void replaceWith(List<NateNode> newNodes) {
		removeChildren();
		Document ownerDocument = wrappingElement.getOwnerDocument();
		for (NateNode newNode: newNodes) {
			for (Node w3cNode : newNode.getRootNodes()) {
				Node importedNode = ownerDocument.importNode(w3cNode, true);
				wrappingElement.appendChild(importedNode);
			}
		}
	}
}
