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

final class HtmlTransformResult implements TransformResult {
	private final Document document;

	HtmlTransformResult(Document document) {
		this.document = document;
	}

	@Override
	public String toHtml() {
	    try {
			Source source = new DOMSource((Node) document);
			Writer stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
			return stringWriter.toString();
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new RuntimeException(e);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}
}