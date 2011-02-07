package org.nate.internal.jsoup;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.xml.sax.SAXException;


public class JsoupBackedNateDocumentFactoryTest {

	@Test
	public void shouldCreateNateDomDocumentFromHtml() throws Exception {
		String source = "<html><head></head><body><div>hello</div></body></html>";
		InputStream input = inputStreamFor(source);
		JsoupBackedNateDocument doc = new JsoupBackedNateDocumentFactory().createFromHtmlDocument(input);
		assertHtmlEqual(source, doc.render());
	}

	@Test
	public void shouldCreateNateDomDocumentFragmentFromHtmlFragment() throws Exception {
		String source = "<div>hello</div>";
		InputStream input = inputStreamFor(source);
		JsoupBackedNateDocumentFragment doc = new JsoupBackedNateDocumentFactory().createFromHtmlFragment(input);
		assertHtmlEqual(source, doc.render());
	}
	
	
	private void assertHtmlEqual(String expected, String actual) throws SAXException, IOException {
		assertXMLEqual("Unexpected html: " + actual, expected.replaceAll("\\s", ""), actual.replaceAll("\\s", ""));
	}

	private InputStream inputStreamFor(String string) {
		return new ByteArrayInputStream(string.getBytes());
	}

}
