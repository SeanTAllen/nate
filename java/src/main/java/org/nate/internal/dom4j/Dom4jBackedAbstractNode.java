package org.nate.internal.dom4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;
import org.dom4j.Node;
import org.nate.encoder.NateNode;

public abstract class Dom4jBackedAbstractNode  implements NateNode {

	private boolean validState = true;

	@Override
	public List<NateNode> find(String selector) {
		verifyState();
		List<NateNode> nateNodes = new ArrayList<NateNode>();
		for (Element element : findMatchingElements(selector)) {
			nateNodes.add(new Dom4jBackedNateElement(element));
		}
		return nateNodes;
	}
	
	protected abstract Set<Element> findMatchingElements(String selector);
	
	protected abstract Collection<? extends Node> getDom4jNodes();

	protected void verifyState() {
		if(!validState)
			throw new IllegalStateException("Not in a valid state");
	}
	
	protected void invalidate() {
		validState = false;
	}

}
