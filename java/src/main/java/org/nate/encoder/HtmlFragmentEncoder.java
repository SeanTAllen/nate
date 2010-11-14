package org.nate.encoder;

import java.io.InputStream;

import org.nate.Encoder;
import org.nate.html.Html;
import org.nate.html.XmlParserBackedHtml;

public class HtmlFragmentEncoder implements Encoder {

	@Override
	public boolean isNullEncoder() {
		return false;
	}

	@Override
	public String type() {
		return "HTMLF";
	}

	@Override
	public Html encode(InputStream source) {
		return XmlParserBackedHtml.fromFragment(source);
	}

}
