package org.nate.encoder;

import java.util.List;

import org.w3c.dom.Node;

public interface NateNode {

	void setAttribute(String name, String value);

	List<NateElement> find(String selector);

	NateNode copy();

	void replaceWith(List<NateNode> newNodes);

	void replaceChildren(NateDocument newChildren);

	void setTextContent(String text);

	String render();

	// TODO: Figure out a way to remove this from the interface, preferably without having to use casts.
	List<Node> getRootNodes();

}
