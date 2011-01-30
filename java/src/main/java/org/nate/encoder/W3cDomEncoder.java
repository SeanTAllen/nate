package org.nate.encoder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.nate.Encoder;
import org.nate.exception.NateException;
import org.nate.internal.dom.XmlBasedNateDomDocumentFactory;

public class W3cDomEncoder implements Encoder {

	private static final XmlBasedNateDomDocumentFactory DOCUMENT_FACTORY = new XmlBasedNateDomDocumentFactory();
	private static final String TYPE = "W3C";

	public String type() {
		return TYPE;
	}

	@Override
	public NateDocument encode(InputStream source) {
		return DOCUMENT_FACTORY.createFromXmlDocument(source);
	}

	@Override
	public NateDocument encode(String source) {
		try {
			return DOCUMENT_FACTORY.createFromXmlDocument(new ByteArrayInputStream(source.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			throw new NateException(e);
		}
	}

}