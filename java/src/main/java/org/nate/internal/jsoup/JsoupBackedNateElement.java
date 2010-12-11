package org.nate.internal.jsoup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Selector.SelectorParseException;
import org.nate.encoder.NateDocument;
import org.nate.encoder.NateNode;
import org.nate.exception.BadCssExpressionException;

public class JsoupBackedNateElement extends JsoupBackedAbstractNode {

	private Element element;

	public JsoupBackedNateElement(Element element) {
		this.element = element;
	}

	@Override
	public NateNode copy() {
		verifyState();
		return new JsoupBackedNateElement(element.clone());
	}

	@Override
	public void removeAttribute(String attributeName) {
		verifyState();
		element.removeAttr(attributeName);
	}

	@Override
	public String render() {
		verifyState();
		return element.outerHtml();
	}

	@Override
	public void replaceChildren(NateDocument newChildrenSource) {
		verifyState();
		removeChildren();
		if (!(newChildrenSource instanceof JsoupBackedNateDocumentFragment)) {
			throw new IllegalStateException(
					"Internal Error.  Expected JsoupBackedNateDocumentFragment, but got: " + newChildrenSource);
		}
		Collection<Node> newChildren = ((JsoupBackedAbstractNode)newChildrenSource).getJsoupNodes();
		for (Node node : newChildren) {
			this.element.appendChild(node.clone());
		}

	}

	@Override
	public void replaceWith(List<NateNode> newNodes) {
		verifyState();
		
		Element parent = this.element.parent();
		List<Node> newFamily = new ArrayList<Node>();
		for (Node sibling : parent.childNodes()) {
			if (sibling == this.element) {
				newFamily.addAll(jsoupNodesOf(newNodes));
			} else {
				newFamily.add(sibling);
			}
		}
		removeChildrenFrom(parent);
		for (Node node : newFamily) {
			parent.appendChild(node.clone());
		}
		
		// This element has been removed, and so no further operations will be valid.
		invalidate();
	}

	private static List<Node> jsoupNodesOf(List<NateNode> nateNodes) {
		List<Node> jsoupNodes = new ArrayList<Node>(nateNodes.size());
		for (NateNode nateNode : nateNodes) {
			jsoupNodes.addAll(((JsoupBackedAbstractNode) nateNode).getJsoupNodes());
		}
		return jsoupNodes;
	}

	@Override
	public void setAttribute(String name, String value) {
		verifyState();
		element.attr(name, value);
	}

	@Override
	public void setTextContent(String text) {
		verifyState();
		removeChildren();
		element.appendText(text);
	}

	private void removeChildren() {
		removeChildrenFrom(element);
	}

	private static void removeChildrenFrom(Element element) {
		// TODO: Measure performance.  I bet this is quadratic in number of children!!!!
		// Copy into new list to avoid ConcurrentModificationException
		List<Node> childNodes = new ArrayList<Node>(element.childNodes());
		for (Node childNode : childNodes) {
			childNode.remove();
		}
	}

	@Override
	protected List<Element> findMatchingElements(String selector) {
		try {
			return element.select(selector);
		} catch (SelectorParseException e) {
			throw new BadCssExpressionException(e);
		}
	}

	@Override
	Collection<Node> getJsoupNodes() {
		return Collections.singleton((Node) element);
	}

}
