package org.nate.encoder;

import java.io.InputStream;

import org.nate.Encoder;
import org.nate.internal.dom.XmlBasedNateDomDocumentFactory;

public class XmllEncoder implements Encoder {

	private static final XmlBasedNateDomDocumentFactory DOCUMENT_FACTORY = new XmlBasedNateDomDocumentFactory();
	private static final String TYPE = "HTML";

	public String type() {
		return TYPE;
	}

	@Override
	public NateDocument encode(InputStream source) {
		return DOCUMENT_FACTORY.createFromXmlDocument(source);
	}

}