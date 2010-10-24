/**
 * 
 */
package org.nate.encoder;

import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.nate.TransformResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class HtmlTransformResult implements TransformResult {
	private final Document document;

	HtmlTransformResult(Document document) {
		this.document = document;
	}

	@Override
	public String toHtml() {
		Writer stringWriter = new StringWriter();
		Result result = new StreamResult(stringWriter);
		// Need to unwap from the fakeroot node that was added in HtmlEncoder.encode(). Must be a better way!!!
		NodeList childNodes = document.getChildNodes().item(0).getChildNodes();
		int length = childNodes.getLength();
		for (int i = 0; i < length; i++) {
			convertNodeToString(childNodes.item(i), result);
		}
	    return stringWriter.toString();
	}

	private void convertNodeToString(Node node, Result result) {
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
}