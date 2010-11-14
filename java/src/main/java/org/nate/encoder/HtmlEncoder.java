package org.nate.encoder;

import java.io.InputStream;

import org.nate.Encoder;
import org.nate.html.Html;
import org.nate.html.XmlParserBackedHtml;

public class HtmlEncoder implements Encoder {

	private static final String TYPE = "HTML";

	public boolean isNullEncoder() {
		return false;
	}

	public String type() {
		return TYPE;
	}

	@Override
	public Html encode(InputStream source) {
		return XmlParserBackedHtml.fromDocument(source);
	}

}