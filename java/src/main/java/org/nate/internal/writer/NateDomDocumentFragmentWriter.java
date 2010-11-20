package org.nate.internal.writer;

import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import static org.nate.internal.W3cUtils.*;

public class NateDomDocumentFragmentWriter implements NateDomNodeWriter {

	private final Element fakeRoot;

	public NateDomDocumentFragmentWriter(Element fakeRoot) {
		this.fakeRoot = fakeRoot;
	}

	@Override
	public String render() {
		Writer stringWriter = new StringWriter();
		Result result = new StreamResult(stringWriter);
		for (Node childNode : asNodeList(fakeRoot.getChildNodes())) {
			convertNodeToString(childNode, result);
		}
		return stringWriter.toString();
	}

}
