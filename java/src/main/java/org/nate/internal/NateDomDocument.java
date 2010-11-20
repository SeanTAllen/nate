package org.nate.internal;



import static java.util.Collections.singletonList;

import java.util.List;

import org.nate.internal.writer.NateDomDocumentFragmentWriter;
import org.nate.internal.writer.NateDomDocumentWriter;
import org.nate.internal.writer.NateDomNodeWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import static org.nate.internal.W3cUtils.*;

class NateDomDocument extends NateDomNode {

	private final NateDomNodeWriter nodeWriter;
	// TODO: May want to inject this...
	private final XmlBasedNateDomDocumentFactory domDocumentFactory = new XmlBasedNateDomDocumentFactory();

	private NateDomDocument(Node node, List<? extends Node> rootNodes, NateDomNodeWriter nodeWriter) {
		super(node, rootNodes);
		this.nodeWriter = nodeWriter;
	}

	static NateDomDocument fromDocument(Document document) {
		return new NateDomDocument(document, singletonList(document.getDocumentElement()), new NateDomDocumentWriter(document));
	}

	static NateDomDocument fromFakeRootWrapedFragment(Element fakeRoot) {
		return new NateDomDocument(fakeRoot, asNodeList(fakeRoot.getChildNodes()), new NateDomDocumentFragmentWriter(fakeRoot));
	}

	@Override
	public String render() {
		return nodeWriter.render();
	}

	/**
	 * Create copies such that modifications will not modify this NateDomDocument.
	 */
	public NateDomDocument copy(String selector) {
		return domDocumentFactory.createFromNateDomNodes(find(selector));
	}
	
}
