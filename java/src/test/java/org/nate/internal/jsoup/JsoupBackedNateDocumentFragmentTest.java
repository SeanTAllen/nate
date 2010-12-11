package org.nate.internal.jsoup;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.nate.testutil.XmlFragmentAssert.assertXmlFragmentsIgnoringWhiteSpaceEqual;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.junit.Test;
import org.nate.encoder.NateDocument;
import org.nate.encoder.NateNode;
import org.nate.exception.BadCssExpressionException;


public class JsoupBackedNateDocumentFragmentTest {

	@Test
	public void shouldFindDesiredElements() throws Exception {
		NateDocument document = createDocumentFragment("<div><p>apple</p> hello <p>banana</p></div>");
		List<NateNode> elements = document.find("p");
		assertThat("Unexpected size for: " + elements, elements.size(), is(2));
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<p>apple</p>", elements.get(0).render());
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<p>banana</p>", elements.get(1).render());
	}

	@Test
	public void shouldFindAnEmptyListWhenNothingMatches() throws Exception {
		NateDocument document = createDocumentFragment("<div><p>apple</p></div>");
		List<NateNode> elements = document.find("a");
		assertThat("Unexpected size for: " + elements, elements.size(), is(0));
	}

	@Test(expected=BadCssExpressionException.class)
	public void shouldThrowBadCssExressionExceptionWhenInvalidCssIsSupplied() throws Exception {
			createDocumentFragment("<div/>").find("p&#*$");
	}

	@Test
	public void shouldCopyDesiredElements() throws Exception {
		String original = "<section><div>a</div>x<div>b</div></section>";
		NateDocument document = createDocumentFragment(original);
		NateNode copy = document.copy("div");
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<div> a </div> <div> b </div>", copy.render());
	}

	@Test
	public void shouldCopyDesiredElementsDeeply() throws Exception {
		String original = "<section><div>a<div>b</div></div>x<div>c</div></section>";
		NateDocument document = createDocumentFragment(original);
		NateNode copy = document.copy("div");
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<div> a <div> b </div> </div> <div> b </div> <div> c </div>", copy.render());
	}

	@Test
	public void shouldLeaveOriginalUnchangedAfterCopy() throws Exception {
		String original = "<div> a <div> b </div> </div> x <div> c </div>";
		NateDocument document = createDocumentFragment(original);
		document.copy("div");
		assertXmlFragmentsIgnoringWhiteSpaceEqual(original, document.render());		
	}

	@Test
	public void shouldCopyNothingWhenNothingMatches() throws Exception {
		String original = "<section><div>a<div>b</div></div>x<div>c</div></section>";
		NateDocument document = createDocumentFragment(original);
		NateNode copy = document.copy("div.foo");
		assertXmlFragmentsIgnoringWhiteSpaceEqual("", copy.render());
	}

	@Test
	public void shouldCopyContentOfDesiredElements() throws Exception {
		String original = "<section><div>a<div>b</div></div>x<div>c</div></section>";
		NateDocument document = createDocumentFragment(original);
		NateNode copy = document.copyContentOf("div");
		assertXmlFragmentsIgnoringWhiteSpaceEqual("a <div> b </div>bc", copy.render());
	}
	
	@Test
	public void shouldLeaveOriginalUnchangedAfterCopyingContent() throws Exception {
		String original = "<section> <div> a <div> b </div> </div> x <div> c </div> </section>";
		NateDocument document = createDocumentFragment(original);
		document.copyContentOf("div");
		assertXmlFragmentsIgnoringWhiteSpaceEqual(original, document.render());	
	}
	
	@Test
	public void shouldCopyContentOfNothingWhenNothingMatches() throws Exception {
		String original = "<section><div>a<div>b</div></div>x<div>c</div></section>";
		NateDocument document = createDocumentFragment(original);
		NateNode copy = document.copyContentOf("div.foo");
		assertXmlFragmentsIgnoringWhiteSpaceEqual("", copy.render());
	}
	
	@Test
	public void shouldReturnCopyWhenRequested() throws Exception {
		String source = "<section> <div> apple </div> </section>";
		NateDocument original = createDocumentFragment(source);
		NateNode copy = original.copy();
		assertXmlFragmentsIgnoringWhiteSpaceEqual(source, copy.render());
		NateNode div = copy.find("div").get(0);
		div.setAttribute("class", "foo");
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<div class='foo'> apple </div>", div.render());
		assertXmlFragmentsIgnoringWhiteSpaceEqual(source, original.render());
	}
	
	@Test
	public void shouldSetTextContentWithSuppliedValue() throws Exception {
		NateDocument document = createDocumentFragment("<div><p>apple</p> hello <p>banana</p></div>");
		document.setTextContent("a & b");
		assertXmlFragmentsIgnoringWhiteSpaceEqual("a &amp; b", document.render());
	}
	
	@Test
	public void shouldReplaceChildren() throws Exception {
		NateDocument document = createDocumentFragment("<div><p>apple</p></div>");
		NateDocument newChildren = createDocumentFragment("a<b>banana</b> or two");
		document.replaceChildren(newChildren);
		assertXmlFragmentsIgnoringWhiteSpaceEqual("a <b>banana</b> or two", document.render());
		assertXmlFragmentsIgnoringWhiteSpaceEqual("a <b>banana</b> or two", newChildren.render());
	}
	
	@Test
	public void shouldReplaceChildrenWhenNoneToStartWith() throws Exception {
		NateDocument document = createDocumentFragment("<div/>");
		NateDocument newChildren = createDocumentFragment("a <b>banana</b> or two");
		document.replaceChildren(newChildren);
		assertXmlFragmentsIgnoringWhiteSpaceEqual("a <b>banana</b> or two", document.render());
		assertXmlFragmentsIgnoringWhiteSpaceEqual("a <b>banana</b> or two", newChildren.render());
	}
	@Test
	public void shouldReplaceSelfWithNewNodes() throws Exception {
		NateNode document = createDocumentFragment("<div>apple</div>");
		NateNode node1 = createDocumentFragment("a <b>banana</b> or two");
		NateNode node2 = elementFor("<p>apple</p>");
		document.replaceWith(asList(node1, node2));
		assertXmlFragmentsIgnoringWhiteSpaceEqual("a <b>banana</b> or two <p>apple</p>", document.render());
		assertXmlFragmentsIgnoringWhiteSpaceEqual("a <b>banana</b> or two", node1.render());
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<p>apple</p>", node2.render());
	}


	private NateDocument createDocumentFragment(String input) {
		 JsoupBackedNateDocument fullDocument = new JsoupBackedNateDocumentFactory()
				.createFromHtmlDocument(new ByteArrayInputStream(input.getBytes()));
		 return fullDocument.copyContentOf("body");		 
	}
	
	private JsoupBackedNateElement elementFor(String input) {
		NateDocument document = createDocumentFragment("<wrapper>" + input + "</wraper>");
		return (JsoupBackedNateElement) document.find("wrapper > *").get(0);
	}

}
