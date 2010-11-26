package org.nate.internal.selector;

import org.nate.encoder.NateElement;
import org.nate.encoder.NateNode;
import org.nate.internal.transformer.NateTransformer;

public class CssSelector implements NateSelector {

	private final String selectorString;

	public CssSelector(String selectorString) {
		this.selectorString = selectorString;
	}

	@Override
	public void transformSelectedNodes(NateTransformer transformer, NateNode node) {
		for (NateElement matchingNode : node.find(selectorString)) {
			transformer.transform(matchingNode);
		}
	}

}
