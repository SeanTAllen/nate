package org.nate.internal.writer;

import static org.nate.internal.W3cUtils.convertNodeToString;

import java.io.StringWriter;

import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;
public class NateDomDocumentWriter implements NateDomNodeWriter{

	private final Node node;

	public NateDomDocumentWriter(Node node) {
		this.node = node;
	}

	@Override
	public String render() {
		StringWriter result = new StringWriter();
		convertNodeToString(node, new StreamResult(result));
	    return result.toString();
	}

}
