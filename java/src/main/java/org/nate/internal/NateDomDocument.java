package org.nate.internal;

import static org.nate.internal.W3cUtils.asNodeList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

class NateDomDocument extends NateDomNode {

	// TODO: May want to inject this...
	private final XmlBasedNateDomDocumentFactory domDocumentFactory = new XmlBasedNateDomDocumentFactory();
	private final List<Node> rootNodes;

	private NateDomDocument(Element wrappingElement, List<Node> rootNodes) {
		super(wrappingElement);
		this.rootNodes = rootNodes;
	}

	@SuppressWarnings("unchecked")
	static NateDomDocument fromFakeRootWrappedFragment(Element wrappingElement) {
		List rootNodes = asNodeList(wrappingElement.getChildNodes());
		return new NateDomDocument(wrappingElement, rootNodes);
	}

	/**
	 * Create copies such that modifications will not modify this NateDomDocument.
	 */
	public NateDomDocument copy(String selector) {
		return domDocumentFactory.createFromW3cNodes(findMatchingW3cElements(selector));
	}

	public NateDomDocument copyContentOf(String selector) {
		Collection<Element> elements = findMatchingW3cElements(selector);
		List<Node> content = new ArrayList<Node>();
		for (Element element : elements) {
			content.addAll(asNodeList(element.getChildNodes()));
		}
		return domDocumentFactory.createFromW3cNodes(content);
	}
		
	@Override
	public List<Node> getRootNodes() {
		return rootNodes;
	}

}
