package org.nate.internal.jsoup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.nate.encoder.NateNode;

public abstract class JsoupBackedAbstractNode implements NateNode {

	private boolean validState = true;

	@Override
	public List<NateNode> find(String selector) {
		verifyState();
		List<NateNode> nateNodes = new ArrayList<NateNode>();
		for (Element element : findMatchingElements(selector)) {
			nateNodes.add(new JsoupBackedNateElement(element));
		}
		return nateNodes;
	}
	
	protected abstract List<Element> findMatchingElements(String selector);

	abstract Collection<Node> getJsoupNodes();
	
	protected void verifyState() {
		if(!validState)
			throw new IllegalStateException("Not in a valid state");
	}
	
	protected void invalidate() {
		validState = false;
	}

}