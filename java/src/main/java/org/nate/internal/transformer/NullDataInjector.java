package org.nate.internal.transformer;

import org.nate.encoder.NateNode;

public class NullDataInjector implements NateTransformer {

	@Override
	public void setAttribute(String attributeName, NateNode node) {
		node.removeAttribute(attributeName);
	}

	@Override
	public void transform(NateNode node) {
	}

}
