package org.nate.internal;

import static org.nate.internal.W3cUtils.asElementList;

import java.util.List;

import org.w3c.dom.Element;

class NateDomDocument extends NateDomNode {

	// TODO: May want to inject this...
	private final XmlBasedNateDomDocumentFactory domDocumentFactory = new XmlBasedNateDomDocumentFactory();
	private final List<Element> rootElements;

	private NateDomDocument(Element wrappingElement, List<Element> rootElements) {
		super(wrappingElement);
		assert wrappingElement.getNodeName().equals(XmlBasedNateDomDocumentFactory.FAKEROOT) : 
			"Should only be constructed from document fragments wrapped in a fake root node.";
		this.rootElements = rootElements;
	}

	static NateDomDocument fromFakeRootWrappedFragment(Element wrappingElement) {
		return new NateDomDocument(wrappingElement, asElementList(wrappingElement.getChildNodes()));
	}

	/**
	 * Create copies such that modifications will not modify this NateDomDocument.
	 */
	public NateDomDocument copy(String selector) {
		return domDocumentFactory.createFromW3cElements(findMatchingW3cElements(selector));
	}
	
	@Override
	public List<Element> getRootElements() {
		return rootElements;
	}
	
}
