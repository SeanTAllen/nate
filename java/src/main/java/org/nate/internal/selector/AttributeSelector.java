package org.nate.internal.selector;

import org.nate.encoder.NateNode;
import org.nate.internal.transformer.NateTransformer;

public class AttributeSelector implements NateSelector {

	private final String attributeName;

	public AttributeSelector(String attributeName) {
		this.attributeName = attributeName;
	}

	@Override
	public void transformSelectedNodes(NateTransformer transformer, NateNode node) {
		transformer.setAttribute(attributeName, node);
	}

}
