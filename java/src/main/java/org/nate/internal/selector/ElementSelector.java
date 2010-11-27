package org.nate.internal.selector;

import org.nate.encoder.NateNode;
import org.nate.internal.transformer.NateTransformer;

public class ElementSelector implements NateSelector {

	private final String selectorString;

	public ElementSelector(String selectorString) {
		this.selectorString = selectorString;
	}

	@Override
	public void transformSelectedNodes(NateTransformer transformer, NateNode node) {
		for (NateNode matchingNode : node.find(selectorString)) {
			transformer.transform(matchingNode);
		}
	}

}
