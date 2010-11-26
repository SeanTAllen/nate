package org.nate.internal.dom;

import static org.nate.internal.dom.W3cUtils.asNodeList;
import static org.nate.internal.dom.W3cUtils.convertNodeToString;
import static org.nate.internal.dom.W3cUtils.convertToNateDomElements;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.nate.encoder.NateDocument;
import org.nate.encoder.NateElement;
import org.nate.exception.BadCssExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import se.fishtank.css.selectors.NodeSelectorException;
import se.fishtank.css.selectors.dom.DOMNodeSelector;

public abstract class NateDomNode {

	// The top element is the top of the tree... either a fake wrapping element or an internal element found by a selector.
	private final Element topElement;
	
	private boolean validState = true;

	NateDomNode(Element topElement) {
		this.topElement = topElement;
	}

	/**
	 * Find references to nodes matching the selector such that modifications to these references will modify this
	 * NateDomNode.
	 */
	public List<NateElement> find(String selector) {
		verifyState();
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
		verifyState();
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

	public void setTextContent(String content) {
		verifyState();
		topElement.setTextContent(content);
	}

	public void setAttribute(String name, String value) {
		verifyState();
		topElement.setAttribute(name, value);
	}

	public void replaceChildren(NateDocument newChildren) {
		verifyState();
		removeChildren();
		Document ownerDocument = topElement.getOwnerDocument();
		for (Node newChild : newChildren.getRootNodes()) {
			topElement.appendChild(ownerDocument.importNode(newChild, true));
		}
	}

	protected void removeChildren() {
		for (Node child : asNodeList(topElement.getChildNodes())) {
			topElement.removeChild(child);
		}
	}

	protected void verifyState() {
		if(!validState)
			throw new IllegalStateException("Not in a valid state");
	}
	
	protected void invalidate() {
		validState = false;
	}

}
