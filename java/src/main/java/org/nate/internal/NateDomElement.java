package org.nate.internal;

import static java.util.Collections.singletonList;

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

class NateDomElement extends NateDomNode {

	private List<Node> rootNodes;

	NateDomElement(Element element) {
		super(element);
		rootNodes = singletonList((Node) element);
	}
	
	@Override
	public List<Node> getRootNodes() {
		return rootNodes;
	}

}
