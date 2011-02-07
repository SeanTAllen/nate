package org.nate.internal.dom4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.dom4j.Branch;
import org.dom4j.Element;
import org.dom4j.Node;
import org.nate.encoder.NateDocument;
import org.nate.exception.BadCssExpressionException;
import org.nate.internal.dom4j.cssselectors.Dom4jNodeSelector;

import se.fishtank.css.selectors.NodeSelectorException;

public abstract class Dom4jBackedAbstactDocument extends Dom4jBackedAbstractNode implements NateDocument {
	
	private final Branch root;
	
	public Dom4jBackedAbstactDocument(Branch root) {
		this.root = root;
	}
	
	@Override
	public NateDocument copy(String selector) {
		List<Element> newElements = new ArrayList<Element>();
		for (Element element : findMatchingElements(selector)) {
			newElements.add((Element) element.clone());
		}
		return new Dom4jBackedDocumentFragment(newElements);
	}
	@SuppressWarnings("unchecked")
	@Override
	public NateDocument copyContentOf(String selector) {
		List<Node> nodes = new ArrayList<Node>();
		for (Element element : findMatchingElements(selector)) {
			for (Node node : (List<Node>) element.content()) {
				nodes.add((Node) node.clone());
			}
		}
		return new Dom4jBackedDocumentFragment(nodes);
	}
	
	@SuppressWarnings("unchecked")
	protected Set<Element> findMatchingElements(String selector) {
		try {
			Set result = new Dom4jNodeSelector(root).querySelectorAll(selector);
			return result;
		} catch (NodeSelectorException e) {
			throw new BadCssExpressionException("Invalid CSS Expression: " + selector, e);
		}
	}

}
