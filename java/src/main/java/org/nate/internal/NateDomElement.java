package org.nate.internal;

import org.nate.internal.writer.NateDomDocumentWriter;
import org.nate.internal.writer.NateDomNodeWriter;
import org.w3c.dom.Element;
import static java.util.Collections.*;

class NateDomElement extends NateDomNode {

	private final NateDomNodeWriter nodeWriter;

	NateDomElement(Element node) {
		super(node, singletonList(node));
		nodeWriter = new NateDomDocumentWriter(node);
	}

	@Override
	public String render() {
		return nodeWriter.render();
	}

}
