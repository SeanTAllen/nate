package org.nate.encoder;

import static java.util.Collections.singletonMap;
import static java.util.Arrays.asList;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.nate.TransformResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class HtmlEncoderTest {

	private HtmlEncoder htmlEncoder = new HtmlEncoder();

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowAnExceptionWhenDataIsNull() throws Exception {
		Document document = (Document) htmlEncoder.encode("<html/>");
		htmlEncoder.transformWith(document, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowAnExceptionWhenDataIsNotAMap() throws Exception {
		Document document = (Document) htmlEncoder.encode("<html/>");
		htmlEncoder.transformWith(document, "");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowAnExceptionWhenKeyIsNotAString() throws Exception {
		Document document = (Document) htmlEncoder.encode("<html/>");
		htmlEncoder.transformWith(document, singletonMap(42, "b"));
	}
	
	@Test
	public void shouldIgnoreNullValues() throws Exception {
		Document document = (Document) htmlEncoder.encode("<html/>");
		TransformResult transformResult = htmlEncoder.transformWith(document, singletonMap("a", null));
		assertXMLEqual("<html/>", transformResult.toHtml());
	}
	
	@Test
	public void shouldMatchAndInjectSingleDataValue() throws Exception {
		Document document = (Document) htmlEncoder.encode("<div class='section'><span class='content'></span></div>");
		TransformResult transformResult = htmlEncoder.transformWith(document, singletonMap(".section", "Hello"));
		assertXMLEqual("<div class='section'>Hello</div> ", transformResult.toHtml());
	}

	@Test
	public void shouldMatchAndInjectMultipleDataValues() throws Exception {
		Document document = (Document) htmlEncoder.encode("<div class='section'><span class='content'></span></div>");
		Map<String, List<String>> data = singletonMap(".section", asList("Section 1", "Section 2"));
		TransformResult transformResult = htmlEncoder.transformWith(document, data);
		assertXmlFragmentsEqual("<div class='section'>Section 1</div><div class='section'>Section 2</div>", transformResult.toHtml());
	}

	private void assertXmlFragmentsEqual(String expected, String actual) throws SAXException, IOException {
		// Wrap in fake roots in case the xml has multiple roots, otherwise you get a parser exception
		assertXMLEqual(wrapInFakeRoot(expected), wrapInFakeRoot(actual));
	}

	private String wrapInFakeRoot(String fragment) {
		return "<fake>" + fragment + "</fake>";
	}
}
