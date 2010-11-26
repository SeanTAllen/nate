package org.nate.internal.dom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;

import org.nate.encoder.NateElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class W3cUtils {

	private W3cUtils() {
	}

	public static List<Node> asNodeList(NodeList nodes) {
		int length = nodes.getLength();
		List<Node> result = new ArrayList<Node>(length);
		for(int i = 0; i < length; i++) {
			result.add(nodes.item(i));
		}
		return result;
	}


	public static void convertNodeToString(Node node, Result result) {
		try {
			Source source = new DOMSource((Node) node);
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty("method", "html");
			xformer.setOutputProperty("omit-xml-declaration", "yes");
			xformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new RuntimeException(e);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}
	
	static List<NateElement> convertToNateDomElements(Collection<Element> elements) {
		List<NateElement> result = new ArrayList<NateElement>(elements.size());
		for (Element element : elements) {
			result.add(new NateDomElement(element));
		}
		return result;
	}


}
