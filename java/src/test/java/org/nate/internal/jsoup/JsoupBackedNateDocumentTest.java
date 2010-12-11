package org.nate.internal.jsoup;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.nate.testutil.XmlFragmentAssert.assertXmlFragmentsIgnoringWhiteSpaceEqual;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.junit.Test;
import org.nate.encoder.NateNode;
import org.nate.exception.BadCssExpressionException;


public class JsoupBackedNateDocumentTest {

	@Test
	public void shouldFindDesiredElements() throws Exception {
		JsoupBackedNateDocument document = createDocument("<div><p>apple</p> hello <p>banana</p></div>");
		List<NateNode> elements = document.find("p");
		assertThat("Unexpected size for: " + elements, elements.size(), is(2));
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<p>apple</p>", elements.get(0).render());
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<p>banana</p>", elements.get(1).render());
	}

	@Test
	public void shouldFindAnEmptyListWhenNothingMatches() throws Exception {
		JsoupBackedNateDocument document = createDocument("<div><p>apple</p></div>");
		List<NateNode> elements = document.find("a");
		assertThat("Unexpected size for: " + elements, elements.size(), is(0));
	}

	@Test(expected=BadCssExpressionException.class)
	public void shouldThrowBadCssExressionExceptionWhenInvalidCssIsSupplied() throws Exception {
			createDocument("<div/>").find("p&#*$");
	}

	@Test
	public void shouldCopyDesiredElements() throws Exception {
		String original = "<body><div>a</div>x<div>b</div></body>";
		JsoupBackedNateDocument document = createDocument(original);
		NateNode copy = document.copy("div");
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<div> a </div> <div> b </div>", copy.render());
	}

	@Test
	public void shouldCopyDesiredElementsDeeply() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		JsoupBackedNateDocument document = createDocument(original);
		NateNode copy = document.copy("div");
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<div> a <div> b </div> </div> <div> b </div> <div> c </div>", copy.render());
	}

	@Test
	public void shouldLeaveOriginalUnchangedAfterCopy() throws Exception {
		String original = "<html> <head/> <body> <div> a <div> b </div> </div> x <div> c </div> </body> </html>";
		JsoupBackedNateDocument document = createDocument(original);
		document.copy("div");
		assertXmlFragmentsIgnoringWhiteSpaceEqual(original, document.render());		
	}

	@Test
	public void shouldCopyNothingWhenNothingMatches() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		JsoupBackedNateDocument document = createDocument(original);
		NateNode copy = document.copy("div.foo");
		assertXmlFragmentsIgnoringWhiteSpaceEqual("", copy.render());
	}

	@Test
	public void shouldCopyContentOfDesiredElements() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		JsoupBackedNateDocument document = createDocument(original);
		NateNode copy = document.copyContentOf("div");
		// TODO: Should really end in bc
		assertXmlFragmentsIgnoringWhiteSpaceEqual("a <div> b </div>bc", copy.render());
	}
	
	@Test
	public void shouldLeaveOriginalUnchangedAfterCopyingContent() throws Exception {
		String original = "<html> <head/> <body> <div> a <div> b </div> </div> x <div> c </div> </body> </html>";
		JsoupBackedNateDocument document = createDocument(original);
		document.copyContentOf("div");
		assertXmlFragmentsIgnoringWhiteSpaceEqual(original, document.render());	
	}
	
	@Test
	public void shouldCopyContentOfNothingWhenNothingMatches() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		JsoupBackedNateDocument document = createDocument(original);
		NateNode copy = document.copyContentOf("div.foo");
		assertXmlFragmentsIgnoringWhiteSpaceEqual("", copy.render());
	}
	
	@Test
	public void shouldReturnCopyWhenRequested() throws Exception {
		String source = "<html> <head/> <body> <div> apple </div> </body> </html>";
		JsoupBackedNateDocument original = createDocument(source);
		NateNode copy = original.copy();
		assertXmlFragmentsIgnoringWhiteSpaceEqual(source, copy.render());
		NateNode div = copy.find("div").get(0);
		div.setAttribute("class", "foo");
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<div class='foo'> apple </div>", div.render());
		assertXmlFragmentsIgnoringWhiteSpaceEqual(source, original.render());
	}


	private JsoupBackedNateDocument createDocument(String input) {
		return new JsoupBackedNateDocumentFactory()
				.createFromHtmlDocument(new ByteArrayInputStream(input.getBytes()));
	}

}
