package org.nate.internal.jsoup;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector.SelectorParseException;
import org.nate.encoder.NateDocument;
import org.nate.encoder.NateNode;
import org.nate.exception.BadCssExpressionException;

public class JsoupBackedNateDocument extends JsoupBackedAbstactDocument implements NateDocument {

	private final Document document;

	public JsoupBackedNateDocument(Document document) {
		this.document = document;
	}

	@Override
	public NateDocument copy() {
		return new JsoupBackedNateDocument(document.clone());
	}

	protected Elements findMatchingElements(String selector) {
		try {
			return document.select(selector);
		} catch (SelectorParseException e) {
			throw new BadCssExpressionException(e);
		}
	}

	@Override
	public void removeAttribute(String attributeName) {
		throw new IllegalStateException("Internal error. Called with :" + attributeName);
	}

	@Override
	public String render() {
		return document.toString();
	}

	@Override
	public void replaceChildren(NateDocument newChildren) {
		throw new IllegalStateException("Internal error.");
	}

	@Override
	public void replaceWith(List<NateNode> newNodes) {
		throw new IllegalStateException("Internal error.");
	}

	@Override
	public void setAttribute(String name, String value) {
		throw new IllegalStateException("Internal error.");
	}

	@Override
	public void setTextContent(String text) {
		throw new IllegalStateException("Internal error.");
	}

	@Override
	public Collection<Node> getJsoupNodes() {
		return Collections.singletonList((Node)document);
	}

}
