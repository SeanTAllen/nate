package org.nate.encoder;

import java.io.InputStream;

import org.nate.Encoder;
import org.nate.internal.jsoup.JsoupBackedNateDocumentFactory;

public class HtmlFragmentEncoder implements Encoder {

	private static final JsoupBackedNateDocumentFactory DOCUMENT_FACTORY = new JsoupBackedNateDocumentFactory();
	private static final String TYPE = "JSOUPF";

	@Override
	public String type() {
		return TYPE;
	}

	@Override
	public NateDocument encode(InputStream source) {
		return DOCUMENT_FACTORY.createFromHtmlFragment(source);
	}
}
