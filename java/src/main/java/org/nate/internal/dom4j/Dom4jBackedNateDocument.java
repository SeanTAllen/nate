package org.nate.internal.dom4j;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Node;
import org.nate.encoder.NateDocument;
import org.nate.encoder.NateNode;


public class Dom4jBackedNateDocument extends Dom4jBackedAbstactDocument implements NateDocument {

	private Document document;

	public Dom4jBackedNateDocument(Document document) {
		super(document);
		this.document = document;
	}

	@Override
	public NateDocument copy() {
		return new Dom4jBackedNateDocument((Document) document.clone());
	}

	@Override
	public void removeAttribute(String attributeName) {
		throw new IllegalStateException("Internal error. Called with :" + attributeName);
	}

	@Override
	public String render() {
		return document.asXML();
	}

	@Override
	public void replaceChildren(NateDocument newChildren) {
		throw new IllegalStateException("Internal error.");
	}

	@Override
	public void replaceWith(List<NateNode> newNodes) {
		throw new IllegalStateException("Internal error.");
	}

	@Override
	public void setAttribute(String name, String value) {
		throw new IllegalStateException("Internal error.");
	}

	@Override
	public void setTextContent(String text) {
		throw new IllegalStateException("Internal error.");
	}

	public Collection<? extends Node> getDom4jNodes() {
		return Collections.singletonList(document);
	}

}
