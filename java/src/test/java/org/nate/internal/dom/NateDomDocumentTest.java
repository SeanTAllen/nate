package org.nate.internal.dom;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.nate.testutil.WhiteSpaceIgnoringXmlMatcher.matchesXmlIgnoringWhiteSpace;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.junit.Test;
import org.nate.encoder.NateDocument;
import org.nate.encoder.NateNode;
import org.nate.exception.BadCssExpressionException;
import org.w3c.dom.Element;


public class NateDomDocumentTest {

	@Test
	public void shouldFindDesiredElements() throws Exception {
		NateNode document = createDocument("<div><p>apple</p> hello <p>banana</p></div>");
		List<NateNode> elements = document.find("p");
		assertThat("Unexpected size for: " + elements, elements.size(), is(2));
		assertThat(elements.get(0).render(), matchesXmlIgnoringWhiteSpace("<p>apple</p>"));
		assertThat(elements.get(1).render(), matchesXmlIgnoringWhiteSpace("<p>banana</p>"));
	}

	@Test
	public void shouldFindAnEmptyListWhenNothingMatches() throws Exception {
		NateNode document = createDocument("<div></div>");
		List<NateNode> elements = document.find("p");
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
		String original = "<body><div>a</div>x<div>b</div></body>";
		NateDocument document = createDocument(original);
		NateNode copy = document.copy("div");
		assertThat(copy.render(), matchesXmlIgnoringWhiteSpace("<div>a</div><div>b</div>"));
	}

	@Test
	public void shouldCopyDesiredElementsDeeply() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		NateDocument document = createDocument(original);
		NateNode copy = document.copy("div");
		assertThat(copy.render(), matchesXmlIgnoringWhiteSpace("<div>a<div>b</div></div><div>b</div><div>c</div>"));
	}
	
	@Test
	public void shouldLeaveOriginalUnchangedAfterCopy() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		NateDocument document = createDocument(original);
		document.copy("div");
		assertThat(document.render(), matchesXmlIgnoringWhiteSpace(original));		
	}

	@Test
	public void shouldCopyNothingWhenNothingMatches() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		NateDocument document = createDocument(original);
		NateNode copy = document.copy("div.foo");
		assertThat(copy.render(), matchesXmlIgnoringWhiteSpace(""));
	}

	@Test
	public void shouldCopyContentOfDesiredElements() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		NateDocument document = createDocument(original);
		NateNode copy = document.copyContentOf("div");
		assertThat(copy.render(), matchesXmlIgnoringWhiteSpace("a<div>b</div>bc"));
	}
	
	@Test
	public void shouldLeaveOriginalUnchangedAfterCopyingContent() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		NateDocument document = createDocument(original);
		document.copyContentOf("div");
		assertThat(document.render(), matchesXmlIgnoringWhiteSpace(original));	
	}
	
	@Test
	public void shouldCopyContentOfNothingWhenNothingMatches() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		NateDocument document = createDocument(original);
		NateNode copy = document.copyContentOf("div.foo");
		assertThat(copy.render(), matchesXmlIgnoringWhiteSpace(""));
	}
	
	@Test
	public void shouldSetTextContentWithSuppliedValue() throws Exception {
		NateDocument document = createDocument("<div><p>apple</p> hello <p>banana</p></div>");
		document.setTextContent("a & b");
		assertThat(document.render(), matchesXmlIgnoringWhiteSpace("a &amp; b"));
	}
	
	@Test
	public void shouldReturnCopyWhenRequested() throws Exception {
		NateNode original = createDocument("<div>apple</div>");
		NateNode copy = original.copy();
		assertThat(copy.render(), matchesXmlIgnoringWhiteSpace("<div>apple</div>"));
		copy.setTextContent("banana");
		assertThat(copy.render(), matchesXmlIgnoringWhiteSpace("banana"));
		assertThat(original.render(), matchesXmlIgnoringWhiteSpace("<div>apple</div>"));
	}
	
	@Test
	public void shouldReplaceChildren() throws Exception {
		NateDocument document = createDocument("<div><p>apple</p></div>");
		NateDocument newChildren = createDocumentFragment("a <b>banana</b> or two");
		document.replaceChildren(newChildren);
		assertThat(document.render(), matchesXmlIgnoringWhiteSpace("a <b>banana</b> or two"));
		assertThat(newChildren.render(), matchesXmlIgnoringWhiteSpace("a <b>banana</b> or two"));
	}
	
	@Test
	public void shouldReplaceChildrenWhenNoneToStartWith() throws Exception {
		NateDocument document = createDocument("<div/>");
		NateDocument newChildren = createDocumentFragment("a <b>banana</b> or two");
		document.replaceChildren(newChildren);
		assertThat(document.render(), matchesXmlIgnoringWhiteSpace("a <b>banana</b> or two"));
		assertThat(newChildren.render(), matchesXmlIgnoringWhiteSpace("a <b>banana</b> or two"));
	}

	@Test
	public void shouldReplaceSelfWithNewNodes() throws Exception {
		NateNode document = createDocument("<div>apple</div>");
		NateNode node1 = createDocumentFragment("a <b>banana</b> or two");
		NateNode node2 = elementFor("<p>apple</p>");
		document.replaceWith(asList(node1, node2));
		assertThat(document.render(), matchesXmlIgnoringWhiteSpace("a <b>banana</b> or two<p>apple</p>"));
		assertThat(node1.render(), matchesXmlIgnoringWhiteSpace("a <b>banana</b> or two"));
		assertThat(node2.render(), matchesXmlIgnoringWhiteSpace("<p>apple</p>"));
	}

	private NateDocument createDocument(String input) {
		return new XmlBasedNateDomDocumentFactory()
			.createFromXmlDocument(new ByteArrayInputStream(input.getBytes()));
	}

	private NateDocument createDocumentFragment(String input) {
		return new XmlBasedNateDomDocumentFactory()
				.createFromXmlDocumentFragment(new ByteArrayInputStream(input.getBytes()));
	}
	
	private NateNode elementFor(String string) {
		NateDocument document = createDocument(string);
		return new NateDomElement((Element) ((AbstactNateDomNode) document).getRootNodes().get(0));
	}

}
