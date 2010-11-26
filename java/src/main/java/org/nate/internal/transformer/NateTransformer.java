package org.nate.internal.transformer;

import org.nate.encoder.NateNode;

public interface NateTransformer {

	void transform(NateNode node);

	void setAttribute(String attributeName, NateNode node);

}
