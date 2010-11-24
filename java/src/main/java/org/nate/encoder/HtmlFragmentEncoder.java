package org.nate.encoder;

import java.io.InputStream;

import org.nate.Encoder;
import org.nate.internal.XmlBasedNateDomDocumentFactory;

public class HtmlFragmentEncoder implements Encoder {
	
	private static final XmlBasedNateDomDocumentFactory DOCUMENT_FACTORY = new XmlBasedNateDomDocumentFactory();

	@Override
	public boolean isNullEncoder() {
		return false;
	}

	@Override
	public String type() {
		return "HTMLF";
	}

	@Override
	public NateDocument encode(InputStream source) {
		return DOCUMENT_FACTORY.createFromXmlDocumentFragment(source);
	}

}
