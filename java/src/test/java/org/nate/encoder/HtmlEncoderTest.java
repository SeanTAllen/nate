package org.nate.encoder;

import static java.util.Collections.singletonMap;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import org.junit.Test;
import org.nate.TransformResult;
import org.w3c.dom.Document;

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

}
