package org.nate.internal.jsoup;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.nate.encoder.NateDocument;

public abstract class JsoupBackedAbstactDocument
	extends JsoupBackedAbstractNode
	implements NateDocument{


	@Override
	public NateDocument copy(String selector) {
		List<Element> newElements = new ArrayList<Element>();
		for (Element element : findMatchingElements(selector)) {
			newElements.add(element.clone());
		}
		return new JsoupBackedNateDocumentFragment(newElements);
	}

	@Override
	public NateDocument copyContentOf(String selector) {
		List<Node> nodes = new ArrayList<Node>();
		for (Element element : findMatchingElements(selector)) {
			for (Node node : element.childNodes()) {
				nodes.add(node.clone());
			}
		}
		return new JsoupBackedNateDocumentFragment(nodes);
	}

}
