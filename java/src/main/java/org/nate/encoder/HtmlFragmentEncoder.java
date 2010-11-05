package org.nate.encoder;

import org.nate.Encoder;
import org.nate.html.Html;
import org.nate.html.XmlParserBackedHtml;

public class HtmlFragmentEncoder implements Encoder {

	@Override
	public Html encode(String source) {
		return XmlParserBackedHtml.fromFragment(source);
	}

	@Override
	public boolean isNullEncoder() {
		return false;
	}

	@Override
	public String type() {
		return "HTMLF";
	}

}
