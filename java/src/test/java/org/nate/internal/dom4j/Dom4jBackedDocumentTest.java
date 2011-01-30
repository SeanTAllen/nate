package org.nate.internal.dom4j;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.nate.testutil.WhiteSpaceIgnoringXmlMatcher.matchesXmlIgnoringWhiteSpace;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.junit.Test;
import org.nate.encoder.NateNode;
import org.nate.exception.BadCssExpressionException;


public class Dom4jBackedDocumentTest {

	@Test
	public void shouldFindDesiredElements() throws Exception {
		Dom4jBackedNateDocument document = createDocument("<div><p>apple</p> hello <p>banana</p></div>");
		List<NateNode> elements = document.find("p");
		assertThat("Unexpected size for: " + elements, elements.size(), is(2));
		assertThat(elements.get(0).render(), matchesXmlIgnoringWhiteSpace("<p>apple</p>"));
		assertThat(elements.get(1).render(), matchesXmlIgnoringWhiteSpace("<p>banana</p>"));
	}

	@Test
	public void shouldFindAnEmptyListWhenNothingMatches() throws Exception {
		Dom4jBackedNateDocument document = createDocument("<div><p>apple</p></div>");
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
		Dom4jBackedNateDocument document = createDocument(original);
		NateNode copy = document.copy("div");
		assertThat(copy.render(), matchesXmlIgnoringWhiteSpace("<div> a </div> <div> b </div>"));
	}

	@Test
	public void shouldCopyDesiredElementsDeeply() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		Dom4jBackedNateDocument document = createDocument(original);
		NateNode copy = document.copy("div");
		assertThat(copy.render(), matchesXmlIgnoringWhiteSpace("<div> a <div> b </div> </div> <div> b </div> <div> c </div>"));
	}

	@Test
	public void shouldLeaveOriginalUnchangedAfterCopy() throws Exception {
		String original = "<html> <head/> <body> <div> a <div> b </div> </div> x <div> c </div> </body> </html>";
		Dom4jBackedNateDocument document = createDocument(original);
		document.copy("div");
		assertThat(document.render(), matchesXmlIgnoringWhiteSpace(original));		
	}

	@Test
	public void shouldCopyNothingWhenNothingMatches() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		Dom4jBackedNateDocument document = createDocument(original);
		NateNode copy = document.copy("div.foo");
		assertThat(copy.render(), matchesXmlIgnoringWhiteSpace(""));
	}

	@Test
	public void shouldCopyContentOfDesiredElements() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		Dom4jBackedNateDocument document = createDocument(original);
		NateNode copy = document.copyContentOf("div");
		// TODO: Should really end in bc
		assertThat(copy.render(), matchesXmlIgnoringWhiteSpace("a <div> b </div>bc"));
	}
	
	@Test
	public void shouldLeaveOriginalUnchangedAfterCopyingContent() throws Exception {
		String original = "<html><head/><body><div>a<div>b</div></div>x<div>c</div></body></html>";
		Dom4jBackedNateDocument document = createDocument(original);
		document.copyContentOf("div");
		assertThat(document.render(), matchesXmlIgnoringWhiteSpace(original));	
	}
	
	@Test
	public void shouldCopyContentOfNothingWhenNothingMatches() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		Dom4jBackedNateDocument document = createDocument(original);
		NateNode copy = document.copyContentOf("div.foo");
		assertThat(copy.render(), matchesXmlIgnoringWhiteSpace(""));
	}
	
	@Test
	public void shouldReturnCopyWhenRequested() throws Exception {
		String source = "<html><head/><body><div>apple</div></body></html>";
		Dom4jBackedNateDocument original = createDocument(source);
		NateNode copy = original.copy();
		assertThat(copy.render(), matchesXmlIgnoringWhiteSpace(source));
		NateNode div = copy.find("div").get(0);
		div.setAttribute("class", "foo");
		assertThat(div.render(), matchesXmlIgnoringWhiteSpace("<div class='foo'>apple</div>"));
		assertThat(original.render(), matchesXmlIgnoringWhiteSpace(source));
	}

	private Dom4jBackedNateDocument createDocument(String input) {
		return new Dom4jBackedNateDocumentFactory()
			.createFromXmlDocument(new ByteArrayInputStream(input.getBytes()));
	}


}
