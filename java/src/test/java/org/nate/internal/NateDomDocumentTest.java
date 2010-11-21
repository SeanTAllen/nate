package org.nate.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;
import static org.nate.testutil.XmlFragmentAssert.assertXmlFragmentsEqual;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.junit.Test;
import org.nate.exception.BadCssExpressionException;


public class NateDomDocumentTest {

	@Test
	public void shouldFindDesiredElements() throws Exception {
		NateDomDocument document = createDocument("<div><p>apple</p> hello <p>banana</p></div>");
		List<NateDomElement> elements = document.find("p");
		assertThat("Unexpected size for: " + elements, elements.size(), is(2));
		assertXmlFragmentsEqual("<p>apple</p>", elements.get(0).render());
		assertXmlFragmentsEqual("<p>banana</p>", elements.get(1).render());
	}

	@Test
	public void shouldFindAnEmptyListWhenNothingMatches() throws Exception {
		NateDomDocument document = createDocument("<div></div>");
		List<NateDomElement> elements = document.find("p");
		assertThat("Unexpected size for: " + elements, elements.size(), is(0));
	}
	
	@Test
	public void shouldThrowBadCssExressionExceptionWhenInvalidCssIsSupplied() throws Exception {
		try {
			createDocument("<div/>").find("p&#*$");
			fail("Should have thrown an exception.");
		} catch (BadCssExpressionException e) {
			assertThat(e.getMessage(), is("Invalid CSS Expression: p&#*$"));
		}
	}

	@Test
	public void shouldCopyDesiredElements() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		NateDomDocument document = createDocument(original);
		NateDomDocument copy = document.copy("div");
		assertXmlFragmentsEqual("<div>a<div>b</div></div><div>b</div><div>c</div>", copy.render());
		assertXmlFragmentsEqual(original, document.render());
	}

	@Test
	public void shouldCopyNothingWhenNothingMatches() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		NateDomDocument document = createDocument(original);
		NateDomDocument copy = document.copy("div.foo");
		assertXmlFragmentsEqual("", copy.render());
		assertXmlFragmentsEqual(original, document.render());
	}

	@Test
	public void shouldCopyContentOfDesiredElements() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		NateDomDocument document = createDocument(original);
		NateDomDocument copy = document.copyContentOf("div");
		assertXmlFragmentsEqual("a<div>b</div>bc", copy.render());
		assertXmlFragmentsEqual(original, document.render());		
	}

	@Test
	public void shouldCopyContentOfNothingWhenNothingMatches() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		NateDomDocument document = createDocument(original);
		NateDomDocument copy = document.copyContentOf("div.foo");
		assertXmlFragmentsEqual("", copy.render());
		assertXmlFragmentsEqual(original, document.render());		
	}

	private NateDomDocument createDocument(String input) {
		return new XmlBasedNateDomDocumentFactory()
			.createFromXmlDocument(new ByteArrayInputStream(input.getBytes()));
	}

}
