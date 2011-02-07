package org.nate.encoder;

import java.util.List;

public interface NateNode {

	void setAttribute(String name, String value);

	void removeAttribute(String attributeName);

	List<NateNode> find(String selector);

	NateNode copy();

	void replaceWith(List<NateNode> newNodes);

	void replaceChildren(NateDocument newChildren);

	void setTextContent(String text);

	String render();
}
