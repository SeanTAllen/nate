package org.nate.encoder;

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

	public Html encode(String source) {
		return XmlParserBackedHtml.fromDocument(source);
	}

}