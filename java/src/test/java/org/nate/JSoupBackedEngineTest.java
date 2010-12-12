package org.nate;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.nate.testutil.XmlFragmentAssert.assertXmlFragmentsEqual;
import static org.nate.testutil.XmlFragmentAssert.assertXmlFragmentsIgnoringWhiteSpaceEqual;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.nate.exception.EncoderNotAvailableException;

// TODO: Remove the duplication between this and EngineTest.
// Will require a better assertXmlFragmentsIgnoringWhiteSpaceEqual.
public class JSoupBackedEngineTest {
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowAnExceptionWhenKeyIsNotAString() throws Exception {
		Engine engine = encodeHtmlFragment("<html/>");
		engine.inject(singletonMap(42, "b"));
	}
	
	@Test
	public void shouldIgnoreNullValuesForTextInjection() throws Exception {
		Engine engine = encodeHtmlFragment("<a/>");
		Engine result = engine.inject(singletonMap("a", null));
		assertXmlFragmentsEqual("<a/>", result.render());
	}

	@Test
	public void shouldMatchAndInjectSingleDataValue() throws Exception {
		Engine engine = encodeHtmlFragment("<span class='section'><span class='content'></span></span>");
		Engine result = engine.inject(singletonMap(".section", "Hello"));
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<span class='section'>Hello</span> ", result.render());
	}

	@Test
	public void shouldMatchAndInjectMultipleDataValues() throws Exception {
		Engine engine = encodeHtmlFragment("Before <div class='section'><span class='content'></span></div> After");
		Map<String, List<String>> data = singletonMap(".section", asList("Section 1", "Section 2"));
		Engine result = engine.inject(data);
		assertXmlFragmentsIgnoringWhiteSpaceEqual("Before <div class='section'> Section 1 </div> <div class='section'> Section 2 </div> After",
				result.render());
	}

