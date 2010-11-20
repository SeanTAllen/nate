package org.nate.internal;

import static java.util.Collections.singletonList;

import java.util.List;

import org.w3c.dom.Element;

class NateDomElement extends NateDomNode {

	private List<Element> rootElements;

	NateDomElement(Element element) {
		super(element);
		rootElements = singletonList(element);
	}
	
	@Override
	public List<Element> getRootElements() {
		return rootElements;
	}

}
