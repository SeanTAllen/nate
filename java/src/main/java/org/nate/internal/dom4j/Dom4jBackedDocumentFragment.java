package org.nate.internal.dom4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.nate.encoder.NateDocument;
import org.nate.encoder.NateNode;

//Aaaarrrgggg... what a horrible inheritance hierarchy I have created!
public class Dom4jBackedDocumentFragment extends Dom4jBackedAbstactDocument implements NateDocument {

	private static final DocumentFactory DOCUMENT_FACTORY = new DocumentFactory();

	private Element pseudoRoot;

	public Dom4jBackedDocumentFragment(List<? extends Node> rootNodes) {
		this(wrapInPseudoRoot(new ArrayList<Node>(rootNodes)));
	}

	private Dom4jBackedDocumentFragment(Element pseudoRoot) {
		super(pseudoRoot);
		this.pseudoRoot = pseudoRoot;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Collection<? extends Node> getDom4jNodes() {
		return pseudoRoot.content();
	}

	@Override
	public NateDocument copy() {
		return new Dom4jBackedDocumentFragment(pseudoRoot.createCopy());
	}

	@Override
	public void removeAttribute(String attributeName) {
		throw new IllegalStateException("Internal error.");
	}

	@Override
	public String render() {
		StringBuilder result = new StringBuilder();
		for (Node node : getDom4jNodes()) {
			result.append(node.asXML());
		}
		return result.toString();
	}

	@Override
	public void replaceChildren(NateDocument newChildrenSource) {
		if (!(newChildrenSource instanceof Dom4jBackedDocumentFragment)) {
			throw new IllegalStateException(
					"Internal Error.  Expected Dom4jBackedDocumentFragment, but got: " + newChildrenSource);
		}
		Collection<? extends Node> newChildren = ((Dom4jBackedDocumentFragment)newChildrenSource).getDom4jNodes();
		
		pseudoRoot = createPseudoRootElement();
		for (Node newNode : newChildren) {
			pseudoRoot.add((Node) newNode.clone());
		}
	}

	@Override
	public void replaceWith(List<NateNode> newNateNodes) {
		this.pseudoRoot = createPseudoRootElement();
		for (NateNode newNode : newNateNodes) {
			Collection<? extends Node> dom4jNodes = ((Dom4jBackedAbstractNode)newNode.copy()).getDom4jNodes();
			for (Node dom4jNode : dom4jNodes) {
				pseudoRoot.add((Node) dom4jNode.clone());
			}
		}
	}

	@Override
	public void setAttribute(String name, String value) {
		throw new IllegalStateException("Internal error.");
	}

	@Override
	public void setTextContent(String text) {
		pseudoRoot = createPseudoRootElement();
		pseudoRoot.setText(text);
	}

	private static Element wrapInPseudoRoot(List<? extends Node> nodes) {
		Element result = createPseudoRootElement();
		for (Node node : nodes) {
			node.detach();
			result.add(node);
		}
		return result;
	}

	private static Element createPseudoRootElement() {
		return DOCUMENT_FACTORY.createElement("PseudoRoot");
	}

}
