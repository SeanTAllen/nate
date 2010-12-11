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


public class JsoupBackedNateElementTest {

	@Test
	public void shouldFindDesiredElements() throws Exception {
		NateNode element = elementFor("<div><p>apple</p> hello <p>banana</p></div>");
		List<NateNode> elements = element.find("p");
		assertThat("Unexpected size for: " + elements, elements.size(), is(2));
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<p>apple</p>", elements.get(0).render());
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<p>banana</p>", elements.get(1).render());
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
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<span>a &amp; b</span>", element.render());
	}
	
	@Test
	public void shouldSetAttributeWithSuppliedValue() throws Exception {
		NateNode element = elementFor("<span><p>apple</p></span>");
		element.setAttribute("foo", "a & \" b");
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<span foo='a &amp; &quot; b'><p>apple</p></span>", element.render());
	}
	
	@Test
	public void shouldReturnCopyWhenRequested() throws Exception {
		NateNode original = elementFor("<span>apple</span>");
		NateNode copy = original.copy();
		copy.setTextContent("banana");
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<span>banana</span>", copy.render());
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<span>apple</span>", original.render());
	}

	@Test
	public void shouldReplaceChildren() throws Exception {
		NateNode element = elementFor("<span><p>apple</p></span>");
		NateDocument newChildren = createDocumentFragment("a <b>banana</b> or two");
		element.replaceChildren(newChildren);
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<span>a <b>banana</b> or two</span>", element.render());
		assertXmlFragmentsIgnoringWhiteSpaceEqual("a <b>banana</b> or two", newChildren.render());
	}
	
	@Test
	public void shouldReplaceSelfWithNewNodes() throws Exception {
		NateNode document = createDocumentFragment("<section><span>apple</span></section>");
		NateNode node1 = createDocumentFragment("a <b>banana</b> or two");
		NateNode node2 = elementFor("<p>apple</p>");
		NateNode element = document.find("span").get(0);
		element.replaceWith(asList(node1, node2));
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<section> a <b>banana</b> or two <p>apple</p> </section>", document.render());
		assertXmlFragmentsIgnoringWhiteSpaceEqual("a <b>banana</b> or two", node1.render());
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<p>apple</p>", node2.render());
	}
	
	@Test
	public void shouldReplaceSelfWithNewNodesWhenNoParent() throws Exception {
		NateNode document = createDocumentFragment("<span>apple</span>");
		NateNode node1 = createDocumentFragment("a <b>banana</b> or two");
		NateNode node2 = elementFor("<p>apple</p>");
		NateNode element = document.find("span").get(0);
		element.replaceWith(asList(node1, node2));
		assertXmlFragmentsIgnoringWhiteSpaceEqual("a <b>banana</b> or two <p>apple</p>", document.render());
		assertXmlFragmentsIgnoringWhiteSpaceEqual("a <b>banana</b> or two", node1.render());
		assertXmlFragmentsIgnoringWhiteSpaceEqual("<p>apple</p>", node2.render());
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
