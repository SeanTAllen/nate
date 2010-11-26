package org.nate.internal.selector;

import org.nate.encoder.NateNode;
import org.nate.internal.transformer.NateTransformer;

public class SelfSelector implements NateSelector {

	@Override
	public void transformSelectedNodes(NateTransformer transformer, NateNode node) {
		transformer.transform(node);
	}

}
