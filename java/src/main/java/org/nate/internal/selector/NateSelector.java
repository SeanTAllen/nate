package org.nate.internal.selector;

import org.nate.encoder.NateNode;
import org.nate.internal.transformer.NateTransformer;

public interface NateSelector {

	void transformSelectedNodes(NateTransformer transformer, NateNode node);

}
