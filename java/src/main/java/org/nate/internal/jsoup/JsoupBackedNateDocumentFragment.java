package org.nate.internal.jsoup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.select.Selector.SelectorParseException;
import org.nate.encoder.NateDocument;
import org.nate.encoder.NateNode;
import org.nate.exception.BadCssExpressionException;

// Aaaarrrgggg... what a horrible inheritance hierarchy I have created!
public class JsoupBackedNateDocumentFragment extends JsoupBackedAbstactDocument implements NateDocument {

	private Element pseudoRoot;

	public JsoupBackedNateDocumentFragment(List<? extends Node> nodes) {
		this(wrapInPseudoRoot(new ArrayList<Node>(nodes)));
	}

	public JsoupBackedNateDocumentFragment(Element pseudoRoot) {
		this.pseudoRoot = pseudoRoot;
	}

	@Override
	public NateDocument copy() {
		return new JsoupBackedNateDocumentFragment(pseudoRoot.clone());
	}

	@Override
	protected List<Element> findMatchingElements(String selector) {
		try {
			List<Element> elements = new ArrayList<Element>();
			for (Node node : getJsoupNodes()) {
				if (node instanceof Element) {
					elements.addAll(((Element) node).select(selector));
				}
			}
			return elements;
		} catch (SelectorParseException e) {
			throw new BadCssExpressionException(e);
		}
	}
	
	@Override
	public void removeAttribute(String attributeName) {
		throw new IllegalStateException("Internal error.");
	}

	@Override
	public String render() {
		StringBuilder result = new StringBuilder();
		for (Node node : getJsoupNodes()) {
			result.append(node.outerHtml());
		}
		return result.toString();
	}

	@Override
	public void replaceChildren(NateDocument newChildrenSource) {
		if (!(newChildrenSource instanceof JsoupBackedNateDocumentFragment)) {
			throw new IllegalStateException(
					"Internal Error.  Expected JsoupBackedNateDocumentFragment, but got: " + newChildrenSource);
		}
		Collection<Node> newChildren = ((JsoupBackedAbstractNode)newChildrenSource).getJsoupNodes();
		
		pseudoRoot = createPseudoRootElement();
		for (Node newNode : newChildren) {
			pseudoRoot.appendChild(newNode.clone());
		}
	}

	@Override
	public void replaceWith(List<NateNode> newNateNodes) {
		this.pseudoRoot = createPseudoRootElement();
		for (NateNode newNode : newNateNodes) {
			Collection<Node> jsoupNodes = ((JsoupBackedAbstractNode)newNode.copy()).getJsoupNodes();
			for (Node jsoupNode : jsoupNodes) {
				pseudoRoot.appendChild(jsoupNode.clone());
			}
		}
	}

	@Override
	public void setAttribute(String name, String value) {
		throw new IllegalStateException("Internal error.");
	}

	@Override
	public void setTextContent(String text) {
		this.pseudoRoot = createPseudoRootElement();
		this.pseudoRoot.appendChild(new TextNode(text, JsoupBackedNateDocumentFactory.BASE_URI));
	}

	@Override
	Collection<Node> getJsoupNodes() {
		return this.pseudoRoot.childNodes();
	}

	private static Element wrapInPseudoRoot(List<? extends Node> nodes) {
		Element result = createPseudoRootElement();
		for (Node node : nodes) {
			result.appendChild(node);
		}
		return result;
	}

	private static Element createPseudoRootElement() {
		return new Element(Tag.valueOf("section"), JsoupBackedNateDocumentFactory.BASE_URI);
	}

}