	@Test
	public void shouldMatchAndInjectIntoASubselection() throws Exception {
		Engine engine = encodeHtmlFragment("<span class='section'><span class='greeting'></span></span>");
		Object data = singletonMap(".section", singletonMap(".greeting", "Hello"));
		Engine result = engine.inject(data);
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<span class='section'><span class='greeting'>Hello</span></span>", result.render());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldMatchAndInjectMultipleDataValuesIntoSubselection() throws Exception {
		Engine engine = encodeHtmlFragment(
				"<section>This is before <div class='section'><span class='greeting'></span></div> and after</section>");
		Object data = singletonMap(".section", asList(singletonMap(".greeting", "Hello"), singletonMap(".greeting", "Goodbye")));
		Engine result = engine.inject(data);
		assertXmlFragmentsIgnoringWhiteSpaceEqual(
				"<section> This is before <div class='section'> <span class='greeting'>Hello</span> </div>" +
				" <div class='section'> <span class='greeting'>Goodbye</span> </div> and after </section>",
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
	public void shouldMatchAndInjectIntoElementAttributeUsingCombinationOfCssAndAttributeSelection() throws Exception {
		Engine engine = encodeHtmlFragment("<a>my link</a>");
		Object data = singletonMap("a @@href", "http://www.example.com");
		Engine result = engine.inject(data);
		assertXmlFragmentsEqual("<a href='http://www.example.com'>my link</a>", result.render());
	}
	
	@Test
	public void shouldRemoveAttributes() throws Exception {
		Engine engine = encodeHtmlFragment("<a href='xxx'>my link</a>");
		Object data = singletonMap("a @@href", null);
		Engine result = engine.inject(data);
		assertXmlFragmentsEqual("<a>my link</a>", result.render());
	}
	
	@Test
	public void shouldIgnoreAttemptsToRemoveNonexistentAttributes() throws Exception {
		Engine engine = encodeHtmlFragment("<a>my link</a>");
		Object data = singletonMap("a @@href", null);
		Engine result = engine.inject(data);
		assertXmlFragmentsEqual("<a>my link</a>", result.render());
	}

	@Test
	public void shouldAllowValuesToBeAnyObject() throws Exception {
		Engine engine = encodeHtmlFragment("<span/>");
		Object data = singletonMap("span", 42L);
		Engine result = engine.inject(data);
		assertXmlFragmentsEqual("<span>42</span>", result.render());
	}
	
	@Test
	public void shouldAllowHtmlWithDoctype() throws Exception {
		String original = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
			"<html> <body> <div/> </body> </html>";
		Engine engine = encodeHtmlDocument(original);
		Object data = singletonMap("div", "hello");
		Engine result = engine.inject(data);
		// XmlUnit has problems with the DOCTYPE
		assertXmlFragmentsIgnoringWhiteSpaceEqual(
				"<html> <head/> <body> <div> hello </div> </body> </html>",
				result.render().replaceAll("<!DOCTYPE[^>]*>", ""));
	}
	
	@Test
	public void shouldBeAbleToExtractClippings() throws Exception {
		Engine engine = encodeHtmlFragment("<span id='header'>Header</span><div id='content'><h1>Content</h1></div>");
		Engine header = engine.select("#header");
		assertXmlFragmentsEqual("<span id='header'>Header</span>", header.render());
	}
	
	@Test
	public void shouldNotBeModifiedByASelect() throws Exception {
		String original = "<div id='header'> Header </div> <div id='content'> <h1>Content</h1> </div>";
		Engine engine = encodeHtmlFragment(original);
		engine.select("#header");
		assertXmlFragmentsIgnoringWhiteSpaceEqual(original, engine.render());
	}
	
	@Test
	public void shouldBeAbleToSelectAllContent() throws Exception {
		Engine engine = encodeHtmlFragment("<div id='header'>header text</div><div id='content'>content text</div><div id='footer'><h1>footer</h1></div>");
		Engine header = engine.select("##div");
		assertXmlFragmentsIgnoringWhiteSpaceEqual("header textcontent text <h1>footer</h1>", header.render());
	}
	
	@Test
	public void shouldNotBeModifiedByASelectContent() throws Exception {
		String original = "<div id='header'> header text </div> <div id='content'> content text </div> <div id='footer'> <h1>footer</h1> </div>";
		Engine engine = encodeHtmlFragment(original);
		engine.select("##div");
		assertXmlFragmentsIgnoringWhiteSpaceEqual(original, engine.render());
	}
	
	@Test
	public void shouldReturnSelectionsWhereNodesMatchThatAreInAncestralRelationship() throws Exception {
		String original = "<div id='outer'> <div id='inner'> hello </div> </div>";
		Engine engine = encodeHtmlFragment(original);
		String selection = engine.select("div").render();
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<div id='outer'> <div id='inner'> hello </div> </div> <div id='inner'> hello </div>", selection);
		assertXmlFragmentsIgnoringWhiteSpaceEqual(original, engine.render());
	}

	@Test
	public void shouldBeAbleToInjectNateEnginesIntoNateEngines() throws Exception {
		Engine engine1 = encodeHtmlFragment("<div id='header'>Header</div><div id='content'></div>");
		Engine engine2 = encodeHtmlFragment("<h1>Hello</h1>");
		Engine result = engine1.inject(singletonMap("#content", engine2));
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<div id='header'> Header </div> <div id='content'> <h1>Hello</h1> </div>", result.render());
	}
	
	@Test
	public void shouldBeAbleToInjectASeletionFromOneDocumentToAnother() throws Exception {
		Engine source = encodeHtmlFragment("<div> <p>one</p> and <p>two</p> </div>");
		Engine selection = source.select("p");
		Engine destination = encodeHtmlFragment("<div id='header'>Header</div><div id='content'></div>");
		Engine result = destination.inject(singletonMap("#content", selection));
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<div id='header'> Header </div> <div id='content'> <p>one</p> <p>two</p> </div>", result.render());
	}
	
	@Test
	public void shouldNotModifyAnInjectedEngine() throws Exception {
		Engine source = encodeHtmlFragment("<div><p>hello</p></div>");
		Engine selection = source.select("p");
		Engine destination = encodeHtmlFragment("<div class='content'/>");
		Engine result = destination.inject(singletonMap(".content", selection));
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<div class='content'> <p>hello</p> </div>", result.render());
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<p>hello</p>", selection.render());
	}
	
	@Test
	public void shouldBeAbleToInjectASelectionMultipleTimes() throws Exception {
		Engine source = encodeHtmlFragment("<div><p>hello</p></div>");
		Engine selection = source.select("p");
		Engine destination = encodeHtmlFragment("<div class='content'/><div class='content'/>");
		Engine result = destination.inject(singletonMap(".content", selection));
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<div class='content'> <p>hello</p> </div> <div class='content'> <p>hello</p> </div>", result.render());
	}
	
	@Test
	public void shouldCopeWithNameSpaces() throws Exception {
		Engine engine = encodeHtmlDocument("<html xmls='http://www.w3.org/1999/xhtml'><body><div id='header'>header</div></body></html>");
		Engine result = engine.inject(singletonMap("body", "hi"));
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<html xmls='http://www.w3.org/1999/xhtml'> <head/> <body> hi </body> </html>", result.render());
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
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<div class='show'> <span>hello</span> </div>", result.render());
	}
	
	@Test(expected=EncoderNotAvailableException.class)
	public void shouldThrowExceptionForUnknownEncodings() throws Exception {
		Nate.newWith("banana", Nate.encoders().encoderFor("unknown"));
	}
	
	private Engine encodeHtmlFragment(String html) {
		return Nate.newWith(html, Nate.encoders().encoderFor("JSOUPF"));
	}
	
	private Engine encodeHtmlDocument(String html) {
		return Nate.newWith(html, Nate.encoders().encoderFor("JSOUP"));
	}

}
