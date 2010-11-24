package org.nate.encoder;

import java.io.InputStream;

import org.nate.Encoder;
import org.nate.internal.XmlBasedNateDomDocumentFactory;

public class HtmlEncoder implements Encoder {

	private static final XmlBasedNateDomDocumentFactory DOCUMENT_FACTORY = new XmlBasedNateDomDocumentFactory();
	private static final String TYPE = "HTML";

	public boolean isNullEncoder() {
		return false;
	}

	public String type() {
		return TYPE;
	}

	@Override
	public NateDocument encode(InputStream source) {
		return DOCUMENT_FACTORY.createFromXmlDocument(source);
	}

}