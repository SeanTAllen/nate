package org.nate.internal.dom4j.cssselectors;

import java.util.List;

import org.dom4j.Branch;
import org.dom4j.Element;
import org.w3c.dom.Node;
/**
 * Simple port of Christer Sandberg's CSS selectors to Dom4j (https://github.com/chrsan/css-selectors)
 */
public class DOMHelper {

	public static Element getPreviousSiblingElement(Branch node) {
		if (node.getNodeType() == Node.DOCUMENT_NODE) {
			return null;
		}
		Element element = (Element) node;
		List<Element> elements = element.getParent().elements();
		int indexOfPreviousSibling = elements.indexOf(element) - 1;
		if (indexOfPreviousSibling < 0) {
			return null;
		}
		return elements.get(indexOfPreviousSibling);
	}

	public static Branch getNextSiblingElement(Branch node) {
		if (node.getNodeType() == Node.DOCUMENT_NODE) {
			return null;
		}
		Element element = (Element) node;
		Element parent = element.getParent();
		if (parent == null) {
			return null;
		}
		List<Element> elements = parent.elements();
		int indexOfNextSibling = elements.indexOf(element) + 1;
		if (indexOfNextSibling >= elements.size()) {
			return null;
		}
		return elements.get(indexOfNextSibling);
	}

}
