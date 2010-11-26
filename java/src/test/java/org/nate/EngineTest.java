package org.nate;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertEquals;
import static org.nate.testutil.XmlFragmentAssert.assertXmlFragmentsEqual;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class EngineTest {
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowAnExceptionWhenDataIsNull() throws Exception {
		Engine engine = encodeHtmlFragment("<html/>");
		engine.inject(null);
	}

	
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowAnExceptionWhenDataIsNotAMap() throws Exception {
		Engine engine = encodeHtmlFragment("<html/>");
		engine.inject("");
	}

	
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowAnExceptionWhenKeyIsNotAString() throws Exception {
		Engine engine = encodeHtmlFragment("<html/>");
		engine.inject(singletonMap(42, "b"));
	}

	
	@Test
	public void shouldIgnoreNullValues() throws Exception {
		Engine engine = encodeHtmlFragment("<a/>");
		Engine result = engine.inject(singletonMap("a", null));
		assertXmlFragmentsEqual("<a/>", result.render());
	}

	@Test
	public void shouldMatchAndInjectSingleDataValue() throws Exception {
		Engine engine = encodeHtmlFragment("<div class='section'><span class='content'></span></div>");
		Engine result = engine.inject(singletonMap(".section", "Hello"));
		assertXmlFragmentsEqual("<div class='section'>Hello</div> ", result.render());
	}

	@Test
	public void shouldMatchAndInjectMultipleDataValues() throws Exception {
		Engine engine = encodeHtmlFragment("Before <div class='section'><span class='content'></span></div> After");
		Map<String, List<String>> data = singletonMap(".section", asList("Section 1", "Section 2"));
		Engine result = engine.inject(data);
		assertXmlFragmentsEqual("Before <div class='section'>Section 1</div><div class='section'>Section 2</div> After",
				result.render());
	}

	@Test
	public void shouldMatchAndInjectIntoASubselection() throws Exception {
		Engine engine = encodeHtmlFragment("<div class='section'><span class='greeting'></span></div>");
		Object data = singletonMap(".section", singletonMap(".greeting", "Hello"));
		Engine result = engine.inject(data);
		assertXmlFragmentsEqual("<div class='section'><span class='greeting'>Hello</span></div>", result.render());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldMatchAndInjectMultipleDataValuesIntoSubselection() throws Exception {
		Engine engine = encodeHtmlFragment(
				"<body>This is before <div class='section'><span class='greeting'></span></div> and after</body>");
		Object data = singletonMap(".section", asList(singletonMap(".greeting", "Hello"), singletonMap(".greeting", "Goodbye")));
		Engine result = engine.inject(data);
			assertXmlFragmentsEqual(
				"<body>This is before <div class='section'><span class='greeting'>Hello</span></div>" +
				"<div class='section'><span class='greeting'>Goodbye</span></div> and after</body>",
				result.render());
	}
	
	@Test
	public void shouldMatchAndInjectIntoElementAttribute() throws Exception {
		Engine engine = encodeHtmlFragment("<a>my link</a>");
		Object data = singletonMap("a", singletonMap("@@href", "http://www.example.com"));
		Engine result = engine.inject(data);
		assertXmlFragmentsEqual("<a href='http://www.example.com'>my link</a>", result.render());
	}
	
	@Test
	public void shouldAllowValuesToBeAnyObject() throws Exception {
		Engine engine = encodeHtmlFragment("<div/>");
		Object data = singletonMap("div", 42L);
		Engine result = engine.inject(data);
		assertXmlFragmentsEqual("<div>42</div>", result.render());
	}
	
	@Test
	public void shouldAllowHtmlWithDoctype() throws Exception {
		Engine engine = encodeHtmlDocument(
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
				"<html><body><div/></body></html>");
		Object data = singletonMap("div", "hello");
		Engine result = engine.inject(data);
		assertXmlFragmentsEqual("<html><body><div>hello</div></body></html>", result.render());
	}
	
	@Test
	public void shouldBeAbleToExtractClippings() throws Exception {
		Engine engine = encodeHtmlFragment("<div id='header'>Header</div><div id='content'><h1>Content</h1></div>");
		Engine header = engine.select("#header");
		assertXmlFragmentsEqual("<div id='header'>Header</div>", header.render());
	}
	
	@Test
	public void shouldNotBeModifiedByASelect() throws Exception {
		String original = "<div id='header'>Header</div><div id='content'><h1>Content</h1></div>";
		Engine engine = encodeHtmlFragment(original);
		engine.select("#header");
		assertXmlFragmentsEqual(original, engine.render());
	}
	
	@Test
	public void shouldBeAbleToSelectAllContent() throws Exception {
		Engine engine = encodeHtmlFragment("<div id='header'>header text</div><div id='content'>content text</div><div id='footer'><h1>footer</h1></div>");
		Engine header = engine.select("##div");
		assertXmlFragmentsEqual("header textcontent text<h1>footer</h1>", header.render());
	}
	
	@Test
	public void shouldNotBeModifiedByASelectContent() throws Exception {
		String original = "<div id='header'>header text</div><div id='content'>content text</div><div id='footer'><h1>footer</h1></div>";
		Engine engine = encodeHtmlFragment(original);
		engine.select("content:div");
		assertXmlFragmentsEqual(original, engine.render());
	}
	
	@Test
	public void shouldReturnSelectionsWhereNodesMatchThatAreInAncestralRelationship() throws Exception {
		String original = "<div id='outer'><div id='inner'>hello</div></div>";
		Engine engine = encodeHtmlFragment(original);
		String selection = engine.select("div").render();
		assertXmlFragmentsEqual("<div id='outer'><div id='inner'>hello</div></div><div id='inner'>hello</div>", selection);
		assertXmlFragmentsEqual(original, engine.render());
	}

	@Test
	public void shouldBeAbleToInjectNateEnginesIntoNateEngines() throws Exception {
		Engine engine1 = encodeHtmlFragment("<div id='header'>Header</div><div id='content'></div>");
		Engine engine2 = encodeHtmlFragment("<h1>Hello</h1>");
		Engine result = engine1.inject(singletonMap("#content", engine2));
		assertXmlFragmentsEqual("<div id='header'>Header</div><div id='content'><h1>Hello</h1></div>", result.render());
	}
	
	@Test
	public void shouldBeAbleToInjectASeletionFromOneDocumentToAnother() throws Exception {
		Engine source = encodeHtmlFragment("<div> <p>one</p> and <p>two</p> </div>");
		Engine selection = source.select("p");
		Engine destination = encodeHtmlFragment("<div id='header'>Header</div><div id='content'></div>");
		Engine result = destination.inject(singletonMap("#content", selection));
		assertXmlFragmentsEqual("<div id='header'>Header</div><div id='content'><p>one</p><p>two</p></div>", result.render());
	}
	
	@Test
	public void shouldNotModifyAnInjectedEngine() throws Exception {
		Engine source = encodeHtmlFragment("<div><p>hello</p></div>");
		Engine selection = source.select("p");
		Engine destination = encodeHtmlFragment("<div class='content'/>");
		Engine result = destination.inject(singletonMap(".content", selection));
		assertXmlFragmentsEqual("<div class='content'><p>hello</p></div>", result.render());
		assertXmlFragmentsEqual("<p>hello</p>", selection.render());
	}
	
	@Test
	public void shouldBeAbleToInjectASelectionMultipleTimes() throws Exception {
		Engine source = encodeHtmlFragment("<div><p>hello</p></div>");
		Engine selection = source.select("p");
		Engine destination = encodeHtmlFragment("<div class='content'/><div class='content'/>");
		Engine result = destination.inject(singletonMap(".content", selection));
		assertXmlFragmentsEqual("<div class='content'><p>hello</p></div><div class='content'><p>hello</p></div>", result.render());
	}
	
	@Test
	public void shouldCopeWithNameSpaces() throws Exception {
		Engine engine = encodeHtmlFragment("<html xmls='http://www.w3.org/1999/xhtml'><body><div id='header'>header</div></body></html>");
		Engine result = engine.inject(singletonMap("body", "hi"));
		assertXMLEqual("<html xmls='http://www.w3.org/1999/xhtml'><body>hi</body></html>", result.render());
	}
	
	@Test
	public void shouldReplaceContentWhenSubselectIsTheSpecialContentPseudoSelector() throws Exception {
		Engine engine = encodeHtmlFragment("<a href='#'>my link</a>");
		Map<String, Object> anchorData = new HashMap<String, Object>();
		anchorData.put("@@href", "http://www.example.com");
		anchorData.put(Engine.CONTENT_ATTRIBUTE, "example.com");
		Engine result = engine.inject(singletonMap("a", anchorData));
		assertXMLEqual("<a href='http://www.example.com'>example.com</a>", result.render());
	}
	
	@Test
	public void shouldRecursivelyApplyTransformsWhenSubselectIsTheSpecialContentPseudoSelector() throws Exception {
		Engine engine = encodeHtmlFragment("<div class=''><span/></div>");
		Map<String, Object> anchorData = new HashMap<String, Object>();
		anchorData.put("@@class", "show");
		anchorData.put(Engine.CONTENT_ATTRIBUTE, singletonMap("span", "hello"));
		Engine result = engine.inject(singletonMap("div", anchorData));
		assertXMLEqual("<div class='show'><span>hello</span></div>", result.render());
	}
	
	@Test
	public void shouldUseNullEngineForUnknownEncodings() throws Exception {
		String source = "banana";
		Engine engine = Nate.newWith(source, Nate.encoders().encoderFor("unknown"));
		Engine newEngine = engine
			.inject(singletonMap(Engine.CONTENT_ATTRIBUTE, "hello"))
			.select("blah");
		assertEquals(source, newEngine.render());
	}
	
	private Engine encodeHtmlFragment(String html) {
		return Nate.newWith(html, Nate.encoders().encoderFor("HTMLF"));
	}
	
	private Engine encodeHtmlDocument(String html) {
		return Nate.newWith(html, Nate.encoders().encoderFor("HTML"));
	}

}
