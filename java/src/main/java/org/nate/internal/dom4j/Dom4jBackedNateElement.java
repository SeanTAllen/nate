package org.nate.internal.dom4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;
import org.dom4j.Node;
import org.nate.encoder.NateDocument;
import org.nate.encoder.NateNode;
import org.nate.exception.BadCssExpressionException;
import org.nate.internal.dom4j.cssselectors.Dom4jNodeSelector;

import se.fishtank.css.selectors.NodeSelectorException;

public class Dom4jBackedNateElement extends Dom4jBackedAbstractNode {

	private final Element element;

	public Dom4jBackedNateElement(Element element) {
		this.element = element;
	}

	@Override
	public NateNode copy() {
		verifyState();
		return new Dom4jBackedNateElement(element.createCopy());
	}

	@Override
	public void removeAttribute(String attributeName) {
		verifyState();
		element.remove(element.attribute(attributeName));
	}

	@Override
	public String render() {
		verifyState();
		return element.asXML();
	}

	@Override
	public void replaceChildren(NateDocument newChildrenSource) {
		verifyState();
		removeChildren();
		if (!(newChildrenSource instanceof Dom4jBackedDocumentFragment)) {
			throw new IllegalStateException(
					"Internal Error.  Expected Dom4jBackedDocumentFragment, but got: " + newChildrenSource);
		}
		Collection<? extends Node> newChildren = ((Dom4jBackedAbstractNode) newChildrenSource).getDom4jNodes();
		for (Node node : newChildren) {
			this.element.add((Node) node.clone());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void replaceWith(List<NateNode> newNodes) {
		verifyState();
		
		Element parent = this.element.getParent();
		List<Node> newFamily = new ArrayList<Node>();
		for (Node sibling : (List<Node>) parent.content()) {
			if (sibling == this.element) {
				newFamily.addAll(cloneAll(dom4jNodesOf(newNodes)));
			} else {
				newFamily.add(sibling);
			}
		}
		removeChildrenFrom(parent);
		for (Node node : newFamily) {
			parent.add(node);
		}
		
		// This element has been removed, and so no further operations will be valid.
		invalidate();
	}

	@Override
	public void setAttribute(String name, String value) {
		verifyState();
		element.addAttribute(name, value);
	}

	@Override
	public void setTextContent(String text) {
		verifyState();
		removeChildren();
		element.setText(text);
	}
	
	@SuppressWarnings("unchecked")
	protected Set findMatchingElements(String selector) {
		try {
			return new Dom4jNodeSelector(element).querySelectorAll(selector);
		} catch (NodeSelectorException e) {
			throw new BadCssExpressionException("Invalid CSS Expression: " + selector, e);
		}
	}

	private void removeChildren() {
		removeChildrenFrom(element);
	}

	private static List<Node> dom4jNodesOf(List<NateNode> nateNodes) {
		List<Node> dom4jNodes = new ArrayList<Node>(nateNodes.size());
		for (NateNode nateNode : nateNodes) {
			dom4jNodes.addAll(((Dom4jBackedAbstractNode) nateNode).getDom4jNodes());
		}
		return dom4jNodes;
	}

	@SuppressWarnings("unchecked")
	private static void removeChildrenFrom(Element element) {
		// TODO: Measure performance.  I bet this is quadratic in number of children!!!!
		// Copy into new list to avoid ConcurrentModificationException
		List<Node> childNodes = new ArrayList<Node>(element.content());
		for (Node childNode : childNodes) {
			element.remove(childNode);
		}
	}

	private static Collection<? extends Node> cloneAll(List<Node> originals) {
		Collection<Node> clones = new ArrayList<Node>(originals.size());
		for (Node node : originals) {
			clones.add((Node) node.clone());
		}
		return clones;
	}

	@Override
	protected Collection<? extends Node> getDom4jNodes() {
		return Collections.singletonList(element);
	}

}
