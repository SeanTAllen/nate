package org.nate.internal.dom;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.nate.testutil.XmlFragmentAssert.assertXmlFragmentsEqual;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.junit.Test;
import org.nate.encoder.NateDocument;
import org.nate.encoder.NateElement;
import org.nate.encoder.NateNode;
import org.nate.exception.BadCssExpressionException;
import org.nate.internal.dom.NateDomElement;
import org.nate.internal.dom.XmlBasedNateDomDocumentFactory;
import org.w3c.dom.Element;


public class NateDomDocumentTest {

	@Test
	public void shouldFindDesiredElements() throws Exception {
		NateNode document = createDocument("<div><p>apple</p> hello <p>banana</p></div>");
		List<NateElement> elements = document.find("p");
		assertThat("Unexpected size for: " + elements, elements.size(), is(2));
		assertXmlFragmentsEqual("<p>apple</p>", elements.get(0).render());
		assertXmlFragmentsEqual("<p>banana</p>", elements.get(1).render());
	}

	@Test
	public void shouldFindAnEmptyListWhenNothingMatches() throws Exception {
		NateNode document = createDocument("<div></div>");
		List<NateElement> elements = document.find("p");
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
		assertXmlFragmentsEqual("<div>a</div><div>b</div>", copy.render());
	}

	@Test
	public void shouldCopyDesiredElementsDeeply() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		NateDocument document = createDocument(original);
		NateNode copy = document.copy("div");
		assertXmlFragmentsEqual("<div>a<div>b</div></div><div>b</div><div>c</div>", copy.render());
	}
	
	@Test
	public void shouldLeaveOriginalUnchangedAfterCopy() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		NateDocument document = createDocument(original);
		document.copy("div");
		assertXmlFragmentsEqual(original, document.render());		
	}

	@Test
	public void shouldCopyNothingWhenNothingMatches() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		NateDocument document = createDocument(original);
		NateNode copy = document.copy("div.foo");
		assertXmlFragmentsEqual("", copy.render());
	}

	@Test
	public void shouldCopyContentOfDesiredElements() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		NateDocument document = createDocument(original);
		NateNode copy = document.copyContentOf("div");
		assertXmlFragmentsEqual("a<div>b</div>bc", copy.render());
	}
	
	@Test
	public void shouldLeaveOriginalUnchangedAfterCopyingContent() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		NateDocument document = createDocument(original);
		document.copyContentOf("div");
		assertXmlFragmentsEqual(original, document.render());	
	}
	
	@Test
	public void shouldCopyContentOfNothingWhenNothingMatches() throws Exception {
		String original = "<body><div>a<div>b</div></div>x<div>c</div></body>";
		NateDocument document = createDocument(original);
		NateNode copy = document.copyContentOf("div.foo");
		assertXmlFragmentsEqual("", copy.render());
	}
	
	@Test
	public void shouldSetTextContentWithSuppliedValue() throws Exception {
		NateDocument document = createDocument("<div><p>apple</p> hello <p>banana</p></div>");
		document.setTextContent("a & b");
		assertXmlFragmentsEqual("a &amp; b", document.render());
	}
	
	@Test
	public void shouldReturnCopyWhenRequested() throws Exception {
		NateNode original = createDocument("<div>apple</div>");
		NateNode copy = original.copy();
		assertXmlFragmentsEqual("<div>apple</div>", copy.render());
		copy.setTextContent("banana");
		assertXmlFragmentsEqual("banana", copy.render());
		assertXmlFragmentsEqual("<div>apple</div>", original.render());
	}
	
	@Test
	public void shouldReplaceChildren() throws Exception {
		NateDocument document = createDocument("<div><p>apple</p></div>");
		NateDocument newChildren = createDocumentFragment("a <b>banana</b> or two");
		document.replaceChildren(newChildren);
		assertXmlFragmentsEqual("a <b>banana</b> or two", document.render());
		assertXmlFragmentsEqual("a <b>banana</b> or two", newChildren.render());
	}
	
	@Test
	public void shouldReplaceChildrenWhenNoneToStartWith() throws Exception {
		NateDocument document = createDocument("<div/>");
		NateDocument newChildren = createDocumentFragment("a <b>banana</b> or two");
		document.replaceChildren(newChildren);
		assertXmlFragmentsEqual("a <b>banana</b> or two", document.render());
		assertXmlFragmentsEqual("a <b>banana</b> or two", newChildren.render());
	}

	@Test
	public void shouldReplaceSelfWithNewNodes() throws Exception {
		NateNode document = createDocument("<div>apple</div>");
		NateNode node1 = createDocumentFragment("a <b>banana</b> or two");
		NateNode node2 = elementFor("<p>apple</p>");
		document.replaceWith(asList(node1, node2));
		assertXmlFragmentsEqual("a <b>banana</b> or two<p>apple</p>", document.render());
		assertXmlFragmentsEqual("a <b>banana</b> or two", node1.render());
		assertXmlFragmentsEqual("<p>apple</p>", node2.render());
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
		return new NateDomElement((Element) ((NateDomNode) document).getRootNodes().get(0));
	}

}
