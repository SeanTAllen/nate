package org.nate.internal.jsoup;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.nate.testutil.WhiteSpaceIgnoringXmlMatcher.matchesXmlIgnoringWhiteSpace;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.junit.Test;
import org.nate.encoder.NateDocument;
import org.nate.encoder.NateNode;


public class JsoupBackedNateElementTest {

	@Test
	public void shouldFindDesiredElements() throws Exception {
		NateNode element = elementFor("<div><p>apple</p> hello <p>banana</p></div>");
		List<NateNode> elements = element.find("p");
		assertThat("Unexpected size for: " + elements, elements.size(), is(2));
		assertThat(elements.get(0).render(), matchesXmlIgnoringWhiteSpace("<p>apple</p>"));
		assertThat(elements.get(1).render(), matchesXmlIgnoringWhiteSpace("<p>banana</p>"));
	}

	@Test
	public void shouldFindNoElementsWhenNoneMatch() throws Exception {
		NateNode element = elementFor("<div><p>apple</p> hello <p>banana</p></div>");
		List<NateNode> elements = element.find("p.foo");
		assertThat("Unexpected size for: " + elements, elements.size(), is(0));
	}
	
	@Test
	public void shouldSetTextContentWithSuppliedValue() throws Exception {
		NateNode element = elementFor("<span><p>apple</p></span>");
		element.setTextContent("a & b");
		assertThat(element.render(), matchesXmlIgnoringWhiteSpace("<span>a &amp; b</span>"));
	}
	
	@Test
	public void shouldSetAttributeWithSuppliedValue() throws Exception {
		NateNode element = elementFor("<span><p>apple</p></span>");
		element.setAttribute("foo", "a & \" b");
		assertThat(element.render(), matchesXmlIgnoringWhiteSpace("<span foo='a &amp; &quot; b'><p>apple</p></span>"));
	}
	
	@Test
	public void shouldReturnCopyWhenRequested() throws Exception {
		NateNode original = elementFor("<span>apple</span>");
		NateNode copy = original.copy();
		copy.setTextContent("banana");
		assertThat(copy.render(), matchesXmlIgnoringWhiteSpace("<span>banana</span>"));
		assertThat(original.render(), matchesXmlIgnoringWhiteSpace("<span>apple</span>"));
	}

	@Test
	public void shouldReplaceChildren() throws Exception {
		NateNode element = elementFor("<span><p>apple</p></span>");
		NateDocument newChildren = createDocumentFragment("a <b>banana</b> or two");
		element.replaceChildren(newChildren);
		assertThat(element.render(), matchesXmlIgnoringWhiteSpace("<span>a <b>banana</b> or two</span>"));
		assertThat(newChildren.render(), matchesXmlIgnoringWhiteSpace("a <b>banana</b> or two"));
	}
	
	@Test
	public void shouldReplaceSelfWithNewNodes() throws Exception {
		NateNode document = createDocumentFragment("<section><span>apple</span></section>");
		NateNode node1 = createDocumentFragment("a <b>banana</b> or two");
		NateNode node2 = elementFor("<p>apple</p>");
		NateNode element = document.find("span").get(0);
		element.replaceWith(asList(node1, node2));
		assertThat(document.render(), matchesXmlIgnoringWhiteSpace("<section> a <b>banana</b> or two <p>apple</p> </section>"));
		assertThat(node1.render(), matchesXmlIgnoringWhiteSpace("a <b>banana</b> or two"));
		assertThat(node2.render(), matchesXmlIgnoringWhiteSpace("<p>apple</p>"));
	}
	
	@Test
	public void shouldLeaveSiblingsUntouchedWhenReplacingSelf() throws Exception {
		NateNode document = createDocumentFragment("<section><span>apple</span><p>orange</p></section>");
		NateNode element = document.find("span").get(0);
		NateNode sibling = document.find("p").get(0);
		element.replaceWith(asList((NateNode)createDocumentFragment("banana")));
		sibling.setAttribute("id", "7");
		assertThat(document.find("p").get(0).render(), matchesXmlIgnoringWhiteSpace("<p id='7'>orange</p>"));
	}
	
	@Test
	public void shouldReplaceSelfWithNewNodesWhenNoParent() throws Exception {
		NateNode document = createDocumentFragment("<span>apple</span>");
		NateNode node1 = createDocumentFragment("a <b>banana</b> or two");
		NateNode node2 = elementFor("<p>apple</p>");
		NateNode element = document.find("span").get(0);
		element.replaceWith(asList(node1, node2));
		assertThat(document.render(), matchesXmlIgnoringWhiteSpace("a <b>banana</b> or two <p>apple</p>"));
		assertThat(node1.render(), matchesXmlIgnoringWhiteSpace("a <b>banana</b> or two"));
		assertThat(node2.render(), matchesXmlIgnoringWhiteSpace("<p>apple</p>"));
	}

	@Test(expected=IllegalStateException.class)
	public void shouldBecomeInvalidAfterReplacementOfSelf() throws Exception {
		NateNode element = elementFor("<div>apple</div>");
		element.replaceWith(asList((NateNode) elementFor("<p>banana</p>")));
		// This should throw an exception
		element.setTextContent("peach");
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
