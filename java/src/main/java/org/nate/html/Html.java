package org.nate.html;

import java.util.List;

public interface Html {

	List<Html> selectNodes(String selector);
	
	List<Html> selectContentOfNodes(String selector);

	void setTextContent(String value);

	void replaceWith(List<Html> newFragments);

	Html cloneFragment();

	String toHtml();

	boolean hasAttribute(String name);

	void setAttribute(String name, Object value);

	void replaceChildren(Html template);

}