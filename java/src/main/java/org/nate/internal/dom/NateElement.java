package org.nate.internal.dom;

import static java.util.Collections.singletonList;

import java.util.List;

import org.nate.encoder.NateNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class NateElement extends AbstactNateNode {

	private final Element element;
	private final List<Node> rootNodes;

	NateElement(Element element) {
		super(element);
		this.element = element;
		rootNodes = singletonList((Node) element);
	}
	
	@Override
	public List<Node> getRootNodes() {
		verifyState();
		return rootNodes;
	}

	@Override
	public NateNode copy() {
		verifyState();
		return new NateElement((Element) element.cloneNode(true));
	}
	

	public void replaceWith(List<NateNode> newNodes) {
		verifyState();
		Document ownerDocument = element.getOwnerDocument();
		Node parentNode = element.getParentNode();
		for (NateNode newNode: newNodes) {
			for (Node w3cNode : ((AbstactNateNode) newNode).getRootNodes()) {
				Node importedNode = ownerDocument.importNode(w3cNode, true);
				parentNode.insertBefore(importedNode, element);
			}
		}
		parentNode.removeChild(element);
		// This element has been removed, and so no further operations will be valid.
		invalidate();
	}

}
