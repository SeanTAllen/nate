package org.nate.encoder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.nate.Encoder;
import org.nate.exception.NateException;
import org.nate.internal.dom.XmlBasedNateDomDocumentFactory;

public class XmlFragmentEncoder implements Encoder {
	
	private static final XmlBasedNateDomDocumentFactory DOCUMENT_FACTORY = new XmlBasedNateDomDocumentFactory();

	@Override
	public String type() {
		return "XMLF";
	}

	@Override
	public NateDocument encode(InputStream source) {
		return DOCUMENT_FACTORY.createFromXmlDocumentFragment(source);
	}

	@Override
	public NateDocument encode(String source) {
		try {
			return DOCUMENT_FACTORY.createFromXmlDocumentFragment(new ByteArrayInputStream(source.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			throw new NateException(e);
		}
	}

}
