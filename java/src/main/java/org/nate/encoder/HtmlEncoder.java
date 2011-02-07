package org.nate.encoder;

import java.io.InputStream;

import org.nate.Encoder;
import org.nate.internal.jsoup.JsoupBackedNateDocumentFactory;

public class HtmlEncoder implements Encoder {

	private static final JsoupBackedNateDocumentFactory DOCUMENT_FACTORY = new JsoupBackedNateDocumentFactory();
	private static final String TYPE = "HTML";

	public String type() {
		return TYPE;
	}

	@Override
	public NateDocument encode(InputStream source) {
		return DOCUMENT_FACTORY.createFromHtmlDocument(source);
	}

	@Override
	public NateDocument encode(String source) {
		return DOCUMENT_FACTORY.createFromHtmlDocument(source);
	}

}