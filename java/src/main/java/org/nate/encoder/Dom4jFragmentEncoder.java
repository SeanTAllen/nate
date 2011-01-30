package org.nate.encoder;

import java.io.InputStream;

import org.nate.Encoder;
import org.nate.internal.dom4j.Dom4jBackedNateDocumentFactory;

public class Dom4jFragmentEncoder implements Encoder {
	private static final Dom4jBackedNateDocumentFactory DOCUMENT_FACTORY = new Dom4jBackedNateDocumentFactory();
	private static final String TYPE = "XMLF";

	public String type() {
		return TYPE;
	}

	@Override
	public NateDocument encode(InputStream source) {
		return DOCUMENT_FACTORY.createFromXmlDocumentFragment(source);
	}

	@Override
	public NateDocument encode(String source) {
		return DOCUMENT_FACTORY.createFromXmlDocumentFragment(source);
	}
}
