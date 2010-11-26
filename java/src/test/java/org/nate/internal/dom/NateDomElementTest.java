package org.nate.internal.dom;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.nate.testutil.XmlFragmentAssert.assertXmlFragmentsEqual;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.junit.Test;
import org.nate.encoder.NateDocument;
import org.nate.encoder.NateElement;
import org.nate.encoder.NateNode;
import org.nate.internal.dom.NateDomElement;
import org.nate.internal.dom.XmlBasedNateDomDocumentFactory;
import org.w3c.dom.Element;

public class NateDomElementTest {

	@Test
	public void shouldFindDesiredElements() throws Exception {
		NateNode element = elementFor("<div><p>apple</p> hello <p>banana</p></div>");
		List<NateElement> elements = element.find("p");
		assertThat("Unexpected size for: " + elements, elements.size(), is(2));
		assertXmlFragmentsEqual("<p>apple</p>", elements.get(0).render());
		assertXmlFragmentsEqual("<p>banana</p>", elements.get(1).render());
	}

	@Test
	public void shouldFindNoElementsWhenNoneMatch() throws Exception {
		NateNode element = elementFor("<div><p>apple</p> hello <p>banana</p></div>");
		List<NateElement> elements = element.find("p.foo");
		assertThat("Unexpected size for: " + elements, elements.size(), is(0));
	}
	
	@Test
	public void shouldSetTextContentWithSuppliedValue() throws Exception {
		NateNode element = elementFor("<div><p>apple</p></div>");
		element.setTextContent("a & b");
		assertXmlFragmentsEqual("<div>a &amp; b</div>", element.render());
	}
	
	@Test
	public void shouldSetAttributeWithSuppliedValue() throws Exception {
		NateNode element = elementFor("<div><p>apple</p></div>");
		element.setAttribute("foo", "a & \" b");
		assertXmlFragmentsEqual("<div foo='a &amp; &quot; b'><p>apple</p></div>", element.render());
	}
	
	@Test
	public void shouldReturnCopyWhenRequested() throws Exception {
		NateNode original = elementFor("<div>apple</div>");
		NateNode copy = original.copy();
		copy.setTextContent("banana");
		assertXmlFragmentsEqual("<div>banana</div>", copy.render());
		assertXmlFragmentsEqual("<div>apple</div>", original.render());
	}

	@Test
	public void shouldReplaceChildren() throws Exception {
		NateNode element = elementFor("<div><p>apple</p></div>");
		NateDocument newChildren = createDocument("a <b>banana</b> or two");
		element.replaceChildren(newChildren);
		assertXmlFragmentsEqual("<div>a <b>banana</b> or two</div>", element.render());
		assertXmlFragmentsEqual("a <b>banana</b> or two", newChildren.render());
	}
	
	@Test
	public void shouldReplaceSelfWithNewNodes() throws Exception {
		NateNode document = createDocument("<div>apple</div>");
		NateNode node1 = createDocument("a <b>banana</b> or two");
		NateNode node2 = elementFor("<p>apple</p>");
		NateElement element = document.find("div").get(0);
		element.replaceWith(asList(node1, node2));
		assertXmlFragmentsEqual("a <b>banana</b> or two<p>apple</p>", document.render());
		assertXmlFragmentsEqual("a <b>banana</b> or two", node1.render());
		assertXmlFragmentsEqual("<p>apple</p>", node2.render());
	}
	
	@Test(expected=IllegalStateException.class)
	public void shouldBecomeInvalidAfterReplacementOfSelf() throws Exception {
		NateNode element = elementFor("<div>apple</div>");
		element.replaceWith(asList(elementFor("<p>banana</p>")));
		// This should throw an exception
		element.setTextContent("peach");
	}
	
	private NateNode elementFor(String string) {
		NateDocument document = createDocument(string);
		return new NateDomElement((Element) document.getRootNodes().get(0));
	}

	private NateDocument createDocument(String input) {
		return new XmlBasedNateDomDocumentFactory()
			.createFromXmlDocumentFragment(new ByteArrayInputStream(input.getBytes()));
	}

}
